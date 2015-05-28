package mx.com.factico.diputinder.httpconnection;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;

import mx.com.factico.diputinder.dialogues.Dialogues;

public class HttpConnection {
	public static final String TAG_CLASS = HttpConnection.class.getSimpleName();
	
	public static final String URL = "https://candidatotransparente.mx/";
    public static final String DIPUTADOS = "scripts/datos/Diputados.json";
	public static final String GOBERNADORES = "scripts/datos/Gobernadores.json";
	public static final String ALCALDIAS = "scripts/datos/Alcaldias.json";

	public static final String PDFS = "api/api/candidatos/transparentes";

	public static final String TWITTER_IMAGE_URL = "https://twitter.com/@%s/profile_image?size=original";

	public static String GET(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		String result = null;
		
		HttpResponse response;
		try {
			response = client.execute(request);
			
			Dialogues.Log(TAG_CLASS, "Http Get Response:" + response.toString(), Log.DEBUG);
			
			HttpEntity httpEntity = response.getEntity();

			result = EntityUtils.toString(httpEntity);
			
			Dialogues.Log(TAG_CLASS, result, Log.DEBUG);
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
