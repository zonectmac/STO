package com.cneop.stoExpress.datacenter.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.cneop.stoExpress.dao.UserXmlService;
import com.cneop.stoExpress.model.User;
import com.cneop.util.DateUtil;

import android.content.Context;
import android.util.Xml;


public class UserXml implements IDownloadXml{

	/**
	 * 解析员工数据
	 * 
	 * @param xmlStr
	 * @return
	 * @throws XmlPullParserException
	 */
	public List<User> parseUserStr(String xmlStr) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(Charset
				.forName("utf-8")));

		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		User user = null;
		List<User> userList = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_DOCUMENT:
				userList = new ArrayList<User>();
				break;
			case XmlPullParser.START_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					user = new User();
				} else if (user != null) {
					if ("EMPLOYEENO".equalsIgnoreCase(parser.getName())) {
						user.setUserNo(parser.nextText());
					} else if ("EMPLOYEE".equalsIgnoreCase(parser.getName())) {
						user.setUserName(parser.nextText().replace("\'", "_"));
					} else if ("AREACODE".equalsIgnoreCase(parser.getName())) {
						user.setStationId(parser.nextText());
					} else if ("PASSWORD".equalsIgnoreCase(parser.getName())) {
						user.setPassword(parser.nextText());
					} else if ("BELONGPOST".equalsIgnoreCase(parser.getName())) {
						user.setUserType(parser.nextText().equals("业务员") ? "1"
								: "2");
					} else if ("OPERFLAG".equalsIgnoreCase(parser.getName())) {
						String stateStr = parser.nextText();
						if (!stateStr.equalsIgnoreCase("D")) {
							stateStr = "A";
						}
						user.setState(stateStr);
					} else if ("LASTUPDATE".equalsIgnoreCase(parser.getName())) {
						String lastTimeStr = DateUtil.formatTimeStr(
								parser.nextText(), "yyyy-MM-dd HH:mm:ss");
						user.setLasttime(lastTimeStr);
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("Row".equalsIgnoreCase(parser.getName())) {
					if (user != null && userList != null) {
						userList.add(user);
						user = null;
					}
				}
				break;
			}
			type = parser.next();
		}
		is.close();
		return userList;
	}

	@Override
	public int dataProcessing(Context context, String downStr) throws Exception {
	 
		int result = 0;
		setContext(context);
		List<User> userList = parseUserStr(downStr);
		if(userList.size() > 0){
			result = userService.addRecord(userList);
		}
		return result;
	}
	
	UserXmlService userService;
	private void setContext(Context context) {
	 
		if (userService == null) {
			userService = new UserXmlService(context);
		}
	}
}
