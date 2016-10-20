package com.cneop.util.net;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.util.Log;

//����webservice��
public class WebService {
	protected String Url;

	public boolean Open(String url) {
		Url = url;
		return true;
	}

	public String Invoke(String FunctionName, String params) {
		try {
			String SERVER_URL = Url + "/" + FunctionName; // ��������WebMethod
			HttpPost request = new HttpPost(SERVER_URL); // ����������Դ��ַ����һ��Http����
			request.addHeader("Content-Type", "application/json;charset=utf-8");// ����Ҫ��Ӹ�Httpͷ���ܵ���WebMethodʱ����JSON����

			// ����
			if (params.length() > 0) {
				HttpEntity bodyEntity = new StringEntity(params, "utf8");// ��������Ҳ����JSON���ݸ�ʽ���ַ������ܴ��ݵ��������ˣ���������"{'Message':'strUserName����Ч��JSON��Ԫ'}"�Ĵ���
				request.setEntity(bodyEntity);
			}
			// */

			// �������ó�ʱʱ��:Ϊ10Ҫ
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
			defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			// ��ȡ��ʱ :

			HttpResponse httpResponse = defaultHttpClient.execute(request); // �������󲢻�ȡ����
			// �������ص�����
			if (httpResponse.getStatusLine().getStatusCode() == 200) // StatusCodeΪ200��ʾ���������ӳɹ���404Ϊ���Ӳ��ɹ�
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
