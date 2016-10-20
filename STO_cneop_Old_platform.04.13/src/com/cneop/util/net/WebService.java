package com.cneop.util.net;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.util.Log;

//访问webservice类
public class WebService {
	protected String Url;

	public boolean Open(String url) {
		Url = url;
		return true;
	}

	public String Invoke(String FunctionName, String params) {
		try {
			String SERVER_URL = Url + "/" + FunctionName; // 带参数的WebMethod
			HttpPost request = new HttpPost(SERVER_URL); // 根据内容来源地址创建一个Http请求
			request.addHeader("Content-Type", "application/json;charset=utf-8");// 必须要添加该Http头才能调用WebMethod时返回JSON数据

			// 参数
			if (params.length() > 0) {
				HttpEntity bodyEntity = new StringEntity(params, "utf8");// 参数必须也得是JSON数据格式的字符串才能传递到服务器端，否则会出现"{'Message':'strUserName是无效的JSON基元'}"的错误
				request.setEntity(bodyEntity);
			}
			// */

			// 增加设置超时时间:为10要
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
			defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			// 读取超时 :

			HttpResponse httpResponse = defaultHttpClient.execute(request); // 发送请求并获取反馈
			// 解析返回的内容
			if (httpResponse.getStatusLine().getStatusCode() == 200) // StatusCode为200表示与服务端连接成功，404为连接不成功
			{
				HttpEntity entity = httpResponse.getEntity();
				String result = EntityUtils.toString(entity);// , "UTF-8"
				return result;
			}
		} catch (Exception e) {
			Log.i("Exception", e.getMessage());
		}
		return "";
	}

}
