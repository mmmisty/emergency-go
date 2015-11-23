package com.company;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class GCMClient {

    private String _apiKey;

    public GCMClient(String apiKey) {
        _apiKey = apiKey;
    }

    public Result sendMessage(String toDevice, String title, String message) throws Exception {
        System.out.println("sendMessage " + toDevice + " " + title + " " + message);

        URL url = new URL("https://gcm-http.googleapis.com/gcm/send");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        // set request headers
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key="+_apiKey);
        conn.setRequestProperty("Content-Type", "application/json");

        // build json object
        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("icon", "ic_launcher");
        notification.put("body", message);
        notification.put("sound", "default");
        notification.put("click_action", "OPEN_ACTIVITY_1");

        JSONObject msg = new JSONObject();
        msg.put("to", toDevice);
        msg.put("notification", notification);
        System.out.println("sendMessage json " + msg.toString());

        // write request body
        conn.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes(msg.toString());
        out.flush();
        //out.close();

        Result result = new Result();
        result.Code = conn.getResponseCode();
        System.out.println("sendMessage code " + result.Code);

        // read response
        InputStream is = null;
        if (result.Code >= 400) {
            is = conn.getErrorStream();
        } else {
            is = conn.getInputStream();
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(is)
        );
        StringBuffer resp = new StringBuffer();
        String line;
        while ((line = in.readLine()) != null) {
            resp.append(line);
        }

        in.close();
        out.close();
        result.RawBody = resp.toString();

        // parse response
        result.Result = new JSONObject(resp);

        return result;
    }

    public class Result {
        public int Code;
        public JSONObject Result;
        public String RawBody;
    }
}
