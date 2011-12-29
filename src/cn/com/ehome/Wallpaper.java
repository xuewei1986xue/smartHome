
package cn.com.ehome;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;

import java.io.IOException;
import java.io.InputStream;


public class Wallpaper extends Activity implements
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    
    private static final String LOG_TAG = "Home";

    private static final Integer[] THUMB_IDS = {
    	  R.drawable.bg_home_icon,
          R.drawable.bg_home1_icon,
          R.drawable.bg_home2_icon,
          R.drawable.bg_home3_icon,
          R.drawable.bg_home4_icon,
    };

    private static final Integer[] IMAGE_IDS = {
            R.drawable.bg_home,
            R.drawable.bg_home1,
            R.drawable.bg_home2,
            R.drawable.bg_home3,
            R.drawable.bg_home4,
    };

    private Gallery mGallery;
    private boolean mIsWallpaperSet;
        
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.wallpaper);

        mGallery = (Gallery) findViewById(R.id.gallery);
        mGallery.setAdapter(new ImageAdapter(this));
        mGallery.setOnItemSelectedListener(this);
        mGallery.setOnItemClickListener(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mIsWallpaperSet = false;
    }

    public void onItemSelected(AdapterView parent, View v, int position, long id) {
        getWindow().setBackgroundDrawableResource(IMAGE_IDS[position]);
    }
    
    public void onItemClick(AdapterView parent, View v, int position, long id) {
        selectWallpaper(position);
    }

    /*
     * When using touch if you tap an image it triggers both the onItemClick and
     * the onTouchEvent causing the wallpaper to be set twice. Synchronize this
     * method and ensure we only set the wallpaper once.
     */
    private synchronized void selectWallpaper(int position) {
        if (mIsWallpaperSet) {
            return;
        }
        mIsWallpaperSet = true;
        try {
            InputStream stream = getResources().openRawResource(IMAGE_IDS[position]);
            setWallpaper(stream);
            setResult(RESULT_OK);
            finish();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to set wallpaper " + e);
        }
    }

    public void onNothingSelected(AdapterView parent) {
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        selectWallpaper(mGallery.getSelectedItemPosition());
        return true;
    }

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        
        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return THUMB_IDS.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);

            i.setImageResource(THUMB_IDS[position]);
            i.setAdjustViewBounds(true);
            i.setLayoutParams(new Gallery.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            i.setBackgroundResource(android.R.drawable.picture_frame);
            return i;
        }

    }

}

    
