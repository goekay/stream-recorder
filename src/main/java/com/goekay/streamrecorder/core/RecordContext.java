package com.goekay.streamrecorder.core;

import com.goekay.streamrecorder.UserConfig;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.goekay.streamrecorder.Utils.cleanUp;
import static com.goekay.streamrecorder.Utils.hasValue;
import static com.goekay.streamrecorder.Utils.print;
import static com.goekay.streamrecorder.Utils.warn;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.09.2016
 */
@Getter
public class RecordContext {

    // user input
    private final UserConfig userConfig;

    // derived/calculated
    private long startMillis;
    private long recordDurationInSeconds;
    private String filePrefix;
    private String fileExtension;
    private File tempFile;
    private StopRecordingStrategy stopRecordingStrategy;

    public RecordContext(UserConfig userConfig) {
        this.userConfig = userConfig;
        setDuration();
    }

    private void setDuration() {
        long duration = TimeUnit.HOURS.toSeconds(userConfig.getHours())
                + TimeUnit.MINUTES.toSeconds(userConfig.getMinutes());

        if (duration <= 0) {
            throw new RuntimeException("Recording duration must be positive");
        }

        this.recordDurationInSeconds = duration;
    }

    public void startingToRecord() {
        startMillis = System.currentTimeMillis();
    }

    public void postProcessHeaders(Map<String, List<String>> headers) throws IOException {
        File dir = userConfig.getRecordDirectory();
        tempFile = dir.toPath().resolve(".recording.tmp").toFile();

        StreamHeaderReader headerReader = new StreamHeaderReader(headers);
        fileExtension = headerReader.getFileExtension();

        String prefix = cleanUp(userConfig.getPreferredFilePrefix());
        if (hasValue(prefix)) {
            filePrefix = prefix;
        } else {
            filePrefix = headerReader.getName();
        }

        Integer kbps = headerReader.getBitrate();
        if (kbps == null) {
            warn("Since bit rate is unknown, the duration of the recording might be inaccurate "
                    + "(probably longer than what you wanted)");
            stopRecordingStrategy = new TimeBasedStrategy(recordDurationInSeconds);
        } else {
            stopRecordingStrategy = new BitrateBasedStrategy(recordDurationInSeconds, kbps);
        }
    }

    public void renameToFinalFileName() throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm")
                                                 .withZone(ZoneId.systemDefault());

        String startStr = dtf.format(Instant.ofEpochMilli(startMillis));
        String stopStr = dtf.format(Instant.now());

        String fileName = filePrefix + "_[" + startStr + "__" + stopStr + "]." + fileExtension;

        Path p = tempFile.toPath();
        Path finalPath = Files.move(p, p.resolveSibling(fileName));
        Path finalFileName = finalPath.getFileName();

        if (finalFileName != null) {
            print("File name: %s", finalFileName.toString());
        }
    }
}
