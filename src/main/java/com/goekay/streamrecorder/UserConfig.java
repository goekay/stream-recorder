package com.goekay.streamrecorder;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;
import lombok.Getter;

import java.io.File;
import java.net.URL;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.11.2015
 */
@Getter
public final class UserConfig {

    @Parameter(
            names = {"-u", "--url"},
            description = "Stream url to record",
            required = true
    )
    private URL streamUrl;

    @Parameter(
            names = {"-d", "--dir"},
            description = "Directory to save the recordings",
            required = true,
            converter = DirConverter.class
    )
    private File recordDirectory;

    @Parameter(
            names = {"-n", "-name",},
            description = "Name of the stream/broadcast. "
                    + "Will be used as the prefix of the file. "
                    + "If not set, the information will be derived from the stream headers"
    )
    private String preferredFilePrefix;

    @Parameter(
            names = {"-h", "--hour"},
            validateWith = PositiveInteger.class,
            description = "Recording duration in hours (can be combined with minutes)"
    )
    private int hours = 0;

    @Parameter(
            names = {"-m", "--min"},
            validateWith = PositiveInteger.class,
            description = "Recording duration in minutes (can be combined with hours)"
    )
    private int minutes = 0;
}
