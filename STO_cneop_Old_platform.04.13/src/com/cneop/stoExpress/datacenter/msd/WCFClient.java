package com.cneop.stoExpress.datacenter.msd;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

class WCFClient {

	private String namespace = "http://tempuri.org/";// 命名空间
	private String url = "http://222.66.109.144:22230/PDATransferWS.svc";// 访问地址
	private String soap_action = "http://tempuri.org/IPdaTransfer/";

	/**
	 * 设置地址
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获取服务器时间
	 * 
	 * @return
	 */
	public String getServerTime() {
		StringBuilder responseStr = new StringBuilder();
		String propertyName = "ServerTimeResult";
		String methodName = "ServerTime";
		String action = soap_action + methodName;
		boolean flag = getRequesData(methodName, action, null, propertyName, responseStr);
		System.out.println("=======2222" + responseStr);
		String serverTime = "";
		if (flag) {
			serverTime = responseStr.toString().trim();
		}
		return serverTime;
	}

	/**
	 * 注册
	 * 
	 * @param registerInfo
	 * @return
	 * @throws Exception
	 */
	public String register(String registerInfo) {// 注册资料
		PropertyInfo requestProperty = new PropertyInfo();
		requestProperty.setName("regsiterInfo");
		requestProperty.setValue(registerInfo);
		// ----------------------------------------------------------------------
		PropertyInfo[] params = { requestProperty };

		StringBuilder responseStr = new StringBuilder();

		// ---------------------------------------------------------------------
		getRequesData("Register", soap_action + "Register", params, "RegisterResult", responseStr);
		return responseStr.toString().trim();
	}

	/**
	 * 下载
	 * 
	 * @param transferParam
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String downData(String transferParam, String param) throws Exception {
		StringBuilder responseStr = new StringBuilder();
		String propertyName = "DownloadResult";
		String methodName = "Download";
		String action = soap_action + methodName;
		PropertyInfo transferPi = new PropertyInfo();
		transferPi.setName("transferParam");
		transferPi.setValue(transferParam);
		PropertyInfo downPraramPi = new PropertyInfo();
		downPraramPi.setName("param");
		downPraramPi.setValue(param);
		PropertyInfo[] paramArray = { transferPi, downPraramPi };

		boolean flag = getRequesData(methodName, action, paramArray, propertyName, responseStr);
		if (!flag) {
			throw new Exception(responseStr.toString().trim());
		}
		return responseStr.toString().trim();
	}

	/**
	 * 上传
	 * 
	 * @param transferParam
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public String uploadData(String transferParam, String content) throws Exception {
		StringBuilder responseStr = new StringBuilder();
		String propertyName = "UploadResult";
		String methodName = "Upload";
		String action = soap_action + methodName;
		PropertyInfo transferPi = new PropertyInfo();
		transferPi.setName("transferParam");
		transferPi.setValue(transferParam);
		PropertyInfo uploadPraramPi = new PropertyInfo();
		uploadPraramPi.setName("content");
		uploadPraramPi.setValue(content);
		PropertyInfo[] paramArray = { transferPi, uploadPraramPi };
		boolean flag = getRequesData(methodName, action, paramArray, propertyName, responseStr);
		String result = "";
		result = responseStr.toString().trim();
		if (!flag) {
			throw new Exception(result);
		}
		return result;
	}

	/**
	 * 扩展方法
	 * 
	 * @param transferParam
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String expandMethod(String transferParam, String param) throws Exception {
		StringBuilder responseStr = new StringBuilder();
		String propertyName = "MethodResult";
		String methodName = "Method";
		String action = soap_action + methodName;
		PropertyInfo transferPi = new PropertyInfo();
		transferPi.setName("transferParam");
		transferPi.setValue(transferParam);
		PropertyInfo paramPi = new PropertyInfo();
		paramPi.setName("param");
		paramPi.setValue(param);
		PropertyInfo[] paramArray = { transferPi, paramPi };
		boolean flag = getRequesData(methodName, action, paramArray, propertyName, responseStr);
		String result = "";
		result = responseStr.toString().trim();
		if (!flag) {
			throw new Exception(result);
		}
		return result;
	}

	/**
	 * 访问服务
	 * 
	 * @param methodName
	 * @param soap_action
	 * @param proInfo
	 * @return
	 */
	private boolean getRequesData(String methodName, String soap_action, PropertyInfo[] proInfo,
			String responsePropetryName, StringBuilder response) {
		boolean flag = false;
		SoapObject soapObject = new SoapObject(namespace, methodName);
		// 添加参数
		if (proInfo != null) {
			for (int i = 0; i < proInfo.length; i++) {
				soapObject.addProperty(proInfo[i]);
			}
		}
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = soapObject;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(soapObject);

		HttpTransportSE trans = new HttpTransportSE(url);
		// trans.debug = true;
		try {
			trans.call(soap_action, envelope);
			flag = true;
		} catch (HttpResponseException e) {
			// TODO Auto-generated catch block
			response.append("网络或服务端异常");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			response.append("网络或服务端异常");
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			SoapObject soapObj = null;
			if (flag == true && envelope.getResponse() != null) {
				soapObj = (SoapObject) envelope.bodyIn;
				Object obj = soapObj.getProperty(responsePropetryName);
				response.append(obj.toString().trim());
				System.out.println("=====================obj>>>>>> \t" + obj.toString().trim());
			}
		} catch (SoapFault e) {
			String values = e.getMessage();
			flag = false;
			response.append(e.getMessage());
		}
		Runtime.getRuntime().gc();
		return flag;
	}

}
