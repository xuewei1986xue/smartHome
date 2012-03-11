package cn.com.ehome;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.WallpaperManager;
import android.app.backup.BackupManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;
import cn.com.ehome.database.EHotelProvider;
import cn.com.ehome.systemmanage.ManageActivity;
import cn.com.ehome.until.GobalFinalData;
import cn.com.ehome.until.XmlPreference;


public class EHomeActivity extends Activity implements OnClickListener {
	
	private static final String SUPER_MASTER_FILE = "/mnt/sdcard/tflash/ehome_master";
	
	private Button mbtn1;
	private Button mbtn2;
	private Button mbtn3;
	private Button mbtn4;
	private Button mbtn5;
	private Button mbtn6;
	private Button mbtnlogo;
	private Button mbtnChgLang;
	
	public final static int DIALOG_POPLANGUAGESEL = 5;
	public final static int DIALOG_LOGIN = 6;
	private Loc[] mLocales;
	private String[] mSpecialLocaleCodes;
	private String[] mSpecialLocaleNames;
	
	private String HOTEL_HOME = "file:///android_asset/test/index.html";
	private String HOTEL_INFO = "file:///android_asset/test/film/index.html";
	EditText etPassword;
	
	private static String TAG = "home";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mbtnlogo = (Button)findViewById(R.id.logo);
        mbtnlogo.setOnClickListener(this);
        
        mbtnChgLang = (Button)findViewById(R.id.chglang);
        mbtnChgLang.setOnClickListener(this);
        
        mbtn1 =  (Button)findViewById(R.id.button1);
        mbtn1.setOnClickListener(this);
        mbtn1.requestFocus();
        mbtn2 =  (Button)findViewById(R.id.button2);
        mbtn2.setOnClickListener(this);
        mbtn3 =  (Button)findViewById(R.id.button3);
        mbtn3.setOnClickListener(this);
        mbtn4 =  (Button)findViewById(R.id.button4);
        mbtn4.setOnClickListener(this);
        mbtn5 =  (Button)findViewById(R.id.button5);
        mbtn5.setOnClickListener(this);
        mbtn6 =  (Button)findViewById(R.id.button6);
        mbtn6.setOnClickListener(this);
        
