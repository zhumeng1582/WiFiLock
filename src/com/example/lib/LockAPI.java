package com.example.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class LockAPI{
	public static final int SUCCESS = 0;
	public static final int FAIL = -1;
	public static final int DISCONNECT = -2;
	
	public static final byte PACKAGE_HEADER =(byte) 0XAA;
	public static final byte PACKAGE_END = (byte) 0xBB;
	public static final byte CMD_OPEN_LOCK = 0X20;
	public static final byte CMD_CLOSE_LOCK = 0X21;
	
	
	private Socket socket;
	private InputStream in = null;  
	private OutputStream  out = null; 
	
	public static String bin2hex(byte[] bs) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer("");
        //byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0xf0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
  
    }	
	
	private int sendData(byte[] msg){ 
        if (socket.isConnected()) {  
            if (!socket.isOutputShutdown()) {  
                 
                try {
					out.write(msg);
					return SUCCESS;
				} catch (IOException e) {
					return FAIL;
		        }
            }
        }  
        return FAIL;
	}
	private byte[] receiveData()
	{
		byte[] str = new  byte[1024];
		if (socket.isConnected()) {  
            if (!socket.isInputShutdown()) { 
            	
        		int cha,i=0;
        		
        		while(true){
        			try {
						cha = in.read();
						if(cha==-1)
            				break;
            			else if (cha == 0xBB)
            			{
            				str[i++] = (byte)cha;
            				break;
            			}
            			else
            				str[i++] = (byte)cha;
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
        			
        			
        		}
				return str;
            	
            }  
        }  
        return null;
	}

	public int  OpenSocket(String host,int port)
	{
		try {
			this.socket = new Socket(host, port);
			in = socket.getInputStream();  
			out = socket.getOutputStream();
			
		} catch (UnknownHostException e) {
			return FAIL;
		} catch (IOException e) {
			return FAIL;
		} 
		return SUCCESS;
	}
	public void CloseSocket()
	{
		try {
			socket.close();
		} catch (IOException e) {
			
		}
	}
	
	public int OpenLock(byte[] addr)
	{
		byte[] midbytes = new byte[9];
		midbytes[0] = PACKAGE_HEADER;
		for(int i =0 ;i<addr.length;i++)
			midbytes[i+1]= addr[i];
		midbytes[5] = 0x01;
		midbytes[6] = CMD_OPEN_LOCK;
		midbytes[7] = (byte) (midbytes[1]^midbytes[2]^midbytes[3]^midbytes[4]^midbytes[5]^midbytes[6]);
		midbytes[8] =  PACKAGE_END;

		if(sendData(midbytes)==SUCCESS){
			midbytes = receiveData();
			if(midbytes[6]==0x00) 
				return SUCCESS;
			else if(midbytes[6]==0x01)
				return DISCONNECT;
		}
		
		return FAIL;
	}
	public int CloseLock(byte[] addr)
	{
		byte[] midbytes = new byte[9];
		midbytes[0] = PACKAGE_HEADER;
		for(int i =0 ;i<addr.length;i++)
			midbytes[i+1]= addr[i];
		midbytes[5] = 0x01;
		midbytes[6] = CMD_CLOSE_LOCK;
		midbytes[7] =  (byte) (midbytes[1]^midbytes[2]^midbytes[3]^midbytes[4]^midbytes[5]^midbytes[6]);
		midbytes[8] =  PACKAGE_END;

		if(sendData(midbytes)==SUCCESS){
			midbytes = receiveData();
			if(midbytes[6]==0x00) 
				return SUCCESS;
			else if(midbytes[6]==0x01)
				return DISCONNECT;
		}
		
		return FAIL;
	}
	
}