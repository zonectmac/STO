package com.cneop.stoExpress.datacenter.download;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.json.JSONObject;

import com.cneop.stoExpress.common.Enums.ESysConfig;
import com.cneop.stoExpress.common.GlobalParas;
import com.cneop.stoExpress.dao.SJMDSendedRecordsDao;
import com.cneop.stoExpress.dao.SysConfigService;
import com.cneop.stoExpress.model.SJMDSendedRecordsVO;
import com.cneop.stoExpress.model.SysConfig;
import com.cneop.util.DateUtil;
import com.cneop.util.StrUtil;
import com.cneop.util.json.JsonUtil;
import com.cneop.util.net.Request;

import android.content.Context;
import android.util.Base64;

/**
 * 收件面单发放记录下载
 * 
 * @author Administrator
 * 
 */
public class SJMDSendedRecords {
	static String key = "NmJxbTU2dUw2WUNmNllDUw==";
	static String charset = "UTF-8";
	static String successKey = "success";
	static String responseitemsKey = "responseitems";
	private static SJMDSendedRecordsDao sJMDSendedRecordsDao = null;
	static String reasonKey = "reason";
	static SysConfigService sysConfigService =null;

	public static String download(Context context,boolean isHandleDownload) {
		if (sJMDSendedRecordsDao == null) {
			sJMDSendedRecordsDao = new SJMDSendedRecordsDao(context);
			sysConfigService=new SysConfigService(context);
		}
		StringBuilder sbContent = new StringBuilder();
		// String content =
		// "{\"sitecode\":\"519000\",\"starttime\":\"2013-08-01\",\"endtime\":\"2014-01-01\"}";
		String strResult = "";
		try {
			String endtime = DateUtil.getDateTimeByPattern("yyyy-MM-dd");
			if(GlobalParas.getGlobalParas().getLastDownMDTime()!=null){
				if(!isHandleDownload	&&
						GlobalParas.getGlobalParas().getLastDownMDTime().equals(endtime)){//表示是当天已下载过了
					 return "今天已下载过了";
				}
				
			}else{
				sysConfigService.UpdateLastMDTime(endtime);
			}
			
	 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			// 半年前的时间
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -6);
			String starttime = sdf.format(calendar.getTime());

			sbContent.append("{\"sitecode\":\"")
			  // .append("519000").append("\",");
					.append(GlobalParas.getGlobalParas().getStationId()).append("\",");
			sbContent.append("\"starttime\":\"").append(starttime).append("\",");
			sbContent.append(" \"endtime\":\"").append(endtime).append("\"} ");

			String content = sbContent.toString();
			String logistics_interface = URLEncoder.encode(content, charset);
			String data_digest = doSign(content, key, charset);
			StringBuilder sbPostdata = new StringBuilder();
			sbPostdata.append("logistics_interface=").append(logistics_interface);
			sbPostdata.append("&data_digest=").append(data_digest);

			String strPostResult = Request.Post(sbPostdata.toString(),
					GlobalParas.getGlobalParas().getSjMdDownloadUrl(), true);
			if (!StrUtil.isNullOrEmpty(strPostResult)) {
				JSONObject jsonObejct = new JSONObject(strPostResult);
				String result = jsonObejct.getString(successKey);
				if (result.equalsIgnoreCase("true")) {
					String grantData = jsonObejct.getString(responseitemsKey);
					List<Object> ObjList = JsonUtil.jsonToList(grantData,
							new SJMDSendedRecordsVO());
					if (ObjList.size() > 0) {
						sJMDSendedRecordsDao.delRecord(null, null);
						sJMDSendedRecordsDao.addRecord(ObjList);
						strResult = "下载成功";
			
						
						
						sysConfigService.UpdateLastMDTime(endtime);
					}
				} else {
					String reason = jsonObejct.getString(reasonKey);
					if (reason.equalsIgnoreCase("S09")) {

						strResult = "没有任务数据反回";
					} else {

						strResult = "服务端异常:" + reason;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			strResult = "下载出错";
		}
		return strResult;

	}

	

	/**
	 * 生成签名
	 * 
	 * @param logistics_interface
	 * @param key
	 * @param charset
	 * @return
	 */
	public static String doSign(String logistics_interface, String key,
			String charset) {
		try {

			return Base64.encodeToString(code32((logistics_interface + key), charset)
					.getBytes(charset), Base64.NO_WRAP);
			// String sign = new String(Base64(code32((logistics_interface + key),
			// charset).getBytes(charset)));
			// return sign;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * MD5 32位加密
	 * 
	 * @param origin
	 *          字符源
	 * @param charset
	 *          字符编码
	 * @return 32位密文
	 */
	public static String code32(String origin, String charset) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			if (charset == null) {
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes()));
			} else {
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes(charset)));
			}
		} catch (Exception exception) {
		}
		return resultString;
	}

	private static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}

		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

}
