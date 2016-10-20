package com.cneop.stoExpress.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.cneop.stoExpress.model.PhotoModel;
import com.cneop.stoExpress.model.QueryView;
import com.cneop.stoExpress.model.UploadView;
import com.cneop.stoExpress.common.Enums.*;
import com.cneop.util.StrUtil;
import com.cneop.util.file.FileUtil;

public class PhotoService {

	String signUnPicPath; // ǩ��δ�ϴ�
	String problemUnPicPath; // �����δ�ϴ�
	String signPicPath; // ǩ�����ϴ�
	String problemPicPath; // ��������ϴ�
	StrUtil strUtil;
	String picSuffix;

	public PhotoService(String signUnPicPath, String problemUnPicPath, String suffix) {
		this.signUnPicPath = signUnPicPath;
		this.problemUnPicPath = problemUnPicPath;
		this.picSuffix = suffix;
	}

	public PhotoService(String signUnPicPath, String signPicPath, String problemUnPicPath, String problemPicPath, String suffix) {
		this.signUnPicPath = signUnPicPath;
		this.problemUnPicPath = problemUnPicPath;
		this.signPicPath = signPicPath;
		this.problemPicPath = problemPicPath;
		strUtil = new StrUtil();
		this.picSuffix = suffix;
	}

	/**
	 * δ�ϴ���Ƭ��ͼ
	 * 
	 * @return
	 */
	public List<UploadView> getUnUploadView() {
		List<UploadView> list = new ArrayList<UploadView>();
		int signCount = FileUtil.getCountInDir(signUnPicPath);
		int problemCount = FileUtil.getCountInDir(problemUnPicPath);
		if (signCount > 0) {
			UploadView signUploadView = new UploadView();
			signUploadView.setScanTypeStr("ǩ��ͼƬ");
			signUploadView.setScanType("sign");
			signUploadView.setTotalCount(signCount);
			signUploadView.setUploadType("pic");
			list.add(signUploadView);
		}
		if (problemCount > 0) {
			UploadView signUploadView = new UploadView();
			signUploadView.setScanTypeStr("�����ͼƬ");
			signUploadView.setScanType("problem");
			signUploadView.setTotalCount(problemCount);
			signUploadView.setUploadType("pic");
			list.add(signUploadView);
		}
		return list;
	}

	/**
	 * ǩ��ͼƬ��ѯ
	 * 
	 * @param barcode
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public QueryView getSignQueryView(String barcode, String startTime, String endTime) {
		return getQueryView(barcode, startTime, endTime, "ǩ��ͼƬ", signPicPath, signUnPicPath);
	}

	/**
	 * �����ͼƬ
	 * 
	 * @param barcode
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public QueryView getProblemQueryView(String barcode, String startTime, String endTime) {
		return getQueryView(barcode, startTime, endTime, "�����ͼƬ", problemPicPath, problemUnPicPath);
	}

	/**
	 * ��ѯ��ͼ
	 * 
	 * @return
	 */
	private QueryView getQueryView(String barcode, String startTime, String endTime, String scanTypeStr, String path, String unPath) {
		QueryView queryView = null;
		int unCount = 0, count = 0;
		if (!strUtil.isNullOrEmpty(barcode)) {
			// ����ͼƬ
			unCount = FileUtil.isExist(unPath + barcode + picSuffix, false);
			count = FileUtil.isExist(path + barcode + picSuffix, false);
		} else {
			unCount = FileUtil.getCountInDir(unPath, startTime, endTime);
			count = FileUtil.getCountInDir(path, startTime, endTime);
		}
		if (unCount > 0 || count > 0) {
			queryView = getQueryView(scanTypeStr, unCount, count);
		}
		return queryView;
	}

	private QueryView getQueryView(String scanTypeStr, int unFinish, int finish) {
		QueryView queryView = new QueryView();
		queryView.setModuleName(scanTypeStr);
		int count = unFinish + finish;
		queryView.setTotalDataCount(count);
		queryView.setUnUpload(unFinish);
		queryView.setScanType(EScanType.OTHER.toString());
		queryView.setUploadType("pic");
		return queryView;
	}

	/**
	 * ���ͼƬ����
	 * 
	 * @param barcode
	 * @param startTime
	 * @param endTime
	 * @param uploadStatus
	 * @param picType
	 * @return
	 */
	public List<PhotoModel> queryDetail(String barcode, String startTime, String endTime, String uploadStatus, EPicType picType) {
		List<PhotoModel> photoModelList = new ArrayList<PhotoModel>();
		StringBuilder pathUnUpload = new StringBuilder();// ���ϴ�
		StringBuilder pathUpload = new StringBuilder();// δ�ϴ�
		getQueryPath(pathUnUpload, pathUpload, picType, uploadStatus);
		if (!strUtil.isNullOrEmpty(barcode)) {
			// ����ͼƬ
			PhotoModel photoModel = new PhotoModel();
			String filePath = pathUnUpload.toString() + barcode + picSuffix;
			if (!strUtil.isNullOrEmpty(pathUnUpload.toString()) && FileUtil.isExist(filePath, false) > 0) {
				photoModel.setBarcode(barcode);
				photoModel.setUploadStatus("δ�ϴ�");
			} else {
				filePath = pathUpload.toString() + barcode + picSuffix;
				if (!strUtil.isNullOrEmpty(pathUpload.toString()) && FileUtil.isExist(filePath, false) > 0) {
					photoModel.setBarcode(barcode);
					photoModel.setUploadStatus("���ϴ�");
				}
			}
			photoModelList.add(photoModel);
		} else {
			List<File> unUploadFileList = FileUtil.getFileByCondition(pathUnUpload.toString(), startTime, endTime);
			getPhotoModel(unUploadFileList, "δ�ϴ�", photoModelList);
			List<File> uploadFileList = FileUtil.getFileByCondition(pathUpload.toString(), startTime, endTime);
			getPhotoModel(uploadFileList, "���ϴ�", photoModelList);
		}
		return photoModelList;
	}

	private void getPhotoModel(List<File> fileList, String uploadStatus, List<PhotoModel> photoModelList) {
		if (fileList != null && fileList.size() > 0) {
			for (File file : fileList) {
				PhotoModel model = new PhotoModel();
				String fileName = file.getName();
				model.setBarcode(fileName.substring(0, fileName.length() - 4));
				model.setUploadStatus(uploadStatus);
				photoModelList.add(model);
			}
		}
	}

	private void getQueryPath(StringBuilder pathUnUpload, StringBuilder pathUpload, EPicType picType, String uploadStatus) {
		if (picType == EPicType.sign) {
			if (!strUtil.isNullOrEmpty(uploadStatus)) {
				if (uploadStatus.equals("1")) {
					// ���ϴ�
					pathUpload.append(signPicPath);
				} else if (uploadStatus.equals("0")) {
					// δ�ϴ�
					pathUnUpload.append(signUnPicPath);
				}
			} else {
				// ��ѯȫ��
				pathUpload.append(signPicPath);
				pathUnUpload.append(signUnPicPath);
			}
		} else if (picType == EPicType.problem) {
			if (!strUtil.isNullOrEmpty(uploadStatus)) {
				if (uploadStatus.equals("1")) {
					// ���ϴ�
					pathUpload.append(problemPicPath);
				} else if (uploadStatus.equals("0")) {
					// δ�ϴ�
					pathUnUpload.append(problemUnPicPath);
				}
			} else {
				// ��ѯȫ��
				pathUpload.append(problemPicPath);
				pathUnUpload.append(problemUnPicPath);
			}
		}
	}
}
