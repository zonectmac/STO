package com.cneop.stoExpress.datacenter.download;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import com.cneop.stoExpress.dao.NextStationService;
import com.cneop.util.DateUtil;

import android.content.Context;
import android.util.Xml;

public class NextStation implements IDownload {

	NextStationService nextStationService;
	
	@Override
	public int dataProcessing(Context context, String downStr) {
	 
		int result=0;
		setContext(context);
		if (!strUtil.isNullOrEmpty(downStr)) {
			List<com.cneop.stoExpress.model.NextStation> nextStationList = null;
			try {
				nextStationList = parseNextStationStr(downStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (nextStationList != null && nextStationList.size() > 0) {
				result = nextStationService.addRecord(nextStationList);
			}
		}
		return result;
	}
	
	private void setContext(Context context){
		if(nextStationService==null){
			nextStationService=new NextStationService(context);
		}
	}
	
	private List<com.cneop.stoExpress.model.NextStation> parseNextStationStr(String xmlStr) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(Charset
				.forName("utf-8")));

		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		com.cneop.stoExpress.model.NextStation nextStation = null;
		List<com.cneop.stoExpress.model.NextStation> nextStationList = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_DOCUMENT:
				nextStationList = new ArrayList<com.cneop.stoExpress.model.NextStation>();
				break;
			case XmlPullParser.START_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					nextStation = new com.cneop.stoExpress.model.NextStation();
				} else if (nextStation != null) {
					if ("NEXTSITENO".equalsIgnoreCase(parser.getName())) {
						nextStation.setStationId(parser.nextText());
					} else if ("NEXTSITENAME"
							.equalsIgnoreCase(parser.getName())) {
						nextStation.setStationName(parser.nextText());
					} else if ("OPERFLAG".equalsIgnoreCase(parser.getName())) {
						String stateStr = parser.nextText();
						if (!stateStr.equalsIgnoreCase("D")) {
							stateStr = "A";
						}
						nextStation.setState(stateStr);
					} else if ("LASTUPDATE".equalsIgnoreCase(parser.getName())) {
						String lastTimeStr = DateUtil.formatTimeStr(
								parser.nextText(), "yyyy-MM-dd HH:mm:ss");
						nextStation.setLasttime(lastTimeStr);
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					if (nextStation != null && nextStationList != null) {
						nextStationList.add(nextStation);
						nextStation = null;
					}
				}
				break;
			}
			type = parser.next();
		}
		is.close();
		return nextStationList;
	}

}
