package cn.com.ehome.app;

import java.util.ArrayList;
import java.util.List;

import cn.com.ehome.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IconifiedTextListAdapter extends BaseAdapter
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