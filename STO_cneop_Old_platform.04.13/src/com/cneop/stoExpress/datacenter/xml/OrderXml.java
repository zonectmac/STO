package com.cneop.stoExpress.datacenter.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.cneop.stoExpress.dao.OrderService;
import com.cneop.stoExpress.model.Order;
import com.cneop.util.DateUtil;

import android.content.Context;
import android.util.Xml;

public class OrderXml implements IDownloadXml{

	public List<Order> parseOrderStr(String xmlStr) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(Charset
				.forName("utf-8")));

		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		Order order = null;
		List<Order> orderList = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("code".equalsIgnoreCase(parser.getName())) {
					if (parser.nextText().equals("00")) {
						orderList = new ArrayList<Order>();
					}
				} else if (orderList != null) {
					if ("row".equalsIgnoreCase(parser.getName())) {
						order = new Order();
					} else if (order != null) {
						if ("logisticid".equalsIgnoreCase(parser.getName())) {
							order.setLogisticid(parser.nextText());
						} else if ("acceptdate".equalsIgnoreCase(parser
								.getName())) {
							String acceptDateStr = DateUtil.formatTimeStr(
									parser.nextText(), "yyyy-MM-dd HH:mm:ss");
							order.setAcceptDate(acceptDateStr);
						} else if ("customercode".equalsIgnoreCase(parser
								.getName())) {
							order.setCustomerCode(parser.nextText());
						} else if ("customername".equalsIgnoreCase(parser
								.getName())) {
							order.setCustomerName(parser.nextText());
						} else if ("sender_name".equalsIgnoreCase(parser
								.getName())) {
							order.setSender_Name(parser.nextText());
						} else if ("sender_address".equalsIgnoreCase(parser
								.getName())) {
							order.setSender_Address(parser.nextText());
						} else if ("sender_phone".equalsIgnoreCase(parser
								.getName())) {
							order.setSender_Phone(parser.nextText());
						} else if ("sender_mobile".equalsIgnoreCase(parser
								.getName())) {
							order.setSender_Mobile(parser.nextText());
						} else if ("destcode"
								.equalsIgnoreCase(parser.getName())) {
							order.setDestcode(parser.nextText());
						} else if ("cusnote".equalsIgnoreCase(parser.getName())) {
							order.setCusnote(parser.nextText());
						}
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("row".equalsIgnoreCase(parser.getName())) {
					if (order != null && orderList != null) {
						orderList.add(order);
						order = null;
					}
				}
				break;
			}
			type = parser.next();
		}
		is.close();
		return orderList;
	}

	@Override
	public int dataProcessing(Context context, String downStr) throws Exception {
	 
		int result = 0;
		setContent(context);
		List<Order> orderlist = parseOrderStr(downStr);
		if(orderlist.size() > 0){
			result = orderService.addRecord(orderlist);
		}
		return result;
	}
	
	OrderService orderService;
	
	private void setContent(Context context){
		if(orderService == null){
			orderService = new OrderService(context);
		}
	}
}
