package emoto.neukom.edu;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Main extends Activity {
	private Backend backend;
	private TextView textView;
	private ImageView imageView;
	private Button button;
	private boolean spawnerToggle, shouldGetImage, shouldUpdateData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		backend = new Backend(this);
		
		textView = (TextView)findViewById(R.id.text); 
		textView.setText("***WELCOME*** (" + backend.getLatitude() + "," + backend.getLongitude() + ")");
		
		imageView = (ImageView)findViewById(R.id.image);
		Bitmap bm = BitmapFactory.decodeFile(backend.getImagePath());
		imageView.setImageBitmap(bm);
		
		button = (Button)findViewById(R.id.button);
		button.setText("Change Zoom");
		
		shouldGetImage = true;
		shouldUpdateData = true;
		spawnerToggle = true;
		
		new BackendThreadSpawner().execute();
		new MapUpdater().execute();
	}
	
	
	//updateMapImage
	
	public void changeZoom(View view) {
		backend.changeZoom();
	}
	
	
	
	protected class MapUpdater extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			while(spawnerToggle && shouldGetImage) {
				System.out.println("Start Map Image Update Thread");
				new ImageUpdateThread().execute();
				try { Thread.sleep(5000); }
				catch (InterruptedException e) { e.printStackTrace(); }
			}
			return null;
		}
	}
	
	protected class ImageUpdateThread extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			shouldGetImage = false;
			System.out.println("Start Updating Map Image");
			boolean isUpdated = backend.getIsSwap();
			backend.updateMapImage();
			while(isUpdated == backend.getIsSwap()) {
				try { Thread.sleep(500); }
				catch (InterruptedException e) { e.printStackTrace(); }
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(Void... params) { }
		@Override
		protected void onPostExecute(Void params) {
			Bitmap bm = BitmapFactory.decodeFile(backend.getImagePath());
			imageView.setImageBitmap(bm);
			System.out.println("Finished Updating Map Image");
			shouldGetImage = true;
		}
	}
	
	
//	
//	@Override
//	public void onPause() {
//		super.onPause();
//		System.out.println("***PAUSE***");
//		spawnerToggle = false;
//	}
//	
//	@Override
//	public void onResume() {
//		super.onResume();
//		System.out.println("***RESUME***");
//		spawnerToggle = true;
//		shouldGetImage = true;
//		shouldUpdateData = true;
//		new BackendThreadSpawner().execute();
//		new MapUpdater().execute();
//	}
	
	
	

	protected class BackendThreadSpawner extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			while(spawnerToggle && shouldUpdateData) {
				new BackendThread().execute();
				try { Thread.sleep(3000); }
				catch (InterruptedException e) { e.printStackTrace(); }
			}
			return null;
		}
	}

	protected class BackendThread extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			shouldUpdateData = false;
			backend.queryServer();
			return null;
		}
		@Override
		protected void onProgressUpdate(Void... params) { }
		@Override
		protected void onPostExecute(Void params) {
			shouldUpdateData = true;
			textView.setText(backend.getServerResponse());
		}
	}
}