        initData();
        setDefaultWallpaper();
        
    }

    private void initData(){
    	/*File file = new File(GobalFinalData.INIT_FILE);
		if (file.exists() == false) {
			return;
		}*/
		
		XmlPreference xmlPreference = new XmlPreference(true,this,GobalFinalData.INIT_FILE);		

		String value = xmlPreference.getKeyValue("hotel");
		if (value != null && value.isEmpty() == false) {
			HOTEL_HOME = value;
		}
		value = xmlPreference.getKeyValue("info");
		if (value != null && value.isEmpty() == false) {
			HOTEL_INFO = value;
		}
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();		
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.logo){
			File imgFile = new File(SUPER_MASTER_FILE);
			if (imgFile.exists()) {
				PackageManager packageManager = this.getPackageManager();				
				Intent intent = new Intent(EHomeActivity.this,ManageActivity.class);				
				if (intent != null) {
					startActivity(intent);
					return;
				}else{
					Toast.makeText(this,"You can't use it this time.",Toast.LENGTH_LONG).show();
				}				
			}
			
			showDialog(DIALOG_LOGIN);
		}else if(id == R.id.chglang){
			showDialog(DIALOG_POPLANGUAGESEL);
		}else if(id == R.id.button1){			
			Intent intent = new Intent(EHomeActivity.this,SmitBrowser.class);	
			intent.putExtra(SmitBrowser.OPEN_URL, HOTEL_HOME);
			startActivity(intent);
		}else if(id == R.id.button2){
			Intent intent = new Intent(EHomeActivity.this,SmitBrowser.class);	
			intent.putExtra(SmitBrowser.OPEN_URL, HOTEL_INFO);
			startActivity(intent);
		}
		else if(id == R.id.button3){
			Intent intent = new Intent();
			intent.setClass(this, InfoActivity.class);
			startActivity(intent);
		}
		else if(id == R.id.button4){

			Intent intent = new Intent();
			intent.setClass(this, AppCati.class);
			intent.putExtra(AppCati.VIEW_MODE, AppCati.MODE_WEBSITE_VIDEO);
			startActivity(intent);
		}
		else if(id == R.id.button5){
			Intent intent = new Intent();
			intent.setClass(this, AppCati.class);
			intent.putExtra(AppCati.VIEW_MODE, AppCati.MODE_APP);
			startActivity(intent);
		}
		else if(id == R.id.button6){
			Intent intent = new Intent();
			intent.setClass(this, AppCati.class);
			intent.putExtra(AppCati.VIEW_MODE, AppCati.MODE_APP_GAME);
			startActivity(intent);
		}
		
	}
	
	@Override
	public Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {

		case DIALOG_POPLANGUAGESEL: {
			LayoutInflater inflater = LayoutInflater.from(this);
			View password = inflater.inflate(R.layout.languagesellistlay, null);
			AlertDialog.Builder mBuilderpass = new AlertDialog.Builder(this);
			mBuilderpass.setTitle(R.string.language_sel).setView(password);

			ListView list = (ListView) password.findViewById(R.id.listviewid);

			getLanguageInfo(list);
			
			list.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					changeLanguage(mLocales[arg2]);
					dismissDialog(DIALOG_POPLANGUAGESEL);
					// TODO
					//toFunPage("OTHER");
					//finish();
				}
			});

			dialog = mBuilderpass.create();
			break;
		}
		case DIALOG_LOGIN: {
			LayoutInflater inflater = LayoutInflater.from(this);
			View password = inflater.inflate(R.layout.login_changepass, null);
			AlertDialog.Builder mBuilderpass = new AlertDialog.Builder(this);
			mBuilderpass.setTitle(R.string.login_title).setView(password);
			final Button login = (Button) password.findViewById(R.id.login);
			etPassword = (EditText) password.findViewById(R.id.inputpassword);
			etPassword.setText("");
			etPassword.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					login.performClick();
					return true;
				}
			});

			login.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (passwordIsRight()) {
						etPassword.setText("");
						Intent mIntent = new Intent(getApplicationContext(),
								ManageActivity.class);
						startActivity(mIntent);
						dismissDialog(DIALOG_LOGIN);
					} else {
						Toast.makeText(getApplicationContext(),
								R.string.passworderror, Toast.LENGTH_SHORT)
								.show();
					}
				}
			});

			password.findViewById(R.id.show_psw).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							CheckBox showPsw = (CheckBox) v;
							if (showPsw.isChecked() == false) {
								etPassword
										.setTransformationMethod(new PasswordTransformationMethod());
							} else {
								etPassword.setTransformationMethod(null);
							}

						}
					});

			dialog = mBuilderpass.create();
		}
		}
		return dialog;
	}
	
	private boolean passwordIsRight() {

		String commonPassword = EHotelProvider.getCurPassword();
		String password = etPassword.getText().toString();
		if (password.equals(commonPassword)) {
			return true;
		} else {
			return false;
		}
	}
	
	private void getLanguageInfo(ListView list) {
		mSpecialLocaleCodes = getResources().getStringArray(
				R.array.special_locale_codes);
		mSpecialLocaleNames = getResources().getStringArray(
				R.array.special_locale_names);

		String[] locales = getAssets().getLocales();
		Arrays.sort(locales);

		final int origSize = locales.length;
		Loc[] preprocess = new Loc[origSize];
		int finalSize = 0;
		for (int i = 0; i < origSize; i++) {
			String s = locales[i];
			int len = s.length();
			if (len == 5) {
				String language = s.substring(0, 2);
				String country = s.substring(3, 5);
				Locale l = new Locale(language, country);

				if (finalSize == 0) {
					Log.v(TAG, "adding initial "
							+ toTitleCase(l.getDisplayLanguage(l)));
					preprocess[finalSize++] = new Loc(toTitleCase(l
							.getDisplayLanguage(l)), l);
				} else {
					if (preprocess[finalSize - 1].locale.getLanguage().equals(
							language)) {
						Log
								.v(
										TAG,
										"backing up and fixing "
												+ preprocess[finalSize - 1].label
												+ " to "
												+ getDisplayName(preprocess[finalSize - 1].locale));
						preprocess[finalSize - 1].label = toTitleCase(getDisplayName(preprocess[finalSize - 1].locale));
						Log.v(TAG, "  and adding "
								+ toTitleCase(getDisplayName(l)));
						preprocess[finalSize++] = new Loc(
								toTitleCase(getDisplayName(l)), l);
					} else {
						String displayName;
						if (s.equals("zz_ZZ")) {
							displayName = "Pseudo...";
						} else {
							displayName = toTitleCase(l.getDisplayLanguage(l));
						}
						Log.v(TAG, "adding " + displayName);
						preprocess[finalSize++] = new Loc(displayName, l);
					}
				}
			}
		}
		mLocales = new Loc[finalSize];
		for (int i = 0; i < finalSize; i++) {
			mLocales[i] = preprocess[i];
		}
		Arrays.sort(mLocales);

		AddListData(list);
	}

	private void AddListData(ListView list) {

		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < mLocales.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle", mLocales[i].label);
			listItem.add(map);
		}
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.languagesellistitem,				
				new String[] { "ItemTitle" },
				new int[] { R.id.title });		
		list.setAdapter(listItemAdapter);

	}

	private static String toTitleCase(String s) {
		if (s.length() == 0) {
			return s;
		}

		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	private String getDisplayName(Locale l) {
		String code = l.toString();

		for (int i = 0; i < mSpecialLocaleCodes.length; i++) {
			if (mSpecialLocaleCodes[i].equals(code)) {
				return mSpecialLocaleNames[i];
			}
		}

		return l.getDisplayName(l);
	}

	private void changeLanguage(Loc mLoc) {
		
			IActivityManager am = ActivityManagerNative.getDefault();
			Configuration config;
			try {
				config = am.getConfiguration();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}

			Loc loc = mLoc;
			config.locale = loc.locale;

			// indicate this isn't some passing default - the user wants this
			// remembered
			config.userSetLocale = true;

			try {
				am.updateConfiguration(config);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Trigger the dirty bit for the Settings Provider.
			BackupManager.dataChanged("com.android.providers.settings");
		

	}

	private static class Loc implements Comparable {
		static Collator sCollator = Collator.getInstance();

		String label;
		Locale locale;

		public Loc(String label, Locale locale) {
			this.label = label;
			this.locale = locale;
		}

		@Override
		public String toString() {
			return this.label;
		}

		public int compareTo(Object o) {
			return sCollator.compare(this.label, ((Loc) o).label);
		}
	}
	
	/**
     * When a drawable is attached to a View, the View gives the Drawable its dimensions
     * by calling Drawable.setBounds(). In this application, the View that draws the
     * wallpaper has the same size as the screen. However, the wallpaper might be larger
     * that the screen which means it will be automatically stretched. Because stretching
     * a bitmap while drawing it is very expensive, we use a ClippedDrawable instead.
     * This drawable simply draws another wallpaper but makes sure it is not stretched
     * by always giving it its intrinsic dimensions. If the wallpaper is larger than the
     * screen, it will simply get clipped but it won't impact performance.
     */
    private class ClippedDrawable extends Drawable {
        private final Drawable mWallpaper;

        public ClippedDrawable(Drawable wallpaper) {
            mWallpaper = wallpaper;
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, bottom);
            // Ensure the wallpaper is as large as it really is, to avoid stretching it
            // at drawing time
            mWallpaper.setBounds(left, top, left + mWallpaper.getIntrinsicWidth(),
                    top + mWallpaper.getIntrinsicHeight());
        }

        public void draw(Canvas canvas) {
            mWallpaper.draw(canvas);
        }

        public void setAlpha(int alpha) {
            mWallpaper.setAlpha(alpha);
        }

        public void setColorFilter(ColorFilter cf) {
            mWallpaper.setColorFilter(cf);
        }

        public int getOpacity() {
            return mWallpaper.getOpacity();
        }
    }
    // you can set your own wallpaper here
    // TODO wallpaper
    private void setDefaultWallpaper() {
    	SharedPreferences s = getSharedPreferences("ehome",  MODE_WORLD_WRITEABLE);
    	boolean init = s.getBoolean("init_wall_papaer", false);
    	if(false == init){
    		try {
        		WallpaperManager.getInstance(this).clear();
        		WallpaperManager.getInstance(this).setResource(R.drawable.bg_home4);
        		Editor editor = s.edit();
        		editor.putBoolean("init_wall_papaer", true);
        		editor.commit();
        		//finish();
        		
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}        
    }
}