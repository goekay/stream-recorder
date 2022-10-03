package com.goekay.streamrecorder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ApplicationTest {

    @Test
    public void actuallyAnIntegrationTest() throws Exception {
        String tmpdir = Files.createTempDirectory("tmpRecordings").toFile().getAbsolutePath();

        String[] args = new String[]{
            "-u", "http://rockfm.rockfm.com.tr:9450",
            "-d", tmpdir,
            "-m", "1",
            "-n", "testing123"
        };

        Application.main(args);

        File file = Paths.get(tmpdir).toFile();
        Assertions.assertTrue(file.exists());

        File[] children = file.listFiles();
        Assertions.assertNotNull(children);

        Assertions.assertEquals(1, children.length);
        Assertions.assertTrue(children[0].getName().startsWith("testing123"));
    }
}
