package com.example.gp.a2allakfeendemo;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Gehad on 5/19/2017.
 */

public class DBmanager {

    String HOST_NAME = "http://ec2-54-200-210-102.us-west-2.compute.amazonaws.com/server_connect/";

    //TODO: need to edit sendGetRequest to take parameters or implement another method that take parameters (for select or call queries with conditions)
    protected String sendGetRequest(String fileName){
        String link = HOST_NAME+fileName;
            try {
                //Connection
                Uri builtUri = Uri.parse(link);
                URL url = new URL(builtUri.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                String result = buffer.toString();
                Log.d("DBManager1", result);
                return result;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("Exception", e.getMessage());
            }
            return null;
    }

    protected String sendPostRequest(String fileName, ArrayList<Object> parameters){
        //TODO:Not working right needs to be edited
        String link = HOST_NAME+fileName;
        try {
            //Connection
            Uri builtUri = Uri.parse(link);
            URL url = new URL(builtUri.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            int i=1;
            for (Object param:parameters) {
                urlConnection.setRequestProperty("param".concat(String.valueOf(i)),param.toString());
            }
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            //output the stream to server
            OutputStream outputPost = new BufferedOutputStream(urlConnection.getOutputStream());
            //writeStream(outputPost);
            outputPost.flush();
            outputPost.close();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            String result = buffer.toString();
            Log.d("DBManager1", result);
            return result;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("Exception", e.getMessage());
        }
        return null;
    }

}
