package com.example.clientudp;

import java.io.File;


public class FileMessage {

	private String name;//文件名称，20个字节（上限10个字符）
	private int packetNumber;//文件被拆的包数，4个字节
	private int fileByteSum;//文件字节总数，5个字节
	private int addZeroNumber;//文件最后一个包的补0数，1个字节
	private String path; //文件路径
	private String destIp;//文件目标ip
	private int destPort;//文件目标端口号
	private int serialNumber;//文件自身序列号
	private int numberInServer;//文件在服务器端的编号

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
