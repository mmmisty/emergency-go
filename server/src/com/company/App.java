package com.company;

import com.sun.xml.internal.ws.server.provider.ProviderInvokerTube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class App {

    private Properties _config;
    private Server _server;

    public App(Properties config) throws IOException {
        _config = config;
    }

    public void Run() {

        int port = Integer.parseInt(_config.getProperty("port"));
        try {
            _server = new Server(port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        _server.Start();
        System.out.println("Server started " + port);

        while (true) {
            // TODO read and process command here
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("# ");
                String s = br.readLine();

                if (s.equals("exit")) {
                    break;
                }

                //TODO process

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        _server.Stop();
    }
}
