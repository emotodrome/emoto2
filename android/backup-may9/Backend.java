package emoto.neukom.edu;

import java.io.File;
import java.util.UUID;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings.Secure;

public class Backend{
	private Server server;
	private MyLocationManager locationManager;
	private ImageManager imageManager;
	private Activity activity;
	private File storage;
	private final String IMAGE_NAME = "map.jpeg";
	private final String IMAGE_NAME_SWAP = "swap.jpeg";
	private String imageName;
	private boolean isSwap;
	private int zoom;
	private final int INIT_ZOOM = 3;
	
	public Backend(Activity activity) {
		this.activity = activity;
		
		locationManager = new MyLocationManager(activity);
		System.out.println("Initial Lat: " + locationManager.getLatitude());
		System.out.println("Initial Long: " + locationManager.getLongitude());

		if(Environment.getExternalStorageState() != null)
			storage = Environment.getExternalStorageDirectory();
		else
			storage = Environment.getDataDirectory();
		

		zoom = INIT_ZOOM;
		//download map to sdcard or data directory
		imageManager = new ImageManager(storage);
		isSwap = false;
		imageName = IMAGE_NAME;
		imageManager.DownloadFromUrl("http://maps.google.com/maps/api/staticmap?center="+locationManager.getLatitude()+","+locationManager.getLongitude()+"&zoom="+zoom+"&size=320x480&maptype=satellite&key=MyGoogleMapsAPIKey&sensor=true", imageName);
		System.out.println("Return from image manager download.");

		server = new Server(getAndroidID(), locationManager.getLongitude(),
				locationManager.getLatitude(), zoom);
	}
	
	public void changeZoom() {
		zoom++;
		if(zoom == 18)
			zoom = 2;
		System.out.println("Zoom is now " + zoom);
	}
	
	public void updateMapImage() {
		new ImageDownload().execute();
	}
	
	protected class ImageDownload extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			System.out.println("Start Backend Image Download");
			//if the swap is being displayed
			if(isSwap)
				imageManager.DownloadFromUrl("http://maps.google.com/maps/api/staticmap?center="+locationManager.getLatitude()+","+locationManager.getLongitude()+"&zoom="+zoom+"&size=320x480&maptype=satellite&key=MyGoogleMapsAPIKey&sensor=true", IMAGE_NAME);
			else
				imageManager.DownloadFromUrl("http://maps.google.com/maps/api/staticmap?center="+locationManager.getLatitude()+","+locationManager.getLongitude()+"&zoom="+zoom+"&size=320x480&maptype=satellite&key=MyGoogleMapsAPIKey&sensor=true", IMAGE_NAME_SWAP);			
			return null;
		}
		@Override
		protected void onProgressUpdate(Void... params) { }
		@Override
		protected void onPostExecute(Void params) {
			if(isSwap)
				imageName = IMAGE_NAME;
			else
				imageName = IMAGE_NAME_SWAP;
			isSwap = !isSwap;
			System.out.println("Finish Backend Image Download");
		}
	}
	
	public boolean getIsSwap() {
		return isSwap;
	}
	
	
	public String getImagePath() {
		return storage.getAbsolutePath() + "\\" + imageName;
	}
	
	public double getLongitude() {
		return locationManager.getLongitude();
	}
	
	public double getLatitude() {
		return locationManager.getLatitude();
	}
	
	public void queryServer() {
		server.runServerLoop(locationManager.getLongitude(), locationManager.getLatitude());
	}
	
	public String getServerResponse() {
		return server.getServerResponse();
	}
	
	
	private String getAndroidID() {
		//Retrieve Android Unique ID
		String android_id = Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID); 
		if (android_id == null) {
			android_id = UUID.randomUUID().toString();
			System.out.println("Running In Emulator. Spoofed ID: " + android_id);
		}
		else {
			System.out.println("Android Device ID: " + android_id);
		}
		return android_id;
	}

	

	
	
}
