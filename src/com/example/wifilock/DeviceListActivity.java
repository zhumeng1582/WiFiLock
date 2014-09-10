package com.example.wifilock;


import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceListActivity extends Activity {

	private ArrayAdapter<String> devicesArrayAdapter;
	private Button btnSearch;
	
	private WifiAdmin mWifiAdmin;  
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	    setContentView(R.layout.devicelist);
	    
	    
	    mWifiAdmin = new WifiAdmin(DeviceListActivity.this);  
	    
	    devicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
	    ListView newDevicesListView = (ListView) findViewById(R.id.tvDeviceList);
        newDevicesListView.setAdapter(devicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(deviceClickListener);
        
        btnSearch = (Button)findViewById(R.id.btnSearch);
        
		btnSearch.setOnClickListener(searchClickListener);
		
		showAllDevice();
        
	}
	
    private OnItemClickListener deviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            
            String ssid = ((TextView) v).getText().toString();
            Intent intent = new Intent();
            Bundle bundle = new Bundle();  
            WifiConfiguration wifiInfo = mWifiAdmin.IsExsits(ssid);
            bundle.putParcelable("wifi_info", wifiInfo);
            intent.putExtras(bundle);  
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };
    private void showAllDevice(){
    	devicesArrayAdapter.clear();
        mWifiAdmin.startScan();  
        List<ScanResult> list=mWifiAdmin.getWifiList();  
        if(list!=null){  
            for(int i=0;i<list.size();i++){  
                ScanResult mScanResult = list.get(i);  
                devicesArrayAdapter.add(mScanResult.SSID);
            }   
        }
    }
	
    private OnClickListener searchClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {  
			showAllDevice();
	   }
	};
}
