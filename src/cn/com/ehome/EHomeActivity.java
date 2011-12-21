package cn.com.ehome;

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
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EHomeActivity extends Activity implements OnClickListener {
   	
	private Button mbtn1;
	private Button mbtn2;
	private Button mbtn3;
	private Button mbtn4;
	private Button mbtn5;
	private Button mbtn6;
	private Button mbtnlogo;
	private Button mbtnChgLang;
	
	public final static int DIALOG_POPLANGUAGESEL = 5;
	private Loc[] mLocales;
	private String[] mSpecialLocaleCodes;
	private String[] mSpecialLocaleNames;
	
	
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
    }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.logo){
			PackageManager packageManager = this.getPackageManager();				
			Intent intent = new Intent();
			intent.setClassName("com.android.launcher", "com.android.launcher2.Launcher");
			if (intent != null) {
				startActivity(intent);
			}else{
				Toast.makeText(this,"You can't use it this time.",Toast.LENGTH_LONG).show();
			}
		}else if(id == R.id.chglang){
			showDialog(DIALOG_POPLANGUAGESEL);
		}else if(id == R.id.button1){
			Intent intent = new Intent();
			intent.setClass(this, InfoActivity.class);
			startActivity(intent);
		}else if(id == R.id.button2){
			Intent intent = new Intent();
			intent.setClass(this, AppCati.class);
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
		}
		return dialog;
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
}