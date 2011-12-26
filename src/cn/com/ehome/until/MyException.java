package cn.com.ehome.until;


public class MyException extends Exception {
	
	public static final int TYPE_SET_ROOM_FAIL = 1001;
	public static final int TYPE_SET_ROOM_REPEAT = 1002;
	public static final int TYPE_BAD_XML = 1003;
	public static final int TYPE_BAD_NETWORK = 1004;
	public static final int TYPE_UNKWON_ERROR = 1005;
	public static final int TYPE_DOWNLOAD_FAIL = 1006;
	
	public int type = 0;
	public String msg;
	
	public MyException(int type, String msg){
		this.type = type;
		this.msg = msg;
	}

}
