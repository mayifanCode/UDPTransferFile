package com.example.clientudp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.util.Log;

/**
 * 接收应答包的线程
 * @author mayifan
 *
 */
public class GetMethod {
	private DatagramPacket datagramPacket;//用来接收的数据报
	private DatagramSocket datagramSocket;//接收的通道
	
	/**
	 * 构造方法
	 * @param datagramSocket
	 */
	public GetMethod(DatagramSocket datagramSocket){
		this.datagramSocket=datagramSocket;
	}		
	
	/**
	 * 获取消息的方法
	 * 返回得到的字节数组
	 * @return
	 */
	public byte[] getMessage(){		
		try{		
		byte[] buffer=new byte[1024];//初始化字节
		datagramPacket=new DatagramPacket(buffer,1024);//创建一个空的包准备接收
		Log.v("MainActivity","等待接收数据报");
		datagramSocket.receive(datagramPacket);//等待接收数据报
//		String str=new String(buffer).trim();//trim可以去掉多余的空格
		Log.v("MainActivity", "收到了字符串");
		return buffer;
		}catch(Exception e){
			e.printStackTrace();
			Log.v("MainActivity", "接收错误");
			return null;
		}
	}  
	

}
