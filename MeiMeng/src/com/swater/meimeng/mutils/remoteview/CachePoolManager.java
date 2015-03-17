package com.swater.meimeng.mutils.remoteview;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.swater.meimeng.mutils.NSlog.NSLoger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 缓存管理器
 * 
 * 
 * @version 1.0
 */
public final class CachePoolManager {
	// protected static ExecutorService pool_cache = Executors
	// .newCachedThreadPool();
	// 缓存临时目录
	public static final File TEMP_DIR = new File(
			Environment.getExternalStorageDirectory(),
			"Android/data/com.meimeng.app/cache");
	public static final File TEMP_DIR_index = new File(
			Environment.getExternalStorageDirectory(),
			"Android/data/com.meimeng.app/cache");
	private static final File TEMP_SOUND_DIR = new File(
			Environment.getExternalStorageDirectory(), "Android/data/com.meimeng.app/cache");
	private static Logger logger = Logger.getLogger(CachePoolManager.class
			.getName());
	static final int DEFAULT_MAX_WIDTH = 200;

	static {
		// 如果缓存文件夹不存在，创建之
		if (!TEMP_DIR.exists()) {
			TEMP_DIR.mkdirs();
		}
		if (!TEMP_DIR_index.exists()) {
			TEMP_DIR_index.mkdirs();
		}
		if (!TEMP_SOUND_DIR.exists()) {
			TEMP_SOUND_DIR.mkdirs();
		}
	}

	/**
	 * 载入[或下载]并返回缓存资源(主方法)<br>
	 * 不会出错，要么返回BYTE[0]要么返回数据<br>
	 * 缓存添加成功与否都不会返回任何异常信息
	 * 
	 * @param url
	 *            资源地址
	 * 
	 * @return 资源数据
	 */
	public static byte[] loadCacheOrDownload(String url) {

		if (url.indexOf("&width=") != -1) {
			// String zoomStr = url.substring(url.indexOf("&width="),
			// url.length());
			// logger.log(Level.INFO,"zoomStr "+zoomStr);
		} else {
			// url = url + "&max=240";
		}

		File tempFile = new File(TEMP_DIR, parseUrlToTempFile(url));
		// logger.log(Level.INFO, "load cache or download, url is : " + url
		// +"; temp file is : " + tempFile.getAbsolutePath());
		byte[] buffers = null;

		// String path = tempFile.getAbsolutePath();
		// Log.d("--loadCacheOrDownload-->getAbsolutePath-->", path + "");
		if (tempFile.exists()) {
			// logger.info("read data from cache");
			// read data from cache
			try {
				return readByteByFile(tempFile);
			} catch (CachePoolException e) {
				logger.severe("read byte from file error : " + e.getMessage());
			}
		}
		// download and save to cache
		// logger.info("download and save to cache");
		try {
			// pool_cache.
			buffers = readByteByURL(url);
			if (buffers == null) {
				return null;
			}

			saveCache(url, buffers);
			return buffers;
		} catch (CachePoolException e) {
			logger.severe("read byte from url error : " + e.getMessage());
		} catch (Exception e) {
			// logger.severe("can not load resource from : " + url +
			// " : "+e.getMessage());
		}
		return new byte[0];
	}

	public static boolean isExistFile(String urlfile) {
		File tempFile = new File(TEMP_DIR, parseUrlToTempFile(urlfile));

		return tempFile.exists();
	}

	public static String getExistFile(String urlfile) {
		File tempFile = new File(TEMP_DIR, parseUrlToTempFile(urlfile));

		return tempFile.getPath();
	}

	public static File getFileUrl(String urlfile) {
		File tempFile = new File(TEMP_DIR, parseUrlToTempFile(urlfile));

		return tempFile;
	}

	// 首页推荐
	public static File getIndexFileUrl(String urlfile) {
		File tempFile = new File(TEMP_DIR_index, parseUrlToTempFile(urlfile));

		return tempFile;
	}

	public static String getExistFileIndex(String urlfile) {
		File tempFile = new File(TEMP_DIR_index, parseUrlToTempFile(urlfile));

		return tempFile.getPath();
	}

	public static boolean isIndexExistFile(String urlfile) {
		File tempFile = new File(TEMP_DIR_index, parseUrlToTempFile(urlfile));

		return tempFile.exists();

	}

	public static String getFilePath(String urlfile) {

		File tempFile = new File(TEMP_DIR_index, parseUrlToTempFile(urlfile));
		return tempFile.getPath();

	}

	public static String loadCacheOrDownloadSound(String url) {
		File tempFile = new File(TEMP_SOUND_DIR, parseVoiceToTempFile(url));
		if (tempFile.exists()) {
			// logger.info("read data from cache");
			// read data from cache
			return tempFile.getAbsolutePath();
		} else {
			try {
				writeSoundTmpFileBySoundURL(url, tempFile.getAbsolutePath());
				return tempFile.getAbsolutePath();
			} catch (Exception e) {
				// logger.severe("can not load resource from : " + url +
				// " : "+e.getMessage());
			}
		}
		return "";
	}

