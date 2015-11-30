package com.goekay.streamrecorder.core;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.11.2015
 */
public class BitrateBasedStrategy implements StopRecordingStrategy {

    private long byteReadSizeTotal;
    private long byteReadSizeCounter = 0;

    public BitrateBasedStrategy(long recordDurationInSeconds, int bitrate) {

        // For example, if the stream is 32 kbit/s:
        // 32 kbit/s = 1000 * 32 bit/s = 1000 * 4 byte/s
        //
        this.byteReadSizeTotal = (1000 * bitrate / 8) * recordDurationInSeconds;
    }

    @Override
    public boolean shouldContinue(int bytesRead) {
        byteReadSizeCounter += bytesRead;
        return byteReadSizeCounter <= byteReadSizeTotal;
    }
}
