package cn.com.ehome.until;

import cn.com.ehome.R;
import cn.com.ehome.database.EHotelProvider;
import android.net.Uri;

public class GobalFinalData {	
	
	public static final Uri CONTENT_SORT_URI = Uri.parse("content://" + EHotelProvider.AUTHORITY + "/"+EHotelProvider.TABLE_NAME);
	public static final Uri CONTENT_PASSWORD_URI = Uri.parse("content://" + EHotelProvider.AUTHORITY + "/"+EHotelProvider.TABLE_PASSWORD);
	
	//������Ϣ
	public static final int APP_SORT_ALL=0;
	
	public static final int APP_UNSORT_ALL=1;

	public static final int APP_SORT_GAME=2;
	public static final int APP_SORT_GAME_1=0;
	public static final int APP_SORT_GAME_2=1;
	public static final int APP_SORT_GAME_3=2;
	public static final int APP_SORT_GAME_4=3;
	public static final int APP_SORT_GAME_5=4;
	
	public static final int APP_SORT_COMMERCE=3;
	public static final int APP_SORT_COMMERCE_1=0;
	public static final int APP_SORT_COMMERCE_2=0;
	public static final int APP_SORT_COMMERCE_3=0;
	public static final int APP_SORT_COMMERCE_4=0;
	public static final int APP_SORT_COMMERCE_5=0;
	
	
	public static final int APP_SORT_SYSTEM=4;
	public static final int APP_SORT_COMMERCE_ALARM=0;

	public static final String ROOT_PATH = "/mnt/sdcard";
	public static  String SAVE_DOWNLOAD_PATH = "";
	public static final int REQUEST_CODE_DOWNLOAD_PATH_SETTING = 10000;
	public static final int REQUEST_CODE_DOWNLOAD_PATH_SETTING_OK = 1;
	public static final int REQUEST_CODE_DOWNLOAD_PATH_SETTING_CANCEL = 0;
	

	public static final String CONFIG_FILE = "/mnt/sdcard/tflash/config.xml";
	
	public static final String FIRST_TIME_FLAG = "FIRST_TIME_FLAG";
	public static final String LANG = "lang";

}
