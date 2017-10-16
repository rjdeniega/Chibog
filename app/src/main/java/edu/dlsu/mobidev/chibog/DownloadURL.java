package edu.dlsu.mobidev.chibog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadURL {
    String data = "";
    InputStream inputStream = null;
    HttpURLConnection urlConnection = null;

    public String readURL(String receivedURL) throws IOException {
        try {
            //create URL and open connection
            URL url = new URL(receivedURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            //read URL with input stream then pass to buffered reader
            inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer stringBuffer = new StringBuffer();
            String line;

            //append lines to string buffer if its not empty
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
            //convert contents of string buffer to string to be passed outside of class
            data = stringBuffer.toString();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
