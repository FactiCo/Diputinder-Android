package mx.com.factico.diputinder.httpconnection;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import mx.com.factico.diputinder.dialogues.Dialogues;

public class HttpConnection {
    public static final String TAG_CLASS = HttpConnection.class.getName();

    //public static final String URL_HOST = "http://liguepolitico-staging.herokuapp.com";
    //public static final String URL_HOST = "http://158.85.249.218";
    public static final String URL_HOST = "https://liguepolitico-2016.herokuapp.com";
    //158.85.249.218
    public static final String GEOCODER = "/geocoder.json?latitude=%s&longitude=%s";

    public static final String COUNTRIES = "/countries";
    public static final String STATES = "/states";
    public static final String CITIES = "/cities";

    public static final String MESSAGES = "/messages";

    public static final String TWITTER_IMAGE_URL = "https://twitter.com/%s/profile_image?size=original";

    public static String GET(String url) {
        String result = null;
        HttpURLConnection urlConnection = null;
        try {
            URL u = new URL(url);
            urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(40000);
            urlConnection.setReadTimeout(40000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK ||
                    urlConnection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                result = convertInputStreamToString(in);

                //Dialogues.Log(TAG_CLASS, result, Log.DEBUG);
            } else {
                InputStream in = new BufferedInputStream(urlConnection.getErrorStream());
                result = convertInputStreamToString(in);
            }


        } catch (IOException e) {
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}
