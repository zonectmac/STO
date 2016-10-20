package com.cneop.stoExpress.datacenter.download;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import com.cneop.stoExpress.dao.OrderAbnormalService;
import com.cneop.util.DateUtil;
import android.content.Context;
import android.util.Xml;

public class OrderAbnormal implements IDownload {

	OrderAbnormalService orderAbnormalService;
	@Override
	public int dataProcessing(Context context, String downStr) {
	 
		int result=0;
		setContext(context);
		if (!strUtil.isNullOrEmpty(downStr)) {
			List<com.cneop.stoExpress.model.OrderAbnormal> orderAbnormalList = null;
			try {
				orderAbnormalList = parseOrderAbnormalStr(downStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (orderAbnormalList != null && orderAbnormalList.size() > 0) {
				result = orderAbnormalService.addRecord(orderAbnormalList);
			}
		}
		return result;
	}
	
	private void setContext(Context context){
		if(orderAbnormalService==null){
			orderAbnormalService=new OrderAbnormalService(context);
		}
	}
	
	private List<com.cneop.stoExpress.model.OrderAbnormal> parseOrderAbnormalStr(String xmlStr)
			throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(Charset
				.forName("utf-8")));

		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		com.cneop.stoExpress.model.OrderAbnormal orderAbnormal = null;
		List<com.cneop.stoExpress.model.OrderAbnormal> orderAbnormalList = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_DOCUMENT:
				orderAbnormalList = new ArrayList<com.cneop.stoExpress.model.OrderAbnormal>();
				break;
			case XmlPullParser.START_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					orderAbnormal = new com.cneop.stoExpress.model.OrderAbnormal();
				} else if (orderAbnormal != null) {
					if ("NO".equalsIgnoreCase(parser.getName())) {
						orderAbnormal.setCode(parser.nextText());
					} else if ("RETURNREASON"
							.equalsIgnoreCase(parser.getName())) {
						orderAbnormal.setReasonDesc(parser.nextText());
					} else if ("OPERFLAG".equalsIgnoreCase(parser.getName())) {
						String stateStr = parser.nextText();
						if (!stateStr.equalsIgnoreCase("D")) {
							stateStr = "A";
						}
						orderAbnormal.setState(stateStr);
					} else if ("LASTUPDATE".equalsIgnoreCase(parser.getName())) {
						String lastTimeStr = DateUtil.formatTimeStr(
								parser.nextText(), "yyyy-MM-dd HH:mm:ss");
						orderAbnormal.setLasttime(lastTimeStr);
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					if (orderAbnormal != null && orderAbnormalList != null) {
						orderAbnormalList.add(orderAbnormal);
						orderAbnormal = null;
					}
				}
				break;
			}
			type = parser.next();
		}
		is.close();
		return orderAbnormalList;
	}

}
