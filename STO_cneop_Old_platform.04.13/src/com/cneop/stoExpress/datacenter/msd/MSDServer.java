package com.cneop.stoExpress.datacenter.msd;

import android.content.Context;

import com.cneop.stoExpress.activity.scan.BaggingOutActivity;
import com.cneop.stoExpress.common.Enums.EDownError;
import com.cneop.stoExpress.common.Enums.ERegResponse;
import com.cneop.stoExpress.common.Enums.EVoiceType;
import com.cneop.stoExpress.common.Enums.RequestOp;
import com.cneop.util.AppContext;
import com.cneop.util.PromptUtils;
import com.cneop.util.StrUtil;
import com.cneop.util.zip.GZipUtil;

public class MSDServer {
	private Context context;

	private WCFClient wcfClient;
	StrUtil strUtil;

	private static MSDServer msdServer;

	private MSDServer(Context context) {
		wcfClient = new WCFClient();
		strUtil = new StrUtil();
		this.context = context;
	}

	public static MSDServer getInstance(Context context) {
		if (msdServer == null) {
			msdServer = new MSDServer(context);

		}
		return msdServer;
	}

	/**
	 * 设置服务器地址
	 * 
	 * @param url
	 */

	public void setUrl(String url) {
		wcfClient.setUrl(url);
	}

	/**
	 * 设置传输参数
	 * 
	 * @param stationId
	 * @param enterpriseId
	 * @param padId
	 * @param version
	 */
	public void SetParam(String stationId, String enterpriseId, String padId, String version) {
		TransferParam t = new TransferParam();
		t.setStationID(stationId);
		t.setEnterpriseID(enterpriseId);
		t.setPdaId(padId);
		t.setVersion(version);
		t.setDefaultLogic(false);
		t.setCompress(true);
		setParam(t);
	}

	/**
	 * 获得服务器时间
	 * 
	 * @return
	 */
	public String getServerTime() {
		return wcfClient.getServerTime();
	}

	/**
	 * 设置传输参数
	 * 
	 * @param tp
	 */
	private TransferParam tParam;// 传输参数

	public void setParam(TransferParam tp) {
		this.tParam = tp;
	}

	/**
	 * 注册:0网络异常 1 未设置参数 2注册成功 3已注册 4其它
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	public ERegResponse register() {
		ERegResponse regResponse = ERegResponse.netError;
		if (!validateTransferPara(this.tParam)) {// 传输数据为空
			regResponse = ERegResponse.paramError;
			return regResponse;
		}
		String head = "reg";
		String registerInfo = this.tParam.ToJson();
		String result = wcfClient.register(registerInfo);// register
		// --------------------------------------------------------------------------------------------------
		if (result.length() > 0) {
			if (result.contains("ORA-00001")) {// ORA-00001: 违反唯一约束条件
				// 有可能是巴枪ID重复引起的,先暂时这么用下,到时候和IT部确认一下什么原因引起的
				String strOld = AppContext.getAppContext().getMD5();
				if (!StrUtil.isNullOrEmpty(strOld)) {
					return ERegResponse.success;
				}
			} else if (result.startsWith(head)) {
				String[] regInfo = result.split(":");
				regResponse = ERegResponse.registered;
				if (regInfo.length > 1) {
					// 保存到文件
					regResponse = ERegResponse.success;
					wirteRegisterInfo(regInfo[1], tParam);
				} else {
					if ("".equals(AppContext.getAppContext().getMD5())) {
						regResponse = ERegResponse.fail;
					} else {
						regResponse = ERegResponse.success;
					}
				}
			} else if (result.contains("网络异常")) {
				regResponse = ERegResponse.netError;
			} else {
				regResponse = ERegResponse.other;
			}
		}
		// --------------------------------------------------------------------------------------------------
		return regResponse;
	}

	/**
	 * 写注册信息
	 * 
	 * @param serverValidateInfo
	 * @param tParam2
	 */
	private void wirteRegisterInfo(String serverValidateInfo, TransferParam tp) {

		AppContext.getAppContext().setMD5(serverValidateInfo);
	}

	/**
	 * 验证传输数据
	 * 
	 * @param tp
	 * @return
	 */
	@SuppressWarnings("static-access")
	private boolean validateTransferPara(TransferParam tp) {
		if (tp == null) {
			return false;
		}
		tp.setMd5("");
		return true;
	}

	/**
	 * 验证是否注册
	 * 
	 * @param tp
	 * @return
	 */
	private boolean isRegister(TransferParam tp) {
		String md5 = AppContext.getAppContext().getMD5();
		if (strUtil.isNullOrEmpty(md5)) {
			return false;
		}
		tp.setMd5(md5);
		return true;
	}

