package com.example.clientudp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.util.Log;
/**
 * ���ݰ����ͣ��������ֽ׶�������0���ļ�������Ϣ�׶�������1���ļ��������ݽ׶�������2�����������ͻ���Ӧ�������3���ͻ��˵�������Ӧ�������4
 * �ļ����ͣ����Է��������룬�����������ֺ���������ݰ��У��������ֲ�ͬ�ļ�
 * 
 */
public class FileTransfer {

	private SendMethod sendMethod;//���ͷ���������
	private GetMethod getMethod;//���շ���������
    private int fileNumber ;//�ļ��ڷ���˵����к�
    private byte[] fileContent= new byte[2];//���е���������
    private ReSendThread reSendThread;//�ش��߳�

    
	/**
	 * ���췽��
	 * @param sendMethod
	 * @param getMethod
	 */
	public FileTransfer(SendMethod sendMethod, GetMethod getMethod){
		this.sendMethod=sendMethod;
		this.getMethod=getMethod;
		this.fileNumber=0;
	}
	/**
	 * �޲ι���
	 */
	public FileTransfer(){		
	}
	
//�ͻ�������������ļ�+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * ����һ���ܵĶ����ṩ�ķ��������������԰��ļ����䵽������
	 */
	public void sendFileToServer(FileMessage fileMessage){
		
		
		//�õ��ļ�������
		ArrayList<Packet> packetList=getPacketList(fileMessage);
		
		//�������ݰ��ش��߳�
		reSendThread=new ReSendThread(sendMethod);
		reSendThread.start();
		
		//��������
		threeHandShake(fileMessage);
		
		//���ļ���Ϣ
		sendFileMessage(fileMessage);
		
		//���ļ�����
		sendFileContent(packetList,fileMessage.getDestIp(),fileMessage.getDestPort());
		
		Log.v("MainActivity", "�ļ��������");
	}
	
	/**
	 * ����ӿڣ��������ӷ�������ȡָ���ļ�
	 */
    public void getFileFromServer(FileMessage fileMessage){
		 //��������
    	 threeHandShake(fileMessage);
    	//�ӷ�������ȡ�ļ���Ϣ
    	 getFileMessage(fileMessage);
    	//�����ļ�����
   	     byte[] data=getFileContent(fileMessage);
   	     Log.v("MainActivity", "�ļ���"+new String(data));
   	     Log.v("MainActivity", "�ļ�����������");
	}
    
//���ļ����������׶εĴ���++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
       
	/**
	 * �ӷ�������ȡָ����Ϣ���ļ����浽����
	 */
	
      
    /**
     * ��������,�ͻ��˺ͷ�������������
     */
    public void threeHandShake(FileMessage fileMessage){ 
    	try{
	    	Log.v("MainActivity", "��ʼһ������");
	    	//һ������		    	
	        byte[] connect=getConnectString(fileMessage.getSerialNumber(),0);//��ȡ��һ�����ֵ��ֽ�����
		    sendMethod.sendMessage(connect, fileMessage.getDestIp(), fileMessage.getDestPort());//��10,��һ������
		    reSendThread.addPacket(new Packet(0, connect, fileMessage.getDestIp(), fileMessage.getDestPort()));//�����ŵ��ط����߳���
		    
		    Log.v("MainActivity", "��ʼ��������");	    
		    //��������
		    byte[] data=getMethod.getMessage();//��ȡ�õ����ַ���
	        reSendThread.removePacket(0);//�õ���Ӧ�����ش��������Ƴ����ݰ�
		    updateUsefulNumber(data);//�����������ֵ�����
	        
		    int second=fileContent[0]+1;//���������������кŲ���һ
		    int first=fileContent[1];//��������Ӧ���
	
		    Log.v("MainActivity", "first:"+first+"");
		    Log.v("MainActivity", "second:"+second+"");
		    Log.v("MainActivity", "fileContent[1]:"+fileContent[1]);
		    if(fileContent[1]!=fileMessage.getSerialNumber()+1){
		    	throw new Exception("û���յ��������кż�һ");//���û���յ���ȷ��Ӧ�����׳��쳣
		    }
	
		    Log.v("MainActivity", "��ʼ��������");	    
		    //��������
	        connect=getConnectString(first,second);//��ȡ���������ֵ��ַ���    
		    sendMethod.sendMessage(connect,fileMessage.getDestIp(),fileMessage.getDestPort());//����Ӧ��Ÿ�����������ʾ�Ѿ��յ��˷����������к� 
		}catch(Exception e){
			Log.v("MainActivity","�������ֳ������쳣");
			e.getMessage();
		}
    }
	
