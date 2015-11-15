package com.company;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

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

    public void Stop() {
        _server.stop(0);
    }

    private void RegisterHandlers() {
        _server.createContext("/", new IndexHandler());
        _server.createContext("/register", new GcmRegisterHandler());
    }

    /////////////////////////////////////////////////////////////////////
    // handlers

    private class GcmRegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange h) throws IOException {
            Map<String, String> params = Server.getURLParams(h);
            String token = params.get("token");
            String user = params.get("user");
            if (token.equals("")) {
                Server.response(h, 400, "missing param 'token'");
                return;
            }
            if (user.equals("")) {
                Server.response(h, 400, "missing param 'user'");
                return;
            }

            System.out.println("Register token: " + user + " " + token);

            //TODO

            Server.response(h, 200, "ok");
        }
    }

    private class EmergencyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange h) throws IOException {

            Server.response(h, 200, "ok");
        }
    }

    private class IndexHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange h) throws IOException {
            Server.response(h, 200, "hello");
        }
    }

    static private void response(HttpExchange httpExchange, int code, String message) {
        try {
            httpExchange.sendResponseHeaders(code, message.length());
            OutputStream out = httpExchange.getResponseBody();
            out.write(message.getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static private Map<String, String> getURLParams(HttpExchange httpExchange){
        String query = httpExchange.getRequestURI().getQuery();

        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
}
