package mx.com.factico.diputinder.httpconnection;

import android.os.Build;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import mx.com.factico.diputinder.dialogues.Dialogues;

public class HttpConnection {
    public static final String TAG_CLASS = HttpConnection.class.getSimpleName();

    public static final String URL_HOST = "http://liguepolitico-staging.herokuapp.com";
    public static final String GEOCODER = "/geocoder.json?latitude=%s&longitude=%s";

    public static final String COUNTRIES = "/countries";
    public static final String STATES = "/states";
    public static final String CITIES = "/cities";

    public static final String TWITTER_IMAGE_URL = "https://twitter.com/%s/profile_image?size=original";

    public static String GET(String url) {
        String result = null;
        HttpURLConnection urlConnection = null;
        try {
            URL u = new URL(url);
            urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(60000);
            urlConnection.setReadTimeout(60000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                result = convertInputStreamToString(in);
            } else {

            }


        } catch (IOException e) {

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            URL url = new URL("http://www.android.com/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String json = convertInputStreamToString(in);

                return json;
            } catch (IOException e) {

            } finally {
                urlConnection.disconnect();
            }
        } else {*/
            /*HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);

            String result = null;

            HttpResponse response;
            try {
                response = client.execute(request);

                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    result = out.toString();
                    out.close();
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }

                /*
                HttpEntity httpEntity = response.getEntity();

                result = EntityUtils.toString(httpEntity, HTTP.UTF_8);

                Dialogues.Log(TAG_CLASS, result, Log.DEBUG);*//*

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        //}*/

        return result;
    }

    public static String GETJSON(String url) {
        InputStream inputStream = null;
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    public static String POST(String url, String json) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        String result = null;

        try {
            // httpPost.setEntity(new StringEntity(json));
            httpPost.setEntity(createStringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");

            // httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, HTTP.UTF_8));
            // httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

            HttpResponse response = httpClient.execute(httpPost);
            Dialogues.Log(TAG_CLASS, "Http Post Response:" + response.toString(), Log.DEBUG);

            HttpEntity httpEntity = response.getEntity();

            result = EntityUtils.toString(httpEntity);

            Dialogues.Log(TAG_CLASS, result, Log.ERROR);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /*public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = "";
            String result = "";
            while ((line = br.readLine()) != null)
                result += line;

            return result;
        }
    }*/

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    private static HttpEntity createStringEntity(String json) {
        StringEntity se = null;
        try {
            se = new StringEntity(json, "UTF-8");
            se.setContentType("application/json; charset=UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG_CLASS, "Failed to create StringEntity", e);
        }
        return se;
    }
}
