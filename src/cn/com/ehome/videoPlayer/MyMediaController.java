package cn.com.ehome.videoPlayer;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import com.android.internal.policy.PolicyManager;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import cn.com.ehome.videoPlayer.SoundView.OnVolumeChangedListener;
import cn.com.ehome.R;

public class MyMediaController extends MediaController{

	private WebChromeClient   	mWebChromeClient = null;
    private MediaPlayerControl  mPlayer;
    private MediaPlayer  		mMediaPlayer;
    private Context             mContext;
    private View                mAnchor;
    private View                mRoot;
    private WindowManager       mWindowManager;
    private Window              mWindow;
    private View                mDecor;
    private ProgressBar         mProgress;
    private TextView            mEndTime, mCurrentTime;
    private boolean             mShowing;
    private boolean             mDragging;
    private static final int    sDefaultTimeout = 3000;
    private static final int    FADE_OUT = 1;
    private static final int    SHOW_PROGRESS = 2;
    private long                mLastSeekEventTime;
    private static final long   SEEK_TIME_OFFSET = 250; //Fix me...(msec)

    // VIEW_SUBTITLE
    private static final int	SHOW_SUBTITLE = 3;
    private boolean             mUseFastForward;
    private boolean             mFromXml;
    private boolean             mListenersSet;
    private View.OnClickListener mNextListener, mPrevListener;
    StringBuilder               mFormatBuilder;
    Formatter                   mFormatter;
    private ImageButton         mPauseButton;
    private ImageButton         mFfwdButton;
    private ImageButton         mRewButton;
    private ImageButton         mNextButton;
    private ImageButton         mPrevButton;
	private ImageButton 		btn_show_up_ctrl;
	
	private TextView			mCurrentTitle = null;
	private ImageButton 		mSubtitleButton;
	private ImageButton 		mSubtitleStreamButton;
	private ImageButton 		mAudioStreamButton;
	private ImageButton 		mPlayrateHighButton;
	private ImageButton 		mPlayrateLowButton;
	private ImageButton 		mScreenLayoutButton;
	
	private ImageButton 		btn_info;
	private View ctrlView_Down;
	private VideoView mVideoView = null;
	private boolean isPlayying = true;
	private boolean isPlayComplent = false;
	private final int VIDEO_SIZE_DEFAULT = 0;// Ĭ�ϴ�С
	private final int VIDEO_SIZE_STRETCH = 1;// ��������
	private final int VIDEO_SIZE_FULLSCREEN = 2;// ȫ����ʾ
	private int video_size_ctrl = 0;
	private int mVideowidth = 320;
	private int mVideoheight = 240;
//	private SeekBar seekbarshow = null;
	private SoundView mSoundView = null;
	private PopupWindow mSoundWindow = null;
	private AudioManager mAudioManager = null; 
	private boolean isSoundShow = false;
	private static long getDuration;
	private static long getcurrentposition;
	
	private final static int PROGRESS_CHANGED = 33;
	private final static int HIDE_CONTROLER = 11;
	private final static int HIDE_POPWIN = 12;
	boolean flag_hide_ctrlView = false;
	private static long DELAY_TIME_HIDE_CTRLVIEW = 30000;
	private static long DELAY_TIME_HIDE_SOUNDVIEW = 2000;
	
	private PopupWindow mMovieNameWindow = null;
	private TextView txtMovieName;
	private TextView txtCurTime;
	private boolean btnClicked = false;
	
	public MyMediaController(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
        SystemProperties.set("tcc.solution.preview","0");
	}

