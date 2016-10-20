package com.cneop.util.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.util.Base64;

/*
 * ZIP解压缩操作类
 */
public class ZipUtil {

	public static final int BUFFER = 1024 * 8;

	/*
	 * Bzip2压缩
	 */
	public static String compressBZip2(String str) {
		String data = null;
		try {
			byte[] bContent = str.getBytes();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BZip2CompressorOutputStream zip2out = new BZip2CompressorOutputStream(out);
			zip2out.write(bContent);
			zip2out.flush();
			zip2out.close();
			data = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/*
	 * Bzip2解压
	 */
	public static String decompressBZip2(String str) {
		String data = "";
		try {
			byte[] bContent = Base64.decode(str, Base64.DEFAULT);
			ByteArrayInputStream bis = new ByteArrayInputStream(bContent);
			BZip2CompressorInputStream zip = new BZip2CompressorInputStream(bis);
			ByteArrayOutputStream decompre = new ByteArrayOutputStream();
			decompress(zip, decompre);
			byte[] b = decompre.toByteArray();
			data = new String(b);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data;
	}

	private static void decompress(CompressorInputStream cis, OutputStream os) {
		try {
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = cis.read(data, 0, BUFFER)) != -1) {
				os.write(data, 0, count);
			}
			cis.close();

			os.flush();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 解压ZIP文件
	 * 
	 * @param zipFileName
	 * @param outputDirectory
	 * @throws IOException
	 */
	public static String AppName;

	@SuppressWarnings("unchecked")
	public static void unzip(String zipFileName, String outputDirectory) throws IOException {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFileName);
			Enumeration e = zipFile.entries();
			ZipEntry zipEntry = null;
			File dest = new File(outputDirectory);
			dest.mkdirs();
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				String entryName = zipEntry.getName();
				AppName = zipEntry.getName();
				InputStream in = null;
				FileOutputStream out = null;
				try {
					if (zipEntry.isDirectory()) {
						String name = zipEntry.getName();
						name = name.substring(0, name.length() - 1);
						File f = new File(outputDirectory + File.separator + name);
						f.mkdirs();
					} else {
						int index = entryName.lastIndexOf("\\");
						if (index != -1) {
							File df = new File(outputDirectory + File.separator + entryName.substring(0, index));
							df.mkdirs();
						}
						index = entryName.lastIndexOf("/");
						if (index != -1) {
							File df = new File(outputDirectory + File.separator + entryName.substring(0, index));
							df.mkdirs();
						}
						File f = new File(outputDirectory + File.separator + zipEntry.getName());
						// f.createNewFile();
						in = zipFile.getInputStream(zipEntry);
						out = new FileOutputStream(f);
						int c;
						byte[] by = new byte[1024];
						while ((c = in.read(by)) != -1) {
							out.write(by, 0, c);
						}
						out.flush();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
					throw new IOException("解压失败：" + ex.toString().trim());
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException ex) {
						}
					}
					if (out != null) {
						try {
							out.close();
						} catch (IOException ex) {
						}
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new IOException("解压失败：" + ex.toString().trim());
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException ex) {
				}
			}
		}
	}
}
