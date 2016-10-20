package com.cneop.stoExpress.datacenter.upload;

import java.util.List;

import com.cneop.stoExpress.model.OrderOperating;
import com.cneop.stoExpress.common.Enums.RequestOp;

/**
 * 订单上传格式
 * 
 * @author Administrator
 * 
 */
public class UpOrderStructure {
	StringBuilder sb;
	private List<OrderOperating> orderOperList;
	private RequestOp op;

	public void setOrderOperList(List<OrderOperating> orderOperList) {
		this.orderOperList = orderOperList;
	}

	public void setOp(RequestOp op) {
		if (op == RequestOp.acceptCallCenterOrder
				|| op == RequestOp.notAcceptCallCenterOrder) {
			this.op = op;
		}
	}	

	public RequestOp getOp() {
		return op;
	}

	public UpOrderStructure() {
		sb = new StringBuilder();
		sb.append("<data>");
	}

	public String toParamString() {
		if (op == null || orderOperList == null || orderOperList.size() == 0) {
			return "";
		}
		for (OrderOperating orderOper : orderOperList) {
			sb.append("<row><employeecode>").append(orderOper.getUserNo())
					.append("</employeecode>");
			sb.append("<employeename>").append(orderOper.getEmployeeName())
					.append("</employeename>");
			sb.append("<employeesite>").append(orderOper.getEmployeeSite())
					.append("</employeesite>");
			sb.append("<employeesitecode>")
					.append(orderOper.getEmployeeSiteCode())
					.append("</employeesitecode>");
			sb.append("<logisticid>").append(orderOper.getLogisticid())
					.append("</logisticid>");
			if (op == RequestOp.acceptCallCenterOrder) {
				// 提取
				sb.append("<billcode>").append(orderOper.getBarcode())
						.append("</billcode>");
				sb.append("<scantime>").append(orderOper.getScantime())
						.append("</scantime></row>");
			} else {
				// 打回
				sb.append("<scantime>").append(orderOper.getScantime())
						.append("</scantime>");
				sb.append("<reason>").append(orderOper.getReasonCode())
						.append("</reason></row>");
			}
		}
		sb.append("</data>");
		return sb.toString().trim();
	}
}
