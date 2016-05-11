package com.example.rest;

import com.example.rest.utils.InitializeDataUtil;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * @author Kamil Walkowiak
 */
public class Server {
    public static void main(String[] args) throws IOException {
        InitializeDataUtil.initializeData();

        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8000).build();
        ResourceConfig config = new ResourceConfig().packages("com.example.rest.resources").register(DeclarativeLinkingFeature.class);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
        server.start();
    }
}
