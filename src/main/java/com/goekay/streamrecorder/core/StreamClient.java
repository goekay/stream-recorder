package com.goekay.streamrecorder.core;

import lombok.Getter;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.09.2016
 */
public class StreamClient {

    @Getter
    private final HttpClient delegate;

    public StreamClient() {

        // Use a custom connection factory to customize the process of
        // initialization of outgoing HTTP connections. Beside standard connection
        // configuration parameters HTTP connection factory can define message
        // parser / writer routines to be employed by individual connections.
        HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory =
                new ManagedHttpClientConnectionFactory(
                        DefaultHttpRequestWriterFactory.INSTANCE,
                        StreamParser::new
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
        BasicHttpClientConnectionManager connManager =
                new BasicHttpClientConnectionManager(socketFactoryRegistry, connFactory);

        this.delegate = HttpClients.custom()
                                   .setConnectionManager(connManager)
                                   .build();
    }
}
