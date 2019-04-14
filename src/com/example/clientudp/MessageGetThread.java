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
			byte[] data=getMethod.getMessage();//������Ϣ����������
			if(data[1]==3){     //����3��ʾ������ļ����ݵ�Ӧ���
				byte[] fourByte=new byte[4];
				fourByte[0]=data[2];
				fourByte[1]=data[3];
				fourByte[2]=data[4];
				fourByte[3]=data[5];
				int number=Tools.fourByteToInt(fourByte);
				reSendThread.removePacket(number);
				if(reSendThread.isEmpty()==true){  //�������յ��ͷ�һ������Ϊ4��Ӧ���
					byte[] buffer=new byte[1024];
					buffer[1]=4;//����Ϊ4����ʾ�����ļ��������
					buffer[6]=(byte)fileNumber;//�ļ����
					sendMethod.sendMessage(buffer, destIp, destPort);//��һ��Ӧ���
					Log.v("MainActivity", "�������ݰ��������");	
				}
				Log.v("MainActivity", "���ݰ���"+number+"�յ�Ӧ��");				
			}
			}catch(Exception e){
				Log.v("MainActivity", "����Ӧ����̳߳������쳣");
				e.getMessage();
			}
		}
	}
}




