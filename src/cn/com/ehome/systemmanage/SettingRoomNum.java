package cn.com.ehome.systemmanage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.ehome.R;
import cn.com.ehome.database.EHotelProvider;
import cn.com.ehome.until.GobalFinalData;
import cn.com.ehome.until.MyException;
import cn.com.ehome.until.XmlPreference;

public class SettingRoomNum extends Activity implements OnClickListener {

    private static final String TAG = "eHotel";
    private static final String PRE = "SettingRoomNum";
    
    public static final String CUSTOM_DIR = "/mnt/sdcard/ehotel";
    public static final String CUSTOM_LOGO_PIC = CUSTOM_DIR + "/logo.png";
    public static final String CUSTOM_WELCOME_MUSIC = CUSTOM_DIR + "/welcome.mp3";
    public static final String CUSTOM_BIRTHDAY_MUSIC = CUSTOM_DIR + "/birthday.mp3";
    
	private static final int DIALOG_PROGRESS = 1;
		
	private TextView mTvCurStatus;
	private Button mUnreg;
	private Button mReg;
	private EditText mInputRoomNum;
	private String mCurRoomNum = "";
	
	private static final String STATUS_SUC = "success";
	private static final String STATUS_REPEAT = "repeat";
	private static final String STATUS_FAIL = "fail";
		
