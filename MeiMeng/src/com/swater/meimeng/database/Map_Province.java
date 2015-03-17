package com.swater.meimeng.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Map_Province {

	/**
	 * @category 省份
	 */
	public static class ProvinceVo {
		String pro_id = "";
		String pro_name = "";

		public String getPro_id() {
			return pro_id;
		}

		public void setPro_id(String pro_id) {
			this.pro_id = pro_id;
		}

		public String getPro_name() {
			return pro_name;
		}

		public void setPro_name(String pro_name) {
			this.pro_name = pro_name;
		}

	}

	/**
	 * @category 城市
	 */
	public static class CityVo {
		String city_id = "";
		String city_name = "";

		public String getCity_id() {
			return city_id;
		}

		public void setCity_id(String city_id) {
			this.city_id = city_id;
		}

		public String getCity_name() {
			return city_name;
		}

		public void setCity_name(String city_name) {
			this.city_name = city_name;
		}

		public void print() {

			System.out.println("print--city--id--" + this.city_id + "--name--"
					+ this.city_name);
		}

	}

	private static Map_Province single_ins = new Map_Province();
	Map<String, String> map_province = new HashMap<String, String>();
	Map<String, String> map_city = new HashMap<String, String>();
	Map<String, String> map_province_reverse = new HashMap<String, String>();
	Map<String, String> map_city_reverse = new HashMap<String, String>();
	Map<String, LinkedList<ProvinceVo>> data_cache = new HashMap<String, LinkedList<ProvinceVo>>();
	List<ProvinceVo> ls_pro = new ArrayList<ProvinceVo>();

	public static Map_Province getSingle() {

		return single_ins;
	}

	private Map_Province() {
		if (map_province.size() < 1) {
			iniPro();
			map_province_reverse = this.mapProReverse();
		}

	}

	/**
	 * @category 得到省份列表
	 */
	public Map<String, String> getMapProvinces() {

		return map_province;
	}

	/**
	 * @category 得到省份列表
	 */
	public List<ProvinceVo> getProvinces() {
//		LinkedList<ProvinceVo> link = new LinkedList<ProvinceVo>();// <ProvinceVo>[35];

		if (data_cache.containsKey("data")) {
			if (data_cache.get("data").size() > 2) {
				return data_cache.get("data");
			}else{
				
				
				
			return	iniCache();
			}

		}else{
			return iniCache();
		}

//		return link;
	}

	LinkedList<ProvinceVo>  iniCache() {
		LinkedList<ProvinceVo> link = new LinkedList<ProvinceVo>();// <ProvinceVo>[35];
//		四川；重庆；北京；上海；广东；湖北；湖南；浙江；江苏；陕西
//		map_province.put("27", "四川");
//		map_province.put("4", "重庆");
//		map_province.put("1", "北京");
//		map_province.put("2", "上海");
//		map_province.put("20", "湖北");
//		map_province.put("21", "浙江");
//		map_province.put("28", "广东");
//		map_province.put("19", "江苏");
//		map_province.put("11", "陕西");
//		map_province.put("25", "湖南");
		ProvinceVo vo = null;
		
		for (int i = 1; i < 11; i++) {
			vo = new ProvinceVo();
			switch (i) {
			case 1:{
				vo.setPro_id("27");
				vo.setPro_name("四川");
				
				
			}
				
				break;
			case 2:{
				vo.setPro_id("4");
				vo.setPro_name("重庆");
				
				
			}
			
			break;
			case 3:{
				
				vo.setPro_id("1");
				vo.setPro_name("北京");
				
			}
			
			break;
			case 4:{
				
				
				vo.setPro_id("2");
				vo.setPro_name("上海");
			}
			
			break;
			case 5:{
				vo.setPro_id("20");
				vo.setPro_name("湖北");
				
				
			}
			
			break;
			case 6:{
				vo.setPro_id("21");
				vo.setPro_name("浙江");
				
				
			}
			
			break;
			case 7:{
				
				vo.setPro_id("28");
				vo.setPro_name("广东");
				
			}
			
			break;
			case 8:{
				
				vo.setPro_id("19");
				vo.setPro_name("江苏");
				
			}
			
			break;
			case 9:{
				vo.setPro_id("11");
				vo.setPro_name("陕西");
				
				
			}
			
			break;
			case 10:{
				vo.setPro_id("25");
				vo.setPro_name("湖南");
				
				
			}
			
			break;

			default:
				break;
			}
			link.add(vo);
		}
		for (Entry<String, String> keys : map_province.entrySet()) {
			vo = new ProvinceVo();
			vo.setPro_id(keys.getKey());
			vo.setPro_name(keys.getValue());
//			if (vo.getPro_id().equals("27")) {
//				link.addFirst(vo);
//			} else if (vo.getPro_id().equals("4")) {
//				link.addFirst(vo);
//
//			} else 
			
//			{

				link.add(vo);
//			}
		}
		link.getFirst();
		 data_cache.put("data", link);
		return link;

	}

	void iniPro() {
		map_province.clear();
//		map_province.put("27", "四川");
//		map_province.put("4", "重庆");
//		map_province.put("1", "北京");
//		map_province.put("2", "上海");
//		map_province.put("20", "湖北");
//		map_province.put("21", "浙江");
//		map_province.put("28", "广东");
//		map_province.put("19", "江苏");
//		map_province.put("11", "陕西");
//		map_province.put("25", "湖南");
		map_province.put("3", "天津");
		map_province.put("5", "黑龙江");
		map_province.put("6", "吉林");
		map_province.put("7", "辽宁");
		map_province.put("8", "内蒙古");
		map_province.put("9", "河北");
		map_province.put("10", "山西");
		map_province.put("12", "山东");
		map_province.put("13", "新疆");
		map_province.put("14", "西藏");
		map_province.put("15", "青海");
		map_province.put("16", "甘肃");
		map_province.put("17", "宁夏");
		map_province.put("18", "河南");

		map_province.put("22", "安徽");
		map_province.put("23", "福建");
		map_province.put("24", "江西");

		map_province.put("26", "贵州");
		map_province.put("29", "云南");
		map_province.put("30", "广西");
		map_province.put("31", "海南");
		map_province.put("32", "香港");
		map_province.put("33", "澳门");
		map_province.put("34", "台湾");
		map_province.put("35", "海外");

	}

	public String getproIdByName(String proName) {

		return map_province_reverse.get(proName);
	}

	public String getcityIdByName(String proName) {

		return map_city_reverse.get(proName);
	}

	/**
	 * @category 反转省份MAP
	 */
	private Map<String, String> mapProReverse() {
		Map<String, String> map_reverse = new HashMap<String, String>();
		for (Entry<String, String> keys : map_province.entrySet()) {
			map_reverse.put(map_province.get(keys.getKey()), keys.getKey());
		}
		return map_reverse;
	}

	/**
	 * @category 反转城市MAP
	 */
	private Map<String, String> mapCityReverse() {
		Map<String, String> map_reverse = new HashMap<String, String>();
		for (Entry<String, String> keys : map_city.entrySet()) {
			map_reverse.put(map_city.get(keys.getKey()), keys.getKey());
		}
		return map_reverse;
	}

}
