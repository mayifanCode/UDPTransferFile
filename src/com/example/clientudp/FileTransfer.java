package com.example.clientudp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.util.Log;
/**
 * 数据包类型：三次握手阶段是类型0；文件传输信息阶段是类型1；文件传输内容阶段是类型2，服务器到客户端应答包类型3，客户端到服务器应答包类型4
 * 文件类型：来自服务器编码，存在三次握手后的所有数据包中，用来区分不同文件
 * 
 */
public class FileTransfer {

	private SendMethod sendMethod;//发送方法的引用
	private GetMethod getMethod;//接收方法的引用
    private int fileNumber ;//文件在服务端的序列号
    private byte[] fileContent= new byte[2];//包中的有用内容
    private ReSendThread reSendThread;//重传线程

    
	/**
	 * 构造方法
	 * @param sendMethod
	 * @param getMethod
	 */
	public FileTransfer(SendMethod sendMethod, GetMethod getMethod){
		this.sendMethod=sendMethod;
		this.getMethod=getMethod;
		this.fileNumber=0;
	}
	/**
	 * 无参构造
	 */
	public FileTransfer(){		
	}
	
//客户端向服务器传文件+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * 这是一个总的对外提供的方法，调用它可以把文件传输到服务器
	 */
	public void sendFileToServer(FileMessage fileMessage){
		
		
		//拿到文件的数据
		ArrayList<Packet> packetList=getPacketList(fileMessage);
		
		//开启数据包重传线程
		reSendThread=new ReSendThread(sendMethod);
		reSendThread.start();
		
		//三次握手
		threeHandShake(fileMessage);
		
		//传文件信息
		sendFileMessage(fileMessage);
		
		//传文件内容
		sendFileContent(packetList,fileMessage.getDestIp(),fileMessage.getDestPort());
		
		Log.v("MainActivity", "文件传输完毕");
	}
	
	/**
	 * 对外接口，调用它从服务器获取指定文件
	 */
    public void getFileFromServer(FileMessage fileMessage){
		 //三次握手
    	 threeHandShake(fileMessage);
    	//从服务器获取文件信息
    	 getFileMessage(fileMessage);
    	//接收文件内容
   	     byte[] data=getFileContent(fileMessage);
   	     Log.v("MainActivity", "文件："+new String(data));
   	     Log.v("MainActivity", "文件接收完毕完毕");
	}
    
//传文件到服务器阶段的代码++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
       
	/**
	 * 从服务器获取指定信息的文件，存到本地
	 */
	
      
    /**
     * 三次握手,客户端和服务器建立连接
     */
    public void threeHandShake(FileMessage fileMessage){ 
    	try{
	    	Log.v("MainActivity", "开始一次握手");
	    	//一次握手		    	
	        byte[] connect=getConnectString(fileMessage.getSerialNumber(),0);//获取第一次握手的字节数组
		    sendMethod.sendMessage(connect, fileMessage.getDestIp(), fileMessage.getDestPort());//发10,第一次握手
		    reSendThread.addPacket(new Packet(0, connect, fileMessage.getDestIp(), fileMessage.getDestPort()));//把它放到重发的线程中
		    
		    Log.v("MainActivity", "开始二次握手");	    
		    //二次握手
		    byte[] data=getMethod.getMessage();//获取得到的字符串
	        reSendThread.removePacket(0);//得到了应答，在重传队列中移除数据包
		    updateUsefulNumber(data);//更新两个部分的数据
	        
		    int second=fileContent[0]+1;//读到服务器的序列号并加一
		    int first=fileContent[1];//服务器的应答号
	
		    Log.v("MainActivity", "first:"+first+"");
		    Log.v("MainActivity", "second:"+second+"");
		    Log.v("MainActivity", "fileContent[1]:"+fileContent[1]);
		    if(fileContent[1]!=fileMessage.getSerialNumber()+1){
		    	throw new Exception("没有收到自身序列号加一");//如果没有收到正确的应答，则抛出异常
		    }
	
		    Log.v("MainActivity", "开始三次握手");	    
		    //三次握手
	        connect=getConnectString(first,second);//获取第三次握手的字符串    
		    sendMethod.sendMessage(connect,fileMessage.getDestIp(),fileMessage.getDestPort());//发送应答号给服务器，表示已经收到了服务器的序列号 
		}catch(Exception e){
			Log.v("MainActivity","三次握手出现了异常");
			e.getMessage();
		}
    }
	
