package com.swater.meimeng.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatCodePointException;
import java.util.Iterator;
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
public class XmlDataGreeting {
	static XmlDataGreeting ins = new XmlDataGreeting();

	private XmlDataGreeting() {
	};

	public static XmlDataGreeting getSingle() {

		return ins;
	}

	static final String TAG = XmlDataGreeting.class.getSimpleName();
	List<GreetVo> data_all = new ArrayList<GreetVo>();
	private static Map<String, List<GreetVo>> Cato_data = new HashMap<String, List<GreetVo>>();
	/** 缓存数据防止多次加载 */
	private static Map<String, Map<String, List<GreetVo>>> cache_cato_list = new HashMap<String, Map<String, List<GreetVo>>>();
	private static Map<String, PersonDataCato> pool_cache = new HashMap<String, PersonDataCato>();
	HashSet<String> Cato_types = new HashSet<String>();
	HashSet<String> Cato_types_type_text_kinds = new HashSet<String>();
	final String key_sex = "sex";
	final String key_type_id = "type_id";
	final String key_value_type = "value_type";
	final String key_type_text = "type_text";
	final String key_value_text = "value_text";
	final String cache_key = "data_ls_greet";
	public void Force_Release(){
		
		cache_cato_list.clear();
		pool_cache.clear();
		Cato_data.clear();
		Cato_types_type_text_kinds.clear();
		Cato_types.clear();
		ins=null;
	}

	// final String file_Name = "m_options.xml";
	// <sex>1</sex>
	// <type_id>1</type_id>
	// <value_type>1</value_type>
	// <type_text>初次印象</type_text>
	// <value_text>有太多的话，到了嘴边，却只剩下一句轻轻的问候：你好。</value_text>

	public class GreetVo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7221949334608251891L;

		String key_sex = "sex";
		String key_type_id = "type_id";
		String key_value_type = "value_type";
		String key_type_text = "type_text";
		String key_value_text = "value_text";
		String cache_key = "data_ls_greet";

		public GreetVo() {
		}

		public String getKey_sex() {
			return key_sex;
		}

		public void setKey_sex(String key_sex) {
			this.key_sex = key_sex;
		}

		public String getKey_type_id() {
			return key_type_id;
		}

		public void setKey_type_id(String key_type_id) {
			this.key_type_id = key_type_id;
		}

		public String getKey_value_type() {
			return key_value_type;
		}

		public void setKey_value_type(String key_value_type) {
			this.key_value_type = key_value_type;
		}

		public String getKey_type_text() {
			return key_type_text;
		}

		public void setKey_type_text(String key_type_text) {
			this.key_type_text = key_type_text;
		}

		public String getKey_value_text() {
			return key_value_text;
		}

		public void setKey_value_text(String key_value_text) {
			this.key_value_text = key_value_text;
		}

		public String getCache_key() {
			return cache_key;
		}

		public void setCache_key(String cache_key) {
			this.cache_key = cache_key;
		}

