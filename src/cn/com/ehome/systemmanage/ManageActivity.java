package cn.com.ehome.systemmanage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser.Component;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.ehome.R;
import cn.com.ehome.database.EHotelProvider;

public class ManageActivity extends Activity implements OnItemClickListener {
	private final static String Tag = "==ManageActivity==";
	
	private ListView listView;	

	public final static int DIALOG_WAIT = 1; // �ȴ�
	public final static int DIALOG_WARN = 2;
	public final static int DIALOG_ERROR = 3;
	public final static int MSG_COPY_FINISH = 1; // �ȴ�

	private final int systemfun[] = { 
			R.string.sys_fun_setting,R.string.sys_fun_setting_dis,
			R.string.sys_fun_set_room_num,R.string.sys_fun_set_room_num_dis,
			R.string.sys_fun_sort,R.string.sys_fun_sort_dis, 
			R.string.sys_fun_copy_sd,R.string.sys_fun_copy_sd_dis,			
			R.string.sys_fun_batinstall,R.string.sys_fun_batinstall_dis,
			R.string.sys_fun_copy_machine,R.string.sys_fun_copy_machine_dis,
			R.string.sys_fun_batuninstall,R.string.sys_fun_batuninstall_dis,
			R.string.sys_fun_openhome,R.string.sys_fun_openhome_dis,
			R.string.sys_fun_set_wallpaper,R.string.sys_fun_set_wallpaper_dis,
			/*R.string.sys_fun_custom,R.string.sys_fun_custom_dis*/};
	
/*	private final static int SYSTEM_SETTING = 0; // 系统锟斤拷锟斤拷
	private final static int APP_SET_ROOM_NUM = 1; // 锟斤拷锟矫凤拷锟斤拷锟?
	private final static int APP_SORT = 2; // 应锟矫凤拷锟斤拷	
	private final static int APP_DATABASE_COPY_SD = 3; // 锟斤拷菘锟酵帮拷装应锟矫革拷锟狡碉拷sd锟斤拷		
	private final static int APP_BATCH = 4; // 锟斤拷锟斤拷锟斤拷装
	private final static int APP_DATABASE_COPY_MACHINE = 5; // 锟斤拷菘锟酵帮拷装应锟矫革拷锟狡回伙拷锟斤拷
	private final static int APP_UNINSTALL_BATCH = 6;
	private final static int APP_START_HOME = 7;
	private final static int APP_PREFERENCE = 8;*/
	
	private final static int SYSTEM_SETTING = 0; 
	private final static int APP_SET_ROOM_NUM = 1; 
	private final static int APP_SORT = 2; 
	private final static int APP_DATABASE_COPY_SD = 3; 
	private final static int APP_BATCH = 4; 
	private final static int APP_DATABASE_COPY_MACHINE = 5;
	private final static int APP_UNINSTALL_BATCH = 6;
	private final static int APP_START_HOME = 7;
	private final static int APP_SET_WALLPAPER = 8;
	private final static int APP_PREFERENCE = 80;
	
	
	public static final String SDCARD = Environment
	.getExternalStorageDirectory()+ "";
	private String cardName = "/tflash";
	
	public static final String EXTENT_CARD1 = "/tflash";
	public static final String EXTENT_CARD2 = "/scsi_sda1";
	public static final String KEY_DIR = "/LOST.DIR";
	
	public String databaseLoadPath = "data/data/cn.com.ehome/databases/ehotel.db";
	
	public static final String CONFIG_FILE = "/hotel_backup/savedatabase.bin";
	public static final String DB_SAVE_PATH = "/hotel_backup/ehotel.db";
	public static final String APK_DES_DIR = "/hotel_backup/";
	
	public static final String apkSrcNandPath = "/data/app/";
	public static final String apkSrcSDPath = "/mnt/asec/";
	
	
	private String mTipsDialogMsg = "tips";
	
