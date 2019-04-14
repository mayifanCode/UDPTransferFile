package com.example.clientudp;

import android.util.Log;
import android.widget.EditText;

public class MessageSendThread extends Thread{
	private EditText textIp;
	private EditText textPort;
	private EditText textContent;
	private SendMethod sendMethod;	
	
	/**
	 * ���췽��
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
				String ip=textIp.getText().toString();//��ȡ����
				Log.v("ip", ip);
				String portString=textPort.getText().toString();//��ȡ����
				Log.v("port", portString);
				int port=Integer.parseInt(portString);//��ȡ����port
				String content=textContent.getText().toString();//��ȡ����
				Log.v("content", content);
				
				try {
					sendMethod.sendMessage(content.getBytes(), ip, port);//������Ϣ
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}						
}









