package com.cneop.stoExpress.datacenter.upload;

import java.util.List;

import com.cneop.stoExpress.dao.OrderOperService;
import com.cneop.stoExpress.datacenter.msd.MSDServer;
import com.cneop.stoExpress.common.Enums.*;
import com.cneop.stoExpress.model.OrderOperating;

import android.content.Context;

public class OrderUploadManage {

	OrderOperService orderOperService;
	Context context;
	int size = 30;

	public OrderUploadManage(Context context) {
		orderOperService = new OrderOperService(context);
		this.context = context;
	}

	/**
	 * 上传订单
	 * 
	 * @return
	 */
	public int uploadOrder() {
		int count = 0;
		count += uploadOrder(EOrderStatus.accept,
				RequestOp.acceptCallCenterOrder);

		count += uploadOrder(EOrderStatus.noAccept,
				RequestOp.notAcceptCallCenterOrder);
		return count;
	}

	private int uploadOrder(EOrderStatus orderStatus, RequestOp op) {
		int count = 0;
		int t = orderOperService.getCountByOper(String.valueOf(orderStatus
				.value()));
		int num = t / size;
		if (t % size != 0) {
			num++;
		}
		for (int i = 0; i < num; i++) {
			List<OrderOperating> acceptOrderList = orderOperService
					.getOrderByFlag(orderStatus.value());
			if (acceptOrderList != null && acceptOrderList.size() > 0) {
				com.cneop.stoExpress.datacenter.msd.UpOrderStructure acceptStructure = new com.cneop.stoExpress.datacenter.msd.UpOrderStructure();
				acceptStructure.setOp(op);
				acceptStructure.setOrderOperList(acceptOrderList);
				String result = "";
				try {
					result = MSDServer.getInstance(context).uploadOrder(
							acceptStructure);
					if (!result.equalsIgnoreCase(EDownError.paramError
							.toString().trim())
							&& !result
									.equalsIgnoreCase(EDownError.unRegisterError
											.toString().trim())) {
						if (result.contains("00")) {
							// 更新上传状态
							count = orderOperService
									.updateStatus(acceptOrderList);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return count;
	}
}
