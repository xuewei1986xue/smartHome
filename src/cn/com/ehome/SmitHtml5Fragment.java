package cn.com.ehome;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.VideoView;
import cn.com.ehome.until.DownloadUtil;
import cn.com.ehome.until.GobalFinalData;

public class SmitHtml5Fragment extends Fragment {
	
	static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);

	private WebView mWebView = null;
	private OnKeyListener mOnKeyListener;
	private View mEmbeddedTitleBar;
	private WebSettings mWebSetting = null;
	
	private String remo = "";

	private String mUrl = "";
	private String mContentDisposition = "";
	
	private final static String USERAGENT = "Mozilla/5.0(iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B367 Safari/531.21.10";
	private boolean bShouldClearHistory = false;	

	private View mCustomView;
	private FrameLayout mCustomViewContainer;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
	private View mViewNeedHide;
	private MyWebChromeClient mChromeClient;
	private Activity mActivity;
	private OnWebLoadProgress onWebLoadProgress;
	private boolean playError = false;
	
	public void setCustomViewContainer(FrameLayout container) {
		mCustomViewContainer = container;
	}

	public void setViewNeedHide(View viewNeedHide) {
		mViewNeedHide = viewNeedHide;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.custom_webview, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = getActivity();
		onWebLoadProgress = (OnWebLoadProgress)mActivity;
		initVariables();
		loadHtml(remo);			
	}	

	@Override
	public void onDestroyView() {
		onWebLoadProgress = null;
		super.onDestroyView();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if(mWebView != null){
			mWebView.freeMemory();
		}
	}

	public boolean canGoBack() {
		if(mCustomView != null){
			return true;
		}
		return mWebView.canGoBack();
	}

	public void goBack() {
		if(mCustomView != null){
			mChromeClient.onHideCustomView();
			return;
		}
		mWebView.goBack();
	}
	public void setMyOnKeyListener(OnKeyListener onkeyListener){		
		mOnKeyListener = onkeyListener;
		if(mWebView != null){
			mWebView.setOnKeyListener(mOnKeyListener);
		}
	}
	public void setEmbeddedTitleBar(View view){
		mEmbeddedTitleBar = view;
		if(mWebView != null){
			mWebView.setEmbeddedTitleBar(view);
		}
	}
	private void initVariables() {
		mWebView = (WebView) getView().findViewById(R.id.customwebview);
		mWebSetting = mWebView.getSettings();
		if (mWebView == null || mWebSetting == null) {
			return;
		}
		mWebView.setFocusableInTouchMode(true);
		mWebView.setFocusable(true);
		mWebView.setOnKeyListener(mOnKeyListener);
		mWebView.setEmbeddedTitleBar(mEmbeddedTitleBar);
		
		/*mWebView.setOnTouchListener(new View.OnTouchListener() { 
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        switch (event.getAction()) { 
		                   case MotionEvent.ACTION_DOWN: 
		                   case MotionEvent.ACTION_UP: 
		                       if (!v.hasFocus()) { 
		                           v.requestFocus(); 
		                       } 
		                       break; 
		               } 
		               return false; 
		            }
		    });*/
		setBrowserAttribute();
		setDownloadListener();
		setWebViewClient();
		mChromeClient = new MyWebChromeClient();
		mWebView.setWebChromeClient(mChromeClient);
		
	}

	private void setDownloadListener() {
		DownloadListener listener = new DownloadListener() {
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {

				mUrl = url;
				mContentDisposition = contentDisposition;				
				Intent mIntent = new Intent("SetSavePathActivity");
				startActivityForResult(mIntent,
						GobalFinalData.REQUEST_CODE_DOWNLOAD_PATH_SETTING);
			}
		};
		mWebView.setDownloadListener(listener);
	}
	
	private boolean isVideoUrl(String url, String[] videoPatern) {
		for (String video : videoPatern) {
			if (url.endsWith(video))
				return true;
		}
		return false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GobalFinalData.REQUEST_CODE_DOWNLOAD_PATH_SETTING
				&& resultCode == Activity.RESULT_OK) {			
			StartDownload();
		}
	}

	private void StartDownload() {

		int indexOfQuote = mContentDisposition.indexOf("\"");
		String fileName = mContentDisposition.substring(indexOfQuote + 1,
				mContentDisposition.length() - 1);
		URL urlToDownload = null;
		try {
			urlToDownload = new URL(mUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		File destinationFile = new File(GobalFinalData.SAVE_DOWNLOAD_PATH + "/"
				+ fileName);
		File destination = new File(GobalFinalData.SAVE_DOWNLOAD_PATH);
		if (!destination.isDirectory()) {
			destination.mkdirs();
		}
		DownloadManager downloadManager = (DownloadManager)this.getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
		downloadManager.setAccessAllDownloads(true);
		DownloadUtil.downloadFile(getView().getContext(), urlToDownload,
				destinationFile, /* headers */null, true, fileName);
		
	}
	
	private void setBrowserAttribute() {
		if (mWebSetting == null) {
			mWebSetting = mWebView.getSettings();
		}
		mWebSetting.setPluginState(PluginState.ON);
		mWebSetting.setJavaScriptEnabled(true);
		mWebSetting.setSupportZoom(true);
		//mWebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		mWebSetting.setAllowFileAccess(true);
		mWebSetting.setLightTouchEnabled(true);
		mWebSetting.setUserAgentString(USERAGENT);
		mWebSetting.setUseWideViewPort(true);
		mWebSetting.setDefaultZoom(ZoomDensity.MEDIUM);
		mWebSetting.setBuiltInZoomControls(true);
		
		// make move slowly
		//mWebSetting.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		mWebSetting.setLoadWithOverviewMode(true);
		mWebSetting.setRenderPriority(RenderPriority.HIGH);
		
		
		//mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		//mWebView.setHorizontalScrollBarEnabled(false);
		//mWebView.setHorizontalScrollbarOverlay(false);
		//mWebView.setSoundEffectsEnabled(true);
		
		mWebView.setScrollbarFadingEnabled(true);
		mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.setMapTrackballToArrowKeys(false); // use trackball directly

		
		SystemProperties.set("tcc.browser.ua", "iPad");
	}

	public void remeberIndexHtml(String indexHTMLFile) {
		remo = indexHTMLFile;
	}

	public void loadHtml(String indexHTMLFile) {
		if (indexHTMLFile.equals("")) {
			Log.e("LoadError", "URL is empty");
			return;
		}
		Log.i("LoadUrl", indexHTMLFile);
		if (mWebView != null) {
			mWebView.loadUrl(indexHTMLFile);
			bShouldClearHistory = true;
		}
		remo = "";

	}

	private void setWebViewClient() {
		WebViewClient wvc = new WebViewClient() {			

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if(onWebLoadProgress != null){
					onWebLoadProgress.onPageStarted();
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (bShouldClearHistory) {
					mWebView.clearHistory();
					bShouldClearHistory = false;
				}
				if(onWebLoadProgress != null){
					onWebLoadProgress.onPageFinished();
				}
			}
		};

		mWebView.setWebViewClient(wvc);
	}

	private class MyWebChromeClient extends WebChromeClient {
		private Bitmap mDefaultVideoPoster;
		private View mVideoProgressView;
		
		@Override
		public void onShowCustomView(View view,
				WebChromeClient.CustomViewCallback callback) {
			
			if (mCustomViewContainer == null) {
				return;
			}	
			if(mViewNeedHide != null){
				mViewNeedHide.setVisibility(View.GONE);
			}		
			if(onWebLoadProgress != null){
				onWebLoadProgress.onStartPlayVideo();
			}
			// if a view already exists then immediately terminate the new one
			if (mCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}	
			playError = false;
			FrameLayout frame = (FrameLayout) view;
	        if (frame.getFocusedChild() instanceof VideoView){
	        	 VideoView video = (VideoView) frame.getFocusedChild();
	        	 // TODO add video
/*	        	MyMediaController control = new MyMediaController(
	        			 SmitHtml5Fragment.this.getActivity(), video);*/
	        	 //control.setWebChromeClient(this);
	        	     	 
	        	 //video.setMediaController(control);
	        }
			mCustomViewContainer.addView(view);
			mCustomView = view;
			mCustomViewCallback = callback;
			mCustomViewContainer.setVisibility(View.VISIBLE);
			
		}

		@Override
		public void onHideCustomView() {
			if (mCustomViewContainer == null) {
				return;
			}
			if (mCustomView == null)
				return;

			// Hide the custom view.
			mCustomView.setVisibility(View.GONE);

			// Remove the custom view from its container.
			mCustomViewContainer.removeView(mCustomView);
			mCustomView = null;
			mCustomViewContainer.setVisibility(View.GONE);
			mCustomViewCallback.onCustomViewHidden();
			if(mViewNeedHide != null){
				mViewNeedHide.setVisibility(View.VISIBLE);
			}	
			
			if(onWebLoadProgress != null){
				onWebLoadProgress.onEndPlayVideo();
			}

		}

		@Override
		public Bitmap getDefaultVideoPoster() {
			if (mDefaultVideoPoster == null) {
				mDefaultVideoPoster = BitmapFactory.decodeResource(
						getResources(), R.drawable.default_video_poster);
			}			
			mWebView.loadUrl("javascript: var v=document.getElementsByTagName('video')[0]; "
					+"v.play(); ");		
			return mDefaultVideoPoster;
		}

		@Override
		public View getVideoLoadingProgressView() {
			if (mVideoProgressView == null) {
				LayoutInflater inflater = LayoutInflater
						.from(SmitHtml5Fragment.this.getActivity());
				mVideoProgressView = inflater.inflate(
						R.layout.video_loading_progress, null);
			}
			return mVideoProgressView;
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if(onWebLoadProgress != null){
				onWebLoadProgress.onProgressChanged(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}
		
	}	
	
	public boolean getPlayError(){
		return playError;
	}

	public interface OnWebLoadProgress{
		void onPageStarted();
		void onPageFinished();
		void onProgressChanged(int newProgress);
		void onStartPlayVideo();
		void onEndPlayVideo();
	}
	
	public WebView getWebview(){
		return mWebView;
	}
	
}