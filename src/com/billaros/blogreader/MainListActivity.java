package com.billaros.blogreader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainListActivity extends ListActivity {

	protected String[] mBlogPostTitles ;
	public static final int NUMBER_OF_POSTS = 20 ;
	public static final String TAG = MainListActivity.class.getSimpleName();
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		
		
		
		/*Resources resources = getResources();
		mBlogPostTitles = resources.getStringArray(R.array.android_names);
		
		ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBlogPostTitles);
		setListAdapter(adapter);*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_list, menu);
		return true;
	}
	
	private class GetBlogPostTask extends AsyncTask<Object, Void, String>{

		@Override
		protected String doInBackground(Object... arg0) {
			try{
				URL blogFeedUrl = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count=" + NUMBER_OF_POSTS);
				HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection(); 
				connection.connect();
				
				int responseCode = -1;
				Log.i(TAG ,"Code:" + responseCode);
			}
			catch(MalformedURLException e){
				Log.e(TAG,"Exception caught:", e);
			}
			catch(IOException e){
				Log.e(TAG,"Exception caught:", e);
			}
			catch(Exception e){
				Log.e(TAG,"Exception caught:", e);
			}
			
			return "Code";
		}
		
	}

}
