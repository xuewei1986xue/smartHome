package cn.com.ehome;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class AppCati extends Activity {

	private GridView gridview;
	private TextView mTextView;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {		 
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.appcat);
	        
	        gridview = (GridView)findViewById(R.id.gridview);
	        
	        gridview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int arg2, long arg3) {
				}
				
			});	  
	        initData();
	 }
	
	private void initData(){

		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		
		List<IconifiedText>	directoryEntries = new ArrayList<IconifiedText>();
		Drawable d = getResources().getDrawable(R.drawable.bt_bg);
		for(int i=0; i<9; i++){
			IconifiedText ic = new IconifiedText("test",d);
			directoryEntries.add(ic);
		}
		
		
		itla.setListItems(directoryEntries);
		
		gridview.setAdapter(itla);		
		
	}
}

class IconifiedTextListAdapter extends BaseAdapter
{
	private Context				mContext	= null;
	private List<IconifiedText>	mItems		= new ArrayList<IconifiedText>();
	public IconifiedTextListAdapter(Context context)
	{
		mContext = context;
	}
	public void addItem(IconifiedText it) { mItems.add(it); }
	public void setListItems(List<IconifiedText> lit) { mItems = lit; }
	public int getCount() { return mItems.size(); }
	public Object getItem(int position) { return mItems.get(position); }
	public boolean areAllItemsSelectable() { return false; }
	public boolean isSelectable(int position) 
	{ 
		return mItems.get(position).isSelectable();
	}
	public long getItemId(int position) { return position; }
	public View getView(int position, View convertView, ViewGroup parent) {
		
    	ViewItem m_ViewItem;
		
		if(convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate( R.layout.gridfileitem,
					null);		
			m_ViewItem = new ViewItem();
			m_ViewItem.shorten_icon = (ImageView) convertView.findViewById(R.id.gridicon);	
			m_ViewItem.name_info = (TextView) convertView.findViewById(R.id.gridname);
			convertView.setTag(m_ViewItem);
		}else
		{
			m_ViewItem = (ViewItem) convertView.getTag();
		}
				
		m_ViewItem.shorten_icon.setImageDrawable(mItems.get(position).getIcon());			
		m_ViewItem.name_info.setText(mItems.get(position).getText());

		return convertView;
	}
	
	static class ViewItem {
		ImageView shorten_icon;
		TextView name_info;
	}
}
class IconifiedText implements Comparable<IconifiedText>
{
	private String		mText		= "";
	private Drawable	mIcon		= null;
	private boolean	mSelectable	= true;
	public IconifiedText(String text, Drawable bullet)
	{
		mIcon = bullet;
		mText = text;
	}
	public boolean isSelectable()
	{
		return mSelectable;
	}
	public void setSelectable(boolean selectable)
	{
		mSelectable = selectable;
	}
	public String getText()
	{
		return mText;
	}
	public void setText(String text)
	{
		mText = text;
	}
	public void setIcon(Drawable icon)
	{
		mIcon = icon;
	}
	public Drawable getIcon()
	{
		return mIcon;
	}
	public int compareTo(IconifiedText other)
	{
		if (this.mText != null)
			return this.mText.compareTo(other.getText());
		else
			throw new IllegalArgumentException();
	}
}
