package com.goekay.streamrecorder.core;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.goekay.streamrecorder.Utils.cleanUp;
import static com.goekay.streamrecorder.Utils.hasValue;
import static com.goekay.streamrecorder.Utils.warn;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.11.2015
 */
@RequiredArgsConstructor
public class StreamHeaderReader {

    private final Map<String, List<String>> headerMap;

    public String getFileExtension() {
        String contentType = getField("content-type");
        return MediaType.getExtension(contentType);
    }

    public String getName() {
        String name = getField("icy-name");
        name = cleanUp(name);
        if (hasValue(name)) {
            return name;
        }

        warn("Could not determine stream name. File name will start with 'unknown_stream_name'");
        return "unknown_stream_name";
    }

    public Integer getBitrate() {
        String bitrate = getField("icy-br");
        if (bitrate != null) {
            return Integer.valueOf(bitrate);
        }

        bitrate = getField("icy-bitrate");
        if (bitrate != null) {
            return Integer.valueOf(bitrate);
        }

        warn("Could not determine stream bit rate");
        return null;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String getField(String key) {
        List<String> values = null;

        for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
            if (key.equalsIgnoreCase(entry.getKey())) {
                values = entry.getValue();
                break;
            }
        }

        if (values == null || values.size() != 1) {
            return null;
        }

        return values.get(0);
    }
}