    /**
     * �����ļ���Ϣ��������
     */
    public void sendFileMessage(FileMessage fileMessage){
    	try{
           Log.v("MainActivity", "��ʼ�����ļ���Ϣ��������");
           byte[] fileMessagePacket=getFileMessageString(fileMessage);//��ȡ�ļ���Ϣ
           Log.v("MainActivity", "��ʼ����message");
           sendMethod.sendMessage(fileMessagePacket,fileMessage.getDestIp(), fileMessage.getDestPort());//�����ļ���Ϣ����
           reSendThread.addPacket(new Packet(0, fileMessagePacket, fileMessage.getDestIp(), fileMessage.getDestPort()));//�����ش��߳�
           getMethod.getMessage();//��ȡ�õ����ַ���
           reSendThread.removePacket(0);//���ش��������Ƴ����ݰ�
   	       Log.v("MainActivity", "�õ���Ӧ�����ֹͣ�ط�");
	    }catch(Exception e){
	    	Log.v("MainActivity", "�����ļ���Ϣ�������쳣");
	    	e.getMessage();
	    }
    }
    
    /**
     * �����ļ��������ݸ�������
     * @param fileMessage
     */
    public void sendFileContent(ArrayList<Packet> packetList,String destIp,int destPort){   
    	    Log.v("MainActivity","��ʼ�����ļ����ݵ�������");
    	    reSendThread.removePacket(0);//�Ƴ��������ط��б��еı��Ϊ0�����ݰ���0�����ݰ��������Ӻʹ��ļ���Ϣ��
    	    new MessageGetThread(getMethod,sendMethod, reSendThread,destIp,destPort,fileNumber).start();//����һ������Ӧ������߳�,���յ��Ͱ����ݰ����ط��������Ƴ�
    	try{
            for(int i=0;i<packetList.size();i++){
            	Packet packet=packetList.get(i);
            	sendMethod.sendMessage(packet.data, destIp, destPort);//�������ݰ���������
            	reSendThread.addPacket(packet);//��������ݰ���ӵ��ط��߳�
            }
            
            
    	}catch(Exception e){
			Log.v("MainActivity", "�����ļ����ݳ������쳣");
			e.getMessage();
		}
    }
    
