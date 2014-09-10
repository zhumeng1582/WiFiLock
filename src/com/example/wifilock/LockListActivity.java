package com.example.wifilock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.lib.LockAPI;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class LockListActivity extends ListActivity {
	

	private List<Map<String, Object>> mData;
	private static final String ipAddress ="192.168.1.203";
	private static final int port =8080;
	private LockAPI lockApi = new LockAPI();
	private int ret=0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mData = getData();
		MyAdapter adapter = new MyAdapter(this);
		setListAdapter(adapter);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();

		map = new HashMap<String, Object>();
		map.put("name", "1号锁");
		map.put("addr", new byte[]{0x03,(byte) 0xD4, (byte) 0x6F, 0x2A});
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "2号锁");
		map.put("addr", new byte[]{0x03,(byte) 0xD4, (byte) 0x6F, 0x22});
		list.add(map);
		
		return list;
	}
	// ListView 中某项被选中后的逻辑
		@Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
			
			Log.v("MyListView4-click", (String)mData.get(position).get("title"));
		}
		
		/**
		 * listview中点击按键弹出对话框
		 */
		public void showInfo(){
			new AlertDialog.Builder(this)
			.setTitle("我的listview")
			.setMessage("介绍...")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
			
		}
		
		
		
		public final class ViewHolder{
			public TextView tVname;
			public ToggleButton tBOperate;
		}
		
		
		public class MyAdapter extends BaseAdapter{

			private LayoutInflater mInflater;
			
			
			public MyAdapter(Context context){
				this.mInflater = LayoutInflater.from(context);
			}
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mData.size();
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				ViewHolder holder = null;
				if (convertView == null) {
					
					holder=new ViewHolder();  
					
					convertView = mInflater.inflate(R.layout.lock_view_list, null);
					holder.tVname = (TextView)convertView.findViewById(R.id.tVLock);
					holder.tBOperate = (ToggleButton)convertView.findViewById(R.id.tBLock);
					convertView.setTag(holder);
					
				}else {
					
					holder = (ViewHolder)convertView.getTag();
				}
				
				
				
				holder.tVname.setText((String)mData.get(position).get("name"));
				final byte[] addr = (byte[])mData.get(position).get("addr");
				
				holder.tBOperate.setOnCheckedChangeListener(new OnCheckedChangeListener(){


					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean isOpen) {					
						ret = lockApi.OpenSocket(ipAddress, port);
						if(ret != LockAPI.SUCCESS){
							Toast.makeText(getApplicationContext(), "锁不在线", Toast.LENGTH_SHORT).show();
							return;
						}
						
						if(isOpen == true){
							
							ret = lockApi.OpenLock(addr);
							if(ret == LockAPI.SUCCESS){
								
							}else if (ret == LockAPI.DISCONNECT){
								Toast.makeText(getApplicationContext(), "锁不在线", Toast.LENGTH_SHORT).show();
							}
							
								
						}else{
							lockApi.CloseLock(addr);
							if(ret == LockAPI.SUCCESS){
								
							}else if (ret == LockAPI.DISCONNECT){
								
								Toast.makeText(getApplicationContext(), "锁不在线", Toast.LENGTH_SHORT).show();
							}
						}
						lockApi.CloseSocket();				
					}
				});
				
				
				return convertView;
			}
			
		}
}
