package com.example.clientudp;

import android.util.Log;
import android.widget.EditText;

public class MessageSendThread extends Thread{
	private EditText textIp;
	private EditText textPort;
	private EditText textContent;
	private SendMethod sendMethod;	
	
	/**
	 * 构造方法
	 * @param sendMethod
	 * @param textIp
	 * @param textPort
	 * @param textContent
	 */
	public MessageSendThread(SendMethod sendMethod,EditText textIp,EditText textPort,EditText textContent){
        this.sendMethod=sendMethod;
		this.textIp=textIp;
		this.textPort=textPort;
		this.textContent=textContent;
	}
	

	public void run(){
				String ip=textIp.getText().toString();//获取内容
				Log.v("ip", ip);
				String portString=textPort.getText().toString();//获取内容
				Log.v("port", portString);
				int port=Integer.parseInt(portString);//获取数字port
				String content=textContent.getText().toString();//获取内容
				Log.v("content", content);
				
				try {
					sendMethod.sendMessage(content.getBytes(), ip, port);//发送信息
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}						
}









