package com.cneop.stoExpress.datacenter.download;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import com.cneop.stoExpress.common.Enums.EDownError;
import com.cneop.stoExpress.common.Enums.EDownTableName;
import com.cneop.stoExpress.common.Enums.EDownType;
import com.cneop.stoExpress.common.Enums.ERegResponse;
import com.cneop.stoExpress.dao.BaseDao;
import com.cneop.stoExpress.dao.OrderService;
import com.cneop.stoExpress.datacenter.msd.MSDServer;
import com.cneop.stoExpress.datacenter.msd.ParamAnalyst;
import com.cneop.stoExpress.model.BarcodeTrackingModel;
import com.cneop.stoExpress.util.ReflectorUtil;
import com.cneop.util.StrUtil;
import com.cneop.util.net.Request;

import android.content.Context;

public class DownloadManager {
	StrUtil strUtil;
	Context context;
	ReflectorUtil reflectorUtil;
	String stationId;
	String statinName;
	String userNo;
	String deviceId;
	OrderService orderService;

	public DownloadManager(Context context, String stationId) {
		strUtil = new StrUtil();
		this.context = context;
		reflectorUtil = new ReflectorUtil(context);
		this.stationId = stationId;
	}

	public DownloadManager(Context context) {
		strUtil = new StrUtil();
		this.context = context;
		reflectorUtil = new ReflectorUtil(context);
	}

	public DownloadManager(Context context, String stationName, String userNo, String deviceId) {
		strUtil = new StrUtil();
		this.context = context;
		this.statinName = stationName;
		this.userNo = userNo;
		this.deviceId = deviceId;
		orderService = new OrderService(context);
	}

	/**
	 * 下载基础数据
	 */
	public int downloadData(EDownType downType) {
		int result = -1;
		BaseDao basDao = reflectorUtil.getDao(downType);
		String lastUpdateTime = basDao.getLastTime();// 获得最后更新时间
		String downStr = "";// XML
		try {
			downStr = MSDServer.getInstance(context).getDownData(getParamAnalyst(downType, lastUpdateTime));
			System.out.println("==================downStr \t" + downStr);
			if (downStr != null && !downStr.equals(EDownError.paramError.toString().trim())
					&& !downStr.equals(EDownError.unRegisterError.toString().trim())) {
				@SuppressWarnings("unchecked")
				Class<IDownload> ownclass = (Class<IDownload>) Class
						.forName("com.cneop.stoExpress.datacenter.download." + downType.toString().trim());
				IDownload downIntance = ownclass.newInstance();
				result = downIntance.dataProcessing(context, downStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private ParamAnalyst getParamAnalyst(EDownType downType, String lastUpdateTime) {
		ParamAnalyst pl = new ParamAnalyst();
		String[] params = null;
		switch (downType) {
		case User:
			params = new String[2];
			params[0] = lastUpdateTime;
			params[1] = stationId;
			pl.add(EDownTableName.tab_employee, params);
			break;
		case Station:
			params = new String[1];
			params[0] = lastUpdateTime;
			pl.add(EDownTableName.tab_siteinfo, params);
			break;
		case Abnormal:
			params = new String[1];
			params[0] = lastUpdateTime;
			pl.add(EDownTableName.tab_problem_type_new, params);
			break;
		case Route:
			params = new String[2];
			params[0] = lastUpdateTime;
			params[1] = stationId;
			pl.add(EDownTableName.tab_routing, params);
			break;
		case Destination:
			params = new String[1];
			params[0] = lastUpdateTime;
			pl.add(EDownTableName.tab_province, params);
			break;
		case NextStation:
			params = new String[2];
			params[0] = lastUpdateTime;
			params[1] = stationId;
			pl.add(EDownTableName.tab_nextstation, params);
			break;
		case OrderAbnormal:
			params = new String[1];
			params[0] = lastUpdateTime;
			pl.add(EDownTableName.tab_returnreason, params);
			break;
		default:
			break;
		}
		return pl;
	}

	/**
	 * 下载服务站点
	 * 
	 * @param stationId
	 * @return
	 */
	@SuppressWarnings("static-access")
	public int downloadServerStation(String url) {
		int result = -1;
		String jsonData = Request.Post("args=" + stationId, url, true);
		if (!strUtil.isNullOrEmpty(jsonData)) {
			try {
				jsonData = URLDecoder.decode(jsonData, "utf-8");
				result = new ServerStation().dataProcessing(context, jsonData);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 下载收件面单发送记录
	 */
	public void downloadSJMDSendedRecords() {
	}

	/**
	 * 单号跟踪
	 * 
	 * @param barcode
	 * @return
	 */
	public boolean getBarcodeTracking(String barcode, StringBuilder barcodeTrackingSb) {
		boolean flag = false;
		try {
			String rowSplitStr = "\r\n";
			String colSplitStr = ",";
			String xmlStr = MSDServer.getInstance(context).barcodeTrack(barcode);
			if (!xmlStr.equalsIgnoreCase(EDownError.paramError.toString().trim())
					&& !xmlStr.equalsIgnoreCase(EDownError.unRegisterError.toString().trim())) {
				BarcodeTrack barcodeTrack = new BarcodeTrack();
				List<BarcodeTrackingModel> barcodeStrList = barcodeTrack.parseXmlStr(xmlStr);
				if (barcodeStrList == null || barcodeStrList.size() == 0) {
					barcodeTrackingSb.append("没有要查询的数据!");
				} else {
					for (int i = barcodeStrList.size() - 1; i >= 0; i--) {
						BarcodeTrackingModel model = barcodeStrList.get(i);
						barcodeTrackingSb.append(model.getTime()).append(colSplitStr).append(model.getMemo())
								.append(rowSplitStr);
					}
					flag = true;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			barcodeTrackingSb.append(e.getMessage());
		}
		return flag;
	}

	/**
	 * 注册
	 * 
	 * @return
	 */
	public ERegResponse register() {
		ERegResponse regResponse = MSDServer.getInstance(context).register();
		return regResponse;
	}

	/**
	 * 订单下载
	 * 
	 * @return
	 */
	public int getOrder() {
		int t = 0;
		String maxLastTime = orderService.getLastTime(userNo, "");
		try {
			String xmlStr = MSDServer.getInstance(context).getOrder(statinName, userNo, maxLastTime, deviceId);
			if (!xmlStr.equals(EDownError.paramError.toString().trim())
					&& !xmlStr.equals(EDownError.unRegisterError.toString().trim())) {
				Order orderDown = new Order(userNo);
				t = orderDown.dataProcessing(context, xmlStr);
			} else {
				t = -2;// 未注册
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			t = -1;// 网络或服务端异常
		}
		return t;
	}

	/**
	 * 订单状态
	 * 
	 * @return
	 */
	public List<com.cneop.stoExpress.model.Order> getOrderStatus() {
		String maxLastTime = orderService.getLastTime(userNo, "0");
		String minLastTime = orderService.getMinAcceptTime(userNo);
		try {
			String xmlStr = MSDServer.getInstance(context).getOrderStatus(deviceId, maxLastTime, minLastTime);
			if (!xmlStr.equals(EDownError.paramError.toString().trim())
					&& !xmlStr.equals(EDownError.unRegisterError.toString().trim())) {
				OrderStatus orderStatusDown = new OrderStatus(userNo);
				List<com.cneop.stoExpress.model.Order> orderList = orderStatusDown.dataProcessing(context, xmlStr);
				return orderList;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