	/**
	 * 保存缓存文件并返回数据
	 * 
	 * @param url
	 * @return
	 * @throws CachePoolException
	 */
	public static void saveCache(String url, byte[] buffers) {
		OutputStream tos = null;
		// 检查能否创建缓存文件
		try {
			File temFile = new File(TEMP_DIR, parseUrlToTempFile(url));
			// Log.d("--saveCache---->>temFile--" + temFile.getAbsolutePath(),
			// "--saveCache--");
			tos = new FileOutputStream(temFile);
		} catch (FileNotFoundException e1) {
			logger.severe("can not create cache file :" + e1.getMessage());
			return;
		}
		// 写入流数据到缓存文件
		try {
			tos.write(buffers);
		} catch (MalformedURLException e) {
			logger.severe(e.getMessage());
		} catch (IOException e) {
			logger.severe(e.getMessage());
		} finally {
			try {
				tos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 从文件读取数据
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] readByteByFile(File file) throws CachePoolException {

		NSLoger.Log("---读取本地缓存文件--" + file.getAbsolutePath());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			int len = 0;
			while ((len = bis.read(buff)) != -1) {
				baos.write(buff, 0, len);
			}
		} catch (Exception e) {
			throw new CachePoolException("loading cache file error :"
					+ e.getMessage(), e);
		} catch (OutOfMemoryError e) {

			System.gc();
			return null;
		} finally {
			try {
				bis.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		byte[] bytes = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
			logger.severe("close baos error :" + e.getMessage());
		}
		return bytes;
	}

	/**
	 * 读取URL流
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static byte[] readByteByURL(String url) throws Exception {
		URLConnection connection = null;
		InputStream uis = null;
		// byte[] buff = new byte[256];
		byte[] buff = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffers = null;
		try {
			// URL _url = new URL(BaseConnect.SCALEIMAGE.concat("?url=") +
			// url);// SCALE_IMAGE_URL.concat("?url=")+url+"&max=240"
			URL _url = new URL(url);// SCALE_IMAGE_URL.concat("?url=")+url+"&max=240"
			connection = _url.openConnection();
			connection.setDoInput(true);
			uis = connection.getInputStream();
			if (uis == null) {
				return null;
			}
			int len = 0;
			while ((len = uis.read(buff)) != -1) {
				baos.write(buff, 0, len);
			}
			buffers = baos.toByteArray();
		} catch (MalformedURLException e) {
			throw e;
		} catch (OutOfMemoryError e) {
			System.gc();
			System.runFinalization();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				baos.close();
			} catch (Exception e) {
			}
			try {
				uis.close();
			} catch (Exception e) {
			}
		}
		return buffers;
	}

	// 将InputStream转换成Bitmap
	public static Bitmap InputStream2Bitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

	public static InputStream ReadNewMethd(String url) {
		InputStream is = null;
		try {
			FileOutputStream fos = new FileOutputStream(parseUrlToTempFile(url));
			is = new URL(url).openStream();

			// time2 = System.currentTimeMillis();

			int data = is.read();
			while (data != -1) {
				fos.write(data);
				data = is.read();
			}
			is.close();
			fos.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
		return is;
	}

	/***
	 * 从网络获取数据后解压保存到本地为缓存文件
	 * 
	 * @param url
	 * @param soundTmp_url
	 * @throws Exception
	 */
	private static void writeSoundTmpFileBySoundURL(String url,
			String soundTmp_url) throws Exception {
		URLConnection connection = null;
		InputStream uis = null;
		GZIPInputStream gzipIn = null;
		byte[] buf = new byte[1024];

		FileOutputStream out = new FileOutputStream(soundTmp_url);
		try {
			URL _url = new URL(url);
			connection = _url.openConnection();
			connection.setDoInput(true);
			uis = connection.getInputStream();
			try {
				gzipIn = new GZIPInputStream(uis);
				int len;
				while ((len = gzipIn.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			} catch (Exception e) {
				// 可能不是压缩文件,尝试直接写
				int len;
				while ((len = uis.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			}
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
			try {
				uis.close();
			} catch (Exception e) {
			}
			try {
				gzipIn.close();
			} catch (Exception e2) {
			}
		}
	}

	/**
	 * 移出指定缓存数据
	 * 
	 * @param url
	 */
	public static void remoteCache(String url) {
		File tmpFile = new File(TEMP_DIR, parseUrlToTempFile(url));
		if (!tmpFile.exists()) {
			return;
		}
		if (!tmpFile.delete()) {
			tmpFile.deleteOnExit();
		}
	}

	/**
	 * 移出所有缓存数据
	 */
	public static void removeAllCache() {
		File[] files = TEMP_DIR.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		for (File file : files) {
			if (!file.delete()) {
				file.deleteOnExit();
			}
		}
	}

	public static String parseUrlToTempFile(String url) {
		String isurl = validateUrl(url);
		return isurl.replaceAll("/|\\\\|:", "");
	}

	public static String parseVoiceToTempFile(String isurl) {
		if (isurl == null || TextUtils.isEmpty(isurl) || "null".equals(isurl)) {
			return "";
		}
		return isurl.replaceAll("/|\\\\|:", "");
	}

	/**
	 * 验证是否是以图片结尾！以保证缓存成功！
	 */
	public static String validateUrl(String url) {
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		if (isEndImg(url) == false) {
			// sb.append(".png");
		}
		return sb.toString();
	}

	public static boolean isEndImg(String url) {
		boolean flag = true;
		if (url.lastIndexOf(".png") == -1 || url.lastIndexOf(".jpg") == -1
				|| url.lastIndexOf(".jpeg") == -1) {
			flag = false;
		}
		return flag;
	}

}