    /**
     * 传输文件信息给服务器
     */
    public void sendFileMessage(FileMessage fileMessage){
    	try{
           Log.v("MainActivity", "开始传输文件信息给服务器");
           byte[] fileMessagePacket=getFileMessageString(fileMessage);//获取文件信息
           Log.v("MainActivity", "开始发送message");
           sendMethod.sendMessage(fileMessagePacket,fileMessage.getDestIp(), fileMessage.getDestPort());//发送文件信息包裹
           reSendThread.addPacket(new Packet(0, fileMessagePacket, fileMessage.getDestIp(), fileMessage.getDestPort()));//存入重传线程
           getMethod.getMessage();//获取得到的字符串
           reSendThread.removePacket(0);//在重传队列中移除数据包
   	       Log.v("MainActivity", "得到了应答包，停止重发");
	    }catch(Exception e){
	    	Log.v("MainActivity", "传输文件信息出现了异常");
	    	e.getMessage();
	    }
    }
    
    /**
     * 传输文件具体内容给服务器
     * @param fileMessage
     */
    public void sendFileContent(ArrayList<Packet> packetList,String destIp,int destPort){   
    	    Log.v("MainActivity","开始传输文件内容到服务器");
    	    reSendThread.removePacket(0);//移除所有在重发列表中的编号为0的数据包（0号数据包用于连接和传文件信息）
    	    new MessageGetThread(getMethod,sendMethod, reSendThread,destIp,destPort,fileNumber).start();//开启一个接收应答包的线程,接收到就把数据包从重发队列中移除
    	try{
            for(int i=0;i<packetList.size();i++){
            	Packet packet=packetList.get(i);
            	sendMethod.sendMessage(packet.data, destIp, destPort);//发送数据包到服务器
            	reSendThread.addPacket(packet);//把这个数据包添加到重发线程
            }
            
            
    	}catch(Exception e){
			Log.v("MainActivity", "传输文件内容出现了异常");
			e.getMessage();
		}
    }
    
