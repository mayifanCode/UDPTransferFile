package com.example.clientudp;

import java.util.ArrayList;

import android.util.Log;

/**
 * �������ݰ��ش����߳�
 * @author mayifan
 *
 */
public class ReSendThread extends Thread{

	private SendMethod sendMethod;
    private ArrayList<Packet> packetList;
	
    /**
     * �̵߳Ĺ��췽��
     * �߳̿�����һֱ��Ч
     * @param sendMethod
     */
	public ReSendThread(SendMethod sendMethod){	
		this.sendMethod=sendMethod;
		packetList=new ArrayList<Packet>();//ʵ��������
	}	
	
	/**
	 * ����ش����е����ݰ�
	 * @param packet
	 */
	public void addPacket(Packet packet){
		packetList.add(packet);
	}
	/**
	 * �Ƴ�ָ����ŵ����ݰ�
	 * @param number
	 */
	public void removePacket(int number){
		for(int i=0;i<packetList.size();i++){
			if(packetList.get(i).number==number){
				packetList.remove(i);
			}
		}
	}

	/**
	 * �ж��ش��б��Ƿ�Ϊ��
	 * @return
	 */
	public boolean isEmpty(){
		if(packetList.size()==0){
			return true;
		}else{
			return false;
		}
	}
	
	
	
	public void run(){
	       try{		        
		        while(true){
		        	Thread.sleep(100);//�տ�ʼ˯�ߵȴ�����
		        	for(int i=0;i<packetList.size();i++){
		        		if(packetList.get(i)!=null){
			        		Packet packet=packetList.get(i);
			        		long time=System.currentTimeMillis()-packet.lastTime;//�����ϴη��͵�ʱ��
			        		if(time>300){
			        			sendMethod.sendMessage(packet.data, packet.destIp,packet.destPort);//����300ms���ط�
			        			Log.v("MainActivity", "�ط���һ�����ݰ������ı���ǣ�"+packet.number);
			        			packet.reSendTimes++;
			        		}
			        		if(packet.reSendTimes>=3){     //���ͳ������ξ��Ƴ�
			        			packetList.remove(i);
			        		}
			        		Thread.sleep(30);//ÿ�����ݰ�����֮��ļ��
		        		}
		        		
		        	}

		        }
 
		   }catch (Exception e) {
			    Log.v("MainActivity", "�ش��߳��쳣");
				e.printStackTrace();
	       }
	}
}
