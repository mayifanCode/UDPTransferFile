package com.example.clientudp;

import java.util.ArrayList;

import android.util.Log;

/**
 * 负责数据包重传的线程
 * @author mayifan
 *
 */
public class ReSendThread extends Thread{

	private SendMethod sendMethod;
    private ArrayList<Packet> packetList;
	
    /**
     * 线程的构造方法
     * 线程开启后一直有效
     * @param sendMethod
     */
	public ReSendThread(SendMethod sendMethod){	
		this.sendMethod=sendMethod;
		packetList=new ArrayList<Packet>();//实例化队列
	}	
	
	/**
	 * 添加重传队列的数据包
	 * @param packet
	 */
	public void addPacket(Packet packet){
		packetList.add(packet);
	}
	/**
	 * 移除指定编号的数据包
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
	 * 判断重传列表是否为空
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
		        	Thread.sleep(100);//刚开始睡眠等待接收
		        	for(int i=0;i<packetList.size();i++){
		        		if(packetList.get(i)!=null){
			        		Packet packet=packetList.get(i);
			        		long time=System.currentTimeMillis()-packet.lastTime;//距离上次发送的时间
			        		if(time>300){
			        			sendMethod.sendMessage(packet.data, packet.destIp,packet.destPort);//超出300ms则重发
			        			Log.v("MainActivity", "重发了一个数据包，它的编号是："+packet.number);
			        			packet.reSendTimes++;
			        		}
			        		if(packet.reSendTimes>=3){     //发送超过三次就移除
			        			packetList.remove(i);
			        		}
			        		Thread.sleep(30);//每次数据包处理之间的间隔
		        		}
		        		
		        	}

		        }
 
		   }catch (Exception e) {
			    Log.v("MainActivity", "重传线程异常");
				e.printStackTrace();
	       }
	}
}
