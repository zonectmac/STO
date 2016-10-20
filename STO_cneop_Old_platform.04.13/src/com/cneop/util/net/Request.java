package com.cneop.util.net;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.cneop.util.model.Enums.ESessionIDType;

/**
 * Get,post请求判断,url信息抓取,跨站处理等
 * 
 * @author Administrator
 * 
 */
public class Request {
	public static String Post(String postdata, String url, boolean b) {

		String resultData = "";
		URL Url;

		HttpURLConnection urlConn;
		try {
			Url = new URL(url);
			urlConn = (HttpURLConnection) Url.openConnection();
			// post请求，要设为true（b为true才能使用）
			urlConn.setDoOutput(b);
			urlConn.setDoInput(b);
			urlConn.setRequestMethod("POST");// 设置方式为POST
			urlConn.setUseCaches(false); // POST请求不能用缓存
			urlConn.setInstanceFollowRedirects(true);
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset= UTF8");
			urlConn.connect();
			DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());

			byte[] byteData;

			String content;// 要上传的参数
			if (postdata != null) {
				out.writeBytes(postdata);
			}

			out.flush();
			out.close();
			// 获取数据
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String inputLine = "";
			while (((inputLine = reader.readLine()) != null)) {
				resultData += inputLine;
				System.out.println("=================resultData \t" + resultData);
			}
			reader.close();
			urlConn.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultData;

	}

	/**
	 * public static void Get() {
	 * 
	 * }
	 */
	static private DefaultHttpClient httpClient;
	static private HttpPost httpPost;
	static private HttpEntity httpEntity;
	static private HttpResponse httpResponse;
	public static String SESSIONID_VALUE = null;
	public static ESessionIDType sessionType = ESessionIDType.JSESSIONID;

	/**
	 * 调用服务器程序带Session功能的post
	 * 
	 * @param path
	 * @param params
	 * @return
	 */
	public static String PostWithSession(String path, List<NameValuePair> params) {
		String ret = "none";
		try {
			httpPost = new HttpPost(path);
			httpEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setEntity(httpEntity);
			// 第一次一般是还未被赋值，若有值则将SessionId发给服务器
			if (null != SESSIONID_VALUE) {
				httpPost.setHeader("Cookie", sessionType.name() + " =" + SESSIONID_VALUE);
			}

			httpClient = new DefaultHttpClient();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = httpResponse.getEntity();

				ret = EntityUtils.toString(entity);
				CookieStore mCookieStore = httpClient.getCookieStore();

				List<Cookie> cookies = mCookieStore.getCookies();
				if (cookies.size() == 0) { // 未登录

				}
				for (int i = 0; i < cookies.size(); i++) {
					// 这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值
					if (sessionType.name().equals(cookies.get(i).getName())) {
						SESSIONID_VALUE = cookies.get(i).getValue();
						break;
					}

				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

}
