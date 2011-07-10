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
	private ImageManager imageManager[];
	private Activity activity;
	private File storage;
	private final String IMAGE_NAME = "map.png";
	private final String IMAGE_NAME_SWAP = "swap.png";
	private String imageName;
	private boolean isSwap;
	private boolean isDownloaded[];
	private int zoom;
	private final int INIT_ZOOM = 3;
	private final int IMAGE_WIDTH = 256;
	private final int IMAGE_HEIGHT = 256;

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
		imageManager = new ImageManager[9];
		for(int i=0; i<9; i++)
			imageManager[i] = new ImageManager(storage);
		isSwap = false;

		isDownloaded = new boolean[9];
		for(int i=0; i<9; i++)
			isDownloaded[i] = false;

		imageName = IMAGE_NAME;




		double longitude = locationManager.getLongitude();
		double latitude = locationManager.getLatitude();
		double top = Mercator.getTopFromLat(latitude, zoom);
		double bottom = Mercator.getBottomFromLat(latitude, zoom);
		double right = Mercator.getRightFromLng(longitude, zoom);
		double left = Mercator.getLeftFromLng(longitude, zoom);

		downloadImage(top, left, zoom, "0"+IMAGE_NAME, 0);
		downloadImage(top, longitude, zoom, "1"+IMAGE_NAME, 1);
		downloadImage(top, right, zoom, "2"+IMAGE_NAME, 2);

		downloadImage(latitude, left, zoom, "3"+IMAGE_NAME, 3);
		downloadImage(latitude, longitude, zoom, "4"+IMAGE_NAME, 4);
		downloadImage(latitude, right, zoom, "5"+IMAGE_NAME, 5);

		downloadImage(bottom, left, zoom, "6"+IMAGE_NAME, 6);
		downloadImage(bottom, longitude, zoom, "7"+IMAGE_NAME, 7);
		downloadImage(bottom, right, zoom, "8"+IMAGE_NAME, 8);

		System.out.println("Return from image manager download.");

		server = new Server(getAndroidID(), locationManager.getLongitude(),
				locationManager.getLatitude(), zoom);
	}

	//
	//	public void getSurroundingPlots(boolean isFirstCall) {
	//		String fileName = IMAGE_NAME_SWAP;
	//		if(isSwap || isFirstCall)
	//			fileName = IMAGE_NAME;
	//		
	//		//launch threads for all of them
	//		
	//		
	//	}







	//	public void getSurroundingPlots(boolean isFirstCall) {
	//		double longitude = locationManager.getLongitude();
	//		double latitude = locationManager.getLatitude();
	//		double top = Mercator.getTopFromLat(latitude, zoom);
	//		double bottom = Mercator.getBottomFromLat(latitude, zoom);
	//		double right = Mercator.getRightFromLng(longitude, zoom);
	//		double left = Mercator.getLeftFromLng(longitude, zoom);
	//		
	//		String fileName = IMAGE_NAME_SWAP;
	//		if(isSwap || isFirstCall)
	//			fileName = IMAGE_NAME;
	//		
	//		downloadImage(top, left, zoom, "0"+fileName);
	//		downloadImage(top, longitude, zoom, "1"+fileName);
	//		downloadImage(top, right, zoom, "2"+fileName);
	//		
	//		downloadImage(latitude, left, zoom, "3"+fileName);
	//		downloadImage(latitude, longitude, zoom, "4"+fileName);
	//		downloadImage(latitude, right, zoom, "5"+fileName);
	//		
	//		downloadImage(bottom, left, zoom, "6"+fileName);
	//		downloadImage(bottom, longitude, zoom, "7"+fileName);
	//		downloadImage(bottom, right, zoom, "8"+fileName);
	//	}


	public void downloadImagesNonThreaded() {
		String fileName = IMAGE_NAME_SWAP;
		if(isSwap)
			fileName = IMAGE_NAME;
		
		double longitude = locationManager.getLongitude();
		double latitude = locationManager.getLatitude();
		double top = Mercator.getTopFromLat(latitude, zoom);
		double bottom = Mercator.getBottomFromLat(latitude, zoom);
		double right = Mercator.getRightFromLng(longitude, zoom);
		double left = Mercator.getLeftFromLng(longitude, zoom);

		downloadImage(top, left, zoom, "0"+fileName, 0);
		downloadImage(top, longitude, zoom, "1"+fileName, 1);
		downloadImage(top, right, zoom, "2"+fileName, 2);

		downloadImage(latitude, left, zoom, "3"+fileName, 3);
		downloadImage(latitude, longitude, zoom, "4"+fileName, 4);
		downloadImage(latitude, right, zoom, "5"+fileName, 5);

		downloadImage(bottom, left, zoom, "6"+fileName, 6);
		downloadImage(bottom, longitude, zoom, "7"+fileName, 7);
		downloadImage(bottom, right, zoom, "8"+fileName, 8);
		switchBuffer();
	}

	public void changeZoom() {
		zoom++;
		if(zoom == 18)
			zoom = 2;
		System.out.println("Zoom is now " + zoom);
	}

	private void getImageAtIndex(int index, String fileName) {
		double longitude = locationManager.getLongitude();
		double latitude = locationManager.getLatitude();
		double top = Mercator.getTopFromLat(latitude, zoom);
		double bottom = Mercator.getBottomFromLat(latitude, zoom);
		double right = Mercator.getRightFromLng(longitude, zoom);
		double left = Mercator.getLeftFromLng(longitude, zoom);

		//		String fileName = IMAGE_NAME_SWAP;
		//		if(isSwap)
		//			fileName = IMAGE_NAME;

		switch(index) {
		case 0:
			downloadImage(top, left, zoom, "0"+fileName, 0); break;
		case 1:
			downloadImage(top, longitude, zoom, "1"+fileName, 1); break;
		case 2:
			downloadImage(top, right, zoom, "2"+fileName, 2); break;
		case 3:
			downloadImage(latitude, left, zoom, "3"+fileName, 3); break;
		case 4:
			downloadImage(latitude, longitude, zoom, "4"+fileName, 4); break;
		case 5:
			downloadImage(latitude, right, zoom, "5"+fileName, 5); break;
		case 6:
			downloadImage(bottom, left, zoom, "6"+fileName, 6); break;
		case 7:
			downloadImage(bottom, longitude, zoom, "7"+fileName, 7); break;
		case 8:
			downloadImage(bottom, right, zoom, "8"+fileName, 8); break;
		}
	}

	private void downloadImage(double latitude, double longitude, int zoom, String fileName, int index) {
		imageManager[index].DownloadFromUrl("http://maps.google.com/maps/api/staticmap?center="+latitude+","+longitude+"&zoom="+zoom+"&size="+IMAGE_WIDTH+"x"+IMAGE_HEIGHT+"&maptype=satellite&key=MyGoogleMapsAPIKey&sensor=true", fileName);
	}

	public void switchBuffer() {
		isSwap = !isSwap;
	}

	protected class ImageDownload extends AsyncTask<Integer, Void, Void> {
		private int index;
		@Override
		protected Void doInBackground(Integer... params) {
			index = params[0]; // 0 if first call, 1 otherwise

			System.out.println("Start Backend Image " + index + " Download");

			String fileName = IMAGE_NAME_SWAP;
			if(isSwap)
				fileName = IMAGE_NAME;
			getImageAtIndex(index, fileName);

			return null;
		}
		@Override
		protected void onProgressUpdate(Void... params) { }
		@Override
		protected void onPostExecute(Void params) {
			isDownloaded[index] = true;
			System.out.println("Finish Backend Image " + index + " Download");
		}
	}

	public void spawnImageDownload(int index) {
		isDownloaded[index] = false;
		new ImageDownload().execute(index);
		while(!isDownloaded[index]) {
			try { Thread.sleep(100); }
			catch (InterruptedException e) { e.printStackTrace(); }
		}
		System.out.println("Returning From Backend Image " + index + " Download");
	}







	//
	//	public boolean getIsSwap() {
	//		return isSwap;
	//	}


	public String getAlternateImageName() {
		if(imageName.equals(IMAGE_NAME))
			return IMAGE_NAME_SWAP;
		return IMAGE_NAME;
	}

	public String getImageName() {
		return imageName;
	}

	public String getImagePath() {
		return storage.getAbsolutePath();
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