		@Override
		public String toString() {
			return "GreetVo [key_sex=" + key_sex + ", key_type_id="
					+ key_type_id + ", key_value_type=" + key_value_type
					+ ", key_type_text=" + key_type_text + ", key_value_text="
					+ key_value_text + ", cache_key=" + cache_key + "]";
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
				if (data_all.size() > 1) {
					return;
				}

			}

		}
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = null;
		try {
			is = context.getAssets().open("greet.xml");
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

		GreetVo icon = null;

		int event = parser.getEventType();

		String tag_begin_parent = "RECORD";// _city";
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:

				break;
			case XmlPullParser.START_TAG:
				String tag_begin = parser.getName();

				if (parser.getName().equals(tag_begin_parent)) {
					icon = new GreetVo();

				} else if (parser.getName().equals(key_sex)) {

					String value = parser.nextText();
					Cato_types.add(value);
					icon.setKey_sex(value);

				} else if (parser.getName().equals(key_type_id)) {
					String value = parser.nextText();
					icon.setKey_type_id(value);

				} else if (key_type_text.equals(parser.getName() == null ? ""
						: parser.getName())) {
					String value = parser.nextText();
					icon.setKey_type_text(value);
					Cato_types_type_text_kinds.add(value);

				} else if (key_value_text.equals(parser.getName() == null ? ""
						: parser.getName())) {
					String value = parser.nextText();
					icon.setKey_value_text(value);
					data_all.add(icon);

				} else if (key_value_type.equals(parser.getName() == null ? ""
						: parser.getName())) {
					String value = parser.nextText();
					icon.setKey_value_type(value);

				}
				break;
			case XmlPullParser.END_TAG:
				if (tag_begin_parent.equals(parser.getName())) {
					Cato_data.put(cache_key, data_all);
//				if (is!=null) {
//					is.close();
//				}
				}
				break;
			}
			event = parser.next();
		}

		state = FaceDatasourceState.FaceDatasourceStateReady;
	}

	public List<GreetVo> getAllSet() {
		// for (iterable_type iterable_element : iterable) {
		//
		// }
		return this.data_all;
	}
	public HashSet<String> getcateKinds(){
		return this.Cato_types_type_text_kinds;
	}
	public String[] getcateKindsToArrayStr(){
		String [] arr=new String[this.Cato_types_type_text_kinds.size()];
		Iterator<String> str=Cato_types_type_text_kinds.iterator();
		int i=0;
		while (str.hasNext()) {
			arr[i]=str.next();
			i++;
			
		}
		return arr;
//		return this.Cato_types_type_text_kinds;
	}

	/**
	 * 
	 * @category 自动排序归类算法
	 * @return Map<String, List<CellVo>>
	 */
	public Map<String, List<GreetVo>> getCatoList() {
		Map<String, List<GreetVo>> his_record = new HashMap<String, List<GreetVo>>();

		/** --缓存数据防止多次加载--- */
		if (cache_cato_list.containsKey(cache_key)) {
			return cache_cato_list.get(cache_key);

		}
		if (this.data_all == null || this.data_all.size() == 0) {
			return null;

		}
		for (GreetVo cell : this.data_all) {
			for (String vatye : Cato_types) {

				if (cell.getKey_sex().equals(vatye)) {
					if (his_record.containsKey(vatye)) {
						his_record.get(vatye).add(cell);

					} else {
						List<GreetVo> ls = new ArrayList<GreetVo>();
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

	public List<GreetVo> getSexSingleData(String sex, String type_text) {
		List<GreetVo> ls=new ArrayList<XmlDataGreeting.GreetVo>();
		try {
			List<GreetVo> data = this.getCatoList().get(sex);
			if (data==null) {
				return  null;
			}
			for (GreetVo gv : data) {
				if (gv.getKey_type_text().equals(type_text)) {
					ls.add(gv);
					
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			return ls;
		}
		
		return ls;

	}
	public String[] getSexSingleDataToString(String sex, String type_text) {
		List<GreetVo> ls=new ArrayList<XmlDataGreeting.GreetVo>();
		try {
			List<GreetVo> data = this.getCatoList().get(sex);
			if (data==null) {
				return  null;
			}
			for (GreetVo gv : data) {
				if (gv.getKey_type_text().equals(type_text)) {
					ls.add(gv);
					
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		String[] str_data=new String[ls.size()];
		for (int i = 0; i < str_data.length; i++) {
			str_data[i]=ls.get(i).getKey_value_text();
			
		}
		return str_data;
		
	}

	/**
	 * 
	 * @category 得到某个分类集合
	 * @return PersonDataCato
	 */
	public PersonDataCato getSingleCato(String key) {
		List<GreetVo> ls = this.getCatoList().get(key);
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
				container[i] = ls.get(i).getKey_sex();
				// map.put(ls.get(i).getKey(), ls.get(i).getValue());
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
			this.maps_reverse = reverseMap(maps);

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