	private Boolean updateCardName(){
		File file = new File(SDCARD + EXTENT_CARD1 + KEY_DIR);
		if(file.isDirectory()){
			cardName = EXTENT_CARD1;
			return true;
		}
		file = new File(SDCARD + EXTENT_CARD2 + KEY_DIR);
		if(file.isDirectory()){
			cardName = EXTENT_CARD2;
			return true;
		}
		return false;
	}

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.admin_fun_layout);
		listView = (ListView) findViewById(R.id.admin_list_id);
		listView.setOnItemClickListener(this);
		AddListData();

		EHotelProvider.SetAdmin(1);
		updateCardName();
		startWatchingExternalStorage();
	}
	

	@Override
	protected void onDestroy() {		
		super.onDestroy();
		stopWatchingExternalStorage();
	}


	@Override
	public void onBackPressed() {
		EHotelProvider.SetAdmin(0);
		super.onBackPressed();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_COPY_FINISH: {
				Log.i(Tag, "==Get Message==");				
				dismissDialog(DIALOG_WAIT);	
				if(msg.arg1 == 0){
					mTipsDialogMsg = getResources().getString(R.string.savesortdatabasefail);
				}else{
					mTipsDialogMsg = getResources().getString(R.string.savesortdatabasesuc);
				}				
				showDialog(DIALOG_ERROR);
				break;
			}
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void AddListData() {

		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < systemfun.length; i+=2) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle", getResources().getString(systemfun[i]));
			map.put("discription", getResources().getString(systemfun[i+1]));
			listItem.add(map);
		}

		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.languagesellistitem, new String[] { "ItemTitle","discription" },
				new int[] { R.id.title,R.id.discription });

		// ��Ӳ�����ʾ
		listView.setAdapter(listItemAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		try{
			if (position == SYSTEM_SETTING) {
				PackageManager packageManager = this.getPackageManager();
				Intent intent = new Intent();
				intent = packageManager
						.getLaunchIntentForPackage("com.android.settings");
				if (intent != null) {
					startActivity(intent);
				}
			} else if(APP_SET_ROOM_NUM == position){
				Intent mIntent = new Intent(this, SettingRoomNum.class);
				startActivity(mIntent);
			}else if (position == APP_SORT) {
				Intent mIntent = new Intent(this, SetSortActivity.class);
				startActivity(mIntent);
			} else if (position == APP_BATCH) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(android.content.Intent.ACTION_VIEW);
				String type = "application/vnd.android.package-archive3";
				intent.setType(type);
				startActivity(intent);
			} else if(position == APP_UNINSTALL_BATCH){
				
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setType("application/vnd.android.package-archive4");			
				startActivity(intent);
			
			}else if (position == APP_DATABASE_COPY_MACHINE) {
			
				if (loaddatabaseconfig()) {				
					mTipsDialogMsg = getResources().getString(R.string.loadsortdatabasesuc);
					showDialog(DIALOG_ERROR);
				} else {
					mTipsDialogMsg = getResources().getString(R.string.loaderror);
					showDialog(DIALOG_ERROR);
				}
			} else if (position == APP_DATABASE_COPY_SD) {
				showDialog(DIALOG_WARN);
			} else if (position == APP_SET_ROOM_NUM) {
				// TODO setting room
				//Intent mIntent = new Intent(this, SettingRoomNum.class);
				//startActivity(mIntent);				
			} else if (position == APP_START_HOME){		
				PackageManager packageManager = this.getPackageManager();				
				Intent intent = new Intent();
				intent.setClassName("com.android.launcher", "com.android.launcher2.Launcher");
				if (intent != null) {
					startActivity(intent);
				}else{
					Toast.makeText(this,"��ʱ����������",Toast.LENGTH_LONG).show();
				}
			} else if (position == APP_PREFERENCE){
				Intent intent = new Intent(this,MyPreferenceActivity.class);
				startActivity(intent);				
			} else if (position == APP_SET_WALLPAPER){
				final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
		        startActivity(Intent.createChooser(pickWallpaper, getString(R.string.menu_wallpaper)));
			}
		}catch (ActivityNotFoundException e){
			e.printStackTrace();
			Toast.makeText(this,R.string.no_activity_find,Toast.LENGTH_LONG).show();
		}		
	}

	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == DIALOG_ERROR){
			AlertDialog ad = (AlertDialog)dialog;
			ad.setMessage(mTipsDialogMsg);
		}
		super.onPrepareDialog(id, dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder mBuilderpass;
		Dialog dialog = null;
		
		switch (id) {
			case DIALOG_WAIT:
				LayoutInflater inflater = LayoutInflater.from(this);
				View password = inflater.inflate(R.layout.progress_layout, null);
				ProgressDialog progress = new ProgressDialog(this);
				progress.setTitle(R.string.waiting);
				progress.setCancelable(false);
				progress.setMessage(getResources().getString(R.string.wait_info));
				progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);				
				dialog = progress;
				break;
			case DIALOG_WARN:
				mBuilderpass = new AlertDialog.Builder(this);
				mBuilderpass.setTitle(R.string.dialog_title);
				mBuilderpass.setMessage(R.string.begin_copy_app);
				mBuilderpass.setPositiveButton(R.string.dialog_ok, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						showDialog(DIALOG_WAIT);
						new CopyThread().start();
					}
				});
				mBuilderpass.setNegativeButton(R.string.dialog_cancel, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();						
					}
				});
				dialog = mBuilderpass.create();
				break;
			case DIALOG_ERROR:
				mBuilderpass = new AlertDialog.Builder(this);
				mBuilderpass.setTitle(R.string.dialog_title);
				mBuilderpass.setMessage(R.string.loaderror);
				mBuilderpass.setPositiveButton(R.string.dialog_ok, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();						
					}
				});				
				dialog = mBuilderpass.create();
				break;
		}

		return dialog;
	}

	public class CopyThread extends Thread {
		
		@Override
		public void run() {
			Message message = new Message();
			message.what = MSG_COPY_FINISH;
			message.arg1 = 1;
			if(updateCardName() == false || savedatabaseconfig() == false){
				message.arg1 = 0;
			}
			Log.i(Tag, "Thread END");			
			mHandler.sendMessage(message);
		}
	}

	// ������ݿ⵽sd�� ondestory
	public boolean isexistconfig() {
		File configFile = new File(SDCARD + cardName + CONFIG_FILE);
		if (configFile.exists() && configFile.length() > 0) {
			return true;
		} else {
			Log.e(Tag, "file no exist");
			return false;
		}
	}

	private boolean savedatabaseconfig() {

		File configFile = new File(SDCARD + cardName + CONFIG_FILE);

		try {
			File f = new File(SDCARD + cardName + APK_DES_DIR);
			f.mkdir();
			File srcFile = new File(databaseLoadPath);
			File dstFile = new File(SDCARD + cardName + DB_SAVE_PATH);
			if (dstFile.exists() && dstFile.length() > 0) {
				dstFile.delete();
			}
			dstFile = new File(SDCARD + cardName + DB_SAVE_PATH);
			dstFile.createNewFile();
			forTransfer(srcFile, dstFile);

			CopyAppFromDataApp();

			// configFile.delete();
		} catch (Exception e) {
			Log.e(Tag, e.toString());
			return false;
		}
		return true;
	}

	// ��sd������ݿ⵽launcher oncreate
	public boolean loaddatabaseconfig() {
		try {

			File dstFile = new File(databaseLoadPath);
			File srcFile = new File(SDCARD + cardName + DB_SAVE_PATH);
			if (srcFile.exists() && srcFile.length() > 0) {
				dstFile.delete();
				dstFile = new File(databaseLoadPath);
				srcFile = new File(SDCARD + cardName + DB_SAVE_PATH);
				forTransfer(srcFile, dstFile);
				return true;

			} else {
				Log.e(Tag, "file no exist");
			}
		} catch (Exception e) {
			Log.e(Tag, e.toString());
		}

		return false;

	}

	// f1 src f2 dst
	public long forTransfer(File f1, File f2) throws Exception {

		int length = 2097152;
		FileInputStream in = new FileInputStream(f1);
		FileOutputStream out = new FileOutputStream(f2);
		FileChannel inC = in.getChannel();
		FileChannel outC = out.getChannel();
		int i = 0;
		while (true) {
			if (inC.position() == inC.size()) {
				inC.close();
				outC.close();
				break;
			}
			if ((inC.size() - inC.position()) < 20971520) {
				length = (int) (inC.size() - inC.position());
			} else {
				length = 20971520;
			}
			inC.transferTo(inC.position(), length, outC);
			inC.position(inC.position() + length);
			i++;
		}

		return 1;
	}

	private void CopyAppFromDataApp() {

		PackageManager pckMan = getPackageManager();
		List<PackageInfo> packs = pckMan.getInstalledPackages(0);
		int count = packs.size();
		String name;
		String[] appAddName = { "", "-1", "-2" };
		String suffix = ".apk";
		File dstPath, srcPath, srcFolder;

		for (int i = 0; i < count; i++) {
			PackageInfo p = packs.get(i);
			Log.i(Tag, "===CopyAppFromDataApp===" + "CUR" + i);
			int j = 0;
			for (j = 0; j < appAddName.length; j++) {
				srcPath = new File(apkSrcNandPath + p.packageName
						+ appAddName[j] + suffix);
				if (srcPath.exists()) {
					name = p.applicationInfo.loadLabel(pckMan).toString();
					dstPath = new File(SDCARD + cardName + APK_DES_DIR + name + suffix);
					try {
						forTransfer(srcPath, dstPath);
					} catch (Exception e) {
						// TODO: handle exception
					}
					break;
				}
			}
			// �Ѿ��ҵ��Ͳ���sd����
			if (j < appAddName.length) {
				continue;
			}

			for (j = 0; j < appAddName.length; j++) {
				srcFolder = new File(apkSrcSDPath + p.packageName
						+ appAddName[j]);
				if (srcFolder.isDirectory()) {
					name = p.applicationInfo.loadLabel(pckMan).toString();
					dstPath = new File(SDCARD + cardName + APK_DES_DIR + name + suffix);
					srcPath = new File(apkSrcSDPath + p.packageName
							+ appAddName[j] + "/pkg.apk");
					try {
						forTransfer(srcPath, dstPath);
					} catch (Exception e) {
						// TODO: handle exception
					}
					break;
				}
			}
		}

	}
	BroadcastReceiver mExternalStorageReceiver;
	
	void startWatchingExternalStorage() {
	    mExternalStorageReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            Log.i("test", "Storage: " + intent.getData());
	            if(Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())){
	            	Toast.makeText(context, R.string.find_card_mount, 
	            			Toast.LENGTH_SHORT).show();
	            }else if(Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())){
	            	Toast.makeText(context, R.string.find_card_umount, 
	            			Toast.LENGTH_SHORT).show();
	            } 
	            updateCardName();
	        }
	    };
	    IntentFilter filter = new IntentFilter();
	    filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
	    filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
	    filter.addDataScheme("file");
	    registerReceiver(mExternalStorageReceiver, filter);	    
	}

	void stopWatchingExternalStorage() {
	    unregisterReceiver(mExternalStorageReceiver);
	}

}
