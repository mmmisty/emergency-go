package com.company;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Server {

    private int _port;
    private HttpServer _server;

    public Server(int port) throws IOException {
        _port = port;
        _server = HttpServer.create(new InetSocketAddress(_port), 0);
        _server.setExecutor(null);
        RegisterHandlers();
    }

    public void Start() {
        _server.start();
    }

    private void RegisterHandlers() {
        _server.createContext("/", new IndexHandler());
    }

    private class IndexHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String resp = "ok";
            httpExchange.sendResponseHeaders(200, resp.length());
            OutputStream out = httpExchange.getResponseBody();
            out.write(resp.getBytes());
            out.close();
        }
    }
}
