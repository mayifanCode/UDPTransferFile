package com.example.clientudp;

public class Tools {

	
	/**
	 * ��һ��int��ֵתΪ6���ֽ�
	 * @param i
	 * @return
	 */
	public static byte[] intTo6Byte(int i){
        byte[] result=new byte[6];
		result[0]=(byte)((i >> 40)& 0xFF);
	    result[1]=(byte)((i >> 32)& 0xFF);
		result[2]=(byte)((i >> 24)& 0xFF);
		result[3]=(byte)((i >> 16)& 0xFF);
		result[4]=(byte)((i >> 8)& 0xFF);
		result[5]=(byte)(i & 0xFF);
		return result;
	}
	
	/**
	 * ��һ��int��ֵתΪ4���ֽ�
	 * @param i
	 * @return
	 */
	public static byte[] intTo4Byte(int i){		
	        byte[] result=new byte[4];
			result[0]=(byte)((i >> 24)& 0xFF);
		    result[1]=(byte)((i >> 16)& 0xFF);
			result[2]=(byte)((i >> 8)& 0xFF);
			result[3]=(byte)(i & 0xFF);
			return result;			    
	}
	
	/**
	 * 2���ֽ�תΪһ��int
	 * @param b
	 * @return
	 */
	public static int twoByteToInt(byte[] b) {
		int num = b[1] & 0xFF;
		num |= ((b[0] << 8) & 0xFF00);
		return num;
	}
	
	
	/**
	 * 4���ֽ�תΪһ��int
	 * @param b
	 * @return
	 */
	public static int fourByteToInt(byte[] b) {
		int num = b[3] & 0xFF;
		num |= ((b[2] << 8) & 0xFF00);
		num |= ((b[1] << 16) & 0xFF0000);
		num |= ((b[0] << 24) & 0xFF0000);
		return num;
	}
	
	/**
	 * 6���ֽ�תΪһ��int
	 * @param b
	 * @return
	 */
	public static int sixByteToInt(byte[] b) {
		int num = b[5] & 0xFF;
		num |= ((b[4] << 8) & 0xFF00);
		num |= ((b[3] << 16) & 0xFF0000);
		num |= ((b[2] << 24) & 0xFF0000);
		num |= ((b[1] << 32) & 0xFF0000);
		num |= ((b[0] << 40) & 0xFF0000);
		return num;
	}
	
	/**
	 * ���ַ�����ÿ���ַ�ת��Ϊ�����ֽ�
	 * ����һ���ֽ�����
	 * @param str
	 * @return
	 */
	public static byte[] get2Byte(String str)throws Exception{
		int length=0;//�ֽ�����ĳ���
		byte[] buffer1=new byte[1024];
		for(int i=0;i<str.length();i++){
			char a=str.charAt(i);
			byte[] bu=new byte[2];
			if(a<256){    //Ӣ���ַ�
				bu[0]=0;
			    bu[1]=(byte)a;
			}else{         //�����ַ�
				bu=(a+"").getBytes("GBK");
			}
			buffer1[length++]=bu[0];
			buffer1[length++]=bu[1];
		}
		byte[] buffer2=new byte[length];
		System.arraycopy(buffer1, 0, buffer2, 0, length);
		return buffer2;
	}
	
	/**
	 * ��������ֽ����黹ԭΪ�ַ����ķ�����ÿ��λ��ԭΪһ���ַ���
	 * @return
	 */
	public static String getString(byte[] buffer)throws Exception{
		String str="";
		for(int i=0;i<buffer.length;i+=2){
			byte[] bu=new byte[2];
			bu[0]=buffer[i];
			bu[1]=buffer[i+1];
			if(bu[0]==0){   //��λΪ0����Ӣ���ַ�
				char a =(char)bu[1];
				str+=a;
			}
			if(bu[0]!=0){
				String a=new String(bu,"GBK");
				str+=a;
			}
		}
		return str;
	}
	
	/**
	 * ��һ��int��ֵת��Ϊ2���ֽ�
	 * @param num
	 * @return
	 */
	public static byte[] intTo2Byte(int num){
		byte[] twoByte=new byte[2];
		if(num>255){
			twoByte[0]=(byte)(num/256);
			twoByte[1]=(byte)(num-(num/256)*256);
		}else{
			twoByte[0]=0;
			twoByte[1]=(byte)num;
		}
		return twoByte;
	}
	
	
	
	
}