    /**
     * ͨ���ļ�·����ȡ�ļ��ֽ����鲢����������������ݰ�����
     */
    public ArrayList<Packet> getPacketList(FileMessage fileMessage){
    	ArrayList<Packet> byteList=new ArrayList<Packet>();
    	Log.v("MainActivity","����");
    	try{   	
//    		Log.v("MainActivity", fileMessage.getPath());
//	    	//�����л�ȡ�ļ����ֽ�
//	    	FileInputStream fis=new FileInputStream(new File(fileMessage.getPath()+".jpg"));
//	    	Log.v("MainActivity","����1");
//	    	ByteArrayOutputStream baos=new ByteArrayOutputStream();
//	    	byte[] buffer=new byte[1024];
//	    	int len=0;
//	    	while((len=fis.read())!=-1){
//	    	   baos.write(buffer, 0, len);
//	    	}
//	    	byte[] data=baos.toByteArray();
//	    	baos.close();
//	    	fis.close();
    		Random random=new Random();//��һ���ֽ�����
    		byte[] data=new byte[65536];
    		for(int i=0;i<65536;i++){
    			data[i]=(byte)random.nextInt(256);
    		}
		
	    	Log.v("MainActivity","����2");
	        int fileByteSum=data.length;//�ֽ������ܳ���
	        fileMessage.setFileByteSum(fileByteSum);//�����ļ��ֽ�����
	    	int packetNumber=fileByteSum/1014+1;//�õ��ܰ���
	    	fileMessage.setPacketNumber(packetNumber);//�����ļ������ݰ�����
	    	int addZeroNumber=packetNumber*1014-fileByteSum;
	    	fileMessage.setAddZeroNumber(addZeroNumber);//���ò�����
	    	//���1014��С���ֽ����飬�ٷ�װ�õ�1024��С���ֽ����飬���õ����ݰ����󣬴������ݰ��б�
	    	Log.v("MainActivity","����3");
	    	for(int i=0;i<packetNumber;i++){
	    		if(i!=packetNumber-1){
		    		byte[] buf=new byte[1014];
		    		System.arraycopy(data, i*1014, buf, 0, 1014);//�õ�һ��1014��С���ֽ�����
		    		byte[] buf2=getPacket(i+1, buf);//��װΪ1024��С���ֽ�����
		    		Packet packet=new Packet(i+1, buf2, fileMessage.getDestIp(), fileMessage.getDestPort());//�õ����ݰ�����
		    		byteList.add(packet);
	    		}else{
	    			byte[] buf=new byte[1014];
	        		System.arraycopy(data, i*1014, buf, 0, 1014-addZeroNumber);//�õ�һ��1014��С���ֽ�����
	        		for(int j= 1014-addZeroNumber;j<1014;j++){
	        			buf[j]=0;
	        		}
	        		byte[] buf2=getPacket(i+1, buf);//��װΪ1024��С���ֽ�����
	        		Packet packet=new Packet(i+1, buf2, fileMessage.getDestIp(), fileMessage.getDestPort());//�õ����ݰ�����
	        		byteList.add(packet);
	    		}
	    	}	    	
		}catch(Exception e){
			Log.v("MainActivity","�ļ����ݶ�ȡ�쳣");
			e.getMessage();
		}
    	return byteList;
    }
        
    /**
     * �����ļ����ݵĴ������ʾ���ֽ�����
     * @param number
     * @param buffer
     * @return
     */
    public byte[] getPacket(int number,byte[] data){
    	
        byte buffer[] =new byte[1024];//����һ���յ��ֽ�����    
        
        //�ļ�ͷ��
    	buffer[0]='$';
    	
    	//����2
    	buffer[1]=2;
        
        //���ı��
    	byte[] fourByte=Tools.intTo4Byte(number);//�ѱ��ת��Ϊ4���ֽ�
        for(int i=2;i<6;i++){
        	buffer[i]=fourByte[i-2];
        }
        
        //Ԥ���ֽ�
    	buffer[6]=(byte) fileNumber;//Ԥ��1,����ļ��ڷ������˵ĺ���
    	buffer[7]=0;//Ԥ����ʣ�������ֽ�
    	buffer[8]=0;
    	
    	//�ļ���������
    	System.arraycopy(data, 0, buffer, 10, 1014);    	
    	
    	//��β���֣�һ���ֽ�  
    	buffer[1023]='@'; 
    	return buffer;
    }
    
    
    /**
     * �����ļ���Ϣ���ֽ�����
     * @param fileMessage
     * @return
     */
    public byte[] getFileMessageString(FileMessage fileMessage)throws Exception{
    	
        byte buffer[] =new byte[1024];//����һ���յ��ֽ�����    
        
        //�ļ�ͷ��
    	buffer[0]='$';
    	
    	//����1
    	buffer[1]=1;
        
        //���ı��
        for(int i=2;i<6;i++){
        	buffer[i]=0;
        }
        
        //Ԥ���ֽ�
    	buffer[6]=(byte) fileNumber;//Ԥ��1,����ļ��ڷ������˵ĺ���
    	buffer[7]=0;//Ԥ����ʣ�������ֽ�
    	buffer[8]=0;
    	//��Ч��������Ϣ
    	for(int i=10;i<1010-fileMessage.getName().length();i++){    //����1
    		buffer[i]=0;
    	}
    	//�ļ����� ,���ȶ�̬	
    	byte[] getByte=Tools.get2Byte(fileMessage.getName());//���ļ�����תΪ�ֽ�����
    	for(int i=1010-getByte.length;i<1010;i++){    //Ϊ�ļ����Ʋ��ָ�ֵ
    		buffer[i]=getByte[i-1010+getByte.length];
    	}
    	buffer[1010]=(byte)(fileMessage.getName().length()*2);//�ļ�����ռ�õ��ֽ���
    	
    	//�ļ�������4���ֽ�
    	byte[] fourByte=Tools.intTo4Byte(fileMessage.getPacketNumber());//�����ֽ���תΪ5���ֽ�
        for(int i=1011;i<1015;i++){
        	buffer[i]=fourByte[i-1011];//��λ��ֵ
        }
        //���ֽ���,6���ֽ�
    	byte[] fiveByte=Tools.intTo6Byte(fileMessage.getFileByteSum());//�����ֽ���תΪ5���ֽ�
        for(int i=1015;i<1021;i++){
        	buffer[i]=fiveByte[i-1015];//��λ��ֵ
        }
        //����������ʾ�Ѷ��ٸ��ֽ�Ϊ��0��,�����ֽ�
        byte[] get2Byte=Tools.intTo2Byte(fileMessage.getAddZeroNumber());       	
        buffer[1021]=get2Byte[0];
    	buffer[1022]=get2Byte[1];   	
    	//��β���֣�һ���ֽ�  
    	buffer[1023]='@'; 
    	return buffer;
    	
    }
       
    
    /**
     * ���ɰ�����Ӧ���ֽ�����
     * �������кź�Ӧ���
     * @return
     */
	public byte[] getConnectString(int first,int second){		
        byte buffer[] =new byte[1024];//����һ���յ��ֽ�����        	
    	buffer[0]='$';//�ļ�ͷ��
    	buffer[1]=0;//����0

        for(int i=2;i<6;i++){   //�����
        	buffer[i]=0;
        }
    	buffer[6]=(byte)fileNumber;//Ԥ��1
    	buffer[7]=0;//Ԥ����ʣ�������ֽ�
    	buffer[8]=0;
    	for(int i=10;i<1021;i++){//����1
    		buffer[i]=0;
    	}
    	buffer[1021]=(byte)first;//��������к�1��Ӧ��Ų���Ϊ0;��һ����seqλ���ڶ���λ��ackλ   
    	buffer[1022]=(byte)second;; 
    	buffer[1023]='@'; //��β���֣�һ���ֽ�  
    	return buffer;
	}
	
