package com.goekay.streamrecorder.core;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.conn.DefaultHttpResponseParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.goekay.streamrecorder.Utils.hasNoValue;
import static com.goekay.streamrecorder.Utils.print;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.09.2016
 */
public class StreamParser extends DefaultHttpResponseParser {

    private final SessionInputBuffer buffer;
    private final RecordContext context;

    public StreamParser(SessionInputBuffer buffer, MessageConstraints constraints,
                        RecordContext context) {
        super(buffer, constraints);
        this.buffer = buffer;
        this.context = context;
    }

    @Override
    public HttpResponse parse() throws IOException, HttpException {
        print("Preparing...");
        Map<String, List<String>> headers = processHeaders();
        context.postProcessHeaders(headers);
        context.startingToRecord();

        print("Recording...");
        processBody();
        context.renameToFinalFileName();

        // we do not care about this returned response
        return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_0, 200, ""));
    }

    private Map<String, List<String>> processHeaders() {
        Map<String, List<String>> headers = new HashMap<>();

        try {
            while (true) {
                String line = buffer.readLine();
                if (hasNoValue(line)) {
                    // If there is an empty line, we reached the end of headers
                    break;
                }
                String[] headerLine = line.split(":", 2);
                if (headerLine.length == 2) {
                    List<String> mm = headers.computeIfAbsent(headerLine[0].trim(), str -> new ArrayList<>());
                    mm.add(headerLine[1].trim());
                }
            }
            return headers;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processBody() {
        try (FileOutputStream outputStream = new FileOutputStream(context.getTempFile())) {

            StopRecordingStrategy strat = context.getStopRecordingStrategy();
            byte[] bytes = new byte[16 * 1024];
            int bytesRead;

            // Actual recording logic
            //
            while ((bytesRead = buffer.read(bytes)) > -1 && strat.shouldContinue(bytesRead)) {
                outputStream.write(bytes, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