    /**
     * 通过文件路径获取文件字节数组并拆包，生成若干数据包对象
     */
    public ArrayList<Packet> getPacketList(FileMessage fileMessage){
    	ArrayList<Packet> byteList=new ArrayList<Packet>();
    	Log.v("MainActivity","到达");
    	try{   	
//    		Log.v("MainActivity", fileMessage.getPath());
//	    	//从流中获取文件总字节
//	    	FileInputStream fis=new FileInputStream(new File(fileMessage.getPath()+".jpg"));
//	    	Log.v("MainActivity","到达1");
//	    	ByteArrayOutputStream baos=new ByteArrayOutputStream();
//	    	byte[] buffer=new byte[1024];
//	    	int len=0;
//	    	while((len=fis.read())!=-1){
//	    	   baos.write(buffer, 0, len);
//	    	}
//	    	byte[] data=baos.toByteArray();
//	    	baos.close();
//	    	fis.close();
    		Random random=new Random();//造一个字节数组
    		byte[] data=new byte[65536];
    		for(int i=0;i<65536;i++){
    			data[i]=(byte)random.nextInt(256);
    		}
		
	    	Log.v("MainActivity","到达2");
	        int fileByteSum=data.length;//字节数组总长度
	        fileMessage.setFileByteSum(fileByteSum);//设置文件字节总数
	    	int packetNumber=fileByteSum/1014+1;//得到总包数
	    	fileMessage.setPacketNumber(packetNumber);//设置文件的数据包总数
	    	int addZeroNumber=packetNumber*1014-fileByteSum;
	    	fileMessage.setAddZeroNumber(addZeroNumber);//设置补零数
	    	//获得1014大小的字节数组，再封装得到1024大小的字节数组，最后得到数据包对象，存入数据包列表
	    	Log.v("MainActivity","到达3");
	    	for(int i=0;i<packetNumber;i++){
	    		if(i!=packetNumber-1){
		    		byte[] buf=new byte[1014];
		    		System.arraycopy(data, i*1014, buf, 0, 1014);//得到一个1014大小的字节数组
		    		byte[] buf2=getPacket(i+1, buf);//封装为1024大小的字节数组
		    		Packet packet=new Packet(i+1, buf2, fileMessage.getDestIp(), fileMessage.getDestPort());//得到数据包对象
		    		byteList.add(packet);
	    		}else{
	    			byte[] buf=new byte[1014];
	        		System.arraycopy(data, i*1014, buf, 0, 1014-addZeroNumber);//得到一个1014大小的字节数组
	        		for(int j= 1014-addZeroNumber;j<1014;j++){
	        			buf[j]=0;
	        		}
	        		byte[] buf2=getPacket(i+1, buf);//封装为1024大小的字节数组
	        		Packet packet=new Packet(i+1, buf2, fileMessage.getDestIp(), fileMessage.getDestPort());//得到数据包对象
	        		byteList.add(packet);
	    		}
	    	}	    	
		}catch(Exception e){
			Log.v("MainActivity","文件数据读取异常");
			e.getMessage();
		}
    	return byteList;
    }
        
    /**
     * 生成文件内容的传输包表示的字节数组
     * @param number
     * @param buffer
     * @return
     */
    public byte[] getPacket(int number,byte[] data){
    	
        byte buffer[] =new byte[1024];//定义一个空的字节数组    
        
        //文件头￥
    	buffer[0]='$';
    	
    	//类型2
    	buffer[1]=2;
        
        //包的编号
    	byte[] fourByte=Tools.intTo4Byte(number);//把编号转化为4个字节
        for(int i=2;i<6;i++){
        	buffer[i]=fourByte[i-2];
        }
        
        //预留字节
    	buffer[6]=(byte) fileNumber;//预留1,存放文件在服务器端的号码
    	buffer[7]=0;//预留的剩余两个字节
    	buffer[8]=0;
    	
    	//文件数据内容
    	System.arraycopy(data, 0, buffer, 10, 1014);    	
    	
    	//结尾部分，一个字节  
    	buffer[1023]='@'; 
    	return buffer;
    }
    
    
    /**
     * 生成文件信息的字节数组
     * @param fileMessage
     * @return
     */
    public byte[] getFileMessageString(FileMessage fileMessage)throws Exception{
    	
        byte buffer[] =new byte[1024];//定义一个空的字节数组    
        
        //文件头￥
    	buffer[0]='$';
    	
    	//类型1
    	buffer[1]=1;
        
        //包的编号
        for(int i=2;i<6;i++){
        	buffer[i]=0;
        }
        
        //预留字节
    	buffer[6]=(byte) fileNumber;//预留1,存放文件在服务器端的号码
    	buffer[7]=0;//预留的剩余两个字节
    	buffer[8]=0;
    	//无效的内容信息
    	for(int i=10;i<1010-fileMessage.getName().length();i++){    //内容1
    		buffer[i]=0;
    	}
    	//文件名称 ,长度动态	
    	byte[] getByte=Tools.get2Byte(fileMessage.getName());//把文件名称转为字节数组
    	for(int i=1010-getByte.length;i<1010;i++){    //为文件名称部分赋值
    		buffer[i]=getByte[i-1010+getByte.length];
    	}
    	buffer[1010]=(byte)(fileMessage.getName().length()*2);//文件名称占用的字节数
    	
    	//文件包数，4个字节
    	byte[] fourByte=Tools.intTo4Byte(fileMessage.getPacketNumber());//把总字节数转为5个字节
        for(int i=1011;i<1015;i++){
        	buffer[i]=fourByte[i-1011];//逐位赋值
        }
        //总字节数,6个字节
    	byte[] fiveByte=Tools.intTo6Byte(fileMessage.getFileByteSum());//把总字节数转为5个字节
        for(int i=1015;i<1021;i++){
        	buffer[i]=fiveByte[i-1015];//逐位赋值
        }
        //补零数（表示把多少个字节为了0）,两个字节
        byte[] get2Byte=Tools.intTo2Byte(fileMessage.getAddZeroNumber());       	
        buffer[1021]=get2Byte[0];
    	buffer[1022]=get2Byte[1];   	
    	//结尾部分，一个字节  
    	buffer[1023]='@'; 
    	return buffer;
    	
    }
       
    
    /**
     * 生成包裹对应的字节数组
     * 输入序列号和应答号
     * @return
     */
	public byte[] getConnectString(int first,int second){		
        byte buffer[] =new byte[1024];//定义一个空的字节数组        	
    	buffer[0]='$';//文件头￥
    	buffer[1]=0;//类型0

        for(int i=2;i<6;i++){   //包编号
        	buffer[i]=0;
        }
    	buffer[6]=(byte)fileNumber;//预留1
    	buffer[7]=0;//预留的剩余两个字节
    	buffer[8]=0;
    	for(int i=10;i<1021;i++){//内容1
    		buffer[i]=0;
    	}
    	buffer[1021]=(byte)first;//自身的序列号1，应答号部分为0;第一个是seq位，第二个位是ack位   
    	buffer[1022]=(byte)second;; 
    	buffer[1023]='@'; //结尾部分，一个字节  
    	return buffer;
	}
	