	/**
	 * ���ֽ������л�ȡ�ڷ���˵��ļ���ź��ļ�����������
	 * @return
	 */
	public void updateUsefulNumber(byte[] data){
		fileNumber=data[6];
		fileContent[0]=data[1021];//seq
		Log.v("MainActivity", "seq"+fileContent[0]);
		fileContent[1]=data[1022];//ack
		Log.v("MainActivity","ack"+ fileContent[1]);
	}

//�ӷ�������ȡ�ļ����ͻ���++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	
    /**
     * �ӷ����������ļ���Ϣ
     */
    public void getFileMessage(FileMessage fileMessage){
    	try{
           Log.v("MainActivity", "��ʼ������������ļ���Ϣ");
           byte[] data=getMethod.getMessage();//���ļ���Ϣ�����ݰ�
           sendMethod.sendMessage(data, fileMessage.getDestIp(), fileMessage.getDestPort());//��һ������ʾ�յ�
           reSendThread.addPacket(new Packet(0, data, fileMessage.getDestIp(), fileMessage.getDestPort()));//�������Ӧ���ŵ��ش��߳���
           updateFileMessage(fileMessage,data);//��ȡ�ļ���Ϣ���ݰ������ݣ�������Ϣ
   	       Log.v("MainActivity", "�õ���Ӧ�����ֹͣ�ط�");
	    }catch(Exception e){
	    	Log.v("MainActivity", "�����ļ���Ϣ�������쳣");
	    	e.getMessage();
	    }
    }
	
