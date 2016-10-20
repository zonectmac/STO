package com.cneop.stoExpress.datacenter.upload;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import com.cneop.util.DateUtil;
import com.cneop.util.ImageUtil;
import com.cneop.util.encrypt.MD5;
import com.cneop.util.json.JsonUtil;
import com.cneop.util.net.Request;

/**
 * 图片上传
 * 
 * @author Administrator
 * 
 */
public class ImageUploadManage {

	private String url;
	private String key = "b0f2169aa609c42c1bc96d4aa5da3aea";
	private DateUtil dateUtil;
	private String stationId;
	private String userId;
	private String companyId;

	public ImageUploadManage(String url, String stationId, String userId,
			String companyId) {
		this.url = url;
		this.stationId = stationId;
		this.userId = userId;
		this.companyId = companyId;
		dateUtil = new DateUtil();
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 
	 * @param picType
	 * @return
	 */
	public int uploadImage(String imgPath, String scanType) {
		int result = -1;
		String barcode = imgPath.substring(imgPath.lastIndexOf("/") + 1,
				imgPath.lastIndexOf("."));
		String picBase64Content = ImageUtil.fileToBase64(imgPath);
		ImageData imageData = new ImageData();
		imageData.setBillCode(barcode);
		imageData.setDatetime(dateUtil.getDateTimeByPattern("yyyyMMddHHmmss"));
		imageData.setEmployee(userId);
		imageData.setPicContent(picBase64Content);
		imageData.setScanType(scanType);
		imageData.setSiteName(stationId);
		List<ImageData> list = new ArrayList<ImageData>();
		list.add(imageData);
		ImagePostFormat imagePostFormat = new ImagePostFormat();
		imagePostFormat.setSource(companyId);
		imagePostFormat.setRequests(list);
		String data = JsonUtil.beanToJson(imagePostFormat);
		String sign = MD5.getMD5Str(data + key);
		try {
			data = URLEncoder.encode(data, "utf-8");
			String postData = "data=" + data + "&sign=" + sign;
			String responseStr = Request.Post(postData, url, true);
			int t = responseStr.indexOf("执行成功");
			if (t > 0) {
				result = 1;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
