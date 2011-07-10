package edu.neukom.emotodrome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Server {
	//	private final String INTERACT_URL = "http://emotodrome.com/scripts/interact.php";
	private final String INTERACT_URL = "http://darrenlincheng.com/emoto/scripts/interact.php";
	private String android_id;
	private double longitude, latitude;
	private int zoom;
	private String serverResponse;

	public Server(String android_id, double longitude, double latitude, int zoom) {
		this.android_id = android_id;
		this.longitude = longitude;
		this.latitude = latitude;
		this.zoom = zoom;
		serverResponse = "";
	}

	public void runServerLoop(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		try {
			InputStream content = queryURL(INTERACT_URL + "?id=" + android_id +
					"&lng=" + this.longitude + "&lat=" + this.latitude + "&mv=99&z=" + zoom);
			parseInputStream(content);
		} catch (IOException e) {e.printStackTrace();}
	}


	//Query URL and open and return input stream
	private InputStream queryURL(String url) throws IOException {
		InputStream content = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = httpclient.execute(new HttpGet(url));
		content = response.getEntity().getContent();
		return content;
	}
	
	public String getServerResponse() {
		return serverResponse;
	}

	//parse input stream and close it 
	private void parseInputStream(InputStream content) throws IOException {
		//Retrieve Content
		InputStreamReader reader = new InputStreamReader(content);
		BufferedReader in = new BufferedReader(reader);
		System.out.println("buffer reader " + in);
		String str;
		serverResponse = "";
		System.out.println("Server Return: \"");
		if((str = in.readLine()) != null) {
			System.out.println("\t" + str);
			serverResponse += str;
		}
		System.out.println("\"");
		content.close();
		in.close();
	}
}
