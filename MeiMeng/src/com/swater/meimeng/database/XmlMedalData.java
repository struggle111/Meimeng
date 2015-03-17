package com.swater.meimeng.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.swater.meimeng.database.Map_Province.CityVo;
import com.swater.meimeng.database.Map_Province.ProvinceVo;
import com.swater.meimeng.mutils.NSlog.NSLoger;

/**
 * @category 个人资料相关分类算法
 */
public class XmlMedalData {
	static XmlMedalData ins = new XmlMedalData();

	private XmlMedalData() {
	};

	public static XmlMedalData getSingle() {

		return ins;
	}
	

	static final String TAG = XmlMedalData.class.getSimpleName();
	List<MedalData> data_all = new ArrayList<MedalData>();
	private static Map<String, String> data_map = new HashMap<String, String>();
	/** 缓存数据防止多次加载 */
	Map<String, Map<String, String>> cache_data = new HashMap<String,Map<String,String>>();
	 final String data_key = "datakey";
	// final String key_field = "field";
	// final String key_value = "value";
	final String cache_key = "data_ls";
	final String key_medal_id = "mid";
	final String key_medal_name = "name";

	// final String file_Name = "m_options.xml";

	public class MedalData implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7221949334608251891L;
		String medal_id = "";
		String medal_name = "";

		public MedalData() {
		}

		public String getMedal_id() {
			return medal_id;
		}

		public void setMedal_id(String medal_id) {
			this.medal_id = medal_id;
		}

		public String getMedal_name() {
			return medal_name;
		}

		public void setMedal_name(String medal_name) {
			this.medal_name = medal_name;
		}

	}

	/**
	 * 数据状态
	 */
	public FaceDatasourceState state = FaceDatasourceState.FaceDatasourceStateUnknown;

	/**
	 * 
	 * @param context
	 * @param reload
	 * @throws IOException
	 * @throws XmlPullParserException
	 */

	public final void init(Context context, boolean reload) throws IOException,
			XmlPullParserException {
		// if (this.data_all != null) {
		// if (cache_cato_list.containsKey(cache_key)) {
		// if (data_all.size() > 1) {
		// return;
		// }
		//
		// }
		//
		// }
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = null;
		try {
			is = context.getAssets().open("medal_data.xml");
		} catch (IOException e) {
			throw e;
		}

		if (is == null) {
			return;
		}

		try {
			parser.setInput(is, "UTF-8");
		} catch (XmlPullParserException e) {
			is.close();
			throw e;
		}

		MedalData icon = null;

		int event = parser.getEventType();

		String tag_begin_parent = "RECORD";// _city";
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:

				break;
			case XmlPullParser.START_TAG:
				String tag_begin = parser.getName();

				if (parser.getName().equals(tag_begin_parent)) {
					icon = new MedalData();

				} else if (parser.getName().equals(key_medal_id)) {

					String value = parser.nextText();
					icon.setMedal_id(value);

				} else if (parser.getName().equals(key_medal_name)) {
					String value = parser.nextText();
					icon.setMedal_name(value);
					data_map.put(icon.getMedal_id(), icon.getMedal_name());
					data_all.add(icon);

				}
				break;
			case XmlPullParser.END_TAG:
				if (tag_begin_parent.equals(parser.getName())) {
					cache_data.put(data_key, data_map);
				}
				break;
			}
			event = parser.next();
		}

		state = FaceDatasourceState.FaceDatasourceStateReady;
	}

	public List<MedalData> getAllData() {
		return data_all;
	}
	public String getMedalNameById(String mid) {
		String name_medal="";
		if (data_map==null||data_map.size()<1) {
			data_map=cache_data.get(data_key);
		}
		try {
			
			name_medal=data_map.get(mid.trim());
		} catch (Exception e) {
			NSLoger.Log("--解析勋章！---发生异常---");
			return "";
		}
		return name_medal;
	}

	public final void init(Context context) throws XmlPullParserException,
			IOException {
		if (null == context) {

		} else {
			this.init(context, false);
		}

	}

	public static enum FaceDatasourceState {
		FaceDatasourceStateUnknown, FaceDatasourceStateReady
	}
}
