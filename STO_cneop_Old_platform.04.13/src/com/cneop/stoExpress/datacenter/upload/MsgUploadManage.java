package com.cneop.stoExpress.datacenter.upload;

import java.util.ArrayList;
import java.util.List;
import com.cneop.stoExpress.dao.MsgSendService;
import com.cneop.stoExpress.model.MsgSend;
import com.cneop.util.json.JsonUtil;
import com.cneop.util.net.Request;

import android.content.Context;

public class MsgUploadManage {

	private String url;
	MsgSendService msgSendService;

	public MsgUploadManage(String url, Context context) {
		this.url = url;
		msgSendService = new MsgSendService(context);
	}
	
	public void setUrl(String url){
		this.url=url;
	}

	/**
	 * ÉÏ´«¶ÌÐÅ
	 * 
	 * @return
	 */
	public int uploadMsgData() {
		int count=-1;
		List<MsgSend> list = msgSendService.getUnUpload();
		if (list != null && list.size() > 0) {
			List<MsgManDao> manDaoList = new ArrayList<MsgManDao>();
			for (MsgSend msgSend : list) {
				MsgManDao model = new MsgManDao();
				model.setCode(msgSend.getBarcode());
				model.setMobile(msgSend.getPhone());
				model.setSiteserver(msgSend.getServerNo());
				manDaoList.add(model);
			}
			String postData = "args=" + JsonUtil.listToJson(manDaoList);
			String result = Request.Post(postData, url, true);
			if (result.equals("OK_RECV")) {
				count = msgSendService.updateStatus(list);
			} 
		}
		return count;
	}
}