	public MyMediaController(Context context, VideoView v) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		mVideoView = v;
        SystemProperties.set("tcc.solution.preview","0");
	}
	
	@Override
	protected View makeControllerView() {
		// TODO Auto-generated method stub
		super.makeControllerView();
		mRoot = LayoutInflater.from(mContext).inflate(R.layout.playying,
				null);
		initControllerView(mRoot);
		return mRoot;
	}

    private void initControllerView(View v) {
    	mPauseButton = (ImageButton) v.findViewById(R.id.btn_play);
        if (mPauseButton != null) {
        	mPauseButton.requestFocus();
        	mPauseButton.setOnClickListener(listen_btn_play);
        }

        mFfwdButton = (ImageButton) v.findViewById(R.id.btn_forward);
        if (mFfwdButton != null) {
            mFfwdButton.setOnClickListener(listen_btn_forward);
        }

        mRewButton = (ImageButton) v.findViewById(R.id.btn_back);
        if (mRewButton != null) {
            mRewButton.setOnClickListener(listen_btn_back);
        }
        
		btn_show_up_ctrl = (ImageButton) v.findViewById(R.id.btn_show_up_ctrl);
		
		String ScreenMode = SystemProperties.get("tcc.solution.screenmode.index", "0");
		if(ScreenMode.equals("0"))
			btn_show_up_ctrl.setBackgroundResource(R.drawable.show_up_ctrl_btn_styles);	
		if(ScreenMode.equals("1"))
			btn_show_up_ctrl.setBackgroundResource(R.drawable.btn_scale_stretch_styles);	
		if(ScreenMode.equals("2"))
			btn_show_up_ctrl.setBackgroundResource(R.drawable.btn_stretch_to_fullscreen_styles);	

        if (btn_show_up_ctrl != null) {
        	btn_show_up_ctrl.setOnClickListener(listen_btn_show_up);
        }
        
		btn_info = (ImageButton) v.findViewById(R.id.btn_info);
        if (btn_info != null) {
        	btn_info.setOnClickListener(listen_btn_info);
        }
        
		ctrlView_Down = v.findViewById(R.id.controldown);
        mProgress = (ProgressBar) v.findViewById(R.id.seekbar);
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(listen_seekbar);
            }
            mProgress.setMax(1000000);
        }

        mEndTime = (TextView) v.findViewById(R.id.duration);
        mCurrentTime = (TextView) v.findViewById(R.id.has_played);

        RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.relativeLayout001); 
		rl.setVisibility(View.INVISIBLE);
        
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mVideoView.setOnPreparedListener(listen_video_prepared);
        
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);  
        mSoundView = new SoundView(mContext);
        mSoundView.setOnVolumeChangeListener(new OnVolumeChangedListener(){
        	@Override
			public void setYourVolume(int index) {
				updateVolume(index);
			}
        });
        	
    	mSoundWindow = new PopupWindow(mSoundView);
    	
    	//��ʼ��VideoView������ʾ�ĵ�������
    	View view = LayoutInflater.from(mContext).inflate(R.layout.movi_name_popupwindow,null);
    	txtMovieName = (TextView)view.findViewById(R.id.movieName);
    	txtCurTime = (TextView)view.findViewById(R.id.curTime);
    	mMovieNameWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	
        
