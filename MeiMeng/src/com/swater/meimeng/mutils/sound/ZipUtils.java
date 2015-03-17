package com.swater.meimeng.mutils.sound;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.util.Log;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.GZIPException;
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZInputStream;
import com.jcraft.jzlib.ZOutputStream;
import com.jcraft.jzlib.ZStream;

public class ZipUtils {
	private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte
	// private static final String ROOTPATH =
	// "/sdcard/91golf/weibo_temp/AudioRecord/";
	private static final String ZIPFILENAME = "/sdcard/meimeng/AudioRecord/zipA.aac";
	private static final String UNCOMPRESSFILE = "/sdcard/meimeng/AudioRecord/unzip.aac";

	// 输入数据的最大长度
	private static final int MAXLENGTH = 102400;
	// 设置缓存大小
	private static final int BUFFERSIZE = 1024;

	/**
	 * 把一个文件转化为字节
	 * 
	 * @param file
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] getByte(File file) throws Exception {
		byte[] bytes = null;
		if (file != null) {
			InputStream is = new FileInputStream(file);
			int length = (int) file.length();
			if (length > Integer.MAX_VALUE) // 当文件的长度超过了int的最大值
			{
				Log.d("ZipUtils", "this file is max ");
				return null;
			}
			bytes = new byte[length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			// 如果得到的字节长度和file实际的长度不一致就可能出错了
			if (offset < bytes.length) {
				Log.d("ZipUtils", "file length is error");
				return null;
			}
			is.close();
		} else {
			Log.d("ZipUtils", "file is not exist!");
		}
		return bytes;
	}

	/***
	 * 
	 * @param file
	 *            源文件
	 * @return
	 * @throws Exception
	 */
	public static byte[] zipFileToByte(String filePath) throws Exception {
		Log.i("liujiang", filePath);
		GZIPOutputStream zipout = new GZIPOutputStream(new FileOutputStream(
				ZIPFILENAME));
		byte buffer[] = new byte[BUFF_SIZE];
		if (filePath != null) {
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(filePath), BUFF_SIZE);
			int realLength;
			while ((realLength = in.read(buffer)) != -1) {
				zipout.write(buffer, 0, realLength);
			}
			in.close();
			// zipout.flush();
			zipout.finish();
			zipout.close();
		}
		// upzzzzzz();
		File f = new File(ZIPFILENAME);
		return getByte(f);
	}

	public static void upzzzzzz() throws Exception {
		GZIPInputStream in = new GZIPInputStream(new FileInputStream(
				ZIPFILENAME));
		FileOutputStream out = new FileOutputStream(UNCOMPRESSFILE);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}

		in.close();
		out.close();
	}

	public static void unZipByInputStream(InputStream is, String tmpSound_url)
			throws Exception {
		GZIPInputStream in = new GZIPInputStream(is);
		FileOutputStream out = new FileOutputStream(tmpSound_url);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}

		in.close();
		out.close();
	}

	/**
	 * ZLib压缩数据
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */

	public static byte[] zLib(byte[] bContent) throws IOException {
		byte[] data = null;
		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ZOutputStream zOut = new ZOutputStream(out,
					JZlib.Z_DEFAULT_COMPRESSION); // 压缩级别,缺省为1级
			DataOutputStream objOut = new DataOutputStream(zOut);
			objOut.write(bContent);
			objOut.flush();
			zOut.close();

			data = out.toByteArray();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return data;
	}

	/**
	 * ZLib解压数据
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */

	public static byte[] unZLib(byte[] bContent) throws IOException {
		byte[] data = new byte[MAXLENGTH];
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bContent);
			ZInputStream zIn = new ZInputStream(in);
			DataInputStream objIn = new DataInputStream(zIn);
			int len = 0;
			int count = 0;

			while ((count = objIn.read(data, len, len + BUFFERSIZE)) != -1) {
				len = len + count;
			}

			byte[] trueData = new byte[len];
			System.arraycopy(data, 0, trueData, 0, len);
			objIn.close();
			zIn.close();
			in.close();
			return trueData;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static byte[] unZlib(InputStream is) throws IOException{
		return unZLib(InputStreamToByte(is));
	}

	/***
	 * 压缩Zip
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */

	public static byte[] zip(byte[] bContent) throws IOException {
		byte[] b = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ZipOutputStream zip = new ZipOutputStream(bos);
			ZipEntry entry = new ZipEntry("zip");
			entry.setSize(bContent.length);
			zip.putNextEntry(entry);
			zip.write(bContent);
			zip.closeEntry();
			zip.close();
			b = bos.toByteArray();
			bos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	/***
	 * 解压Zip
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */

	public static byte[] unZip(byte[] bContent) throws IOException {
		byte[] b = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bContent);
			ZipInputStream zip = new ZipInputStream(bis);

			while (zip.getNextEntry() != null) {
				byte[] buf = new byte[1024];
				int num = -1;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while ((num = zip.read(buf, 0, buf.length)) != -1) {
					baos.write(buf, 0, num);
				}
				b = baos.toByteArray();
				baos.flush();
				baos.close();
			}
			zip.close();
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	
	/**
	 * GZip压缩数据
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public static byte[] gZip(byte[] bContent) throws IOException {
		byte[] data = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream gOut = new GZIPOutputStream(out, bContent.length);
			// 压缩级别,缺省为1级
			DataOutputStream objOut = new DataOutputStream(gOut);
			objOut.write(bContent);
			objOut.flush();
			gOut.close();
			data = out.toByteArray();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return data;
	}

	/**
	 * GZip解压数据
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public static byte[] unGZip(byte[] bContent) throws IOException {
		byte[] data = new byte[MAXLENGTH];
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bContent);
			GZIPInputStream pIn = new GZIPInputStream(in);
			DataInputStream objIn = new DataInputStream(pIn);
			int len = 0;
			int count = 0;
			while ((count = objIn.read(data, len, len + BUFFERSIZE)) != -1) {
				len = len + count;
			}
			byte[] trueData = new byte[len];
			System.arraycopy(data, 0, trueData, 0, len);
			objIn.close();
			pIn.close();
			in.close();
			return trueData;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static byte[] myDeflater(byte[] bContent){
		//*
		int err;
	    int comprLen=40000;
	    byte[] compr=new byte[comprLen];

	    Deflater deflater = null;

	    try {
	      deflater = new Deflater(JZlib.Z_DEFAULT_COMPRESSION, JZlib.DEF_WBITS + 16);
	    }
	    catch(GZIPException e){
	      // never happen, because argument is valid.
	    }

	    deflater.setInput(bContent);
	    deflater.setOutput(compr);
	    while(deflater.total_in!=bContent.length && deflater.total_out<comprLen){
	      deflater.avail_in=deflater.avail_out=1; // force small buffers
	      err=deflater.deflate(JZlib.Z_NO_FLUSH);
	      CHECK_ERR(deflater, err, "deflate");
	    }

	    while(true){
	      deflater.avail_out=1;
	      err=deflater.deflate(JZlib.Z_FINISH);      
	      if(err==JZlib.Z_STREAM_END)break;
	      CHECK_ERR(deflater, err, "deflate");
	    }

	    err=deflater.end();      
	    CHECK_ERR(deflater, err, "deflateEnd");
	    return compr;
	    //*/
		/*
		Deflater deflater = new Deflater();
		deflater.setInput(bContent);
		deflater.finish();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		while (!deflater.finished()) {
			int byteCount = deflater.deflate(buf);
			baos.write(buf, 0, byteCount);
		}
		deflater.end();

		byte[] compressedBytes = baos.toByteArray();
		return compressedBytes;
		*/
	}
	
	public static byte[] myInflater(byte[] bContent) throws IOException, DataFormatException, AssertionError{
		/*
		int err;
	    int comprLen=40000;
	    int uncomprLen=comprLen;
	    //byte[] compr=new byte[comprLen];
	    byte[] uncompr=new byte[uncomprLen];
		
	    Inflater inflater = null;
	    try {
	      inflater = new Inflater(JZlib.DEF_WBITS + 32);
	    }
	    catch(GZIPException e){
	    }


	    inflater.setInput(bContent);
	    inflater.setOutput(uncompr);

	    err=inflater.init();
	    CHECK_ERR(inflater, err, "inflateInit");

	    while(inflater.total_out<uncomprLen &&
	      inflater.total_in<comprLen) {
	      inflater.avail_in=inflater.avail_out=1;  force small buffers 
	      err=inflater.inflate(JZlib.Z_NO_FLUSH);
	      if(err==JZlib.Z_STREAM_END) break;
	      CHECK_ERR(inflater, err, "inflate");
	    }

	    err=inflater.end();
	    CHECK_ERR(inflater, err, "inflateEnd");

	    return uncompr;
	    */
	    //byte[] compressedBytes = ...
		int cachesize = 1024; 
		byte output[] = new byte[0];
	     int decompressedByteCount = bContent.length; // From your format's metadata.
	     Inflater inflater = new Inflater();
	     inflater.setInput(bContent);
	     
	     ByteArrayOutputStream o = new ByteArrayOutputStream(bContent.length); 
	     try {
	    	 byte[] buf = new byte[cachesize];
	    	 int got; 
	    	 while (!inflater.finished()) { 
	    	 got = inflater.inflate(buf); 
	     		o.write(buf, 0, got); 
	    	 } 
	    	 output = o.toByteArray(); 
	     }catch(Exception e){ 
	    	 e.printStackTrace(); 
	     }finally {
	    	 try { 
	    		 o.close(); 
	    	 } catch (IOException e) { 
	    		 e.printStackTrace(); 
	    	 } 
	     } 
	     return output; 
	}
	
	static void CHECK_ERR(ZStream z, int err, String msg) {
	    if(err!=JZlib.Z_OK){
	      if(z.msg!=null) System.out.print(z.msg+" "); 
	      Log.d("ZipUtils", msg+" error: "+err); 
	      //System.exit(1);
	    }
	}
	
	private static byte[] InputStreamToByte(InputStream is) throws IOException { 
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream(); 
		int ch; 
		while ((ch = is.read()) != -1) { 
		bytestream.write(ch); 
		} 
		byte imgdata[] = bytestream.toByteArray(); 
		bytestream.close(); 
		return imgdata; 
	}
}
