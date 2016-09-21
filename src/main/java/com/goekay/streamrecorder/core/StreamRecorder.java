package com.goekay.streamrecorder.core;

import com.goekay.streamrecorder.UserConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.IOException;

/**
 * Mothership
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.11.2015
 */
public class StreamRecorder implements Closeable {

    private final RecordContext context;
    private final CloseableHttpClient client;

    public StreamRecorder(UserConfig config) {
        context = new RecordContext(config);
        client = HttpClients.custom()
                            .setConnectionManager(initConnectionManager())
                            .build();
    }

    private HttpClientConnectionManager initConnectionManager() {
        // Use a custom connection factory to customize the process of
        // initialization of outgoing HTTP connections. Beside standard connection
        // configuration parameters HTTP connection factory can define message
        // parser / writer routines to be employed by individual connections.
        HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory =
                new ManagedHttpClientConnectionFactory(
                        DefaultHttpRequestWriterFactory.INSTANCE,
                        (buffer, constraints) -> new StreamParser(buffer, constraints, context)
                );

        // SSL context for secure connections can be created either based on
        // system or application specific properties.
        SSLContext sslcontext = SSLContexts.createSystemDefault();

        // Create a registry of custom connection socket factories for supported
        // protocol schemes.
        Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                        .register("https", new SSLConnectionSocketFactory(sslcontext))
                        .build();

        // Create a connection manager with custom configuration.
        return new BasicHttpClientConnectionManager(socketFactoryRegistry, connFactory);
    }

    public void start() throws IOException {
        HttpGet get = new HttpGet(context.getUserConfig().getStreamUrl().toString());
        client.execute(get);
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }
}
