package com.cneop.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.util.EncodingUtils;
import android.content.Context;

public class FileAccess {
	private FileAccess() {

	}

	private Context context;
	private static FileAccess _instance = new FileAccess();

	public static FileAccess getInstance(Context context) {
		_instance.context = context;
		return _instance;
	}

	public void chmod(String paramString1, String paramString2) {
		try {
			String str1 = "chmod " + paramString1 + " " + paramString2;
			Process localProcess = Runtime.getRuntime().exec(str1);
		} catch (IOException localIOException) {
			String str2 = "------chmod file :" + paramString1 + " " + paramString2 + "error!";
			localIOException.printStackTrace();
		}
	}

	/**
	 * һ��˽���ļ����µ��ļ���ȡ��/data/data/����/files��
	 * 
	 * @param fileName
	 * @param message
	 */
	public void writeFileData(String fileName, String message) {
		try {

			FileOutputStream fout = this.context.openFileOutput(fileName, Context.MODE_PRIVATE);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * //���ļ���./data/data/����/files/����
	 * 
	 * @param fileName
	 * @return
	 */
	public String readFileData(String fileName) {
		String res = "";
		try {
			FileInputStream fin = this.context.openFileInput(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * д�� ��sdcardĿ¼�ϵ��ļ���Ҫ��FileOutputStream�� ������openFileOutput
	 * ��ͬ�㣺openFileOutput����raw�������ģ�FileOutputStream���κ��ļ�������
	 * 
	 * @param fileName
	 * @param message
	 */
	// д��/mnt/sdcard/Ŀ¼������ļ�
	public void writeFileSdcard(String fileName, String message) {

		try {

			// FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);

			FileOutputStream fout = new FileOutputStream(fileName);

			byte[] bytes = message.getBytes();

			fout.write(bytes);

			fout.close();

		}

		catch (Exception e) {

			e.printStackTrace();

		}

	}

	// ����/mnt/sdcard/Ŀ¼������ļ�
	public String readFileSdcard(String fileName) {

		String res = "";

		try {

			FileInputStream fin = new FileInputStream(fileName);

			int length = fin.available();

			byte[] buffer = new byte[length];

			fin.read(buffer);

			res = EncodingUtils.getString(buffer, "UTF-8");

			fin.close();

		}

		catch (Exception e) {

			e.printStackTrace();

		}

		return res;

	}

	/**
	 * ������resource�е�raw�ļ����л�ȡ�ļ�����ȡ���ݣ���Դ�ļ�ֻ�ܶ�����д��
	 * 
	 * @param fileInRaw
	 * @return
	 */
	public String readFromRaw(int fileInRaw) {
		String res = "";
		try {
			InputStream in = this.context.getResources().openRawResource(fileInRaw);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			res = EncodingUtils.getString(buffer, "GBK");
			// res = new String(buffer,"GBK");
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * ������asset�л�ȡ�ļ�����ȡ���ݣ���Դ�ļ�ֻ�ܶ�����д��
	 * 
	 * @param fileName
	 * @return
	 */
	public String readFromAsset(String fileName) {
		String res = "";
		try {
			InputStream in = this.context.getResources().getAssets().open(fileName);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

}
