package com.example.gp.a2allakfeendemo;

import android.net.Uri;
import android.util.Log;

import com.example.gp.a2allakfeendemo.Data.Parameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Gehad on 5/19/2017.
 */

public class DBmanager {

    String HOST_NAME = "http://ec2-54-200-210-102.us-west-2.compute.amazonaws.com/server_connect/";

    //To send get request ( parameters are not confidential )
    protected String sendRequest(String requestMethod,boolean ourServer,String Link,ArrayList<Parameter> parameters){
        String link;
        if (ourServer)
            link = HOST_NAME+Link;
        else
            link = Link;

        try {
                //add parameters to the link
                String url_str=link;
                if (parameters != null){
                    url_str += "?";
                    int i=0;
                    for (Parameter param:parameters) {
                        if (i==0)
                            url_str += (param.getName() + "=" + param.getValue());
                        else
                            url_str += ("&" + param.getName() + "=" + param.getValue());
                        i++;
                    }
                }
                //Connection
                Log.v("final_url",url_str);
                Uri builtUri = Uri.parse(url_str);
                URL url = new URL(builtUri.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(requestMethod);
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



}