	private String SET_ROOM_URL = "http://192.168.1.222:8080/e_hotel/checkRoom.action";	
	
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setting_room_num);
		setUpView();
		XmlPreference xmlPreference = new XmlPreference(GobalFinalData.CONFIG_FILE);
		String value = xmlPreference.getKeyValue("set_room_url");
		if (value != null && value.isEmpty() == false) {
			SET_ROOM_URL = value;
		}
	}

	private void setUpView() {
		mTvCurStatus = (TextView) findViewById(R.id.cur_status);
		mUnreg = (Button) findViewById(R.id.btn_unreg);
		mUnreg.setOnClickListener(this);
		mReg = (Button) findViewById(R.id.btn_reg);
		mReg.setOnClickListener(this);
		mInputRoomNum = (EditText) findViewById(R.id.editText_roomNum);
		
		updateView();
	}

	private void updateView() {
		mCurRoomNum = EHotelProvider.getRoomNum();
		if (mCurRoomNum == null || mCurRoomNum.isEmpty()) {
			mTvCurStatus.setText(R.string.no_room_num);
			mUnreg.setEnabled(false);
		} else {
			String curRoom = getResources().getString(R.string.cur_room_num_is);
			curRoom = String.format(curRoom, mCurRoomNum);			
			mTvCurStatus.setText(Html.fromHtml(curRoom));
			mUnreg.setEnabled(true);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog dialog = null;
		switch(id){
		case DIALOG_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			dialog = mProgressDialog;
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setMessage("Waiting");
			break;
		}
		return dialog;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog, args);
	}

	@Override
	public void onClick(View v) {
		
		
		if (v.getId() == R.id.btn_unreg) {			
			if(mCurRoomNum.isEmpty()){
				return;
			}
			new RegAsycPost().execute("false",mCurRoomNum);
		} else if (v.getId() == R.id.btn_reg) {
			String roomNum = mInputRoomNum.getText().toString();
			if(roomNum.isEmpty()){
				Toast.makeText(SettingRoomNum.this, R.string.input_cannt_null,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if(mCurRoomNum.equalsIgnoreCase(roomNum)){
				Toast.makeText(SettingRoomNum.this, R.string.have_set_this_roomnum,
						Toast.LENGTH_SHORT).show();
				return;
			}
			new RegAsycPost().execute("true",roomNum);
		}
		showDialog(DIALOG_PROGRESS);

	}

	private void httpReq(String roomNum) throws MyException {		

		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		DefaultHttpClient client = new DefaultHttpClient(httpParameters);
		
		HttpPost post = new HttpPost(SET_ROOM_URL);
		List nvps = new ArrayList();
		nvps.add(new BasicNameValuePair("roomNum", roomNum));
		
		UrlEncodedFormEntity p_entity = null;
		try {
			p_entity = new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		post.setEntity(p_entity);
		HttpResponse response = null;
		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new MyException(MyException.TYPE_BAD_NETWORK,"ClientProtocolException");
		} catch (IOException e) {
			e.printStackTrace();
			throw new MyException(MyException.TYPE_BAD_NETWORK,"IOException");
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {			
			e.printStackTrace();
			throw new MyException(MyException.TYPE_BAD_XML,"error");
		}
		InputStream inputStream = null;
		try {
			inputStream = response.getEntity().getContent();
			//inputStream = getResources().getAssets().open("CheckRoom.xml");
		} catch (IllegalStateException e) {
			
			e.printStackTrace();
			e.printStackTrace();
			throw new MyException(MyException.TYPE_BAD_NETWORK,"IllegalStateException");
		} catch (IOException e) {
			
			e.printStackTrace();
			throw new MyException(MyException.TYPE_BAD_NETWORK,"IllegalStateException");
		} catch (NullPointerException e){
			e.printStackTrace();
			throw new MyException(MyException.TYPE_BAD_NETWORK,"IllegalStateException");
		}
		
		Document doc = null;
		try {
			doc = db.parse(inputStream);
		} catch (SAXException e) {		
			e.printStackTrace();
			throw new MyException(MyException.TYPE_BAD_NETWORK,"SAXException");
		} catch (IOException e) {			
			e.printStackTrace();
			throw new MyException(MyException.TYPE_BAD_NETWORK,"IOException");
		}		
		doc.getDocumentElement().normalize();
		
		NodeList nodeList = doc.getElementsByTagName("status");
		if(nodeList == null || nodeList.getLength() == 0){
			throw new MyException(MyException.TYPE_BAD_XML,"error");
		}
		Node node = nodeList.item(0);
		NamedNodeMap attrs = node.getAttributes();
		Node nodeStatus = attrs.getNamedItem("data");
		if(nodeStatus == null){
			throw new MyException(MyException.TYPE_BAD_XML,"error");
		}
		String statusValue = nodeStatus.getNodeValue();
		if(STATUS_SUC.equalsIgnoreCase(statusValue)){
			
		}else if(STATUS_REPEAT.equalsIgnoreCase(statusValue)){
			throw new MyException(MyException.TYPE_SET_ROOM_REPEAT,"repeat set");
		}else if(STATUS_FAIL.equalsIgnoreCase(statusValue)){
			throw new MyException(MyException.TYPE_SET_ROOM_FAIL,"error");
		}
		
		
		nodeList = doc.getElementsByTagName("hotelLogo");
		if(nodeList == null || nodeList.getLength() == 0){
			throw new MyException(MyException.TYPE_BAD_XML,"error");
		}
		node = nodeList.item(0);
		attrs = node.getAttributes();
		nodeStatus = attrs.getNamedItem("data");
		if(nodeStatus == null){
			throw new MyException(MyException.TYPE_BAD_XML,"error");
		}
		String logoUrl = nodeStatus.getNodeValue();
		
		nodeList = doc.getElementsByTagName("welcomeMusic");
		if(nodeList == null || nodeList.getLength() == 0){
			throw new MyException(MyException.TYPE_BAD_XML,"error");
		}
		node = nodeList.item(0);
		attrs = node.getAttributes();
		nodeStatus = attrs.getNamedItem("data");
		if(nodeStatus == null){
			throw new MyException(MyException.TYPE_BAD_XML,"error");
		}
		String welcomeMusicUrl = nodeStatus.getNodeValue();
		
		
		nodeList = doc.getElementsByTagName("birthdayMusic");
		if(nodeList == null || nodeList.getLength() == 0){
			throw new MyException(MyException.TYPE_BAD_XML,"error");
		}
		node = nodeList.item(0);
		attrs = node.getAttributes();
		nodeStatus = attrs.getNamedItem("data");
		if(nodeStatus == null){
			throw new MyException(MyException.TYPE_BAD_XML,"error");
		}
		String birthdayMusicUrl = nodeStatus.getNodeValue();
		
		// download		
		if(logoUrl != null && logoUrl.isEmpty()== false){
			if(false == downloadFile(logoUrl,CUSTOM_LOGO_PIC)){
				File file = new File(CUSTOM_LOGO_PIC);
				file.delete();
				throw new MyException(MyException.TYPE_DOWNLOAD_FAIL,CUSTOM_LOGO_PIC);
			}
		}		
		if(welcomeMusicUrl != null && welcomeMusicUrl.isEmpty()== false){
			if(false == downloadFile(welcomeMusicUrl,CUSTOM_WELCOME_MUSIC)){
				File file = new File(CUSTOM_WELCOME_MUSIC);
				file.delete();
				throw new MyException(MyException.TYPE_DOWNLOAD_FAIL,CUSTOM_WELCOME_MUSIC);
			}
		}
		if(birthdayMusicUrl != null && birthdayMusicUrl.isEmpty()== false){
			if(false == downloadFile(birthdayMusicUrl,CUSTOM_BIRTHDAY_MUSIC)){
				File file = new File(CUSTOM_BIRTHDAY_MUSIC);
				file.delete();
				throw new MyException(MyException.TYPE_DOWNLOAD_FAIL,CUSTOM_BIRTHDAY_MUSIC);
			}
		}
	}
	
	
	@SuppressWarnings("finally")
	private Boolean downloadFile(String surl, String desPath){
		int nBufSize = 1024; 				
		Boolean bOk = true;
		FileOutputStream fos = null;
		try {	
			File directory = new File(CUSTOM_DIR);
			directory.mkdir();
			URL url = new URL(surl);
		    File file = new File(desPath);	
		    file.createNewFile();
		    fos = new FileOutputStream(file);		    
		   
		    URLConnection ucon = url.openConnection();
		    InputStream is = ucon.getInputStream();
		    BufferedInputStream bis = new BufferedInputStream(is);		    
		  
		    byte[] data = new byte[nBufSize]; 
		    int nFinishSize = 0;
		    int nread = 0;
		    while( (nread = bis.read(data, 0, nBufSize)) != -1){
		    	fos.write(data, 0, nread);                	
		    	nFinishSize += nread;
		    }              
			data = null;    
			Log.d("test", "finish");
		        
		} catch (IOException e) {
		        Log.d(TAG, PRE + "Error: " + e);	       
		        bOk = false;
		} catch (Exception e){
				 e.printStackTrace(); 
				 bOk = false;
		} finally{
			try {
				if(fos != null)
					fos.close();
			} catch (IOException e) {
				Log.d(TAG, PRE + "Error: " + e);
				e.printStackTrace();
			}
			return bOk;
		}
	}	

	class RegAsycPost extends AsyncTask<String, Integer, MyException> {
		private Boolean bReg = true;
		private String roomNum;

		@Override
		protected MyException doInBackground(String... params) {
			bReg = params[0].equalsIgnoreCase("true");
			roomNum = params[1];
			
			MyException exp = null;
			if(bReg == false){
				return null;
			}
			try {
				httpReq(roomNum);
			} catch (MyException e) {
				exp = e;
				e.printStackTrace();
			}			
			return exp;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

		}

		@Override
		protected void onPostExecute(MyException exception) {
			dismissDialog(DIALOG_PROGRESS);
			int strId = R.string.setting_room_fail_ureg;
			if (bReg == true) {
				if(exception == null){
					EHotelProvider.setRoomNum(roomNum);
					updateView();
					Toast.makeText(SettingRoomNum.this, R.string.setting_room_suc,
							Toast.LENGTH_SHORT).show();
					return;
					
				}else{
					
					Toast.makeText(SettingRoomNum.this, R.string.setting_room_fail,
							Toast.LENGTH_SHORT).show();	
					strId = R.string.setting_room_fail;
				}
				
			}else{				
				if(exception == null){
					EHotelProvider.setRoomNum("");
					updateView();
					Toast.makeText(SettingRoomNum.this, R.string.setting_room_suc_ureg,
							Toast.LENGTH_SHORT).show();
					return;
					
				}else{
					Toast.makeText(SettingRoomNum.this, R.string.setting_room_fail_ureg,
							Toast.LENGTH_SHORT).show();					
				}				
			}	
			
			switch(exception.type){
			case MyException.TYPE_SET_ROOM_REPEAT:
				strId = R.string.setting_room_repeat;
				break;
			case MyException.TYPE_BAD_NETWORK:
				strId = R.string.setting_room_net_problem;
				break;
			case MyException.TYPE_BAD_XML:
				strId = R.string.setting_room_parse_xml_fail;
				break;
			case MyException.TYPE_SET_ROOM_FAIL:
				strId = R.string.setting_room_return_fail;
				break;
			case MyException.TYPE_DOWNLOAD_FAIL:
				strId = R.string.setting_room_download_fail;
				break;
			}
			String msg = getResources().getString(strId) + "," + getResources().getString(
					R.string.error_code) + exception.type;
			Toast.makeText(SettingRoomNum.this, msg,
					Toast.LENGTH_LONG).show();	
		}
	}

}
