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
        try {
            _server = new Server(_config);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        _server.Start();

        while (true) {
            // TODO read and process command here
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("# ");
                String s = br.readLine();

                if (s.equals("exit")) {
                    break;
                }

                String[] ss = s.split(" ");
                switch (ss[0]) {
                    case "send":
                        if (ss.length <= 1) {
                            System.out.println("invalid arg");
                        }
                        _server.SendToAll("Emergency", ss[1]);
                    default:
                        System.out.println("invalid cmd");
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
