package com.billaros.blogreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainListActivity extends ListActivity {

	
	public static final int NUMBER_OF_POSTS = 20 ;
	public static final String TAG = MainListActivity.class.getSimpleName();
	protected JSONObject mBlogData  ;
	protected ProgressBar mProgressBar;
	private final String KEY_TITLE = "title";
	private final String KEY_AYTHOR = "author";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		if(isNetworkAvailable()){
			mProgressBar.setVisibility(View.VISIBLE);
			GetBlogPostTask getBlogPostTask = new GetBlogPostTask();
			getBlogPostTask.execute();
		}else{
			Toast.makeText(this, "Network is unavailble!", Toast.LENGTH_LONG).show();
		}
		
		/*Resources resources = getResources();
		mBlogPostTitles = resources.getStringArray(R.array.android_names);
		
		ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBlogPostTitles);
		setListAdapter(adapter);*/
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		
		boolean isAvailable = false;
		
		if(networkInfo != null && networkInfo.isConnected()){
			isAvailable = true;
		}
		
		return isAvailable;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_list, menu);
		return true;
	}
	public void handleBlogResponce() {
		mProgressBar.setVisibility(View.INVISIBLE);
		
		if (mBlogData == null){
			UpdateDisplayForError();
			
		}else{
			try {
				JSONArray jsonPosts = mBlogData.getJSONArray("posts");
				ArrayList <HashMap <String ,String> > blogPosts = new ArrayList <HashMap <String,String>>();
				
				for(int i =0; i<jsonPosts.length(); i++){
					JSONObject post = jsonPosts.getJSONObject(i);
					String title = post.getString(KEY_TITLE);
					title = Html.fromHtml(title).toString();
					
					String author = post.getString(KEY_AYTHOR);
					author = Html.fromHtml(author).toString();
					
					HashMap <String, String> blogPost = new HashMap<String,String>();
					blogPost.put(KEY_TITLE,title);
					blogPost.put(KEY_AYTHOR,author);
					
					blogPosts.add(blogPost);
					
				}
				
				String[] keys = { KEY_TITLE , KEY_AYTHOR};
				int[] ids = {android.R.id.text1 , android.R.id.text2};
				SimpleAdapter adapter = new SimpleAdapter(this, blogPosts,android.R.layout.simple_list_item_2,keys,ids);
				
				
				setListAdapter(adapter);
				
				
			} catch (JSONException e) {
				Log.e(TAG,"Exception caught!", e);
			} 
		}
	}

	private void UpdateDisplayForError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.title));
		builder.setMessage(getString(R.string.error_message));
		builder.setPositiveButton(android.R.string.ok, null);
		AlertDialog dialog = builder.create();
		dialog.show();
		
		
		TextView emptyTextView = (TextView) getListView().getEmptyView();
		emptyTextView.setText(getString(R.string.no_items));
	}
	
	private class GetBlogPostTask extends AsyncTask<Object, Void, JSONObject>{

		@Override
		protected JSONObject doInBackground(Object... arg0) {
			
			int responseCode = -1;
			JSONObject jsonResponce = null;
			
			try{
				URL blogFeedUrl = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count=" + NUMBER_OF_POSTS);
				HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection(); 
				connection.connect();
				
				responseCode = connection.getResponseCode();
				
				if (responseCode == HttpURLConnection.HTTP_OK){
					InputStream inputStream = connection.getInputStream();
					Reader reader = new InputStreamReader(inputStream);
					
					int contentlength = connection.getContentLength();
					char[] charArray = new char[contentlength];
					
					reader.read(charArray);
					String responseData = new String(charArray);
					//Log.v(TAG, responseData );
					
					jsonResponce = new JSONObject(responseData);
					
			
					
				}else{
					Log.i(TAG, "Unsuccesful HTTP Responce Code:" + responseCode);
				}
				
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
			
			return jsonResponce;
			
		}
		
		
		@Override
		protected void onPostExecute(JSONObject result){
			mBlogData = result ;
			handleBlogResponce();
		}
		
	}

	

}
