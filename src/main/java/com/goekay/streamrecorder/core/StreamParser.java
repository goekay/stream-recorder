package com.goekay.streamrecorder.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.09.2016
 */
@Slf4j
public class StreamParser extends DefaultHttpResponseParser {

    private final SessionInputBuffer buffer;
    private final Map<String, String> headers = new HashMap<>();

    public StreamParser(SessionInputBuffer buffer, MessageConstraints constraints) {
        super(buffer, constraints);
        this.buffer = buffer;
    }

    @Override
    public HttpResponse parse() throws IOException, HttpException {

        processHeaders();
        log.info("Finished reading headers: {}", headers);
        processBody();

        // we do not care about this returned response
        return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_0, 200, ""));
    }

    private void processHeaders() {
        try {
            while (true) {
                String line = buffer.readLine();
                if (isBlank(line)) {
                    break;
                }
                String[] headerLine = line.split(":", 2);
                if (headerLine.length == 2) {
                    headers.put(headerLine[0].trim(), headerLine[1].trim());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processBody() {
        try {
            byte[] bytes = new byte[16 * 1024];

            // Actual recording logic
            //
            while (buffer.read(bytes) > -1) {
                // TODO
                log.info("read bytes");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isBlank(String line) {
        return line == null || line.isEmpty();
    }
}
