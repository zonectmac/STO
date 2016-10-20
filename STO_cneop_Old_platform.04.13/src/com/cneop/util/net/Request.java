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
 * Get,post�����ж�,url��Ϣץȡ,��վ�����
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
			// post����Ҫ��Ϊtrue��bΪtrue����ʹ�ã�
			urlConn.setDoOutput(b);
			urlConn.setDoInput(b);
			urlConn.setRequestMethod("POST");// ���÷�ʽΪPOST
			urlConn.setUseCaches(false); // POST�������û���
			urlConn.setInstanceFollowRedirects(true);
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset= UTF8");
			urlConn.connect();
			DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());

			byte[] byteData;

			String content;// Ҫ�ϴ��Ĳ���
			if (postdata != null) {
				out.writeBytes(postdata);
			}

			out.flush();
			out.close();
			// ��ȡ����
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
	 * ���÷����������Session���ܵ�post
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
			// ��һ��һ���ǻ�δ����ֵ������ֵ��SessionId����������
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
				if (cookies.size() == 0) { // δ��¼

				}
				for (int i = 0; i < cookies.size(); i++) {
					// �����Ƕ�ȡCookie['PHPSESSID']��ֵ���ھ�̬�����У���֤ÿ�ζ���ͬһ��ֵ
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
