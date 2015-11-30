package com.goekay.streamrecorder.core;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.11.2015
 */
public interface StopRecordingStrategy {
    boolean shouldContinue(int bytesRead);
}
