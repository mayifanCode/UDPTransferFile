package com.example.clientudp;
import java.net.DatagramSocket;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private DatagramSocket datagramSocket;//���ط���ͨ��
	private DatagramSocket datagramSocket2;//���ؽ���ͨ��

	private Button button;	//���Ͱ�ť
	//���̲���������
	private static final int NONE=0;
    private static final String IMAGE_UNSPECIFIED="image/*";
    private static final int PHOTOZOOM =1; //������ͼ���ȡͼƬ�İ�ť
	
	private String destIp;//Ŀ��IP
	private int destPort;//Ŀ����ն˶˿ں�
//	private String localIp;//����IP	
	private int localSendPort;//���ط��Ͷ˿ں�
	private int localReceivePort;//���ؽ��ն˿ں�
	
	private SendMethod sendMethod;//������
	private GetMethod getMethod;//������
	private FileTransfer fileTransfer;//�ļ�������
	
    private int count=0;
         
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//���ø��๹�췽��
        setContentView(R.layout.activity_main);//���õ�ǰ����
        
        init();//��ʼ��
        
        //��ȡ���
        button=(Button)this.findViewById(R.id.button1);//���Ͱ�ť     

        //Ϊ��ť�Ӽ�����
        button.setOnClickListener(new OnClickListener() {			
        	//��ͼ���ȡ��Ƭ
			public void onClick(View v) {
				Intent intent=new Intent(Intent.ACTION_PICK);//ѡ�����ݵ���ͼ
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_UNSPECIFIED );//�������ݺ����ͣ���ͼ���л�ȡͼƬ
				startActivityForResult(intent, PHOTOZOOM);//ִ����ͼ�����������루���������ҵ����Ҫ����ָ����								
			}
		});      

        
  	
      
    }


    /**
     * ����֮ǰactivity�����󷵻ص����ݣ�������
     */
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
    	Log.v("test", "onActivityResult is: requestCode: "+requestCode+" resultCode: "+resultCode+" data: "+data);
    	//ͼ���ȡ��Ҫ��activity����
    	if(resultCode==NONE){
    		return;
    	}
    	//�������ݣ���û�д����õ���Ƭ��ֱ�ӷ���
    	if(data==null)
    		return;
    	if(requestCode==PHOTOZOOM){
    		Uri uri=data.getData();//���ص��ǵ�ַ
   
    		String path=uri.getPath();
    		
    		String name="1"+count+"3.jpg";
    		count++;
    		
    		//�ļ��ϴ�����
    		upLoadingFile(path,name);
    	}
    	super.onActivityResult(requestCode, resultCode, data);//ʹ�ò������ݵ�֮ǰ��activity   	
    }    
    
 
    
    
    /**
     * �ļ��ϴ�������
     * @param path
     */
     public void upLoadingFile(final String path,final String name){
     	new Thread(){
	    	public void run(){
	    		//�ļ���Ϣ
	    		FileMessage fileMessage=new FileMessage();	
		        fileMessage.setDestIp(destIp);
		        fileMessage.setDestPort(destPort);
		        fileMessage.setPath(path);
		        fileMessage.setName(name);
		        Random random=new Random();
	            fileMessage.setSerialNumber(random.nextInt(100));
	    		//�ϴ��ļ�
	    		fileTransfer.sendFileToServer(fileMessage);
	    	    
    		}
    	}.start();  
     }
     
     
    /**
     * �������ã�������
     */
    public void init(){
    	destIp="192.168.31.122";//Ŀ��ip
    	destPort=9999;//Ŀ����ն˶˿ں�
//   	localIp="";//����ip��δ֪
    	localSendPort=9999;//���ط��Ͷ˶˿ں�    	
    	localReceivePort=8888;//���ؽ��ն˶˿ں�   	 	
    	try { 	    
    		datagramSocket=new DatagramSocket(localSendPort);//���͵�����ͨ��
			datagramSocket2=new DatagramSocket(localReceivePort);//���յ�����ͨ��	
			Log.v("MainActivity", "��ʼ���ɹ�");
		} catch (Exception e) {
			Log.v("MainActivity", "��ʼ������");
			e.printStackTrace();
		}

    	sendMethod=new SendMethod(datagramSocket);//ʵ�������ͷ���������
    	getMethod=new GetMethod(datagramSocket2);//ʵ�������շ��������� 
    	fileTransfer=new FileTransfer(sendMethod,getMethod);//�ļ�����������
    }
          



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}






