package cn.com.ehome;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

public class AdFragment extends Fragment implements ViewFactory, OnClickListener {
	
	ImageSwitcher is;
	private Handler mHandler = new Handler();
	long intervalTime = 10000;
	UpdateTask updateTask;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		is = new ImageSwitcher(getActivity());
		is.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		is.setFactory(this);
		is.setInAnimation(getActivity(), R.anim.push_left_in);
		is.setOutAnimation(getActivity(), R.anim.push_left_out);
		return is;
	}
	
	@Override
	public View makeView() {

		ImageView i = new ImageView(getActivity());
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		//i.setScaleType(ImageView.ScaleType.CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
		LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		i.setFocusable(true);
		i.setBackgroundResource(R.drawable.ad_bg);
		i.setOnClickListener(this);
		return i;
	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.baidu.com"));
		getActivity().startActivity(intent);
		
	}
	public void setIntervalTime(long time){
		intervalTime = time;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateTask = new UpdateTask();
		
		
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		mHandler.removeCallbacks(updateTask);
		super.onPause();
	}

	@Override
	public void onResume() {
		mHandler.removeCallbacks(updateTask);
		mHandler.postAtTime(updateTask, intervalTime);
		super.onResume();
	}

	
	
	class UpdateTask implements Runnable {
		
		
		private int time = 0;
		
		
	   public void run() {
		   
		   mHandler.postDelayed(this, intervalTime);	   
		   
		   
		  int i = time%3;
		  String path = "image/ad1.png";
		  switch(i){
		  case 0:
			  //is.setImageResource(R.drawable.bg_icon_0);
			  path = "image/ad4.png";
			  break;
		  case 1:
			  //is.setImageResource(R.drawable.bg_icon_1);
			  path = "image/ad3.png";
			  break;
		  case 2:
			  //is.setImageResource(R.drawable.bg_icon_2);
			  path = "image/ad1.png";
			  break;
		  }	
		  
		  try {
				InputStream inputStream = getActivity().getAssets().open(path);
				is.setImageDrawable(new BitmapDrawable(inputStream));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      time++;
	      if(time>10){
	    	  time = 0;
	      }
	   }
		}
	

}
