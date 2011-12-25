package cn.com.ehome;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class InfoActivity extends Activity implements OnClickListener {
		
	private Button mBtn1;
	private Button mBtn2;
	private Button mBtn3;
	private Button mBtn4;
	
	private Button mBtnBack;
	
	private View mContent;
	private int mLevel = 1;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.info);
	        
	        mBtn1 = (Button)findViewById(R.id.button1);
	        mBtn2 = (Button)findViewById(R.id.button2);
	        mBtn3 = (Button)findViewById(R.id.button3);
	        mBtn4 = (Button)findViewById(R.id.button4);
	        mBtnBack = (Button)findViewById(R.id.back);
	        
	        mBtn1.setOnClickListener(this);
	        mBtn2.setOnClickListener(this);
	        mBtn3.setOnClickListener(this);
	        mBtn4.setOnClickListener(this);
	        mBtnBack.setOnClickListener(this);
	        
	        mContent = (View)findViewById(R.id.content);
	        mLevel = 1;
	        initData();
	 }
	 @Override
	 public void onClick(View v) {
		 int id = v.getId();
		 switch(mLevel){
		 case 1:
			 switch(id){
			 case R.id.button1:
				 break;
			 case R.id.button2:
				 break;
			 case R.id.button3:
				 break;
			 case R.id.button4:
				 break;
			 default:
				break;
			 }		 
			 break;
		 case 2:
			 switch(id){
			 case R.id.button1:
				 break;
			 case R.id.button2:
				 break;
			 case R.id.button3:
				 break;
			 case R.id.button4:
				 break;
			 default:
				break;
			 }		 
			 break;
		 case 3:
			 switch(id){
			 case R.id.button1:
				 break;
			 case R.id.button2:
				 break;
			 case R.id.button3:
				 break;
			 case R.id.button4:
				 break;
			 default:
				break;
			 }		 
			 break;
		 case 4:
			 switch(id){
			 case R.id.button1:
				 break;
			 case R.id.button2:
				 break;
			 case R.id.button3:
				 break;
			 case R.id.button4:
				 break;
			 default:
				break;
			 }		 
			 break;
		 default:
			break;
		 }	 

		 setData(mLevel,id);
		 if(id == R.id.back){
			 Intent intent = new Intent();
			 intent.setClass(this, EHomeActivity.class);
			 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 startActivity(intent);
			 //finish();
		 }
	 }
	 
	 private void initData(){
		 setData(mLevel,R.id.button1);		 
	 }
	 
	 private void setData(int level, int id){
		 switch(mLevel){
		 case 1:
			 switch(id){
			 case R.id.button1:
				 mContent.setBackgroundResource(R.drawable.btn1_1);
				 break;
			 case R.id.button2:
				 mContent.setBackgroundResource(R.drawable.btn2_1);
				 break;
			 case R.id.button3:
				 mContent.setBackgroundResource(R.drawable.btn1_3);
				 break;
			 case R.id.button4:
				 mContent.setBackgroundResource(R.drawable.btn1_4);
				 break;
			 default:
				break;
			 }		 
			 break;
		 case 2:
			 switch(id){
			 case R.id.button1:
				 break;
			 case R.id.button2:
				 break;
			 case R.id.button3:
				 break;
			 case R.id.button4:
				 break;
			 default:
				break;
			 }		 
			 break;
		 case 3:
			 switch(id){
			 case R.id.button1:
				 break;
			 case R.id.button2:
				 break;
			 case R.id.button3:
				 break;
			 case R.id.button4:
				 break;
			 default:
				break;
			 }		 
			 break;
		 case 4:
			 switch(id){
			 case R.id.button1:
				 break;
			 case R.id.button2:
				 break;
			 case R.id.button3:
				 break;
			 case R.id.button4:
				 break;
			 default:
				break;
			 }		 
			 break;
		 default:
			break;
		 }		
	 }
		
}
