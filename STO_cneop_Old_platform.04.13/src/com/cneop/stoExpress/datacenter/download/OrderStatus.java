package com.cneop.stoExpress.datacenter.download;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import com.cneop.stoExpress.dao.OrderService;
import com.cneop.stoExpress.model.Order;
import com.cneop.util.StrUtil;

import android.content.Context;
import android.util.Xml;

public class OrderStatus {

	OrderService orderService;
	StrUtil strUtil;
	String userNo;

	public OrderStatus(String userNo) {
		this.userNo = userNo;
	}

	public List<Order> dataProcessing(Context context, String downStr) {
	 
		setContext(context);
		List<Order> orderList = null;
		if (!strUtil.isNullOrEmpty(downStr)) {
			List<Order> orderTempList = null;
			try {
				orderTempList = parseOrderStatusStr(downStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (orderTempList != null && orderTempList.size() > 0) {
				orderList = new ArrayList<Order>();
				for (Order order : orderTempList) {
					Order t = orderService.getOrder(order.getLogisticid(),
							userNo, "0");
					if (t != null) {
						t.setIsUrge(order.getIsUrge());
						if (order.getIsUrge() == 1) {
							// 催促
							orderList.add(0, t);
						} else if (order.getIsUrge() == 2) {
							// 取消
							orderList.add(t);
						}
					}
				}
				int result = orderService.updateOrderStatus(orderList);
				if (result > 0) {
					return orderList;
				}
			}
		}
		return null;
	}

	private void setContext(Context context) {
		if (orderService == null) {
			orderService = new OrderService(context);
			strUtil=new StrUtil();
		}
	}

	private List<com.cneop.stoExpress.model.Order> parseOrderStatusStr(String xmlStr)
			throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(Charset
				.forName("utf-8")));

		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		com.cneop.stoExpress.model.Order order = null;
		List<com.cneop.stoExpress.model.Order> orderList = null;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("code".equalsIgnoreCase(parser.getName())) {
					if (parser.nextText().equals("00")) {
						orderList = new ArrayList<com.cneop.stoExpress.model.Order>();
					}
				} else if (orderList != null) {
					if ("row".equalsIgnoreCase(parser.getName())) {
						order = new com.cneop.stoExpress.model.Order();
					} else if (order != null) {
						if ("logisticid".equalsIgnoreCase(parser.getName())) {
							order.setLogisticid(parser.nextText());
						} else if ("remind".equalsIgnoreCase(parser.getName())) {
							String remind = parser.nextText();
							if (remind.equals("订单催促")) {
								order.setIsUrge(1);
							} else if (remind.equals("订单取消")) {
								order.setIsUrge(2);
							}
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

}
