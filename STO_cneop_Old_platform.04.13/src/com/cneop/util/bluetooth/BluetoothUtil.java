package com.cneop.util.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Arrays;

import com.cneop.stoExpress.util.BrocastUtil;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

@SuppressLint({ "HandlerLeak", "NewApi" })
public class BluetoothUtil {

	public BluetoothDevice remoteDevice;
	public BluetoothAdapter bluetoothAdapter;
	private String remoteAddr;
	private String code;


	private String splitStr;
	private boolean isReverse;
	private boolean isCreateScuess = false;;
	private BluetoothSocket mmSocket;
	private ReadDataThread readDataThread;
	private boolean readDataExitFlag = false;
	private int mBufferSize = 1024;
	private InputStream inputStream = null;
	private DecimalFormat weightFormat = new DecimalFormat("#.##");
	private BrocastUtil brocastUtil;
	private boolean connectStatus = false;
	private static boolean isPaired = false;
	private static BluetoothUtil instance ;

	public  BluetoothUtil(Context context) {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		brocastUtil = new BrocastUtil(context);
	}
 

	 public  BluetoothUtil(Context context, String remoteAddr, String code,
			String splitStr, boolean isRverse) {
 
		 this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 this.brocastUtil = new BrocastUtil(context);
		 this.remoteAddr = remoteAddr;
		 this.code = code;
		 this.splitStr = splitStr;
		 this.isReverse = isRverse;
	}

	public boolean getConnectStatus() {
		return this.connectStatus;
	}

	public void setParam(String remoteAddr, String code, String splitStr,
			boolean isReverse) {
		this.remoteAddr = remoteAddr;
		this.code = code;
		this.splitStr = splitStr;
		this.isReverse = isReverse;
	}

	public boolean isOpen() {
		return bluetoothAdapter.isEnabled();
	}

