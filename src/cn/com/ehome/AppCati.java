package cn.com.ehome;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.ehome.app.IconifiedText;
import cn.com.ehome.app.IconifiedTextListAdapter;
import cn.com.ehome.database.EHotelProvider;
import cn.com.ehome.until.GobalFinalData;
import cn.com.ehome.until.WebsiteList;

public class AppCati extends Activity implements OnItemClickListener, OnItemSelectedListener {

	private GridView gridview;
	private TextView mTextView;
	private WebView mWebview;
	private TextView mDescription;
	private int mMode = 1;
	private List<Website> mWebsitelist = null;
	
	public static final String VIEW_MODE = "mode";
	public static final int MODE_APP = 1;
	public static final int MODE_APP_GAME = 2;
	public static final int MODE_WEBSITE_VIDEO = 3;
	
	private ArrayList<ApplicationInfo> mApplications;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {		 
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.appcat);
	        
	        gridview = (GridView)findViewById(R.id.gridview);
	        mDescription = (TextView)findViewById(R.id.description);
	        
	        gridview.setOnItemClickListener(this);	 
	        
	        gridview.setOnItemSelectedListener(this);
	        
	        mWebview = (WebView)findViewById(R.id.webView1);
	        mWebview.loadUrl("file:///mnt/sdcard/google.png");
	        
	       
	        Intent intent = getIntent();
	        mMode = intent.getIntExtra(VIEW_MODE, MODE_WEBSITE_VIDEO);
	        switch(mMode){
	        	case MODE_APP:
	        		loadApplications(MODE_APP);
	        	case MODE_APP_GAME:
	        		loadApplications(MODE_APP_GAME);
	        	break;
	        	case MODE_WEBSITE_VIDEO:
	        		WebsiteList websiteList = new WebsiteList(this, "website_video.xml");
	        		mWebsitelist = websiteList.parse();
	        		MySimpleAdapter websiteAdapter = new MySimpleAdapter(mWebsitelist);	        		
	        		gridview.setAdapter(websiteAdapter);
	        	break;
	        }
	        
	 }

	@Override
	public void onItemClick(AdapterView<?> arg0, View view,
			int position, long id) {
		 
		switch(mMode){
	     	case MODE_APP:
	     	case MODE_APP_GAME:
	     		Intent intent = EHotelProvider.getIntentById(id);
	     		if(intent!=null){
	     			AppCati.this.startActivity(intent);
	     		}
	     		break;	     	
	     		/*if(mApplications !=null){	    			
	    			if(position >=0 && position < mApplications.size()){
	    				AppCati.this.startActivity(mApplications.get(position).intent);	    				
	    			}
	    		}*/	
	     	case MODE_WEBSITE_VIDEO:
	     		if(mWebsitelist != null){
	     			Intent intent2 = new Intent();
		     		intent2.setAction(Intent.ACTION_VIEW);
		     		intent2.setData(Uri.parse(mWebsitelist.get(position).url));
		     		this.startActivity(intent2);
	     		}	     		
	     	break;
		}
		
	}
	@Override
	public void onItemSelected(AdapterView<?> av, View v,
			int position, long id) {
		switch(mMode){
     	case MODE_APP:
    		if(mApplications !=null){
    			
    			if(position >=0 && position < mApplications.size())
    			mDescription.setText(mApplications.get(position).title + "是很好玩的一个apk......");
    		}		
     	break;
     	case MODE_WEBSITE_VIDEO:
     		if(mWebsitelist != null){
     			if(position%2 == 0){
     				mWebview.loadUrl("file:///mnt/sdcard/HTML/test2.html");
     			}else{
     				mWebview.loadUrl("file:///mnt/sdcard/HTML/test.html");
     			}
     		}	
     	break;
	}
				
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		mDescription.setText("这里显示图文说明");
		
	}

	private void loadApplications(int type) {
		CursorAdapter adapter = null;
		if(type == MODE_APP){
			adapter = new SortAppAdapter(this,EHotelProvider.QuerySort(GobalFinalData.APP_SORT_COMMERCE,
					GobalFinalData.APP_SORT_COMMERCE_1),true);
		}else if(type == MODE_APP_GAME){
			adapter = new SortAppAdapter(this,EHotelProvider.QuerySort(GobalFinalData.APP_SORT_GAME,
					GobalFinalData.APP_SORT_GAME_1),true);
			
		}
		gridview.setAdapter(adapter);

		/*PackageManager manager = getPackageManager();
	
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	
		final List<ResolveInfo> apps = manager.queryIntentActivities(
				mainIntent, 0);
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
	
		if (apps != null) {
			final int count = apps.size();
	
			if (mApplications == null) {
				mApplications = new ArrayList<ApplicationInfo>(count);
			}
			mApplications.clear();
	
			for (int i = 0; i < count; i++) {
				ApplicationInfo application = new ApplicationInfo();
				ResolveInfo info = apps.get(i);
	
				application.title = info.loadLabel(manager);
				application.packageName = info.activityInfo.applicationInfo.packageName;
				application.setActivity(new ComponentName(
						info.activityInfo.applicationInfo.packageName,
						info.activityInfo.name), Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				application.iconBitmap = info.activityInfo.loadIcon(manager);
	
				application.id = i;
	
				mApplications.add(application);
			}
		}
		
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		
		List<IconifiedText>	directoryEntries = new ArrayList<IconifiedText>();
		Drawable d = getResources().getDrawable(R.drawable.bt_bg);
		for(int i=0; i<9; i++){
			IconifiedText ic = new IconifiedText("test",d);
			directoryEntries.add(ic);
		}
		
		
		itla.setListItems(directoryEntries);
		
		gridview.setAdapter(new AllAppAdapter(this));	*/
	}
	
	public class AllAppAdapter extends BaseAdapter {
		Context mContext;

		public AllAppAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			if (mApplications != null) {
				return mApplications.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return mApplications.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ApplicationInfo info = new ApplicationInfo(
					mApplications.get(position));

			if (convertView == null) {
				final LayoutInflater inflater = LayoutInflater.from(mContext);
				convertView = inflater.inflate(R.layout.gridfileitem, parent,
						false);				
			}

			ImageView appIcon = (ImageView) convertView.findViewById(R.id.gridicon);
			Drawable icon = info.iconBitmap;
			if (icon != null) {
				appIcon.setBackgroundDrawable(icon);
			} else {
				appIcon.setBackgroundResource(R.drawable.ic_launcher);
			}

			final TextView textView = (TextView) convertView
					.findViewById(R.id.gridname);
			if (info.title != null) {
				textView.setText(info.title);
			} else {
				//textView.setText(R.string.loading);
			}
			
			convertView.setTag(position);

			return convertView;

		}
	}
	
	public class MySimpleAdapter extends BaseAdapter {		
		
		
		private List<Website> lists = null;
		
		public MySimpleAdapter(List<Website> lists){
			this.lists = lists;
		}

		@Override
		public int getCount() {
			if(lists != null){
				return lists.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(lists != null){
				return lists.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			Website ws = lists.get(position);
			
			if (convertView == null) {
				final LayoutInflater inflater = LayoutInflater.from(AppCati.this);
				convertView = inflater.inflate(R.layout.gridfileitem, parent,
						false);				
			}

			ImageView appIcon = (ImageView) convertView.findViewById(R.id.gridicon);

			Uri uri = Uri.parse(ws.icon);
			appIcon.setImageURI(uri);
			

			final TextView textView = (TextView) convertView
					.findViewById(R.id.gridname);
		
			textView.setText(ws.name);			

			return convertView;
		}
		
	}
	
	public class SortAppAdapter extends CursorAdapter {

		public SortAppAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			ImageView appIcon = (ImageView) view.findViewById(R.id.gridicon);
			final TextView textView = (TextView) view.findViewById(R.id.gridname);		
			
			Intent intent = null;
			String packageName = null;
			String intentString;

			intentString = cursor.getString(cursor
					.getColumnIndex(EHotelProvider.APP_INTENT));
			try {
				intent = Intent.parseUri(intentString, 0);
				packageName = intent.getComponent().getPackageName();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			PackageManager manager = mContext.getPackageManager();
			Drawable drawable = null;
			try {
				drawable = manager.getApplicationIcon(packageName);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			if (drawable == null) {
				appIcon.setBackgroundResource(R.drawable.bt_bg);
			} else {
				appIcon.setBackgroundDrawable(drawable);
			}
		
			textView.setText(cursor.getString(cursor
					.getColumnIndex(EHotelProvider.APP_TITLE)));			
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final LayoutInflater inflater = LayoutInflater.from(mContext);
			View convertView = inflater.inflate(R.layout.gridfileitem, parent,
					false);
			
			return convertView;
		}
	}
}



