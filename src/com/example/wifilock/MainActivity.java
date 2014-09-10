package com.example.wifilock;
import com.example.lib.LockAPI;
import com.example.lib.SwitchView;
import com.example.lib.SwitchView.OnSwitchChangeListener;

import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {

private static final int REQUEST_CONNECT_DEVICE = 1;
	
	//private ImageButton tBLock;
	//private ImageView imageView=null;
	
	private WifiAdmin mWifiAdmin;  
	private SwitchView switchView;
	private static final String ipAddress ="192.168.1.203";
	private static final int port =8080;
	private LockAPI lockApi = new LockAPI();
	private int ret=0;
	private Boolean isOpen= false;
	private byte[] addr =  new byte[]{0x03,(byte) 0xD4, (byte) 0x6F, 0x22};
	private ProgressDialog progressDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mWifiAdmin = new WifiAdmin(MainActivity.this);

		
		
		switchView = (SwitchView) findViewById(R.id.switchview);
		switchView.setSwitchStatus(true);
		switchView.setOnSwitchChangeListener(new OnSwitchChangeListener() {
			@Override
			public void onSwitchChanged(boolean open) {
				Message msg_listData = new Message();
                msg_listData.what = comm.OPERATING;
                mHandler.sendMessage(msg_listData);
                
				 new Thread() {
                     public void run() {                        
                             try {
                                     
                            	 ret = lockApi.OpenSocket(ipAddress, port);
                 				if(ret != LockAPI.SUCCESS){
                 					 Message msg_listData = new Message();
                                     msg_listData.what = comm.OPEN_SOCKET_ERROR;
                                     mHandler.sendMessage(msg_listData);
                 					return;
                 				}
                 				  
                 				if(isOpen == false){
                 					isOpen = true;
                 					ret = lockApi.OpenLock(addr);
                 					
                 					if(ret == LockAPI.SUCCESS){
                 						
                 						Message msg_listData = new Message();
                                        msg_listData.what = comm.LOCK_OPEN_SUCCESS;
                                        mHandler.sendMessage(msg_listData);
                 					}else if (ret == LockAPI.DISCONNECT){
                 						Message msg_listData = new Message();
                                        msg_listData.what = comm.LOCK_DISCONNECT;
                                        mHandler.sendMessage(msg_listData);
                 						
                 					}
                 					
                 						
                 				}else{
                 					isOpen = false;
                 					ret = lockApi.CloseLock(addr);
                 					
                 					if(ret == LockAPI.SUCCESS){
                 						Message msg_listData = new Message();
                                        msg_listData.what = comm.CLOSE_SOCKET_SUCCESS;
                                        mHandler.sendMessage(msg_listData);
                 					}else if (ret == LockAPI.DISCONNECT){
                 						Message msg_listData = new Message();
                                        msg_listData.what = comm.LOCK_DISCONNECT;
                                        mHandler.sendMessage(msg_listData);
                 					}
                 				}
                 				lockApi.CloseSocket();
                             } catch (Exception e) {
                                     
                             }
                     }
             }.start();
             
             
			}
		});
		
	}


	@SuppressWarnings("deprecation")
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
            AlertDialog isExit = new AlertDialog.Builder(this).create();  
            isExit.setTitle("ϵͳ��ʾ");  
            isExit.setMessage("ȷ��Ҫ�˳���");  
            isExit.setButton("ȷ��", listener);  
            isExit.setButton2("ȡ��", listener);  
            isExit.show();  
  
        }  
          
        return false;  
          
    }  
	
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()  
    {  
        public void onClick(DialogInterface dialog, int which)  
        {  
            switch (which)  
            {  
            case AlertDialog.BUTTON_POSITIVE:// "ȷ��"��ť�˳�����  
                finish();  
                break;  
            case AlertDialog.BUTTON_NEGATIVE:// "ȡ��"�ڶ�����ťȡ���Ի���  
                break;  
            default:  
                break;  
            }  
        }  
    };   
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
        	if(resultCode == Activity.RESULT_OK){
        		Bundle b = data.getExtras(); 
        		WifiConfiguration wifiInfo  = b.getParcelable("wifi_info");
        		if(wifiInfo == null)
        			Toast.makeText(this, "wifi ��Ч", Toast.LENGTH_SHORT).show();
        		else if(mWifiAdmin.addNetWork(wifiInfo)){
        			Toast.makeText(this, "���ӳɹ�:"+wifiInfo.SSID, Toast.LENGTH_SHORT).show();
        		}else
        			Toast.makeText(this, "����ʧ��:"+wifiInfo.SSID, Toast.LENGTH_SHORT).show();
        	}
            break;
        default: break;
        }
        
    }
    
    
	private MessageHandler mHandler = new MessageHandler();
    
	public class MessageHandler extends Handler {
		
		public MessageHandler() {      
        }  
		public MessageHandler(Looper looper) {   
			super(looper);   
        }   
  
        @Override   
        public void handleMessage(Message msg) {
//        	SimpleDateFormat formatter = new SimpleDateFormat("yyyy��MM��dd��   HH:mm:ss     ");     
//        	Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��     
//        	String str = formatter.format(curDate); 
        	
        	switchView.setSwitchStatus(isOpen);
        	switch (msg.what){
        		case comm.LOCK_OPEN_SUCCESS:
        			
        			if(progressDialog != null)
        				progressDialog.dismiss();
        			break;
        		case comm.LOCK_CLOSE_SUCCESS:
        			if(progressDialog != null)
        				progressDialog.dismiss();
        			break;
	        	case comm.OPEN_SOCKET_ERROR:
	        		Toast.makeText(getApplicationContext(), "��Socketʧ��", Toast.LENGTH_SHORT).show();
	        		if(progressDialog != null)
        				progressDialog.dismiss();
	            	break;
	        	case comm.LOCK_DISCONNECT:
	        		Toast.makeText(getApplicationContext(), "��������", Toast.LENGTH_SHORT).show();
	        		if(progressDialog != null)
        				progressDialog.dismiss();
	        		if(isOpen == true){
	        			
	        		}else{
	        			
	        		}
	            	break;
	        	case comm.OPERATING:
	        		//progressDialog = null;
	        		progressDialog = ProgressDialog.show(MainActivity.this, "��", "���ڲ���,���Ժ�"); 
	        		break;
	            default :break;
            }  
        	
        }
	}   

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.Search:
        	Intent deviceListIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(deviceListIntent, REQUEST_CONNECT_DEVICE);
            return true;
        case R.id.Exit:
            finish();
            return true;
        }
        return false;
    }

}
