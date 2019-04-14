package com.example.clientudp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.util.Log;

public class SendMethod {


	private DatagramPacket datagramPacket;//发送数据报
    private InetAddress destInetAddress;//目标地址,ip需要包装为它才能传入包
    private DatagramSocket datagramSocket;//发送通道（缺省）
    
	public SendMethod(DatagramSocket datagramSocket){
		this.datagramSocket=datagramSocket;
	}
	
	public SendMethod(){
		
	}
	
	
    /**
     * 发信息的方法
     */
    public void sendMessage(byte[] buffer,String ip,int port)throws Exception{ 


         Log.v("MainActivity", "ip："+ip);
         Log.v("MainActivity", "端口号："+String.valueOf(port));
         
         destInetAddress=InetAddress.getByName(ip);//把ip包装为地址
         try {       	 
			datagramPacket=new DatagramPacket(buffer,buffer.length,destInetAddress,port);//创建数据报
        	datagramSocket.send(datagramPacket); //发送数据报        	 			
			Log.v("MainActivity", "成功发送的消息");	
		} catch (Exception e) {	
			Log.v("MainActivity", "error");
			e.printStackTrace();
		}        
    }

	
	
}
