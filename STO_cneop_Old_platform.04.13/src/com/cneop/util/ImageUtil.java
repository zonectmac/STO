package com.cneop.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;

public class ImageUtil {

	/**
	 * �ļ�תbase64
	 * 
	 * @param filePath
	 * @return
	 */
	public static String fileToBase64(String filePath) {
		File file = new File(filePath);
		try {
			FileInputStream inputfile = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			inputfile.read(buffer);
			inputfile.close();
			return Base64.encodeToString(buffer, Base64.NO_WRAP);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * �ƶ�ͼƬ
	 * 
	 * @param srcFileName
	 *          :ԭ·��
	 * @param destDirName Ŀ��·��
	 * @return
	 */
	public static boolean movePic(String srcFileName, String destDirName) {
		FileInputStream fin;
		try {
			if (!new File(srcFileName).exists()) {
				return false;
			}
			fin = new FileInputStream(srcFileName);
			int length = fin.available();

			byte[] buffer = new byte[length];
			fin.read(buffer);
			fin.close();

			FileOutputStream fout = new FileOutputStream(destDirName);
			fout.write(buffer);
			fout.close();
			return true;

		} catch (Exception e) {
			Log.e("movePic", e.getMessage());
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * base64תͼƬ
	 * 
	 * @param imgStr
	 * @param imgFilePath
	 * @return
	 */
	public static boolean GenerateImage(String imgStr, String imgFilePath) {// ���ֽ������ַ�������Base64���벢����ͼƬ
		if (imgStr == null)
			// ͼ������Ϊ��
			return false;
		try {
			// Base64����
			byte[] bytes = Base64.decode(imgStr, Base64.NO_WRAP);
			for (int i = 0; i < bytes.length; ++i) {
				if (bytes[i] < 0) {// �����쳣����
					bytes[i] += 256;
				}
			}
			// ����jpegͼƬ
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(bytes);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static Bitmap readBitmap(String imgPath) {
		try {
			return BitmapFactory.decodeFile(imgPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param imgPath
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap zoomImg(String imgPath, int newWidth, int newHeight) {
		Bitmap bm = readBitmap(imgPath);
		return zoomImg(bm, newWidth, newHeight);
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param bm
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// ���ͼƬ�Ŀ��
		int width = bm.getWidth();
		int height = bm.getHeight();
		// �������ű���
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// ȡ����Ҫ���ŵ�matrix����
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// �õ��µ�ͼƬ
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		return newbm;
	}

	/**
	 * �������
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * �������ͼ
	 * 
	 * @param imgPath
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromPath(String imgPath,int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgPath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(imgPath, options);
	}

}