	/**
	 * 从字节数组中获取在服务端的文件编号和文件的有用内容
	 * @return
	 */
	public void updateUsefulNumber(byte[] data){
		fileNumber=data[6];
		fileContent[0]=data[1021];//seq
		Log.v("MainActivity", "seq"+fileContent[0]);
		fileContent[1]=data[1022];//ack
		Log.v("MainActivity","ack"+ fileContent[1]);
	}

//从服务器获取文件到客户端++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	
    /**
     * 从服务器接收文件信息
     */
    public void getFileMessage(FileMessage fileMessage){
    	try{
           Log.v("MainActivity", "开始向服务器接收文件信息");
           byte[] data=getMethod.getMessage();//读文件信息的数据包
           sendMethod.sendMessage(data, fileMessage.getDestIp(), fileMessage.getDestPort());//回一个包表示收到
           reSendThread.addPacket(new Packet(0, data, fileMessage.getDestIp(), fileMessage.getDestPort()));//把这个回应包放到重传线程中
           updateFileMessage(fileMessage,data);//读取文件信息数据包的内容，更新信息
   	       Log.v("MainActivity", "得到了应答包，停止重发");
	    }catch(Exception e){
	    	Log.v("MainActivity", "接收文件信息出现了异常");
	    	e.getMessage();
	    }
    }
	