//		myHandler.sendEmptyMessage(PROGRESS_CHANGED);
    }
    
	private OnClickListener listen_btn_back = new OnClickListener() {
		public void onClick(View v) {
/*			getDuration = mVideoView.getDuration();
			getcurrentposition = mVideoView.getCurrentPosition() - (int)(getDuration/20);

			if(getcurrentposition < 0)
				getcurrentposition = 0;
			mProgress.setProgress((int)getcurrentposition);*/
			btnClicked = true;
			getcurrentposition = mVideoView.getCurrentPosition() - 15000;
			mVideoView.seekTo((int)getcurrentposition);
			setProgress();
			show();
		}
	};

	private OnClickListener listen_btn_play = new OnClickListener() {
		public void onClick(View v) {
			if (mVideoView.isPlaying()) {
				mVideoView.pause();
			} else {
				btnClicked = true;  
				mVideoView.start();
				isPlayComplent = false;
			}
			updatePauseAndPlay();
//			isPlayying = !isPlayying;
			btnClicked = true;
			show();
		}
	};

	 private void updatePauseAndPlay() {
	        if (mRoot == null || mPauseButton == null)
	            return;
	        
			if (mVideoView.isPlaying()) {
				mPauseButton.setBackgroundResource(R.drawable.pause_btn_styles);
			} else {
				mPauseButton.setBackgroundResource(R.drawable.play_btn_styles);
			}
	 }
	 
	private OnClickListener listen_btn_forward = new OnClickListener() {
		public void onClick(View v) {
//			getDuration = mVideoView.getDuration();
//			getcurrentposition = mVideoView.getCurrentPosition() + (int)(getDuration/20);
/*			if(getcurrentposition > getDuration)
				getcurrentposition = getDuration;
			
			Log.i("getDuration>>>>>>>>", getDuration + " ");
			Log.i("getcurrentposition>>>>>>>>", getcurrentposition + " ");
			mProgress.setProgress((int)getcurrentposition);*/
			btnClicked = true;
			getcurrentposition = mVideoView.getCurrentPosition() + 15000;
			mVideoView.seekTo((int)getcurrentposition);
			setProgress();
			show();
		}
	};


	private OnSeekBarChangeListener listen_seekbar = new OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekbar, int progress,
				boolean fromUser) {

			if (fromUser) {
				Log.i("", "=========seekbarshow "
						+ "setOnSeekBarChangeListener");
				Log.i("", progress + "progress");
				
				mVideoView.seekTo(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			show(3600000);
			myHandler.removeMessages(PROGRESS_CHANGED);
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			show(sDefaultTimeout);
			myHandler.sendEmptyMessage(PROGRESS_CHANGED);
		}


	};
    
	private OnClickListener listen_btn_show_up = new OnClickListener() {
		public void onClick(View v) {
 			mVideoView.setCommandProcess(2);
 			
// 			Log.i("11111"," " + SystemProperties.get("tcc.solution.mbox", "0"));
// 			Log.i("11111"," " + SystemProperties.get("tcc.solution.use.screenmode", "0"));
// 			Log.i("11111"," " + SystemProperties.getInt("persist.sys.output_mode", 1));
// 			Log.i("11111"," " + SystemProperties.get("tcc.solution.screenmode.index", "0"));
// 			Log.i("11111"," " + SystemProperties.get("tcc.solution.screenmode.index", "1"));
// 			Log.i("11111"," " + SystemProperties.get("tcc.solution.screenmode.index", "2"));
 			
			video_size_ctrl++;
			if (video_size_ctrl > 2)
				video_size_ctrl = 0;

			switch (video_size_ctrl) {
			case 0:
//				setVideoSize(VIDEO_SIZE_DEFAULT);
				btn_show_up_ctrl
						.setBackgroundResource(R.drawable.show_up_ctrl_btn_styles);
				Toast.makeText(mContext, R.string.play_original,
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
//				setVideoSize(VIDEO_SIZE_STRETCH);
				btn_show_up_ctrl
						.setBackgroundResource(R.drawable.btn_scale_stretch_styles);
				Toast.makeText(mContext, R.string.play_scale_stretch,
						Toast.LENGTH_SHORT).show();
				break;
			case 2:
//				setVideoSize(VIDEO_SIZE_FULLSCREEN);
				btn_show_up_ctrl
						.setBackgroundResource(R.drawable.btn_stretch_to_fullscreen_styles);
				Toast.makeText(mContext, R.string.play_stretch_to_fullscreen,
						Toast.LENGTH_SHORT).show();
				break;
			}
			show(sDefaultTimeout);
		}
	};


	private OnClickListener listen_btn_info = new OnClickListener() {
		public void onClick(View v) {
			btnClicked = true;
			if(isSoundShow){
				mSoundWindow.dismiss();
			}else{
				if(mSoundWindow.isShowing()){
					mSoundWindow.update(0,100,SoundView.MY_HEIGHT,SoundView.MY_WIDTH);
				}else{
					mSoundWindow.showAtLocation(mVideoView, Gravity.CENTER|Gravity.CENTER_VERTICAL, 0, 100);
					mSoundWindow.update(0,100,SoundView.MY_HEIGHT,SoundView.MY_WIDTH);
				}
				myHandler.removeMessages(HIDE_POPWIN);
				myHandler.sendEmptyMessageDelayed(HIDE_POPWIN,
						DELAY_TIME_HIDE_SOUNDVIEW);
			}
			isSoundShow = !isSoundShow;
			show();
		}
	};

	private OnPreparedListener listen_video_prepared = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer arg0) {
			Log.i("", "===============setonpraperlistener");
			mMediaPlayer = arg0;
//			mMediaPlayer.setOnInfoListener(on_info_listener);
			
			mVideowidth = arg0.getVideoWidth();
			mVideoheight = arg0.getVideoHeight();
			Log.i("mVideowidth", " " + mVideowidth);
			Log.i("mVideoheight", " " + mVideoheight);
			
			if(mWebChromeClient != null){
				mWebChromeClient.getVideoLoadingProgressView().setVisibility(View.GONE);
			}
		}
	};
	
	public void setVideoSize(int style) {
		Log.e("����ǰ��ȣ�-------" ,"---" + mVideoView.getLayoutParams().width);
		Log.e("����ǰ�߶ȣ�-------" ,"---" + mVideoView.getLayoutParams().height);
//		LayoutParams lp = (LayoutParams) mVideoView.getLayoutParams();// ���ò��ֲ���
		
		mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = mWindowManager.getDefaultDisplay();
		int screenwidth = display.getWidth();
		int screenheight = display.getHeight();		
        
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(screenwidth, screenheight, 17);

		int videowidth = 0;
		int videoheight = 0;

		videowidth = mVideowidth;
		videoheight = mVideoheight;

        System.out.println("videowidth : " + videowidth);
        System.out.println("videoheight : " + videoheight);
        
		switch (style) {
		case VIDEO_SIZE_DEFAULT:
			if (screenwidth > screenheight) {
				lp.width = videowidth;
				lp.height = videoheight;

				if (videowidth >= screenwidth || videoheight >= screenheight) {
					
					if ((videowidth * screenheight) >= (screenwidth * videoheight)) {
							lp.width = screenwidth;
							lp.height = videoheight * screenwidth / videowidth;
						
						} else {
							lp.width = videowidth * screenheight / videoheight;
							lp.height = screenheight;
						}
	              }
			}
			break;

		case VIDEO_SIZE_STRETCH:

			if (screenwidth > screenheight) {
				if ((videowidth * screenheight) >= (screenwidth * videoheight)) {
					lp.width = screenwidth;
					lp.height = videoheight * screenwidth / videowidth;

				} else {
					lp.width = videowidth * screenheight / videoheight;
					lp.height = screenheight;
				}
			} 
			break;
		case VIDEO_SIZE_FULLSCREEN:
		
			if (screenwidth > screenheight) {
				lp.width = screenwidth;
				lp.height = screenheight;
			} else {
				lp.width = screenwidth;
				lp.height = screenheight;
			}
			break;
		default:
			break;
		}
		
		Log.e("lp.width��-------" ,"---" + lp.width);
		Log.e("lp.height��-------" ,"---" + lp.height);

//		mVideoView.getLayoutParams().width = lp.width;
//		mVideoView.getLayoutParams().height = lp.height;
		mVideoView.setLayoutParams(lp);
		Log.e("���ú��ȣ�-------" ,"---" + mVideoView.getLayoutParams().width);
		Log.e("���ú�߶ȣ�-------" ,"---" + mVideoView.getLayoutParams().height);
		
	}
	
	public Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
