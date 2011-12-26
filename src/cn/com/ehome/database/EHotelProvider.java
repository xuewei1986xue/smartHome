package cn.com.ehome.database;

import java.net.URISyntaxException;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import cn.com.ehome.ApplicationInfo;
import cn.com.ehome.until.GobalFinalData;

public class EHotelProvider extends ContentProvider {
	public static SQLiteDatabase sqlDB;
	public static DatabaseHelper dbHelper;
	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 4;
	public static final String TABLE_NAME = "sortinfo";
	public static final String TABLE_PASSWORD = "passinfo";
	public static final Uri CONTENT_URI  = Uri.parse("content://cn.com.ehome.database.EHotelProvider");
	public static final String AUTHORITY  = "cn.com.ehome.database.EHotelProvider";
	public static final Uri CONTENT_PASSWORD_URI = Uri.parse("content://" + AUTHORITY + "/passinfo");
	public static Context mContext;
	
	public static final String ID = "_id";
	public static final String APP_TITLE = "_app_title";	
	public static final String APP_INTENT = "_app_intent";
	public static final String APP_PACKAGE_NAME = "_app_package_name";
	public static final String APP_SORT = "_app_sort";
	public static final String APP_SORT_CHILD = "_app_sort_child";
	
	public static final String CURISADMIN="_isadmin";
	public static final String USER_NAME = "_username";
	public static final String PASSWORD = "_password";
	public static final String ROOMNUM = "_roomNum";
	public static final String TAG = "======eHotelProvider======";

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// create table

			db.execSQL("CREATE TABLE  IF NOT EXISTS " + TABLE_NAME + "( "
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "_app_title TEXT," 
					+ APP_INTENT + " TEXT,"					
					+ "_app_sort Integer,"
					+ "_app_sort_child Integer,"
					+ APP_PACKAGE_NAME + " TEXT"+ ");");
			
			db.execSQL("CREATE TABLE  IF NOT EXISTS " + TABLE_PASSWORD + "( "
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "_isadmin  Integer,"
					+ "_username TEXT," 
					+ "_password TEXT,"
					+ "_roomNum TEXT"
					+ ");");
		

			db.execSQL("INSERT INTO passinfo " + "(_isadmin, _username, _password, _roomNum) "
					+ "VALUES" + "('0','admin', '123456', '');"); // ��������

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}

	@Override
	public int delete(Uri uri, String s, String[] as) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();// url.getPathSegments().get(0);
		return db.delete(uri.getPathSegments().get(0), s, as);
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentvalues) {
		sqlDB = dbHelper.getWritableDatabase();
		Log.i("===", "INSERT");
		long rowId = sqlDB.insert(uri.getPathSegments().get(0), "", contentvalues);
		if (rowId > 0) {
			Log.i("===", "ROWID");
			Uri rowUri = ContentUris.appendId(CONTENT_URI.buildUpon(), rowId).build();
			Log.i("===", "ROWURI");
			getContext().getContentResolver().notifyChange(rowUri, null);
			return rowUri;
		}
		
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		
		dbHelper = new DatabaseHelper(getContext());
		mContext=getContext();
		
		Cursor abcCursor=query(CONTENT_PASSWORD_URI,null,null,null,null);//test
		return (dbHelper == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		qb.setTables(uri.getPathSegments().get(0));

		Cursor c = qb.query(db, projection, selection, null, null, null,
				sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String s, String[] as) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db.update(uri.getPathSegments().get(0), values, s, as);
	}

	public static Cursor QuerySort(int group,int child){
		Cursor mCursor=null;
		String where=null;
		String orderBy;
		String limit;
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		qb.setTables(GobalFinalData.CONTENT_SORT_URI.getPathSegments().get(0));
		
		if (group<0&&child<0) {
			where=null;
		}else if (group<0) {
			where = APP_SORT + "=" + group;
		}else if (child<0) {
			where = APP_SORT_CHILD + "=" + child;
		}else {
			where = APP_SORT + "=" + group +" "+ "AND" +" " + APP_SORT_CHILD + "=" + child;
		}
		
		mCursor = qb.query(db, null, where, null, null, null,
				null);
		mCursor.setNotificationUri(mContext.getContentResolver(), GobalFinalData.CONTENT_SORT_URI);
		
		return mCursor;
	}
	
	public static void InsertSort(ApplicationInfo info,int group,int child){
		if (info==null) {
			return;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();		
		ContentValues values = new ContentValues();
		values.put(APP_TITLE, info.title.toString());
		values.put(APP_INTENT, info.intent.toUri(0));
		values.put(APP_PACKAGE_NAME, info.packageName);
		values.put(APP_SORT, group);
		values.put(APP_SORT_CHILD, child);
		db.insert(TABLE_NAME, null, values);
		
		//mContext.getContentResolver().notifyChange(GobalFinalData.CONTENT_SORT_URI, null);
	}

	public static void DeleteSort(int id){
		String where;
		SQLiteDatabase db = dbHelper.getWritableDatabase();	
		where = ID + "=" + id;
		db.delete(TABLE_NAME, where, null);
	}
	
	public static void DeleteSort(String packageName){
		String where;
		SQLiteDatabase db = dbHelper.getWritableDatabase();	
		where = APP_PACKAGE_NAME + "=\"" + packageName + "\"";
		int n = db.delete(TABLE_NAME, where, null);
		Log.e("tt", String.valueOf(n) + " " + packageName);
	}
	
	public static Intent getIntentById(long id){
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = dbHelper.getReadableDatabase();		
		String where = ID + "=" + id;
		Cursor c = db.query(TABLE_NAME,new String[]{APP_INTENT}, where, null, null, null, null);
		Intent intent = null;
		if(c != null && c.getCount()>0){
			c.moveToFirst();
			String intentString = c.getString(0);
			try {
				intent = Intent.parseUri(intentString, 0);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return intent;
		
	}
	

	public static boolean IsAdmin(){
		Cursor mCursor;
		int admin;
		SQLiteDatabase db = dbHelper.getWritableDatabase();		
		mCursor=db.query(TABLE_PASSWORD, null, null, null, null, null, null);
		mCursor.moveToFirst();
		admin=mCursor.getInt(1);
		mCursor.close();
		if (admin<1) {
			return false;
		}else {
			return true;
		}
	}
	

	public static void SetAdmin(int admin){
		SQLiteDatabase db = dbHelper.getWritableDatabase();		
		ContentValues values = new ContentValues();
		values.put(CURISADMIN, admin);
		db.update(TABLE_PASSWORD, values, null, null);
	}
	
	public static String getCurPassword(){
		Cursor mCursor;
		String password;
		SQLiteDatabase db = dbHelper.getReadableDatabase();		
		mCursor=db.query(TABLE_PASSWORD, null, null, null, null, null, null);
		mCursor.moveToFirst();
		password=mCursor.getString(3);
		mCursor.close();
		return password;
	}
	
	public static String getRoomNum(){
		Cursor mCursor;
		String password;
		SQLiteDatabase db = dbHelper.getReadableDatabase();		
		mCursor=db.query(TABLE_PASSWORD, null, null, null, null, null, null);
		mCursor.moveToFirst();
		password=mCursor.getString(4);
		mCursor.close();
		return password;
	}
	
	public static void setRoomNum(String roomNum){
		SQLiteDatabase db = dbHelper.getWritableDatabase();		
		ContentValues values = new ContentValues();
		values.put(ROOMNUM, roomNum);
		db.update(TABLE_PASSWORD, values, null, null);
	}
}
