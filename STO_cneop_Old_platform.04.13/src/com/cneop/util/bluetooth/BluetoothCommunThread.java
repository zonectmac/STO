package com.cneop.util.bluetooth;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

/**
 * 蓝牙通讯线程
 * 
 * @author GuoDong
 * 
 */
public class BluetoothCommunThread extends Thread {

	private Handler serviceHandler; // 与Service通信的Handler
	private BluetoothSocket socket;
	private ObjectInputStream inStream; // 对象输入流
	private ObjectOutputStream outStream; // 对象输出流
	public volatile boolean isRun = true; // 运行标志位

	/**
	 * 构造函数
	 * 
	 * @param handler
	 *            用于接收消息
	 * @param socket
	 */
	public BluetoothCommunThread(  BluetoothSocket socket) {
		//this.serviceHandler = handler;
		this.socket = socket;
		try {
			this.outStream = new ObjectOutputStream(socket.getOutputStream());
			this.inStream = new ObjectInputStream(new BufferedInputStream(
					socket.getInputStream()));
			
			int a=1;
		} catch (Exception e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// 发送连接失败消息
			// serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		int i;
		while (true) {
			if (!isRun) {
				break;
			}
			if(inStream==null)
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 continue;
			}
			try {
				byte[] arrayOfByte = new byte[BlueToothClientUtils.mBufferSize];
				i = this.inStream.read(arrayOfByte);
		 
				parseData(arrayOfByte,i);
				// mHandler.obtainMessage(MESSAGE_READ, bytes, -1,
				// buffer).sendToTarget();

				/*
				 * Object obj = inStream.readObject();
				 * //发送成功读取到对象的消息，消息的obj参数为读取到的对象 Message msg =
				 * serviceHandler.obtainMessage(); msg.what =
				 * BluetoothTools.MESSAGE_READ_OBJECT; msg.obj = obj;
				 * msg.sendToTarget();
				 */
			} catch (Exception ex) {
				// 发送连接失败消息
				// serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
				ex.printStackTrace();
				return;
			}
		}

		// 关闭流
		if (inStream != null) {
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (outStream != null) {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	  private String parseData(byte[] paramArrayOfByte, int paramInt)
	  {
	    Object localObject = null;
	    String strResult="0";
	  //  if ((paramArrayOfByte == null) || (paramArrayOfByte.length < paramInt)) {}
	   // for (;;)
	   // {
	    
	      String[] arrayOfString = new String(paramArrayOfByte, 0, paramInt).split("=");
	      if ((arrayOfString != null) && (arrayOfString.length >= 3))
	      {
	        String str1 = new StringBuffer(arrayOfString[1]).reverse().toString().trim();
	        try
	        {
	          double d = Double.parseDouble(str1);
	          if (d > -1.E-005D)
	          {
	            localObject = d;
	            strResult=String.valueOf(d);
	          }
	          else
	          {
	            localObject = "0";
	            
	          }
	        }
	        catch (NumberFormatException localNumberFormatException) {}
	      }
	   // }
	      System.out.println("当前重量:"+strResult);
	      return strResult;
	  }

	/**
	 * 写入一个可序列化的对象
	 * 
	 * @param obj
	 */
	public void writeObject(Object obj) {
		try {
			outStream.flush();
			outStream.writeObject(obj);
			outStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void cancel() {
		isRun = false;
	}
}
