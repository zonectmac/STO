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
	 * ���÷�������ַ
	 * 
	 * @param url
	 */

	public void setUrl(String url) {
		wcfClient.setUrl(url);
	}

	/**
	 * ���ô������
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
	 * ��÷�����ʱ��
	 * 
	 * @return
	 */
	public String getServerTime() {
		return wcfClient.getServerTime();
	}

	/**
	 * ���ô������
	 * 
	 * @param tp
	 */
	private TransferParam tParam;// �������

	public void setParam(TransferParam tp) {
		this.tParam = tp;
	}

	/**
	 * ע��:0�����쳣 1 δ���ò��� 2ע��ɹ� 3��ע�� 4����
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	public ERegResponse register() {
		ERegResponse regResponse = ERegResponse.netError;
		if (!validateTransferPara(this.tParam)) {// ��������Ϊ��
			regResponse = ERegResponse.paramError;
			return regResponse;
		}
		String head = "reg";
		String registerInfo = this.tParam.ToJson();
		String result = wcfClient.register(registerInfo);// register
		// --------------------------------------------------------------------------------------------------
		if (result.length() > 0) {
			if (result.contains("ORA-00001")) {// ORA-00001: Υ��ΨһԼ������
				// �п����ǰ�ǹID�ظ������,����ʱ��ô����,��ʱ���IT��ȷ��һ��ʲôԭ�������
				String strOld = AppContext.getAppContext().getMD5();
				if (!StrUtil.isNullOrEmpty(strOld)) {
					return ERegResponse.success;
				}
			} else if (result.startsWith(head)) {
				String[] regInfo = result.split(":");
				regResponse = ERegResponse.registered;
				if (regInfo.length > 1) {
					// ���浽�ļ�
					regResponse = ERegResponse.success;
					wirteRegisterInfo(regInfo[1], tParam);
				} else {
					if ("".equals(AppContext.getAppContext().getMD5())) {
						regResponse = ERegResponse.fail;
					} else {
						regResponse = ERegResponse.success;
					}
				}
			} else if (result.contains("�����쳣")) {
				regResponse = ERegResponse.netError;
			} else {
				regResponse = ERegResponse.other;
			}
		}
		// --------------------------------------------------------------------------------------------------
		return regResponse;
	}

	/**
	 * дע����Ϣ
	 * 
	 * @param serverValidateInfo
	 * @param tParam2
	 */
	private void wirteRegisterInfo(String serverValidateInfo, TransferParam tp) {

		AppContext.getAppContext().setMD5(serverValidateInfo);
	}

	/**
	 * ��֤��������
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
	 * ��֤�Ƿ�ע��
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
	 * ����
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
	 * �ϴ�
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

		// String fj = cs.fj();// װ��ҳ�淢��
		if (cs.getScanType().equals("ZC")) { // װ��������Ҫ�ϴ������ļ�ZC,FJ
			zcfj = cs.getUploadStructure_hkfj(); // װ��������
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
		String transferPara = tParam.ToJson();// ��ǹ���ò���
		if (tParam.isCompress()) {
			content = GZipUtil.gZipString(content);
			// fj = GZipUtil.gZipString(fj);// װ�����淢��
			zcfj = GZipUtil.gZipString(zcfj);
			ZBFJ = GZipUtil.gZipString(ZBFJ);
			ZBFB = GZipUtil.gZipString(ZBFB);
			ZDFJ = GZipUtil.gZipString(ZDFJ);

		}
		result = wcfClient.uploadData(transferPara, content);// �ϴ�����

		// -------------------------------------------------------
		if (BaggingOutActivity.ZD_FJ.equals("ZD_FJ")) {
			// result = wcfClient.uploadData(transferPara, fj);
			result = wcfClient.uploadData(transferPara, ZDFJ);
		}

		if (cs.getScanType().equals("ZC")) {// װ��������Ҫ�ϴ������ļ�ZC,FJ

			result = wcfClient.uploadData(transferPara, zcfj);
		}
		// ����װ��
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
	 * ��չ����
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
	 * ���Ÿ���
	 * 
	 * @param barcode
	 * @return
	 * @throws Exception
	 */
	public String barcodeTrack(String barcode) throws Exception {
		return expandMethod(RequestOp.track, barcode);
	}

	/**
	 * ���ض���
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
	 * ��ȡ����״̬
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
	 * �����ϴ�
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
