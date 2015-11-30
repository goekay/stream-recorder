package com.goekay.streamrecorder;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

import java.io.File;

import static com.goekay.streamrecorder.Utils.print;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.11.2015
 */
public class DirConverter implements IStringConverter<File> {

    @Override
    public File convert(String dir) {
        File recordDir = new File(dir);

        boolean created = recordDir.mkdir();
        if (created) {
            print("Created directory: %s", dir);
        }

        if (!recordDir.isDirectory()) {
            throw new ParameterException(dir + " is not a directory");
        }

        return recordDir;
    }
}
