package com.example.clientudp;

import java.io.File;


public class FileMessage {

	private String name;//�ļ����ƣ�20���ֽڣ�����10���ַ���
	private int packetNumber;//�ļ�����İ�����4���ֽ�
	private int fileByteSum;//�ļ��ֽ�������5���ֽ�
	private int addZeroNumber;//�ļ����һ�����Ĳ�0����1���ֽ�
	private String path; //�ļ�·��
	private String destIp;//�ļ�Ŀ��ip
	private int destPort;//�ļ�Ŀ��˿ں�
	private int serialNumber;//�ļ��������к�
	private int numberInServer;//�ļ��ڷ������˵ı��

	public FileMessage(String name,int packetNumber,int fileByteSum,int addZeroNumber,String path,String destIp,int destPort){
		this.name=name;
		this.packetNumber=packetNumber;
		this.fileByteSum=fileByteSum;
		this.addZeroNumber=addZeroNumber;
		this.path=path;
		this.destIp=destIp;
		this.destPort=destPort;
	}
	
	public FileMessage(){
		
	}
	
	
	
	public String getName(){
		return this.name;
	}
	public int getPacketNumber(){
		return this.packetNumber;
	}
	public int getFileByteSum(){
		return this.fileByteSum;
	}
	public int getAddZeroNumber(){
		return this.addZeroNumber;
	}
	public String getPath(){
		return this.path;
	}
	public String getDestIp(){
		return this.destIp;
	}
	public int getDestPort(){
		return this.destPort;
	}
	public int getSerialNumber(){
		return this.serialNumber;
	}
	public int numberInServer(){
		return this.numberInServer;
	}
	
	
	public void setName(String name){
		this.name=name;
	}
    public void setPacketNumber(int packetNumber){
    	this.packetNumber=packetNumber;
    }
	public void setFileByteSum(int fileByteSum){
		this.fileByteSum=fileByteSum;
	}
	public void setAddZeroNumber(int addZeroNumber){
		this.addZeroNumber=addZeroNumber;
	}
	public void setPath(String path){
		this.path=path;
	}
	public void setDestIp(String destIp){
		this.destIp=destIp;
	}
	public void setDestPort(int destPort){
		this.destPort=destPort;
	}
	public void setSerialNumber(int serialNumber){
		this.serialNumber=serialNumber;
	}
	public void setNumberInServer(int numberInServer){
		this.numberInServer=numberInServer;
	}
	
	
}
