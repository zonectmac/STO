package com.cneop.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cneop.util.DateUtil;
import com.cneop.util.StrUtil;

public class FileUtil {

	/**
	 * 文件或目录是否存在，不存在创建
	 * 
	 * @param path
	 */
	public static int isExist(String path, boolean isCreate) {
		int count = 0;
		File file = new File(path);
		boolean flag = file.exists();
		if (!flag && isCreate) {
			file.mkdirs();
		}
		if (flag) {
			count++;
		}
		return count;
	}

	/**
	 * 移动文件 (该方法的局限性是: 移动的的目录要同级别,这个很关键,你交换的两个文件夹要有相同的层数.)
	 * 
	 * @param srcFileName
	 *            源文件完整路径
	 * @param destDirName
	 *            目的目录完整路径 注:本方法无法判断文件是否移动成功,判断是否成功,请在调用此方法后,调用new
	 *            File(destDirName).exists()来判断 如加在此方法内判断,经实践发现不行.
	 */
	public static void moveFile(String srcFileName, String destDirName) {

		if (srcFileName.contains("/")) {
			srcFileName = srcFileName.replace("/", "//");
		}
		if (destDirName.contains("/")) {
			destDirName = destDirName.replace("/", "//");
		}
		File srcFile = new File(srcFileName);
		if (!srcFile.exists() || !srcFile.isFile())
			return;

		getPathWritingPopedom(srcFileName);
		getPathWritingPopedom(destDirName);

		srcFile.renameTo(new File(destDirName));
	}

	/**
	 * 获取文件的读写权限
	 * 
	 * @param path
	 */
	public static void getPathWritingPopedom(String path) {
		String permission = "777";

		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得目录下文件的个数
	 * 
	 * @param dirPath
	 * @return
	 */
	public static int getCountInDir(String dirPath) {
		int count = 0;
		File dir = new File(dirPath);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null) {
				count = files.length;
			}
		}
		return count;
	}

	/**
	 * 根据条件获得目录下的文件数量
	 * 
	 * @param dirPath
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int getCountInDir(String dirPath, String startTime, String endTime) {
		int count = 0;
		List<File> fileList = getFileByCondition(dirPath, startTime, endTime);
		count = fileList.size();
		return count;
	}

	/**
	 * 根据条件获得目录下的文件
	 * 
	 * @param dirPath
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<File> getFileByCondition(String dirPath, String startTime, String endTime) {
		List<File> fileList = new ArrayList<File>();
		StrUtil strUtil = new StrUtil();
		if (strUtil.isNullOrEmpty(dirPath)) {
			return fileList;
		}
		File dir = new File(dirPath);
		if (dir.isDirectory()) {
			DateUtil dateUtil = new DateUtil();
			String pattern = "yyyy-MM-dd HH:mm";
			Date startTimeD = dateUtil.getDateFormStr(startTime, pattern);
			Date endTimeD = dateUtil.getDateFormStr(endTime, pattern);
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					long lastUpdateTime = files[i].lastModified();
					if (lastUpdateTime >= startTimeD.getTime() && lastUpdateTime <= endTimeD.getTime()) {
						fileList.add(files[i]);
					}
				}
			}
		}
		return fileList;
	}

	/**
	 * 删除文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean delFile(String path) {
		File file = new File(path);
		if (file.isFile()) {
			return file.delete();
		}
		return false;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public void copyFolder(String oldPath, String newPath) {

		try {
			isExist(newPath, true); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString().trim());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 删除指定日期以前的文件
	 * 
	 * @param dirPath
	 * @param date
	 */
	public static void deleteFileByDate(File file, java.util.Date date) {
		if (file.exists() == false) {
			return;
		} else {

			if (file.isFile()) {
				if (file.lastModified() < date.getTime()) {
					file.delete();
				}
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					if (file.lastModified() < date.getTime()) {
						file.delete();
					}

					return;
				}
				for (File f : childFile) {
					deleteFileByDate(f, date);
				}
				file.delete();
			}
		}
	}

}
