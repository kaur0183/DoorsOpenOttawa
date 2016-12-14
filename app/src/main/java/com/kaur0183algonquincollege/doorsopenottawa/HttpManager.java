package com.kaur0183algonquincollege.doorsopenottawa;


import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * In this class HttpManager is getting the url and all the data form json and return it
 *
 * @author Prabhjot kaur (kaur0183@algonquinlive.com)
 */

public class HttpManager {

    public static String getData(RequestPackage p, String userName, String password) {

        BufferedReader reader = null;
        HttpURLConnection con = null;

        byte[] loginBytes = (userName + ":" + password).getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));

        String uri = p.getUri();
        if (p.getMethod() == HttpMethod.GET) {
            uri += "?" + p.getEncodedParams();
        }

        try {
            // open the URI
            URL url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty("Authorization", loginBuilder.toString());

            JSONObject json = new JSONObject(p.getParams());
            String params = json.toString();

            if (p.getMethod() == HttpMethod.POST || p.getMethod() == HttpMethod.PUT) {
                con.addRequestProperty("Accept", "application/json");
                con.addRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(params);
                writer.flush();
            }

            // make a buffered reader
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            // read the HTTP response from URI one-line-at-a-time
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            // return the HTTP response
            return sb.toString();

            // exception handling: a) print stack-trace, b) return null
        } catch (Exception e) {
            e.printStackTrace();
            try {
                int status = con.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
}