//			Log.i(TAG, "receive messgae");
			switch (msg.what) {
			case HIDE_CONTROLER:
				Log.i("", "===============hidecontroler send messgae");
				ctrlView_Down.setVisibility(View.INVISIBLE);
				if (mSoundWindow.isShowing()){
					isSoundShow = false;
					mSoundWindow.dismiss();
				}
				flag_hide_ctrlView = true;

				break;
			case PROGRESS_CHANGED:
				/*if(txtCurTime!=null){
				SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//�������ڸ�ʽ
			    txtCurTime.setText(df.format(new Date()));// new Date()Ϊ��ȡ��ǰϵͳʱ��
				}*/
				setProgress();   // �����һ�����һ��
				sendMessageDelayed(obtainMessage(PROGRESS_CHANGED), 1000);
				break;

			case HIDE_POPWIN:
				if (mSoundWindow.isShowing()){
					isSoundShow = false;
					mSoundWindow.dismiss();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};
	
   private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
	
	private void setProgress(){
		getcurrentposition = mVideoView.getCurrentPosition();
		getDuration = mVideoView.getDuration();
		if (mProgress != null){
			if(getDuration >= 0){
	             mProgress.setMax((int) getDuration);
	             mProgress.setProgress((int) getcurrentposition);
			}
	        int percent = mVideoView.getBufferPercentage();
			Log.i("  percent  ", "" + percent);
	        mProgress.setSecondaryProgress((int)getDuration*percent/100);
		}
		if (mEndTime != null)
			mEndTime.setText(stringForTime((int)getDuration));
		if (mCurrentTime != null)
			mCurrentTime.setText(stringForTime((int)getcurrentposition));
	}
	
    private void updateVolume(int index){
    	if(mAudioManager!=null){
    			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
    	}
		myHandler.removeMessages(HIDE_POPWIN);
		myHandler.sendEmptyMessageDelayed(HIDE_POPWIN,
				DELAY_TIME_HIDE_SOUNDVIEW);
    }
    
    public void setWebChromeClient(WebChromeClient client){
    	mWebChromeClient = client;
    }
    
    public void show() {
		super.show();
		if(mRoot!=null && !btnClicked){
			slideShowMediaController();
		}
		btnClicked = false;
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
        }
        if(mMovieNameWindow!=null){
        	showMovieNameWindow();
        }
        updatePauseAndPlay();
		myHandler.sendEmptyMessage(PROGRESS_CHANGED);
        Log.i("MediaController", "-----show()---");
    }
    
    public void hide() {
    	super.hide();
    	if(mMovieNameWindow!=null){
    		hideMovieNameWindow();
    	}
    	myHandler.removeMessages(PROGRESS_CHANGED);
        Log.i("MediaController", "-----hide()---");
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
            mPauseButton.getBackground().setAlpha(255);
        }
        if (mFfwdButton != null) {
            mFfwdButton.setEnabled(enabled);
            mFfwdButton.getBackground().setAlpha(255);
        }
        if (mRewButton != null) {
            mRewButton.setEnabled(enabled);
            mRewButton.getBackground().setAlpha(255);
        }
        if (mProgress != null) {
            mProgress.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }
    
    public void disableUnsupportedButtons(){
        try {
            if (mPauseButton != null && !mVideoView.canPause()) {
                mPauseButton.setEnabled(false);
                mPauseButton.getBackground().setAlpha(150);
            }
            if (mRewButton != null && !mVideoView.canSeekBackward()) {
                mRewButton.setEnabled(false);
                mRewButton.getBackground().setAlpha(150);
            }
            if (mFfwdButton != null && !mVideoView.canSeekForward()) {
                mFfwdButton.setEnabled(false);
                mFfwdButton.getBackground().setAlpha(150);
            }
			// TELECHIPS, SSG
			if (mProgress != null && (!mVideoView.canSeekForward() || !mVideoView.canSeekBackward())) {
				mProgress.setEnabled(false);
			}
        } catch (IncompatibleClassChangeError ex) {
            // We were given an old version of the interface, that doesn't have
            // the canPause/canSeekXYZ methods. This is OK, it just means we
            // assume the media can be paused and seeked, and so we don't disable
            // the buttons.
        }
    	
    }

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		int keyCode = event.getKeyCode();
        if (event.isDown() && (
                keyCode ==  KeyEvent.KEYCODE_HEADSETHOOK ||
                keyCode ==  KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ||
                keyCode ==  KeyEvent.KEYCODE_SPACE)) {
        	Log.i("dispatchKeyEvent","-------dispatchKeyEvent");
			if (mVideoView.isPlaying()) {
				mVideoView.pause();
				mPauseButton.setBackgroundResource(R.drawable.play_btn_styles);
			} else {
				mVideoView.start();
				mPauseButton.setBackgroundResource(R.drawable.pause_btn_styles);
			}
			show(sDefaultTimeout);
			if (mPauseButton != null) {
				mPauseButton.requestFocus();
			}
			return true;
        }else{
        	return super.dispatchKeyEvent(event);
        }
	}
	
