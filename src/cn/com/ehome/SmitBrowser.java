package cn.com.ehome;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Process;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import cn.com.ehome.SmitHtml5Fragment.OnWebLoadProgress;
import cn.com.ehome.until.GobalFinalData;
import cn.com.ehome.until.IBrowserActivity;
import cn.com.ehome.until.XmlPreference;

public class SmitBrowser extends FragmentActivity implements OnWebLoadProgress,
		IBrowserActivity, OnKeyListener {
	
	public static final String OPEN_URL = "open_url";

	SmitHtml5Fragment webviewFragment;
	FrameLayout mCustomViewContainer;
	View mNeedHideView;
	
	Boolean mCanHideFakeTitleBar;
	Boolean mFirstShowMenu = true;
	Boolean mNotWebMode = false;
	Boolean mVideoMode = false;


	private static String LOGTAG = "SmitBrowser";
	private static final String WEBVEW_FRAGMENT = "webView";
	private static final String BROWSER_FRAGMENT = "browser";


	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		//readBaseUrl();
		setContentView(R.layout.smit_browser);
		setupviews();	
	}

	
	


	@Override
	public void onAttachFragment(Fragment fragment) {
		// TODO Auto-generated method stub
		super.onAttachFragment(fragment);
	}





	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		String url = getIntent().getStringExtra(OPEN_URL);
		if(url != null){
			webviewFragment.loadHtml(url);
		}else{
			webviewFragment.loadHtml("file:///android_asset/game.html");
		}
	}





	@Override
	protected void onDestroy() {	
		
		super.onDestroy();
	}


	private void setupviews() {		
		
		mCustomViewContainer = (FrameLayout)findViewById(R.id.fullscreen_custom_content);
		mNeedHideView = findViewById(R.id.containerBody);	
		
		webviewFragment = new SmitHtml5Fragment();
		webviewFragment.setCustomViewContainer(mCustomViewContainer);
		webviewFragment.setViewNeedHide(mNeedHideView);
		webviewFragment.setMyOnKeyListener(this);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.containerBody, webviewFragment, WEBVEW_FRAGMENT);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
	}

	
	@Override
	public void stopLoading() {
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mNeedHideView.getVisibility() == View.GONE) {
			return super.onKeyDown(keyCode, event);
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		if (webviewFragment != null && webviewFragment.canGoBack()) {
			webviewFragment.goBack();
			return;
		}
		super.onBackPressed();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		
		return false;
	}


	@Override
	public void onPageFinished() {
		
	}

	@Override
	public void onPageStarted() {

	}	

	@Override
	public void onStartPlayVideo() {
		
	}

	@Override
	public void onEndPlayVideo() {
		mVideoMode = false;
	}

	@Override
	public void onProgressChanged(int newProgress) {
		// TODO Auto-generated method stub		
	}
	
}
