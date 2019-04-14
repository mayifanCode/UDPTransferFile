package com.example.clientudp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.util.Log;

/**
 * ����Ӧ������߳�
 * @author mayifan
 *
 */
public class GetMethod {
	private DatagramPacket datagramPacket;//�������յ����ݱ�
	private DatagramSocket datagramSocket;//���յ�ͨ��
	
	/**
	 * ���췽��
	 * @param datagramSocket
	 */
	public GetMethod(DatagramSocket datagramSocket){
		this.datagramSocket=datagramSocket;
	}		
	
	/**
	 * ��ȡ��Ϣ�ķ���
	 * ���صõ����ֽ�����
	 * @return
	 */
	public byte[] getMessage(){		
		try{		
		byte[] buffer=new byte[1024];//��ʼ���ֽ�
		datagramPacket=new DatagramPacket(buffer,1024);//����һ���յİ�׼������
		Log.v("MainActivity","�ȴ��������ݱ�");
		datagramSocket.receive(datagramPacket);//�ȴ��������ݱ�
//		String str=new String(buffer).trim();//trim����ȥ������Ŀո�
		Log.v("MainActivity", "�յ����ַ���");
		return buffer;
		}catch(Exception e){
			e.printStackTrace();
			Log.v("MainActivity", "���մ���");
			return null;
		}
	}  
	

}
