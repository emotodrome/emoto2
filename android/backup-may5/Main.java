package edu.neukom.emotodrome;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class Main extends Activity {
	private Backend backend;
	private TextView textView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		backend = new Backend(this);
		new BackendThreadSpawner().execute();

		textView = (TextView)findViewById(R.id.jpgname); 
		textView.setText("***WELCOME***");
	}

	protected class BackendThreadSpawner extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			while(true) {
				new BackendThread().execute();
				try { Thread.sleep(1000); }
				catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
	}

	protected class BackendThread extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			backend.queryServer();
			return null;
		}
		@Override
		protected void onProgressUpdate(Void... params) { }
		@Override
		protected void onPostExecute(Void params) {
			textView.setText(backend.getServerResponse());
		}
	}
}