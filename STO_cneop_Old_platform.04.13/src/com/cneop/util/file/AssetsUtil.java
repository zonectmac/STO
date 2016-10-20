package com.cneop.util.file;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.util.EncodingUtils;
import android.content.Context;

public class AssetsUtil {
	
	private Context context;
	
	public AssetsUtil(Context context){
		this.context=context;
	}
	
	/**
	 * 获取Assets下的文件并读取
	 * @param fileName
	 * @return
	 */
	public String getContentFromAsset(String fileName){
		String res="";
		try {
			InputStream in=context.getResources().getAssets().open(fileName);
			int length=in.available();
			byte[] buffer=new byte[length];
			in.read(buffer);
			res=EncodingUtils.getString(buffer, "gbk");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * 获取Assets下的文件并读取
	 * @param fileName
	 * @return
	 */
	public InputStream getStreamFormAsset(String fileName){
		InputStream in=null;
		try {
			in=context.getResources().getAssets().open(fileName);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return in;
	}
}
