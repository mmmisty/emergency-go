package com.company;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        String configPath = "./config.properties";
        if (args.length > 0) {
            configPath = args[0];
        }

        Properties config = loadConfig(configPath);
        if (config == null) {
            System.out.println("Load config failed. path=" + configPath);
            System.exit(1);
        }

        App app = null;
        try {
            app = new App(config);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        app.Run();  // run forever
    }

    private static Properties loadConfig(String path) {
        Properties config = new Properties();
        try {
            FileInputStream file = new FileInputStream(path);
            config.load(file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return config;
    }
}