	/**
	 * 获取密码
	 * @return
	 */
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 打开
	 */
	public boolean open() {
		boolean flag = false;
		// 要检验蓝牙模块是否已打开

		if (bluetoothAdapter != null) {
			flag = true;
			if (!bluetoothAdapter.isEnabled()) {
				flag = bluetoothAdapter.enable();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	/**
	 * 搜索
	 */
	public void search() {
		if (open()) {
			if(bluetoothAdapter.isDiscovering()){
				stopSearch();
			}
			bluetoothAdapter.startDiscovery();
		}
	}

	/**
	 * 停止搜索
	 */
	public void stopSearch() {
		if (open()) {
			bluetoothAdapter.cancelDiscovery();
		}
	}

	/**
	 * 取消配对
	 */
	public void cancelPair() {
		if (!isPaired) {
			return;
		}
		try {
			ClsUtils.removeBond(remoteDevice.getClass(), remoteDevice);
			isPaired = true;
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 验证蓝牙地址
	 * 
	 * @param addr
	 * @return
	 */
	public boolean checkAddr(String addr) {
		String addrT = getMacAddrFormat(addr);
		return BluetoothAdapter.checkBluetoothAddress(addrT);
	}

	public String getMacAddrFormat(String addr) {
		String addrT = addr;
		if (!addrT.contains(":")) {
			char[] c = addrT.toCharArray();
			StringBuilder sb = new StringBuilder();
			int j = 0;
			for (int i = 0; i < c.length; i++) {
				j++;
				sb.append(c[i]);
				if (j == 2 && i != c.length - 1) {
					sb.append(":");
					j = 0;
				}
			}
			addrT = sb.toString().trim();
		}
		return addrT;
	}

	/**
	 * 配对
	 * 
	 * @param strAddr
	 * @param strPsw
	 * @return
	 */
	public boolean pair() {
		// readDataExitFlag=false;
		// boolean isOpened=open();
		// Log.i("bluetooth","is opened :"+isOpened);
		// if (!isOpened) {
		// return false;
		// }
		// bluetoothAdapter.cancelDiscovery();
		// if (BluetoothAdapter.checkBluetoothAddress(remoteAddr)) {
		// BluetoothDevice device = bluetoothAdapter
		// .getRemoteDevice(remoteAddr);
		// if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
		// try {
		// ClsUtils.createBond(device.getClass(), device);
		// remoteDevice = device;
		// isCreateScuess = true;
		// connectStatus = true;
		// } catch (Exception e) {
		// Log.i("bluetooth","error :"+e.getMessage());
		// e.printStackTrace();
		// }
		// } else {
		// remoteDevice = device;
		// connectStatus = connect();
		// }
		// }
		//
		// return connectStatus;

		boolean isPairSuc = pair(remoteAddr, this.code);
		if (!isPairSuc) {
			return isPairSuc;
		}

		if (remoteDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
			connectStatus = connect();
		}
		return connectStatus;
	}

	public boolean pair(String strAddr, String strPsw)
	{

		boolean result = false;
		boolean isOpened = open();
		Log.i("bluetooth", "is opened :" + isOpened);
		if (!isOpened) {
			return false;
		}

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		bluetoothAdapter.cancelDiscovery();

		if (!bluetoothAdapter.isEnabled())
		{
			bluetoothAdapter.enable();
		}

		if (!BluetoothAdapter.checkBluetoothAddress(strAddr))
		{ // 检查蓝牙地址是否有效

			Log.d("mylog", "devAdd un effient!");
		}

		BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strAddr);

		if (device.getBondState() == BluetoothDevice.BOND_NONE)
		{
			try
			{
				Log.d("mylog", "NOT BOND_BONDED");
				ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
				ClsUtils.createBond(device.getClass(), device);
				remoteDevice = device; // 配对完毕就把这个设备对象传给全局的remoteDevice
				result = true;
			} catch (Exception e)
			{

				Log.d("mylog", "setPiN failed!");
				e.printStackTrace();
			} //

		}
		else
		{
			Log.d("mylog", "HAS BOND_BONDED");
			try
			{
				ClsUtils.createBond(device.getClass(), device);
				ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
				ClsUtils.createBond(device.getClass(), device);
				remoteDevice = device; // 如果绑定成功，就直接把这个设备对象传给全局的remoteDevice
				result = true;
			} catch (Exception e)
			{

				Log.d("mylog", "setPiN failed!");
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 设置配对码
	 */
	public void setPin() {
		if (isCreateScuess) {
			try {
				ClsUtils.setPin(remoteDevice.getClass(), remoteDevice, code);
				ClsUtils.createBond(remoteDevice.getClass(), remoteDevice);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * 连接
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public boolean connect() {
		boolean flag = false;
		if(mmSocket!=null){
			if(mmSocket.isConnected()){
				try {
					mmSocket.close();
				} catch (IOException e) {				 
					e.printStackTrace();
				}
			}			
		}
		
		BluetoothSocket tmp = null;
		try {
			Method m = remoteDevice.getClass().getMethod("createRfcommSocket",
					new Class[] { int.class });
			tmp = (BluetoothSocket) m.invoke(remoteDevice, 1);
		} catch (Exception e) {
			Log.i("bluetooth", "connect error:" + e.getMessage());
			// TODO: handle exception
		}
		mmSocket = tmp;
		if (mmSocket != null) {
			int connetTime = 0;

			while (!flag && connetTime <= 10) {
				try {
					
					mmSocket.connect();
					flag = true;
					break;
				} catch (IOException e) {
					connetTime++;
					try {
						Thread.sleep(100);
						mmSocket.close();
						
					} catch (Exception closeException) {
					}
					
				}
			}

		}

		if (flag) {

			readDataExitFlag = false;
			// 启动读数据线程
			if (readDataThread == null) {
				readDataThread = new ReadDataThread();
				readDataThread.setName("readDataThread");
				readDataThread.isDaemon();
				readDataThread.start();
			}

		}
		return flag;
	}

	public void cancel() {
		try {
			readDataExitFlag = true;
			if (inputStream != null) {
				 cancelPair();

				inputStream.close();
				inputStream = null;
			}

			if (mmSocket != null) {
				mmSocket.close();
				mmSocket = null;
			}
			if (remoteDevice != null) {
				remoteDevice = null;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class ReadDataThread extends Thread {

		@Override
		public void run() {

			super.run();
			try {
				inputStream = mmSocket.getInputStream();

				int len = 0;
				while (!readDataExitFlag) {
					byte[] dataByte = new byte[mBufferSize];
					try {
						if (inputStream == null) {
							break;
						}
						len = inputStream.read(dataByte);
						if (len > 0) {
							String weight = dealwithData(dataByte, len);
							brocastUtil.sendWeightBrocast(weight);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					Thread.sleep(250);
				}
			} catch (IOException e1) {

				e1.printStackTrace();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

		private String dealwithData(byte[] dataByte, int len) {

			try {
				String dataStr = new String(dataByte, 0, len,
						Charset.forName("ASCII"));
				String[] soucreArray = dataStr.split(splitStr);
				String[] destiantionArray = null;
				if (soucreArray.length > 4) {
					destiantionArray = Arrays.copyOfRange(soucreArray, 0, 3);
				} else {
					destiantionArray = soucreArray;
				}
				double num = 0;
				int count = 0;
				int i = 0;
				int endIndex = destiantionArray.length;
				if (destiantionArray.length > 2) {
					i = 1;
					endIndex = destiantionArray.length - 1;
				}
				for (; i < endIndex; i++) {
					String str = destiantionArray[i];
					if (isReverse) {
						str = new StringBuffer(destiantionArray[i]).reverse()
								.toString().trim();
					}
					str = str.replace("kg", "");
					str = str.replace("KG", "");
					str = str.replace(" ", "");
					if (!"".equals(str)) {
						num += Double.parseDouble(str.trim());
						count++;
					}
				}
				if (destiantionArray.length > 0) {
					num = num / count;
				}
				return String.valueOf(weightFormat.format(num));
			} catch (Exception e) {

			}
			return "";
		}
	}
}