	/**
	 * 下载
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String getDownData(ParamAnalyst pl) throws Exception {
		if (pl == null) {
			return EDownError.otherError.toString().trim();
		}

		if (!validateTransferPara(tParam)) {
			return EDownError.paramError.toString().trim();
		}
		if (!isRegister(tParam)) {
			return EDownError.unRegisterError.toString().trim();
		}
		String result = wcfClient.downData(tParam.ToJson(), pl.paramToString());

		if (!StrUtil.isNullOrEmpty(result)) {
			if (tParam.isCompress()) {
				result = GZipUtil.gZipUnString(result);
			}
		}
		return result;
	}

	/***
	 * 上传
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public String uploadData(com.cneop.stoExpress.datacenter.upload.UpContentStructure cs) throws Exception {
		String zcfj = "", ZBFJ = "", ZBFB = "", ZDFJ = "";

		if (cs == null) {
			return EDownError.otherError.toString().trim();
		}
		String content = cs.getUploadStructure();

		// String fj = cs.fj();// 装袋页面发件
		if (cs.getScanType().equals("ZC")) { // 装车发件需要上传两个文件ZC,FJ
			zcfj = cs.getUploadStructure_hkfj(); // 装车发件。
		}
		if (cs.getScanType().equals("ZB")) {
			ZBFJ = cs.getUploadStructure_hkfj();
			ZBFB = cs.getUploadStructure_hkfb();
		}
		if (cs.getScanType().equals("ZD")) { //
			ZDFJ = cs.getUploadStructure_hkfj(); //
		}
		String head = "anyType";
		String result = "";
		if (!validateTransferPara(tParam)) {
			return EDownError.paramError.toString().trim();
		}
		if (!isRegister(tParam)) {
			return EDownError.unRegisterError.toString().trim();
		}
		// --------------------------------------------------------------------------
		String transferPara = tParam.ToJson();// 巴枪配置参数
		if (tParam.isCompress()) {
			content = GZipUtil.gZipString(content);
			// fj = GZipUtil.gZipString(fj);// 装袋界面发件
			zcfj = GZipUtil.gZipString(zcfj);
			ZBFJ = GZipUtil.gZipString(ZBFJ);
			ZBFB = GZipUtil.gZipString(ZBFB);
			ZDFJ = GZipUtil.gZipString(ZDFJ);

		}
		result = wcfClient.uploadData(transferPara, content);// 上传数据

		// -------------------------------------------------------
		if (BaggingOutActivity.ZD_FJ.equals("ZD_FJ")) {
			// result = wcfClient.uploadData(transferPara, fj);
			result = wcfClient.uploadData(transferPara, ZDFJ);
		}

		if (cs.getScanType().equals("ZC")) {// 装车发件需要上传两个文件ZC,FJ

			result = wcfClient.uploadData(transferPara, zcfj);
		}
		// 航空装包
		if (cs.getScanType().equals("ZB")) {
			result = wcfClient.uploadData(transferPara, ZBFJ);
			result = wcfClient.uploadData(transferPara, ZBFB);
		}
		System.out.println("===================>>> \t" + cs.getScanType());
		if (result.startsWith(head)) {
			return EDownError.noError.toString().trim();
		}
		return result;
	}

	/**
	 * 扩展方法
	 * 
	 * @param op
	 * @param paramStr
	 * @return
	 * @throws Exception
	 */
	private String expandMethod(RequestOp op, String paramStr) throws Exception {
		String result = "";
		if (!validateTransferPara(tParam)) {
			return EDownError.paramError.toString().trim();
		}
		if (!isRegister(tParam)) {
			return EDownError.unRegisterError.toString().trim();
		}
		String transferPara = tParam.ToJson();
		StringBuilder paramSb = new StringBuilder();
		paramSb.append("<request><code>");
		paramSb.append(op.toString().trim());
		paramSb.append("</code><param>");
		paramSb.append(paramStr);
		paramSb.append("</param></request>");
		result = wcfClient.expandMethod(transferPara, paramSb.toString().trim());
		if (tParam.isCompress()) {
			result = GZipUtil.gZipUnString(result);
		}
		return result;
	}

	/**
	 * 单号跟踪
	 * 
	 * @param barcode
	 * @return
	 * @throws Exception
	 */
	public String barcodeTrack(String barcode) throws Exception {
		return expandMethod(RequestOp.track, barcode);
	}

	/**
	 * 下载订单
	 * 
	 * @param stationName
	 * @param numId
	 * @param maxOrderTime
	 * @param deviceId
	 * @return
	 * @throws Exception
	 */
	public String getOrder(String stationName, String numId, String maxOrderTime, String deviceId) throws Exception {
		String splitStr = ",";
		StringBuilder paramSb = new StringBuilder();
		paramSb.append(stationName).append(splitStr);
		paramSb.append(numId).append(splitStr);
		paramSb.append(maxOrderTime).append(splitStr);
		paramSb.append(deviceId);
		return expandMethod(RequestOp.getCallCenterOrderByID, paramSb.toString().trim());
	}

	/**
	 * 获取订单状态
	 * 
	 * @param deviceId
	 * @param maxOrderTime
	 * @param minOrderTime
	 * @return
	 * @throws Exception
	 */
	public String getOrderStatus(String deviceId, String maxOrderTime, String minOrderTime) throws Exception {
		String splitStr = ",";
		StringBuilder paramSb = new StringBuilder();
		paramSb.append(deviceId).append(splitStr);
		paramSb.append(minOrderTime).append(splitStr);
		paramSb.append(maxOrderTime);
		return expandMethod(RequestOp.getOrderStatus, paramSb.toString().trim());
	}

	/**
	 * 订单上传
	 * 
	 * @return
	 * @throws Exception
	 */
	public String uploadOrder(UpOrderStructure orderStructure) throws Exception {
		if (orderStructure == null) {
			return EDownError.otherError.toString().trim();
		}
		String param = orderStructure.toParamString();
		return expandMethod(orderStructure.getOp(), param);
	}
}
