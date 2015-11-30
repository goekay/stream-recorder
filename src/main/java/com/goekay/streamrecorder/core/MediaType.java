package com.goekay.streamrecorder.core;

import lombok.RequiredArgsConstructor;

import static com.goekay.streamrecorder.Utils.warn;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.11.2015
 */
@RequiredArgsConstructor
public enum MediaType {

    AACP("audio/aacp", "aac"),
    AAC("audio/aac", "aac"),
    MP3("audio/mpeg", "mp3");

    private final String contentTypeHeader;
    private final String fileExtension;

    public static String getExtension(String typeHeader) {
        for (MediaType mt : MediaType.values()) {
            if (mt.contentTypeHeader.equalsIgnoreCase(typeHeader)) {
                return mt.fileExtension;
            }
        }

        warn("Could not determine stream type. File extension will be 'raw'");
        return "raw";
    }
}
