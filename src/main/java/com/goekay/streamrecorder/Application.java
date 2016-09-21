package com.goekay.streamrecorder;

import com.beust.jcommander.JCommander;
import com.goekay.streamrecorder.core.StreamRecorder;

import java.io.IOException;

import static com.goekay.streamrecorder.Utils.error;
import static com.goekay.streamrecorder.Utils.print;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.11.2015
 */
public class Application {

    public static void main(String... args) {
        UserConfig config = new UserConfig();
        JCommander jCommander = new JCommander(config);

        if (args.length == 0) {
            jCommander.setProgramName("stream-recorder");
            jCommander.usage();
        } else {
            jCommander.parse(args);
            run(config);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static void run(UserConfig config) {
        print("[Config] Stream: %s", config.getStreamUrl());
        print("[Config] Recording directory: %s", config.getRecordDirectory());
        print("[Config] Recording duration: %s hour(s) %s minute(s)", config.getHours(), config.getMinutes());

        try (StreamRecorder recorder = new StreamRecorder(config)) {
            recorder.start();
        } catch (IOException e) {
            error("Error occurred %s", e.getMessage());
        }
        print("Done");
    }
}
