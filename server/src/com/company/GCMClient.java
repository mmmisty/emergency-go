package com.company;


import org.json.JSONObject;
import org.json.JSONWriter;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class GCMClient {

    private String _apiKey;

    public GCMClient(String apiKey) {
        _apiKey = apiKey;
    }

    private Result sendMessage(String toDevice, String title, String message) throws Exception {
        URL url = new URL("https://gcm-http.googleapis.com/gcm/send");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        // set request headers
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key="+_apiKey);
        conn.setRequestProperty("Content-Type", "application/json");

        // build json object
        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("icon", "myicon");
        notification.put("text", message);

        JSONObject msg = new JSONObject();
        msg.put("to", toDevice);
        msg.put("notification", notification);

        // write request body
        conn.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes(msg.toString());
        out.flush();
        out.close();

        Result result = new Result();
        result.Code = conn.getResponseCode();

        // read response
        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );
        StringBuffer resp = new StringBuffer();
        String line;
        while ((line = in.readLine()) != null) {
            resp.append(line);
        }
        in.close();

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
