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
	private DatagramSocket datagramSocket;//本地发送通道
	private DatagramSocket datagramSocket2;//本地接收通道

	private Button button;	//发送按钮
	//过程参数的配置
	private static final int NONE=0;
    private static final String IMAGE_UNSPECIFIED="image/*";
    private static final int PHOTOZOOM =1; //触发从图库获取图片的按钮
	
	private String destIp;//目标IP
	private int destPort;//目标接收端端口号
//	private String localIp;//本地IP	
	private int localSendPort;//本地发送端口号
	private int localReceivePort;//本地接收端口号
	
	private SendMethod sendMethod;//发送类
	private GetMethod getMethod;//接收类
	private FileTransfer fileTransfer;//文件传输类
	
    private int count=0;
         
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//调用父类构造方法
        setContentView(R.layout.activity_main);//设置当前布局
        
        init();//初始化
        
        //获取组件
        button=(Button)this.findViewById(R.id.button1);//发送按钮     

        //为按钮加监听器
        button.setOnClickListener(new OnClickListener() {			
        	//从图库获取照片
			public void onClick(View v) {
				Intent intent=new Intent(Intent.ACTION_PICK);//选择数据的意图
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_UNSPECIFIED );//设置数据和类型，从图库中获取图片
				startActivityForResult(intent, PHOTOZOOM);//执行意图并传入请求码（请求码根据业务需要自行指定）								
			}
		});      

        
  	
      
    }


    /**
     * 接收之前activity结束后返回的数据，并处理
     */
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
    	Log.v("test", "onActivityResult is: requestCode: "+requestCode+" resultCode: "+resultCode+" data: "+data);
    	//图库获取需要有activity返回
    	if(resultCode==NONE){
    		return;
    	}
    	//若无数据，则没有从相册得到照片，直接返回
    	if(data==null)
    		return;
    	if(requestCode==PHOTOZOOM){
    		Uri uri=data.getData();//返回的是地址
   
    		String path=uri.getPath();
    		
    		String name="1"+count+"3.jpg";
    		count++;
    		
    		//文件上传方法
    		upLoadingFile(path,name);
    	}
    	super.onActivityResult(requestCode, resultCode, data);//使得参数传递到之前的activity   	
    }    
    
 
    
    
    /**
     * 文件上传服务器
     * @param path
     */
     public void upLoadingFile(final String path,final String name){
     	new Thread(){
	    	public void run(){
	    		//文件信息
	    		FileMessage fileMessage=new FileMessage();	
		        fileMessage.setDestIp(destIp);
		        fileMessage.setDestPort(destPort);
		        fileMessage.setPath(path);
		        fileMessage.setName(name);
		        Random random=new Random();
	            fileMessage.setSerialNumber(random.nextInt(100));
	    		//上传文件
	    		fileTransfer.sendFileToServer(fileMessage);
	    	    
    		}
    	}.start();  
     }
     
     
    /**
     * 参数配置（工厂）
     */
    public void init(){
    	destIp="192.168.31.122";//目标ip
    	destPort=9999;//目标接收端端口号
//   	localIp="";//本地ip，未知
    	localSendPort=9999;//本地发送端端口号    	
    	localReceivePort=8888;//本地接收端端口号   	 	
    	try { 	    
    		datagramSocket=new DatagramSocket(localSendPort);//发送的数据通道
			datagramSocket2=new DatagramSocket(localReceivePort);//接收的数据通道	
			Log.v("MainActivity", "初始化成功");
		} catch (Exception e) {
			Log.v("MainActivity", "初始化错误");
			e.printStackTrace();
		}

    	sendMethod=new SendMethod(datagramSocket);//实例化发送方法的引用
    	getMethod=new GetMethod(datagramSocket2);//实例化接收方法的引用 
    	fileTransfer=new FileTransfer(sendMethod,getMethod);//文件传输类引用
    }
          



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}






