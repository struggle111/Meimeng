package com.swater.meimeng.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;

import com.swater.meimeng.database.Map_Province.CityVo;
import com.swater.meimeng.database.Map_Province.ProvinceVo;

/**
 * @category 城市信息
 */
public class XmlDataCity {
	static final String TAG = XmlDataCity.class.getSimpleName();
	List<ItemVo> cities_all = null;
	private static Map<String, List<ItemVo>> cache_data = new HashMap<String, List<ItemVo>>();
	private static Map<String, List<CityVo>> cache_city = new HashMap<String, List<CityVo>>();
	private static Map<String, String> map_id_city = new HashMap<String, String>();
	final String cache_key = "data_key";
	public void CloseRes(){
		cache_data.clear();
		cache_city.clear();
		map_id_city.clear();
		if (cities_all!=null) {
			cities_all.clear();
		}
		System.gc();
	}

	public class ItemVo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7221949334608251891L;
		String id = "";
		String name = "";
		String proid = "";

		public String getProid() {
			return proid;
		}

		public void setProid(String proid) {
			this.proid = proid;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
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

		XmlPullParser parser = Xml.newPullParser();
		InputStream is = null;
		try {
			is = context.getAssets().open("data_city.xml");
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

		ItemVo icon = null;

		int event = parser.getEventType();

		String tag_begin_parent = "data";// _city";
		String tag_begin_cell = "city_id";
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:

				break;
			case XmlPullParser.START_TAG:
				if (tag_begin_parent.equals(parser.getName())) {
					cities_all = new ArrayList<ItemVo>();

				}

				if (tag_begin_cell.equals(parser.getName())) {
					icon = new ItemVo();
					icon.setName(parser.getAttributeValue(0));
					icon.setId(parser.getAttributeValue(1));
					icon.setProid(parser.getAttributeValue(2));
					map_id_city.put(icon.getName(), icon.getId());
					/*
					 * icon.setKey(parser.getAttributeValue(0));
					 * icon.setFileName(parser.getAttributeValue(1));
					 */

					cities_all.add(icon);
				}
				break;
			case XmlPullParser.END_TAG:
				if (tag_begin_parent.equals(parser.getName())) {
					cache_data.put(cache_key, cities_all);
					// iconsMap.put(currentType, cities_all);
				}
				break;
			}
			event = parser.next();
		}

		state = FaceDatasourceState.FaceDatasourceStateReady;
	}

	/**
	 * @category 得到所有城市数据
	 */
	public List<ItemVo> getAllCityData() {
		cache_data.put(cache_key, cities_all);

		return this.cities_all;
	}

	/**
	 * @category 得到所有省份数据
	 */

	public List<ProvinceVo> getAllProvinceData() {

		return Map_Province.getSingle().getProvinces();
	}

	/**
	 * @category 根据城市名字得到城市ID
	 */

	public String getCityIdByName(String name) {

		return map_id_city.get(name);
	}

	/**
	 * @category 根据省份ID---得到对应的城市数据
	 */
	public List<CityVo> getCitiesByProId(String proid) {
		List<CityVo> ls = new ArrayList<CityVo>();
		CityVo vo = null;
		if (TextUtils.isEmpty(proid)) {
			return null;
		} else {
			if (this.cities_all == null) {
				cities_all = XmlDataCity.cache_data.get(cache_key);

			}
			if (cache_city.containsKey(proid)) {
				return cache_city.get(proid);

			} else {

				for (ItemVo cell : this.cities_all) {
					if (cell.getProid().equals(proid)) {
						vo = new CityVo();
						vo.setCity_id(cell.getId());
						vo.setCity_name(cell.getName());
						ls.add(vo);

					}

				}
				cache_city.put(proid, ls);
				return ls;

			}

		}

	}

	public final void init(Context context) throws XmlPullParserException,
			IOException {
		if (null == context) {
			return;
		}
		this.init(context, false);
	}

	public static enum FaceDatasourceState {
		FaceDatasourceStateUnknown, FaceDatasourceStateReady
	}
}
