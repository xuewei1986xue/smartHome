package cn.com.ehome.systemmanage;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cn.com.ehome.ApplicationInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import cn.com.ehome.R;
import cn.com.ehome.database.EHotelProvider;
import cn.com.ehome.until.GobalFinalData;

public class SetSortActivity extends Activity implements OnItemClickListener,
		OnChildClickListener, OnGroupClickListener {
	private static final String TAG = "SetSortActivity";
	private static final boolean DEBUG = false;

	private AlertDialog.Builder mBuilderpass;
	private AlertDialog mAlertpass;
	private ListView applist;
	private ExpandableListView sortlist, addlist;
	private ArrayList<ApplicationInfo> mApplications;
	private ArrayList<ApplicationInfo> mNoSortApp;
	private ApplicationInfo sel_app;
	private Cursor mSortAppCursor;
	private int mCurDelIndex = -1;
	private int curgroup, curchild;

	public final static int DIALOG_SORT = 5; 
	public final static int DIALOG_DEL_SORT_APP = 6;

	private int app_sort[] = { R.string.all_app,R.string.unsort_app, R.string.all_game,
			R.string.all_commerce, R.string.all_tool };
	private int app_all_abc[][] = {
			{},
			{},
			{ R.string.all_game_1, R.string.all_game_2/*, R.string.all_game_3, 
				R.string.all_game_4, R.string.all_game_5*/},
			{ R.string.all_commerce_1/*, R.string.all_commerce_2, R.string.all_commerce_3,
				R.string.all_commerce_4, R.string.all_commerce_5*/  }, 
			{ R.string.all_tool_1 } };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_sort);
		setupViews();
		sortlist.setAdapter(new MyExpandableListAdapter(this));
		sortlist.setOnChildClickListener(this);
		sortlist.setOnGroupClickListener(this);

		loadApplications();
		bindApplications();
		
		
	}

	public void setupViews() {
		sortlist = (ExpandableListView) findViewById(R.id.fun_sort_list_id);

		applist = (ListView) findViewById(R.id.app_sort_list_id);
		applist.setOnItemClickListener(this);
		
		findViewById(R.id.btn_export).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// TODO out put xml file
			
				EHotelProvider.savaAsConfig(GobalFinalData.CONFIG_FILE);
				Toast.makeText(SetSortActivity.this, R.string.sava_file_suc, Toast.LENGTH_SHORT).show();
				
			}
			
		});
		findViewById(R.id.btn_import).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
			
				EHotelProvider.importConfigFile(GobalFinalData.CONFIG_FILE);
				Toast.makeText(SetSortActivity.this, R.string.import_file_suc, Toast.LENGTH_SHORT).show();
				
			}
			
		});
	}

	private void PopSortSel(int id) {
		Dialog dlg = onCreateDialog(id);
		dlg.show();

		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {

				}
				return false;
			}
		});
	}

	public Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {

		case DIALOG_SORT: {
			LayoutInflater inflater = LayoutInflater.from(this);
			View password = inflater.inflate(R.layout.add_sort_listlay, null);
			mBuilderpass = new AlertDialog.Builder(this);
			mBuilderpass.setTitle(R.string.sort_app).setView(password);

			addlist = (ExpandableListView) password
					.findViewById(R.id.add_sort_expand_id);

			addlist.setAdapter(new MySortExpandableListAdapter(this));
			addlist.setOnChildClickListener(this);

			mAlertpass = mBuilderpass.create();
			dialog = mAlertpass;
			break;
		}
		case DIALOG_DEL_SORT_APP: {
			Button mOk, mCancel;
			LayoutInflater inflater = LayoutInflater.from(this);
			View tips = inflater.inflate(R.layout.tipsdialog, null);
			mOk = (Button) tips.findViewById(R.id.button_id_ok);
			mCancel = (Button) tips.findViewById(R.id.button_id_cancel);
			mBuilderpass = new AlertDialog.Builder(this);
			mBuilderpass.setTitle(R.string.sort_app_del).setView(tips);
			mAlertpass = mBuilderpass.create();
			dialog = mAlertpass;

			mOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mAlertpass != null) {
						mAlertpass.dismiss();
						mAlertpass = null;
					}
					EHotelProvider.DeleteSort(mCurDelIndex);

					if (mSortAppCursor != null) {
						mSortAppCursor.close();
						mSortAppCursor = null;
					}
					mSortAppCursor = EHotelProvider.QuerySort(curgroup,
							curchild);
					if (mSortAppCursor != null) {
						applist.setAdapter(new SortAppAdapter(
								getApplicationContext(), mSortAppCursor, true));

					}

				}
			});
			mCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mAlertpass != null) {
						mAlertpass.dismiss();
						mAlertpass = null;
					}
				}
			});
			break;
		}
		}

		return dialog;
	}

	private void loadNoSortApp(){
		Intent intent = null;
		String intentString;
		Cursor mCursor;
		String curPackagename;
		int allAppCount=0;

		if ( mNoSortApp== null) {
			mNoSortApp = new ArrayList<ApplicationInfo>();
		}
		mNoSortApp.clear();
		
		mCursor = EHotelProvider.QuerySort(-1, -1);
		int mCount = mCursor.getCount();
		
		
		allAppCount=mApplications.size();
		for (int i = 0; i < allAppCount; i++) {
			curPackagename=mApplications.get(i).intent.getComponent().getPackageName();
			int curi = 0;
			for (curi = 0; curi < mCount; curi++) {
				mCursor.moveToPosition(curi);
				String app_intent = mCursor.getString(mCursor
						.getColumnIndex(EHotelProvider.APP_INTENT));
				Intent mIntent = null;
				String mPackagename = null;
				try {
					mIntent = Intent.parseUri(app_intent, 0);
					mPackagename = mIntent.getComponent().getPackageName();
				} catch (Exception e) {
					// TODO: handle exception
				}
				if (curPackagename.equals(mPackagename)) {
					break;
				} else {
					continue;
				}
			}
			if (curi==mCount) {
				ApplicationInfo applicationInfo=new ApplicationInfo(mApplications.get(i));
				mNoSortApp.add(applicationInfo);
			}
		}
		mCursor.close();
	}
	
	private void loadApplications() {

		PackageManager manager = getPackageManager();

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
	}

	public void bindApplications() {
		applist.setAdapter(new AllAppAdapter(this));
		applist.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (parent.getAdapter() instanceof AllAppAdapter) {
			sel_app = (ApplicationInfo) mApplications.get(position);
			PopSortSel(DIALOG_SORT);
		}else if (parent.getAdapter() instanceof NoSortAppAdapter) {
			sel_app = (ApplicationInfo) mNoSortApp.get(position);
			PopSortSel(DIALOG_SORT);
		} else if (parent.getAdapter() instanceof SortAppAdapter) {
			PopSortSel(DIALOG_DEL_SORT_APP);
			SortAppAdapter adatpAdapte = (SortAppAdapter) parent.getAdapter();
			adatpAdapte.getCursor().moveToPosition(position);
			mCurDelIndex = adatpAdapte.getCursor().getInt(0);
		}
	}

	//
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {

		if (parent == sortlist) {
			if (groupPosition == GobalFinalData.APP_SORT_ALL) {
				loadApplications();
				bindApplications();
			}else if (groupPosition == GobalFinalData.APP_UNSORT_ALL) {
				loadNoSortApp();
				applist.setAdapter(new NoSortAppAdapter(this));
				applist.setOnItemClickListener(this);
			}

		}
		return false;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {

		if (parent == sortlist) {
			curgroup = groupPosition;
			curchild = childPosition;

			if (mSortAppCursor != null) {
				mSortAppCursor.close();
				mSortAppCursor = null;
			}
			mSortAppCursor = EHotelProvider.QuerySort(groupPosition,
					childPosition);
			if (mSortAppCursor != null) {
				applist.setAdapter(new SortAppAdapter(this, mSortAppCursor, true));

			}

		} else if (parent == addlist) {
			if (mAlertpass != null) {
				mAlertpass.dismiss();
				mAlertpass = null;
			}
			// ����ǲ����Ѿ����뵽������

			Intent intent = null;

			String intentString;
			Cursor mCursor;
			String curPackagename;

			mCursor = EHotelProvider.QuerySort(-1, -1);
			intent = sel_app.intent;
			curPackagename = intent.getComponent().getPackageName();

			int mCount = mCursor.getCount();
			int curi = 0;
			for (curi = 0; curi < mCount; curi++) {
				mCursor.moveToPosition(curi);
				String app_intent = mCursor.getString(mCursor
						.getColumnIndex(EHotelProvider.APP_INTENT));
				Intent mIntent = null;
				String mPackagename = null;
				try {
					mIntent = Intent.parseUri(app_intent, 0);
					mPackagename = mIntent.getComponent().getPackageName();
				} catch (Exception e) {
					// TODO: handle exception too bad design
				}
				if (curPackagename.equals(mPackagename)) {
					break;
				} else {
					continue;
				}
			}

			String paramOne, paramtwo;
			int group, child;
			if (curi == mCount) {
				EHotelProvider.InsertSort(sel_app, groupPosition + GobalFinalData.APP_SORT_GAME,
						childPosition);	
				//�����δ����Ҫ����
				loadNoSortApp();
				applist.setAdapter(new NoSortAppAdapter(this));
				applist.setOnItemClickListener(this);
			} else {
				mCursor.moveToPosition(curi);
				group = mCursor.getInt(mCursor
						.getColumnIndex(EHotelProvider.APP_SORT));
				child = mCursor.getInt(mCursor
						.getColumnIndex(EHotelProvider.APP_SORT_CHILD));
				paramOne = getApplicationContext().getResources().getString(
						app_sort[group]);
				paramtwo = getApplicationContext().getResources().getString(
						app_all_abc[group][child]);
				Toast.makeText(
						getApplicationContext(),
						getApplicationContext().getResources().getString(
								R.string.be_sort, paramOne,
								paramtwo), Toast.LENGTH_SHORT).show();
			}
			
			mCursor.close();
		}

		return true;
	}

	public class MyExpandableListAdapter extends BaseExpandableListAdapter {

/*		private int app_sort[] = { R.string.all_app,R.string.unsort_app, R.string.all_game,
				R.string.all_commerce, R.string.all_tool };
		private int app_all_abc[][] = {
				{},
				{},
				{ R.string.all_game_1, R.string.all_game_2, R.string.all_game_3, R.string.all_game_4, R.string.all_game_5},
				{ R.string.all_commerce_1, R.string.all_commerce_2, R.string.all_commerce_3,
					R.string.all_commerce_4, R.string.all_commerce_5  }, { R.string.all_tool_1 } };*/

		private Context mContext;
		private ArrayList<String> groups = new ArrayList<String>();
		private ArrayList<ArrayList<String>> children = new ArrayList<ArrayList<String>>();
		private int text_left_pos=0;
		
		public MyExpandableListAdapter(Context context) {
			mContext = context;
			text_left_pos=(int)mContext.getResources().getDimension(R.dimen.expand_text_left);
			
			int grouplen = app_sort.length;
			int childlen = 0;
			for (int i = 0; i < grouplen; i++) {
				groups.add(mContext.getResources().getString(app_sort[i]));

				children.add(new ArrayList<String>());
				childlen = app_all_abc[i].length;
				for (int j = 0; j < childlen; j++) {
					children.get(i).add(
							mContext.getResources()
									.getString(app_all_abc[i][j]));
				}

			}

		}

		public Object getChild(int groupPosition, int childPosition) {
			// return children[groupPosition][childPosition];
			return children.get(groupPosition).get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			// return children[groupPosition].length;
			return children.get(groupPosition).size();
		}

		public TextView getGenericView() {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);
			TextView textView = new TextView(SetSortActivity.this);	
			textView.setLayoutParams(lp);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(text_left_pos, 0, 0, 0);
			return textView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(getChild(groupPosition, childPosition).toString());
			return textView;
		}

		public Object getGroup(int groupPosition) {
			// return groups[groupPosition];
			return groups.get(groupPosition);
		}

		public int getGroupCount() {
			// return groups.length;
			return groups.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(getGroup(groupPosition).toString());
			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}
	}

	public class MySortExpandableListAdapter extends BaseExpandableListAdapter {

		private int app_sort[] = { R.string.all_game, R.string.all_commerce,
				R.string.all_tool };
		private int app_all_abc[][] = {
				{ R.string.all_game_1, R.string.all_game_2, 
					/*R.string.all_game_3, R.string.all_game_4, R.string.all_game_5*/},
				{ R.string.all_commerce_1, /*R.string.all_commerce_2, R.string.all_commerce_3,
					R.string.all_commerce_4, R.string.all_commerce_5 */ },
				{ R.string.all_tool_1 } };


		private Context mContext;
		private ArrayList<String> groups = new ArrayList<String>();
		private ArrayList<ArrayList<String>> children = new ArrayList<ArrayList<String>>();
		private int text_left_pos=0;
		
		public MySortExpandableListAdapter(Context context) {
			mContext = context;
			text_left_pos=(int)mContext.getResources().getDimension(R.dimen.expand_text_left);
			
			int grouplen = app_sort.length;
			int childlen = 0;
			for (int i = 0; i < grouplen; i++) {
				groups.add(mContext.getResources().getString(app_sort[i]));

				children.add(new ArrayList<String>());
				childlen = app_all_abc[i].length;
				for (int j = 0; j < childlen; j++) {
					children.get(i).add(
							mContext.getResources()
									.getString(app_all_abc[i][j]));
				}

			}

		}

		public Object getChild(int groupPosition, int childPosition) {
			// return children[groupPosition][childPosition];
			return children.get(groupPosition).get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			// return children[groupPosition].length;
			return children.get(groupPosition).size();
		}

		public TextView getGenericView() {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);

			TextView textView = new TextView(SetSortActivity.this);

			textView.setLayoutParams(lp);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(text_left_pos, 0, 0, 0);
			return textView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(getChild(groupPosition, childPosition).toString());
			return textView;
		}

		public Object getGroup(int groupPosition) {
			// return groups[groupPosition];
			return groups.get(groupPosition);
		}

		public int getGroupCount() {
			// return groups.length;
			return groups.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(getGroup(groupPosition).toString());
			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}
	}

	public class AllAppAdapter extends BaseAdapter {
		Context mContext;

		public AllAppAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mContext = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
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
				convertView = inflater.inflate(R.layout.set_sort_item, parent,
						false);
			}

			ImageView appIcon = (ImageView) convertView.findViewById(R.id.icon);
			Drawable icon = info.iconBitmap;
			if (icon != null) {
				appIcon.setBackgroundDrawable(icon);
			} else {
				appIcon.setBackgroundResource(R.drawable.bt_bg);
			}

			final TextView textView = (TextView) convertView
					.findViewById(R.id.title);
			if (info.title != null) {
				textView.setText(info.title);
			} else {
				textView.setText(R.string.loading);
			}
			
			final TextView textViewInf = (TextView) convertView.findViewById(R.id.appInf);
			textViewInf.setText(info.packageName);

			return convertView;

		}
	}

	public class SortAppAdapter extends CursorAdapter {

		public SortAppAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);

		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			ImageView appIcon = (ImageView) view.findViewById(R.id.icon);

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

			final TextView textView = (TextView) view.findViewById(R.id.title);
			textView.setText(cursor.getString(cursor
					.getColumnIndex(EHotelProvider.APP_TITLE)));
			final TextView textViewInf = (TextView) view.findViewById(R.id.appInf);
			textViewInf.setText(cursor.getString(cursor
					.getColumnIndex(EHotelProvider.APP_PACKAGE_NAME)));
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final LayoutInflater inflater = LayoutInflater.from(mContext);
			View convertView = inflater.inflate(R.layout.set_sort_item, parent,
					false);
			return convertView;
		}
	}
	
	
	public class NoSortAppAdapter extends BaseAdapter {
		Context mContext;
		public NoSortAppAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mContext = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mNoSortApp != null) {
				return mNoSortApp.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mNoSortApp.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ApplicationInfo info = new ApplicationInfo(
					mNoSortApp.get(position));

			if (convertView == null) {
				final LayoutInflater inflater = LayoutInflater.from(mContext);
				convertView = inflater.inflate(R.layout.set_sort_item, parent,
						false);
			}

			ImageView appIcon = (ImageView) convertView.findViewById(R.id.icon);
			Drawable icon = info.iconBitmap;
			if (icon != null) {
				appIcon.setBackgroundDrawable(icon);
			} else {
				appIcon.setBackgroundResource(R.drawable.bt_bg);
			}

			final TextView textView = (TextView) convertView
					.findViewById(R.id.title);
			if (info.title != null) {
				textView.setText(info.title);
			} else {
				textView.setText(R.string.loading);
			}
			final TextView textViewInf = (TextView) convertView.findViewById(R.id.appInf);
			textViewInf.setText(info.packageName);

			return convertView;

		}
	}
}