    /**
     * �����ļ���Ϣ
     * @param fileMessage
     * @param data
     * @throws Exception
     */
     public void updateFileMessage(FileMessage fileMessage,byte[] data)throws Exception{
    	 //�ļ�������̬����
    	 int nameByteLength=data[1010];//����ռ���ֽ���
    	 byte[] names=new byte[nameByteLength];
    	 for(int i=1010-nameByteLength;i<1010;i++){    //Ϊ�ļ����Ʋ��ָ�ֵ
     		names[i-1010+nameByteLength]=data[i];
     	}
    	String name=Tools.getString(names);
    	fileMessage.setName(name); 
    	//�ļ�������4���ֽ�
    	byte[] packetNum=new byte[4];
    	for(int i=1011;i<1015;i++){
        	packetNum[i-1011]=data[i];
        }
        int packetSum=Tools.fourByteToInt(packetNum);
        fileMessage.setPacketNumber(packetSum);
        //���ֽ���,6���ֽ�
        byte[] sixByte=new byte[6];
        for(int i=1015;i<1021;i++){
        	sixByte[i-1015]=data[i];//��λ��ֵ
        }
        int byteSum=Tools.sixByteToInt(sixByte);
        fileMessage.setFileByteSum(byteSum);
        //��0��
        byte[] addZeroNumber=new byte[2];
        addZeroNumber[0]=data[1021];
        addZeroNumber[1]=data[1022];
    	int addZeroNum=Tools.twoByteToInt(addZeroNumber);
    	fileMessage.setAddZeroNumber(addZeroNum);
    	
     }
	
	
	
	
	/**
	 * �ӷ�������ȡ���ݰ������ݰ��Ǿ������ݵĲ��,�õ��ļ����ֽ�����
	 * @param fileMessage
	 */
	public byte[] getFileContent(FileMessage fileMessage){
	    Log.v("MainActivity","��ʼ�ӷ����������ļ�����");
	    HashMap<Integer, byte[]> packetMap=new HashMap<Integer, byte[]>();//��һ����ϣ�������ݰ�
	    int length=fileMessage.getPacketNumber();//���ݰ���
	    byte[] buffer2=null;//��Ž��յ��������ֽ�
		try{
		    reSendThread.removePacket(0);//�Ƴ��������ط��б��еı��Ϊ0�����ݰ���0�����ݰ��������Ӻʹ��ļ���Ϣ��
		    //��ѭ���н������ݰ�
	        while(true){
	        	byte[] data=getMethod.getMessage();
	        	byte[] fourByte=new byte[4];
	        	fourByte[0]=data[2];
	        	fourByte[1]=data[3];
	        	fourByte[2]=data[4];
	        	fourByte[3]=data[5];
	        	int num=Tools.fourByteToInt(fourByte);//��ȡint�ͱ��
	        	if(packetMap.get(num)==null){   //map�в�����
	        		packetMap.put(num,data );//�������
	        		sendMethod.sendMessage(data, fileMessage.getDestIp(), fileMessage.getDestPort());//��һ������ʾ�յ��˸ñ�ŵ����ݰ�
	        		reSendThread.addPacket(new Packet(num, data, fileMessage.getDestIp(), fileMessage.getDestPort()));//�ѻ�Ӧ���ŵ��ش��߳���
	        	}
	        	//У���Ƿ����������а�
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
	        //�����а����ϵ������ֽ�����
	        byte[] buffer1=new byte[1024*fileMessage.getPacketNumber()];//������0�ĳ���
		    for(int i=0;i<fileMessage.getPacketNumber();i++){       
		    	System.arraycopy(packetMap.get(i), 0, buffer1, i*1024, 1024);
		    }
		    buffer2=new byte[fileMessage.getFileByteSum()];//ȥ����0���ֵĳ���
		    System.arraycopy(buffer1, 0, buffer2, 0, fileMessage.getFileByteSum());		    
	    }catch(Exception e){
	    	Log.v("MainActivity","�ļ����ݽ����쳣");
	    	e.getMessage();
	    }
	    return buffer2;
	}
	
	
	
}

/**
 * ���ݰ���
 * @author mayifan
 *
 */
class Packet{
	
	public int number;//���ı��
	public int reSendTimes;//�ش�����
	public byte[] data;//��ŵ�����
	public long lastTime;//���һ�η��͵�ʱ��
	public String destIp;//Ŀ��IP
	public int destPort;//Ŀ��˿ں�
		
	public Packet(int number,byte[] data,String destIp,int destPort){
        this.number=number;
        this.data=data;
        this.destIp=destIp;
        this.destPort=destPort;
        this.reSendTimes=0;
        this.lastTime=System.currentTimeMillis();
	}
	
}
	





