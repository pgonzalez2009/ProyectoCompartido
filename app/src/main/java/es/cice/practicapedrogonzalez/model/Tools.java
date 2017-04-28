package es.cice.practicapedrogonzalez.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Tools {
	private final static String TAG = "Tools";

	public static boolean isNetworkAvailable(Context context) {
		 Log.e(TAG, "- isNetworkAvailable()...");
		 boolean rdo = false;
		 ConnectivityManager connectivity = (ConnectivityManager)
					context.getSystemService(Context.CONNECTIVITY_SERVICE);

		 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			 Network[] networks = connectivity.getAllNetworks();
			 NetworkInfo networkInfo;
			 for (Network mNetwork : networks) {
				 networkInfo = connectivity.getNetworkInfo(mNetwork);
				 if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
					 rdo = true;
				 }
			 }
		 }else{
			 if (connectivity != null) {
				 NetworkInfo[] info = connectivity.getAllNetworkInfo();
				 if (info != null) {
					 for (int i = 0; i < info.length; i++) {
						 if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							 rdo = true;
						 }
					 }
				 }
			 }
		 }
		 return rdo;
	 }

	private static String getParamDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
		Log.e(TAG, "- getParamDataString()...");
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}

		return result.toString();
	}

	public static String getGoogleApiData(String requestURL, HashMap<String, String> params) {
		HttpURLConnection con = null ;
		URL url;
		InputStream is = null;
		Log.e(TAG, "- getGooglePlaceData()...");

		try {
			url = new URL(requestURL + getParamDataString(params));
			Log.e(TAG, "- requestURL : " + requestURL);
			Log.e(TAG, "- params : " + getParamDataString(params));

			con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			Log.e(TAG, "- connect()...");

			StringBuffer buffer = new StringBuffer();
			is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = br.readLine()) != null )
				buffer.append(line + "\r\n");

			is.close();
			con.disconnect();
			return buffer.toString();
		}
		catch(Throwable t) {
			Log.e(TAG, "- exception...");
			t.printStackTrace();
		}
		finally {
			try { is.close(); } catch(Throwable t) {}
			try { con.disconnect(); } catch(Throwable t) {}
		}
		return null;
	}

	public static Bitmap getGooglePlacePhoto(String requestURL, HashMap<String, String> params){
		Log.e(TAG, "- getGooglePlacePhoto()...");
		Bitmap imgFoto = null;
		byte[] byteImg = null;

		try {
			Log.e(TAG, "- requestURL : " + requestURL);
			Log.e(TAG, "- params : " + getParamDataString(params));
			byteImg = getInternetBitmap(requestURL + getParamDataString(params));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "- exception...");
			e.printStackTrace();
		}

		if(byteImg != null){
			imgFoto = BitmapFactory.decodeByteArray(byteImg, 0, byteImg.length);
		}
		return imgFoto;
	}

	public static byte[] getInternetBitmap(String requestURL){
		Log.e(TAG, "- getInternetBitmap()...");
		InputStream in;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] bitmapRdo = null;
		byte[] data = new byte[16384];

		try {
			in = OpenHttpConnection(requestURL);
			int nRead;

			if (in !=null){
				while ((nRead = in.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}

				buffer.flush();
				bitmapRdo = buffer.toByteArray();
			}
		} catch (IOException e) {
			Log.e(TAG, "- exception...");
			e.printStackTrace();
		}
		return bitmapRdo;
	}

	private static InputStream OpenHttpConnection(String strURL) throws IOException{
		Log.e(TAG, "- OpenHttpConnection()...");
		InputStream inputStream = null;
		URL url = new URL(strURL);
		URLConnection conn = url.openConnection();
		try{
			HttpURLConnection httpConn = (HttpURLConnection)conn;
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			Log.e(TAG, "- connect()...");
			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = httpConn.getInputStream();
			}
		} catch (Exception ex) {
			Log.e(TAG, "- exception...");
			ex.printStackTrace();
		} return inputStream;
	}
}
