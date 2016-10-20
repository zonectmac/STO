package com.cneop.stoExpress.datacenter.download;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import com.cneop.stoExpress.dao.DestinationService;
import com.cneop.stoExpress.model.DestinationStation;
import com.cneop.util.DateUtil;
import com.cneop.util.HzPyUtil;
import android.content.Context;
import android.util.Xml;

public class Destination implements IDownload {

	DestinationService destinationService;
	HzPyUtil hzpyUtil;

	@Override
	public int dataProcessing(Context context, String downStr) {
	 
		int result = 0;
		setContext(context);
		if (!strUtil.isNullOrEmpty(downStr)) {
			List<com.cneop.stoExpress.model.DestinationStation> destinationList = null;
			try {
				destinationList = parseDestinationStr(downStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (destinationList != null && destinationList.size() > 0) {
				result = destinationService.addRecord(destinationList);
			}
		}
		return result;
	}

	private void setContext(Context context) {
		if (destinationService == null) {
			destinationService = new DestinationService(context);
			hzpyUtil=new HzPyUtil();
		}
	}
	
	private List<DestinationStation> parseDestinationStr(String xmlStr)
			throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(Charset
				.forName("utf-8")));

		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		DestinationStation destinationStation = null;
		List<DestinationStation> destinationStationList = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_DOCUMENT:
				destinationStationList = new ArrayList<DestinationStation>();
				break;
			case XmlPullParser.START_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					destinationStation = new DestinationStation();
				} else if (destinationStation != null) {
					if ("PROVINCENO".equalsIgnoreCase(parser.getName())) {
						destinationStation.setProvinceNo(parser.nextText());
					} else if ("PROVINCE".equalsIgnoreCase(parser.getName())) {
						String provinceName = parser.nextText();
						destinationStation.setProvince(provinceName);
						destinationStation.setAlphabet(hzpyUtil
								.getAllFirstLetter(provinceName));
					} else if ("OPERFLAG".equalsIgnoreCase(parser.getName())) {
						String stateStr = parser.nextText();
						if (!stateStr.equalsIgnoreCase("D")) {
							stateStr = "A";
						}
						destinationStation.setState(stateStr);
					} else if ("LASTUPDATE".equalsIgnoreCase(parser.getName())) {
						String lastTimeStr = DateUtil.formatTimeStr(
								parser.nextText(), "yyyy-MM-dd HH:mm:ss");
						destinationStation.setLasttime(lastTimeStr);
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					if (destinationStation != null
							&& destinationStationList != null) {
						destinationStationList.add(destinationStation);
						destinationStation = null;
					}
				}
				break;
			}
			type = parser.next();
		}
		is.close();
		return destinationStationList;
	}

}