    /**
     * 更新文件信息
     * @param fileMessage
     * @param data
     * @throws Exception
     */
     public void updateFileMessage(FileMessage fileMessage,byte[] data)throws Exception{
    	 //文件名，动态长度
    	 int nameByteLength=data[1010];//名字占的字节数
    	 byte[] names=new byte[nameByteLength];
    	 for(int i=1010-nameByteLength;i<1010;i++){    //为文件名称部分赋值
     		names[i-1010+nameByteLength]=data[i];
     	}
    	String name=Tools.getString(names);
    	fileMessage.setName(name); 
    	//文件包数，4个字节
    	byte[] packetNum=new byte[4];
    	for(int i=1011;i<1015;i++){
        	packetNum[i-1011]=data[i];
        }
        int packetSum=Tools.fourByteToInt(packetNum);
        fileMessage.setPacketNumber(packetSum);
        //总字节数,6个字节
        byte[] sixByte=new byte[6];
        for(int i=1015;i<1021;i++){
        	sixByte[i-1015]=data[i];//逐位赋值
        }
        int byteSum=Tools.sixByteToInt(sixByte);
        fileMessage.setFileByteSum(byteSum);
        //补0数
        byte[] addZeroNumber=new byte[2];
        addZeroNumber[0]=data[1021];
        addZeroNumber[1]=data[1022];
    	int addZeroNum=Tools.twoByteToInt(addZeroNumber);
    	fileMessage.setAddZeroNumber(addZeroNum);
    	
     }
	
	
	
	
	/**
	 * 从服务器获取数据包，数据包是具体内容的拆分,得到文件的字节数组
	 * @param fileMessage
	 */
	public byte[] getFileContent(FileMessage fileMessage){
	    Log.v("MainActivity","开始从服务器接收文件内容");
	    HashMap<Integer, byte[]> packetMap=new HashMap<Integer, byte[]>();//建一个哈希表存放数据包
	    int length=fileMessage.getPacketNumber();//数据包数
	    byte[] buffer2=null;//存放接收到的所有字节
		try{
		    reSendThread.removePacket(0);//移除所有在重发列表中的编号为0的数据包（0号数据包用于连接和传文件信息）
		    //在循环中接收数据包
	        while(true){
	        	byte[] data=getMethod.getMessage();
	        	byte[] fourByte=new byte[4];
	        	fourByte[0]=data[2];
	        	fourByte[1]=data[3];
	        	fourByte[2]=data[4];
	        	fourByte[3]=data[5];
	        	int num=Tools.fourByteToInt(fourByte);//获取int型编号
	        	if(packetMap.get(num)==null){   //map中不存在
	        		packetMap.put(num,data );//存放数据
	        		sendMethod.sendMessage(data, fileMessage.getDestIp(), fileMessage.getDestPort());//回一个包表示收到了该编号的数据包
	        		reSendThread.addPacket(new Packet(num, data, fileMessage.getDestIp(), fileMessage.getDestPort()));//把回应包放到重传线程中
	        	}
	        	//校验是否收齐了所有包
	        	int add=0;
	        	for(int i=1;i<length+1;i++){
	        		if(packetMap.get(i)!=null){
	        			add++;
	        		}
	        	}
	        	if(add==length){
	        		break;
	        	}
	        }
	        //把所有包整合到接收字节数组
	        byte[] buffer1=new byte[1024*fileMessage.getPacketNumber()];//包括补0的长度
		    for(int i=0;i<fileMessage.getPacketNumber();i++){       
		    	System.arraycopy(packetMap.get(i), 0, buffer1, i*1024, 1024);
		    }
		    buffer2=new byte[fileMessage.getFileByteSum()];//去掉补0部分的长度
		    System.arraycopy(buffer1, 0, buffer2, 0, fileMessage.getFileByteSum());		    
	    }catch(Exception e){
	    	Log.v("MainActivity","文件内容接收异常");
	    	e.getMessage();
	    }
	    return buffer2;
	}
	
	
	
}

/**
 * 数据包类
 * @author mayifan
 *
 */
class Packet{
	
	public int number;//包的编号
	public int reSendTimes;//重传次数
	public byte[] data;//存放的数据
	public long lastTime;//最后一次发送的时间
	public String destIp;//目标IP
	public int destPort;//目标端口号
		
	public Packet(int number,byte[] data,String destIp,int destPort){
        this.number=number;
        this.data=data;
        this.destIp=destIp;
        this.destPort=destPort;
        this.reSendTimes=0;
        this.lastTime=System.currentTimeMillis();
	}
	
}
	





