package edu.neukom.emotodrome;

import java.io.File;
import java.util.UUID;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings.Secure;

public class Backend{
	private Server server;
	private MyLocationManager locationManager;
	private ImageManager imageManager;
	private Activity activity;
	private File storage;

	private final int ZOOM = 3;
	
	public Backend(Activity activity) {
		this.activity = activity;
		
		locationManager = new MyLocationManager(activity);
		System.out.println("Initial Lat: " + locationManager.getLatitude());
		System.out.println("Initial Long: " + locationManager.getLongitude());
/*
		if(Environment.getExternalStorageState() != null)
			storage = Environment.getExternalStorageDirectory();
		else
			storage = Environment.getDataDirectory();
		

		//download map to sdcard or data directory
		imageManager = new ImageManager(storage);
		imageManager.DownloadFromUrl("http://maps.google.com/maps/api/staticmap?center="+locationManager.getLatitude()+","+locationManager.getLongitude()+"&zoom="+ZOOM+"&size=320x480&maptype=satellite&key=MyGoogleMapsAPIKey&sensor=true", "neukom_image.jpeg");
		System.out.println("Return from image manager download.");
		*/

/*
		TextView jpgName = (TextView)activity.findViewById(R.id.jpgname);
		System.out.println("jpg name is " + jpgName);
		//    ImageView jpgView = (ImageView)findViewById(R.id.jpgview);
		ImageView jpgView = (ImageView)activity.findViewById(R.id.jpgview);
		System.out.println("jpg view is " + jpgView);

		if(jpgName == null || jpgView == null) {
			System.out.println("Views are null");
		}
		else {
			String myJpgPath = "/sdcard/neukom_image.jpeg"; //UPDATE WITH YOUR OWN JPG FILE
			System.out.println("set jpg name path");
			jpgName.setText(myJpgPath);
			System.out.println("set jpg name path to jpg name text view");

			BitmapFactory.Options options = new BitmapFactory.Options();
			System.out.println("set bitmap factory options");
			//	    options.inSampleSize = 2;
			Bitmap bm = BitmapFactory.decodeFile(myJpgPath, options);
			System.out.println("decode file");
			jpgView.setImageBitmap(bm); 
			System.out.println("set image view");
		}
		*/

		server = new Server(getAndroidID(), locationManager.getLongitude(),
				locationManager.getLatitude(), ZOOM);
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