/*	private MediaPlayer.OnInfoListener on_info_listener = new MediaPlayer.OnInfoListener() {

		@Override
		public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			Log.i("onInfo-----arg1"," " + arg1);
			Log.i("onInfo-----arg2"," " + arg2);
			if(arg1 == 701)
				mWebChromeClient.getVideoLoadingProgressView().setVisibility(View.VISIBLE);
			if(arg1 == 702)
				mWebChromeClient.getVideoLoadingProgressView().setVisibility(View.GONE);
	
			return false;
		}
	};*/
	
	private void showMovieNameWindow(){
		Uri uri = (Uri)mVideoView.getTag();
		if(uri!=null){
			File file = new File(uri.getPath());
			Log.i("--------" , file.getName());
			txtMovieName.setText(file.getName());
		}
//		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//�������ڸ�ʽ
//	    txtCurTime.setText(df.format(new Date()));// new Date()Ϊ��ȡ��ǰϵͳʱ��
		mMovieNameWindow.showAtLocation(mVideoView, Gravity.CENTER|Gravity.TOP, 0, 0);
	}
	
	private void hideMovieNameWindow(){
		mMovieNameWindow.dismiss();
	}
	
	private void slideShowMediaController(){
		Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(500);
		mRoot.startAnimation(animation);
	}
}
