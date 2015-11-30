package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;

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

//        while (true) {
//            // TODO read and process command here
//            try {
//                Scanner s1 = new Scanner(System.in);
////                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//                System.out.println("Input command, e.g., send NavyPier 41.8916241 -87.6094798");
////                String s = br.readLine();
//                String s = s1.nextLine();
//
//                if (s.equals("exit")) {
//                    break;
//                }
//
//                String[] ss = s.split(" ");
//                if (ss[0].equals("send")) {
//                    if (ss.length <= 4) {
//                        System.out.println("Invalid args");
//                        continue;
//                    }
//                    try {
//                        double d1 = Double.parseDouble(ss[2]);
//                        double d2 = Double.parseDouble(ss[3]);
//                    } catch (NumberFormatException e) {
//                        System.out.println("Invalid coordinates.");
//                        continue;
//                    }
//                    _server.SendToAll("Emergency", s.substring(5, s.length()));
//                } else {
//                    System.out.println("Invalid command.");
//                }
//                //TODO process
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                break;
//            }
//        }

        _server.Stop();
    }
}
