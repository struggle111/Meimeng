package com.swater.meimeng.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatCodePointException;
import java.util.LinkedList;
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
 * @category 个人调查问卷----相关分类算法
 */
public class XmlSurveyData {
	static XmlSurveyData ins = new XmlSurveyData();

	private XmlSurveyData() {
	};

	public static XmlSurveyData getSingle() {

		return ins;
	}

	static final String TAG = XmlSurveyData.class.getSimpleName();

	List<SurveyVo> sy_data_all = new ArrayList<SurveyVo>();
	final String POOL_KEY_ALL = "POOL_KEY_ALL";
	private static Map<String, List<SurveyVo>> pool_sy_data = new HashMap<String, List<SurveyVo>>();
	/** 缓存数据防止多次加载 */
	private static Map<String, Map<String, List<SurveyVo>>> cache_cato_list = new HashMap<String, Map<String, List<SurveyVo>>>();
	HashSet<String> Cato_types_id = new HashSet<String>();
	
	public void ReleaseClear(){
		sy_data_all.clear();
		pool_sy_data.clear();
		cache_cato_list.clear();
		
		
	}

	final String KEY_SEX = "sex";
	final String KEY_TYPE_ID = "type_id";
	final String KEY_QUESTION_ID = "question_id";
	final String KEY_OP_ID = "option_id";
	final String KEY_TY_TEXT = "type_text";
	final String KEY_QUES_TEXT = "question_text";
	final String KEY_OP_TEXT = "option_text";
	final String KEY_OP_DESC = "option_description";

	final String file_Name = "survey_data.xml";

	public class SurveyVo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7221949334608251891L;
		String sex = "";
		String id_type = "";
		String id_ques = "";
		String id_option = "";
		String text_type = "";
		String text_ques = "";
		String text_option = "";
		String text_op_desc = "";

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public String getId_type() {
			return id_type;
		}

		public void setId_type(String id_type) {
			this.id_type = id_type;
		}

		public String getId_ques() {
			return id_ques;
		}

		public void setId_ques(String id_ques) {
			this.id_ques = id_ques;
		}

		public String getId_option() {
			return id_option;
		}

		public void setId_option(String id_option) {
			this.id_option = id_option;
		}

		public String getText_type() {
			return text_type;
		}

		public void setText_type(String text_type) {
			this.text_type = text_type;
		}

		public String getText_ques() {
			return text_ques;
		}

		public void setText_ques(String text_ques) {
			this.text_ques = text_ques;
		}

		public String getText_option() {
			return text_option;
		}

		public void setText_option(String text_option) {
			this.text_option = text_option;
		}

		public String getText_op_desc() {
			return text_op_desc;
		}

		public void setText_op_desc(String text_op_desc) {
			this.text_op_desc = text_op_desc;
		}

		@Override
		public String toString() {
			return "SurveyVo [sex=" + sex + ", id_type=" + id_type
					+ ", id_ques=" + id_ques + ", id_option=" + id_option
					+ ", text_type=" + text_type + ", text_ques=" + text_ques
					+ ", text_option=" + text_option + ", text_op_desc="
					+ text_op_desc + "]";
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

		/**
		 * if (this.data_all != null) { if
		 * (cache_cato_list.containsKey(cache_key)) { if (data_all.size()>1) {
		 * return ; }
		 * 
		 * }
		 * 
		 * }
		 */
		XmlPullParser parser = Xml.newPullParser();
		InputStream is = null;
		try {
			is = context.getAssets().open(file_Name);
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

		SurveyVo sv = null;

		int event = parser.getEventType();

		String tag_begin_parent = "RECORD";// _city";
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:

				break;
			case XmlPullParser.START_TAG:
				String tag_begin = parser.getName();

				if (parser.getName().equals(tag_begin_parent)) {
					sv = new SurveyVo();

				} else if (parser.getName().equals(KEY_OP_ID)) {
					String value = parser.nextText();
					sv.setId_option(value);

				} else if (KEY_OP_TEXT.equals(parser.getName() == null ? ""
						: parser.getName())) {
					String value = parser.nextText();
					sv.setText_option(value);

				} else if (KEY_QUES_TEXT.equals(parser.getName() == null ? ""
						: parser.getName())) {
					String value = parser.nextText();
					sv.setText_ques(value);

				} else if (KEY_QUESTION_ID.equals(parser.getName() == null ? ""
						: parser.getName())) {
					String value = parser.nextText();
					sv.setId_ques(value);

				} else if (KEY_SEX.equals(parser.getName() == null ? ""
						: parser.getName())) {
					String value = parser.nextText();
					sv.setSex(value);

				} else if (KEY_TY_TEXT.equals(parser.getName() == null ? ""
						: parser.getName())) {
					String value = parser.nextText();
					sv.setText_type(value);
					Cato_types_id.add(value);

				} else if (KEY_TYPE_ID.equals(parser.getName() == null ? ""
						: parser.getName())) {
					String value = parser.nextText();
					sv.setId_type(value);

				} else if (KEY_OP_DESC.equals(parser.getName() == null ? ""
						: parser.getName())) {
					String value = parser.nextText();
					sv.setText_op_desc(value);
					sy_data_all.add(sv);

				}
				break;
			case XmlPullParser.END_TAG:
				if (tag_begin_parent.equals(parser.getName())) {
					// Cato_data.put(cache_key, data_all);
					pool_sy_data.put(POOL_KEY_ALL, sy_data_all);
				}
				break;
			}
			event = parser.next();
		}

