package com.goekay.streamrecorder.core;

import com.goekay.streamrecorder.RecordConfig;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.goekay.streamrecorder.Utils.cleanUp;
import static com.goekay.streamrecorder.Utils.hasValue;
import static com.goekay.streamrecorder.Utils.print;
import static com.goekay.streamrecorder.Utils.warn;

/**
 * Mothership
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.11.2015
 */
@RequiredArgsConstructor
public class StreamRecorder {

    private final RecordConfig config;

    private File tempFile;
    private long startMillis;

    private StopRecordingStrategy stopRecordingStrategy;
    private String filePrefix;
    private String fileExtension;

    public void start() throws IOException {
        startMillis = System.currentTimeMillis();
        URLConnection connection = prepare();

        try (FileOutputStream outputStream = new FileOutputStream(tempFile);
             InputStream is = connection.getInputStream()) {

            print("Recording...");

            byte[] buffer = new byte[16 * 1024];
            int bytesRead;

            // Actual recording logic
            //
            while ((bytesRead = is.read(buffer)) > -1 && stopRecordingStrategy.shouldContinue(bytesRead)) {
                outputStream.write(buffer, 0, bytesRead);
            }

        } finally {
            setFinalFileName();
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private URLConnection prepare() throws IOException {
        print("Preparing...");

        URLConnection connection;
        try {
            connection = config.getStreamUrl().openConnection();
            connection.connect();
        } catch (Exception e) {
            throw new RuntimeException("Connection could not be established");
        }

        File dir = config.getRecordDirectory();
        tempFile = dir.toPath().resolve(".recording.tmp").toFile();

        StreamHeaderReader headerReader = new StreamHeaderReader(connection.getHeaderFields());
        fileExtension = headerReader.getFileExtension();

        String prefix = cleanUp(config.getPreferredFilePrefix());
        if (hasValue(prefix)) {
            filePrefix = prefix;
        } else {
            filePrefix = headerReader.getName();
        }

        Integer kbps = headerReader.getBitrate();
        if (kbps == null) {
            warn("Since bit rate is unknown, the duration of the recording might be inaccurate "
                    + "(probably longer than what you wanted)");
            stopRecordingStrategy = new TimeBasedStrategy(config);
        } else {
            stopRecordingStrategy = new BitrateBasedStrategy(config.getRecordDurationInSeconds(), kbps);
        }

        return connection;
    }

    private void setFinalFileName() throws IOException {
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
