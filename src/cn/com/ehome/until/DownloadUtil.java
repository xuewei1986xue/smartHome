package cn.com.ehome.until;

import java.net.URL;

import android.net.Uri;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;

public class DownloadUtil {
	
	static Context m_ctx; 
	static URL m_url; 
	static File m_destination;
	static Map<String, String> m_headers; 
	static boolean m_notification; 
	static String m_title;
	public static boolean downloadFile(Context ctx, URL url, File destination,
								Map<String, String> headers, boolean notification, String title) {
		m_ctx = ctx;
		m_url = url;
		m_destination = destination;
		m_headers = headers;
		m_notification = notification;
		m_title = title;
		if(destination.exists())
		{
			final File file2 = destination;
			Builder builder = new Builder(ctx);
			//builder.setTitle(R.string.notice);
			//builder.setMessage(R.string.is_recover_the_same_file);
			builder.setPositiveButton(android.R.string.ok,
					new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							file2.delete();
							startDownloadActivity();
						}
					});
			builder.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			builder.setCancelable(false);
			builder.create();
			builder.show();
			return true;
		}else{
			startDownloadActivity();
			return true;
		}

	}
	
	
	public static void startDownloadActivity(){
			DownloadManager mgr = (DownloadManager) m_ctx.getSystemService(Context.DOWNLOAD_SERVICE);
			Request request = new Request(Uri.parse(m_url.toString()));
			request.setDestinationUri(Uri.fromFile(m_destination));
			if(m_headers != null){
				for (Entry<String, String> entry : m_headers.entrySet()) {
					request.addRequestHeader(entry.getKey(), entry.getValue());
				}
			}
			//request.setDescription(m_ctx.getString(R.string.save_path) + m_destination.toString());
			request.setTitle(m_title);
			request.setShowRunningNotification(true);
			request.setVisibleInDownloadsUi(true);
			request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
			mgr.enqueue(request);
			String archiveFilePath="system/app/DownloadProviderUi.apk";
	        PackageManager pm = m_ctx.getPackageManager();  
	        PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);  
	        if(info != null){  
	            ApplicationInfo appInfo = info.applicationInfo;  
	            String packageName = appInfo.packageName;
	            Intent intent = new Intent();
				intent = pm.getLaunchIntentForPackage(packageName);
				if (intent != null)
					m_ctx.startActivity(intent); 
	        }  
	}
}
