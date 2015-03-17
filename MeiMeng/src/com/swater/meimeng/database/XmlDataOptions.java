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

/**
 * @category 个人资料相关分类算法
 */
public class XmlDataOptions {
	static XmlDataOptions ins = new XmlDataOptions();

	private XmlDataOptions() {
	};

	public static XmlDataOptions getSingle() {

		return ins;
	}

	static final String TAG = XmlDataOptions.class.getSimpleName();
	List<CellVo> data_all = new ArrayList<XmlDataOptions.CellVo>();
	private static Map<String, List<CellVo>> Cato_data = new HashMap<String, List<CellVo>>();
	/** 缓存数据防止多次加载 */
	private static Map<String, Map<String, List<CellVo>>> cache_cato_list = new HashMap<String, Map<String, List<CellVo>>>();
	private static Map<String, PersonDataCato> pool_cache = new HashMap<String, PersonDataCato>();
	HashSet<String> Cato_types = new HashSet<String>();
	final String key_key = "key";
	final String key_field = "field";
	final String key_value = "value";
	final String cache_key = "data_ls";
	final String file_Name = "m_options.xml";
	public void ClearAllCache(){
		cache_cato_list.clear();
		Cato_data.clear();
		Cato_types.clear();
		pool_cache.clear();
		data_all.clear();
		
		
	}

	public class CellVo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7221949334608251891L;
		String field = "";
		String key = "";
		String value = "";

		public CellVo() {
}
		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "CellVo [field=" + field + ", key=" + key + ", value="
					+ value + "]";
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
		if (this.data_all != null) {
			if (cache_cato_list.containsKey(cache_key)) {
				if (data_all.size()>1) {
					return ;
				}
				
			}

		}
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = null;
		try {
//			is = context.getAssets().open("m_options.xml");
			is = context.getAssets().open("new_ops.xml");
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

		CellVo icon = null;

		int event = parser.getEventType();

		String tag_begin_parent = "RECORD";// _city";
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:

				break;
			case XmlPullParser.START_TAG:
				String tag_begin = parser.getName();

				if (parser.getName().equals(tag_begin_parent)) {
					icon = new CellVo();

				} else if (parser.getName().equals(key_field)) {

					String value = parser.nextText();
					Cato_types.add(value);
					icon.setField(value);

				} else if (parser.getName().equals(key_key)) {
					String value = parser.nextText();
					icon.setKey(value);

				} else if (key_value.equals(parser.getName() == null ? ""
						: parser.getName())) {
					String value = parser.nextText();
					icon.setValue(value);
					data_all.add(icon);

				}
				break;
			case XmlPullParser.END_TAG:
				if (tag_begin_parent.equals(parser.getName())) {
					Cato_data.put(cache_key, data_all);
				}
				break;
			}
			event = parser.next();
		}

		state = FaceDatasourceState.FaceDatasourceStateReady;
	}

	/**
	 * 
	 * @category 自动排序归类算法
	 * @return Map<String, List<CellVo>>
	 */
	public Map<String, List<CellVo>> getCatoList() {
		Map<String, List<CellVo>> his_record = new HashMap<String, List<CellVo>>();

		/** --缓存数据防止多次加载--- */
		if (cache_cato_list.containsKey(cache_key)) {
			return cache_cato_list.get(cache_key);

		}
		if (this.data_all == null || this.data_all.size() == 0) {
			return null;

		}
		for (CellVo cell : this.data_all) {
			for (String vatye : Cato_types) {

				if (cell.getField().equals(vatye)) {
					if (his_record.containsKey(vatye)) {
						his_record.get(vatye).add(cell);

					} else {
						List<CellVo> ls = new ArrayList<XmlDataOptions.CellVo>();
						ls.add(cell);
						his_record.put(vatye, ls);

					}

					break;
				} else {
					continue;
				}

			}

		}
		cache_cato_list.put(cache_key, his_record);

		return his_record;
	}

	/**
	 * 
	 * @category 得到某个分类集合
	 * @return PersonDataCato
	 */
	public PersonDataCato getSingleCato(String key) {
		List<CellVo> ls = this.getCatoList().get(key);
		if (ls == null) {
			return null;
		} else {
			if (pool_cache.containsKey(key)) {
				return pool_cache.get(key);

			}
			PersonDataCato pdc = new PersonDataCato();
			String[] container = new String[ls.size()];
			Map<String, String> map = new HashMap<String, String>();

			for (int i = 0; i < container.length; i++) {
				container[i] = ls.get(i).getValue();
				map.put(ls.get(i).getKey(), ls.get(i).getValue());
			}
			pdc.setCato_Name(key);
			pdc.setDataArray(container);
			pdc.setMaps(map);
			pool_cache.put(key, pdc);
			return pdc;

		}

	}

	/** 自定义数据格式 */
	public class PersonDataCato {

		String[] dataArray = null;
		String cato_Name = "";
		Map<String, String> maps = null;
		Map<String, String> maps_reverse = new HashMap<String, String>();

		public String[] getDataArray() {
			return dataArray;
		}

		public void setDataArray(String[] dataArray) {
			this.dataArray = dataArray;
		}

		public String getCato_Name() {
			return cato_Name;
		}

		public void setCato_Name(String cato_Name) {
			this.cato_Name = cato_Name;
		}

		public Map<String, String> getMaps() {
			return maps;
		}

		public void setMaps(Map<String, String> maps) {
			this.maps = maps;
			this.maps_reverse=reverseMap(maps);
			
		}

		/** 反转MAP */
		public Map<String, String> getMaps_reverse() {
			return reverseMap(maps);
		}

	}

	public Map<String, String> reverseMap(Map<String, String> keysParam) {
		Map<String, String> res = new HashMap<String, String>();
		if (keysParam == null) {
			return res;

		}
		for (Entry<String, String> keys : keysParam.entrySet()) {
			res.put(keys.getValue(), keys.getKey());
		}

		return res;
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
