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
	private ImageView maps[];
//	topLeft, top, topRight;
//	private ImageView left, center, right;
//	private ImageView bottomLeft, bottom, bottomRight;
	private Button button;
	private boolean spawnerToggle, shouldGetImage, shouldUpdateData;
	private int updatedCount;
	private boolean buttonAvailable;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		backend = new Backend(this);
		
		textView = (TextView)findViewById(R.id.text); 
		textView.setText("***WELCOME*** (" + backend.getLatitude() + "," + backend.getLongitude() + ")");
		
		
		maps = new ImageView[9];
		maps[0] = (ImageView)findViewById(R.id.topleft);
		maps[1] = (ImageView)findViewById(R.id.top);
		maps[2] = (ImageView)findViewById(R.id.topright);
		
		maps[3] = (ImageView)findViewById(R.id.left);
		maps[4] = (ImageView)findViewById(R.id.center);
		maps[5] = (ImageView)findViewById(R.id.right);
		
		maps[6] = (ImageView)findViewById(R.id.bottomleft);
		maps[7] = (ImageView)findViewById(R.id.bottom);
		maps[8] = (ImageView)findViewById(R.id.bottomright);
		
		for(int i=0; i<9; i++) {
			System.out.println(backend.getImagePath()+"/"+i+backend.getImageName());
			Bitmap bm = BitmapFactory.decodeFile(backend.getImagePath()+"/"+i+backend.getImageName());
			maps[i].setImageBitmap(bm);
		}
		
		button = (Button)findViewById(R.id.button);
		button.setText("Change Zoom");
		
		shouldGetImage = true;
		shouldUpdateData = true;
		spawnerToggle = true;
		
		updatedCount = 0;
		
		buttonAvailable = true;
		
//		new BackendThreadSpawner().execute();
//		new MapUpdater().execute();
	}
	
	
	//updateMapImage
	
	public void changeZoom(View view) {
		//NONTHREADED
		buttonAvailable = false;
		button.setText("Loading Images...");
		
		backend.changeZoom();
		System.out.println("changeZoom");
		updatedCount = 0;
		backend.downloadImagesNonThreaded();
		
		for(int i=0; i<9; i++) {
			System.out.println(backend.getImagePath()+"/"+i+backend.getImageName());
			Bitmap bm = BitmapFactory.decodeFile(backend.getImagePath()+"/"+i+backend.getImageName());
			maps[i].setImageBitmap(bm);
		}
		backend.switchBuffer();
		button.setText("Change Zoom");
		
		//THREADED
//		if(buttonAvailable) {
//			buttonAvailable = false;
//			button.setText("Loading Images...");
//			backend.changeZoom();
//			System.out.println("changeZoom");
//			updatedCount = 0;
//			for(int i=0; i<9; i++)
//				new NewImageUpdateThread().execute(i);
//		}
	}
	
	
//	
//	protected class MapUpdater extends AsyncTask<Void, Void, Void> {
//		@Override
//		protected Void doInBackground(Void... params) {
//			while(spawnerToggle && shouldGetImage) {
//				System.out.println("Start Map Image Update Thread");
//				new NewImageUpdateThread().execute();
//				try { Thread.sleep(30000); }
//				catch (InterruptedException e) { e.printStackTrace(); }
//			}
//			return null;
//		}
//	}
	
	
	protected class NewImageUpdateThread extends AsyncTask<Integer, Void, Void> {
		private int index;
		@Override
		protected Void doInBackground(Integer... params) {
			index = params[0];
			System.out.println(index);
			
			backend.spawnImageDownload(index);
			return null;
		}
		@Override
		protected void onPostExecute(Void params) {
			updatedCount++;
			System.out.println(backend.getImagePath()+"/"+index+backend.getAlternateImageName());
			Bitmap bm = BitmapFactory.decodeFile(backend.getImagePath()+"/"+index+backend.getAlternateImageName());
			maps[index].setImageBitmap(bm);
			System.out.println("Displayed Image Map "+index + ", " + updatedCount +" of 9 done");
			
			if(updatedCount == 9) {
				System.out.println("Finished Displaying All Map Images");
				buttonAvailable = true;
				button.setText("Change Zoom");
				backend.switchBuffer();
			}
		}
	}
	
	
//	protected class ImageUpdateThread extends AsyncTask<Void, Void, Void> {
//		@Override
//		protected Void doInBackground(Void... params) {
//			shouldGetImage = false;
//			System.out.println("Start Updating Map Image");
//			boolean isUpdated = backend.getIsSwap();
//			backend.updateMapImage();
//			
//			
//			while(isUpdated == backend.getIsSwap()) {
//				try { Thread.sleep(500); }
//				catch (InterruptedException e) { e.printStackTrace(); }
//			}
//			return null;
//		}
//		@Override
//		protected void onProgressUpdate(Void... params) { }
//		@Override
//		protected void onPostExecute(Void params) {
//			for(int i=0; i<9; i++) {
//				System.out.println(backend.getImagePath()+"/"+i+backend.getImageName());
//				Bitmap bm = BitmapFactory.decodeFile(backend.getImagePath()+"/"+i+backend.getImageName());
//				maps[i].setImageBitmap(bm);
//			}
//			
//			System.out.println("Finished Updating Map Image");
//			shouldGetImage = true;
//		}
//	}
	
	@Override
	public void onPause() {
		super.onPause();
		System.out.println("***PAUSING***");
		spawnerToggle = false;
		finish();
	}
	@Override
	public void onStop() {
		super.onStop();
		System.out.println("***STOPPING***");
	}
	
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