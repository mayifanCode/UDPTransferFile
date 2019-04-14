package com.example.clientudp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.util.Log;

public class SendMethod {


	private DatagramPacket datagramPacket;//�������ݱ�
    private InetAddress destInetAddress;//Ŀ���ַ,ip��Ҫ��װΪ�����ܴ����
    private DatagramSocket datagramSocket;//����ͨ����ȱʡ��
    
	public SendMethod(DatagramSocket datagramSocket){
		this.datagramSocket=datagramSocket;
	}
	
	public SendMethod(){
		
	}
	
	
    /**
     * ����Ϣ�ķ���
     */
    public void sendMessage(byte[] buffer,String ip,int port)throws Exception{ 


         Log.v("MainActivity", "ip��"+ip);
         Log.v("MainActivity", "�˿ںţ�"+String.valueOf(port));
         
         destInetAddress=InetAddress.getByName(ip);//��ip��װΪ��ַ
         try {       	 
			datagramPacket=new DatagramPacket(buffer,buffer.length,destInetAddress,port);//�������ݱ�
        	datagramSocket.send(datagramPacket); //�������ݱ�        	 			
			Log.v("MainActivity", "�ɹ����͵���Ϣ");	
		} catch (Exception e) {	
			Log.v("MainActivity", "error");
			e.printStackTrace();
		}        
    }

	
	
}
