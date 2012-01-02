package cn.com.ehome.videoPlayer;

import cn.com.ehome.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MyPlayerActivity extends Activity{

	private VideoView mVideoView;
	private Context mContext;
	private boolean videoError = false ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			if(getRequestedOrientation()
				== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}	
			
        mVideoView = (VideoView) findViewById(R.id.video01);
        mContext = this;
        
        Uri uri = getIntent().getData();
        if(uri!= null){   		        
        	mVideoView.setVideoURI(uri);
        	mVideoView.setTag(uri);
        }
        MyMediaController mMediaController = new MyMediaController(this,mVideoView);
//        MediaController mMediaController = new MediaController(this);
		mVideoView.setMediaController(mMediaController);
		mVideoView.setOnErrorListener(listen_video_err);
		mVideoView.setOnCompletionListener(listen_video_complete);

		mVideoView.start();
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(mReceiver_Screen_off_on, intentFilter);

    }
    
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mReceiver_Screen_off_on != null) {
			unregisterReceiver(mReceiver_Screen_off_on);
			mReceiver_Screen_off_on = null;
		}
		
	}
	
	private BroadcastReceiver mReceiver_Screen_off_on = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				mVideoView.pause();
				
			}else if(action.equals(Intent.ACTION_SCREEN_ON)){
				mVideoView.start();
			}
		}
	};
	
	private OnErrorListener listen_video_err = new OnErrorListener() {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			videoError = true ;
			Toast.makeText(mContext, R.string.play_error,Toast.LENGTH_SHORT).show();
			listen_video_complete.onCompletion(mp);
			return true;
		}
	};
	
	private OnCompletionListener listen_video_complete = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			finish();
			return;
		}
	};
	
	public boolean getVideoErrorState(){
		return videoError;
	}
}
