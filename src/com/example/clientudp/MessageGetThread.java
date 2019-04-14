package com.example.clientudp;

import android.util.Log;

public class MessageGetThread extends Thread {

	private GetMethod getMethod;
	private ReSendThread reSendThread;
	private SendMethod sendMethod;
	private String destIp;
	private int destPort;
	private int fileNumber;
	
	public MessageGetThread(GetMethod getMethod,SendMethod sendMethod,ReSendThread reSendThread,String destIp,int destPort,int fileNumber){
		this.getMethod=getMethod;
		this.reSendThread=reSendThread;
		this.sendMethod=sendMethod;
		this.destIp=destIp;
		this.destPort=destPort;
		this.fileNumber=fileNumber;
	}
	
	
	public void run(){
		while(true){
			try{
			byte[] data=getMethod.getMessage();//接收消息的阻塞方法
			if(data[1]==3){     //类型3表示这个是文件内容的应答包
				byte[] fourByte=new byte[4];
				fourByte[0]=data[2];
				fourByte[1]=data[3];
				fourByte[2]=data[4];
				fourByte[3]=data[5];
				int number=Tools.fourByteToInt(fourByte);
				reSendThread.removePacket(number);
				if(reSendThread.isEmpty()==true){  //包都被收到就发一个类型为4的应答包
					byte[] buffer=new byte[1024];
					buffer[1]=4;//类型为4，表示所有文件接受完毕
					buffer[6]=(byte)fileNumber;//文件编号
					sendMethod.sendMessage(buffer, destIp, destPort);//发一个应答包
					Log.v("MainActivity", "所有数据包接收完毕");	
				}
				Log.v("MainActivity", "数据包："+number+"收到应答");				
			}
			}catch(Exception e){
				Log.v("MainActivity", "接收应答的线程出现了异常");
				e.getMessage();
			}
		}
	}
}