		state = FaceDatasourceState.FaceDatasourceStateReady;
	}

	/**
	 * 
	 * @category 得到所有
	 * @return Map<String, List<SurveyVo>>
	 * @param int；1-男；2女；
	 */

	Map<String, List<SurveyVo>> his_record = new HashMap<String, List<SurveyVo>>();

	private Map<String, List<SurveyVo>> getDataList(String sex) {
		his_record.clear();
		// if (cache_cato_list.containsKey(cache_key)) {
		// return cache_cato_list.get(cache_key);
		//
		// }
		if (sy_data_all == null || sy_data_all.size() == 0) {
			return null;

		}
		for (SurveyVo cell : this.sy_data_all) {
			for (String vatye : Cato_types_id) {

				if (cell.getText_type().equals(vatye)
						&& cell.getSex().equals(sex)) {
					if (his_record.containsKey(vatye)) {
						his_record.get(vatye).add(cell);

					} else {
						List<SurveyVo> ls = new ArrayList<SurveyVo>();
						ls.add(cell);
						his_record.put(vatye, ls);

					}

					break;
				} else {
					continue;
				}

			}

		}
		// cache_cato_list.put(cache_key, his_record);

		return his_record;
	}

	/**
	 * 
	 * @category 查找某个类型的问题集合
	 * @param sex
	 *            =0 通用， 1-男；2女；
	 * @param sex
	 *            采用自动递归判断！--当没有找到相关类型，自动递归！ 防止出现空数据
	 * @return SurveyData
	 */
	Map<String, LinkedList<SurveyVo>> result =null;

	public SurveyData getSingleQuests(String sex, String type) {
		result= new HashMap<String, LinkedList<SurveyVo>>();
		SurveyData data = new SurveyData();
		List<SurveyVo> list = new ArrayList<XmlSurveyData.SurveyVo>();
		List<SurveyVo>  c1=getDataList(sex.trim()).get(type);
		List<SurveyVo>  c2=getDataList("0").get(type);
		if (c1!=null) {
			list.addAll(c1);
			
		}
		if (c2!=null) {
			
			list.addAll(c2);
		}
		

		/**
		 * @return 自动递归判断！--当没有找到相关类型，自动递归！ 防止出现空数据
		 */
//		if (null == list) {
////			list = getDataList("0").get(type);
////			if (list == null) {
//				sex = sex.equals("1") ? "2" : "1";
//				list = this.getDataList(sex).get(type);
////
////			}
//
//		}
		HashSet<String> cato_ques = new HashSet<String>();

		List<String> arr_keys = new ArrayList<String>();
		Map<String, String> k_map = new HashMap<String, String>();
		
//		[SurveyVo [sex=1, id_type=1, id_ques=1, id_option=1, text_type=理想对象, text_ques=你希望Ta是一个什么样的人？, text_option=活泼开朗, text_op_desc=我希望她是一个活泼开朗的人；],
//		 SurveyVo [sex=1, id_type=1, id_ques=1, id_option=2, text_type=理想对象, text_ques=你希望Ta是一个什么样的人？, text_option=理性智慧, text_op_desc=我希望她是一个理性智慧的人；], 
//		 SurveyVo [sex=1, id_type=1, id_ques=1, id_option=3, text_type=理想对象, text_ques=你希望Ta是一个什么样的人？, text_option=乐观积极, text_op_desc=我希望她是一个乐观积极的人；], 
//		 SurveyVo [sex=1, id_type=1, id_ques=1, id_option=4, text_type=理想对象, text_ques=你希望Ta是一个什么样的人？, text_option=悲观消极, text_op_desc=我希望她是一个悲观消极的人；],
//		 SurveyVo [sex=1, id_type=1, id_ques=1, id_option=5, text_type=理想对象, text_ques=你希望Ta是一个什么样的人？, text_option=内向害羞, text_op_desc=我希望她是一个内向害羞的人；], 
//		 SurveyVo [sex=1, id_type=1, id_ques=1, id_option=6, text_type=理想对象, text_ques=你希望Ta是一个什么样的人？, text_option=天真可爱, text_op_desc=我希望她是一个天真可爱的人；],
//		 SurveyVo [sex=1, id_type=1, id_ques=1, id_option=7, text_type=理想对象, text_ques=你希望Ta是一个什么样的人？, text_option=大大咧咧, text_op_desc=我希望她是一个大大咧咧的人；],
//		 SurveyVo [sex=1, id_type=1, id_ques=1, id_option=8, text_type=理想对象, text_ques=你希望Ta是一个什么样的人？, text_option=仔细认真, text_op_desc=我希望她是一个仔细认真的人；], 
//		 SurveyVo [sex=1, id_type=1, id_ques=1, id_option=9, text_type=理想对象, text_ques=你希望Ta是一个什么样的人？, text_option=感性冲动, text_op_desc=我希望她是一个感性冲动的人；], 
//		 SurveyVo [sex=1, id_type=1, id_ques=1, id_option=10, text_type=理想对象, text_ques=你希望Ta是一个什么样的人？, text_option=温柔贤惠, text_op_desc=我希望她是一个温柔贤惠的人；], 
//		 SurveyVo [sex=1, id_type=1, id_ques=1, id_option=11, text_type=理想对象, text_ques=你希望Ta是一个什么样的人？, text_option=浪漫迷人, text_op_desc=我希望她是一个浪漫迷人的人；], 
//		 SurveyVo [sex=1, id_type=1, id_ques=1, id_option=12, text_type=理想对象, text_ques=你希望Ta是一个什么样的人？, text_option=时尚女郎, text_op_desc=我希望她是一个时尚女郎；], 
//		 SurveyVo [sex=1, id_type=1, id_ques=4, id_option=1, text_type=理想对象, text_ques=如果女朋友太优秀会感到压力吗？, text_option=会，只会和比自己低一点的女孩交往, text_op_desc=假如女朋友太优秀，我会有一定压力，只会和比自己低一点的女孩交往。], 
//		 SurveyVo [sex=1, id_type=1, id_ques=4, id_option=2, text_type=理想对象, text_ques=如果女朋友太优秀会感到压力吗？, text_option=会，但是会极力提高自己，一定要超过她  , text_op_desc=假如女朋友太优秀，我会有一定压力，但是会极力提高自己，一定要超过她。],
//		 SurveyVo [sex=1, id_type=1, id_ques=4, id_option=3, text_type=理想对象, text_ques=如果女朋友太优秀会感到压力吗？, text_option=不会，我本身已经足够优秀   , text_op_desc=假如女朋友太优秀，我也不会有压力，因为我本身已经足够优秀。], 
//		 SurveyVo [sex=1, id_type=1, id_ques=4, id_option=4, text_type=理想对象, text_ques=如果女朋友太优秀会感到压力吗？, text_option=不会，优秀的女友带出去很有面子, text_op_desc=假如女朋友太优秀，我也不会有压力，因为优秀的女友带出去很有面子。]]

		for (SurveyVo surveyVo : list) {
			cato_ques.add(surveyVo.getText_ques());
			k_map.put(surveyVo.getText_ques(), surveyVo.getId_ques());
			
			
			if (result.containsKey(surveyVo.getText_ques())) {
				result.get(surveyVo.getText_ques()).add(surveyVo);

			} else {
				LinkedList<SurveyVo> ls = new LinkedList<XmlSurveyData.SurveyVo>();
				ls.add(surveyVo);
				result.put(surveyVo.getText_ques(), ls);
			}

			// cato_ques.add(surveyVo.getId_ques());
		}

		for (String string : cato_ques) {
			arr_keys.add(string);

		}

		data.setKinds_map(k_map);
		data.setKinds_ques(cato_ques);
		data.setSets_question(result);
		data.setKinds_ls(arr_keys);
		return data;
	}

	/** 自定义数据封装 */
	public class SurveyData {
		// List<SurveyVo> question = null;
		HashSet<String> kinds_ques = null;
		List<String> kinds_ls = null;

		public List<String> getKinds_ls() {
			return kinds_ls;
		}

		public void setKinds_ls(List<String> kinds_ls) {
			this.kinds_ls = kinds_ls;
		}

		Map<String, String> kinds_map = null;
		Map<String, String> kinds_map_Reverse = null;

		public Map<String, String> getKinds_map_Reverse() {
			return reverseMap(kinds_map);
		}

		public Map<String, String> getKinds_map() {
			return kinds_map;
		}

		public void setKinds_map(Map<String, String> kinds_map) {
			this.kinds_map = kinds_map;
		}

		public HashSet<String> getKinds_ques() {
			return kinds_ques;
		}

		public void setKinds_ques(HashSet<String> kinds_ques) {
			this.kinds_ques = kinds_ques;
		}

		Map<String, LinkedList<SurveyVo>> sets_question = null;// 问题对应的选项集合

		public Map<String, LinkedList<SurveyVo>> getSets_question() {
			return sets_question;
		}

		public void setSets_question(
				Map<String, LinkedList<SurveyVo>> sets_question) {
			this.sets_question = sets_question;
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
