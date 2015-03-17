package com.swater.meimeng.fragment.recommend;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;
import com.swater.meimeng.activity.adapterGeneral.vo.AnSwerVo;
import com.swater.meimeng.activity.adapterGeneral.vo.HobbyVo;
import com.swater.meimeng.database.ShareUserConstant;
import com.swater.meimeng.fragment.recommend.BarPushActivity.SubVo;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.constant.MConstantUser.UserAll_hobby_info;
import com.swater.meimeng.mutils.mygrid.MyListView;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * @category 个性展示
 */
public class PersonalAdapter extends BaseAdapter implements ShareUserConstant {

	Context con = null;
	String datajson = "";
	List<AnSwerVo> arr_sueveys = new ArrayList<AnSwerVo>();

	void parselData(String data) {
		try {
			arr_sueveys.clear();
			JSONArray obj_survey = new JSONArray(data);// obj.getJSONArray("personality_info");

			if (GeneralUtil.isNotNullObject(obj_survey)
					&& obj_survey.length() > 0) {
				int length = obj_survey.length();
				// NSLoger.Log("--length-->>" + length);
				getAnList(obj_survey);
				// adapter_personlity.notifyDataSetChanged();
				// String msg = adapter_personlity.getObjs().toString();
				// NSLoger.Log(msg + "--msg-->>");
				if (length < 6) {
					for (int i = length; i < 7; i++) {
						AnSwerVo asv = new AnSwerVo();

						asv.setAnswer("暂时保密");

						switch (i) {
						case 0: {
							asv.setType("理想对象");

						}

							break;
						case 1: {
							asv.setType("婚后生活");

						}

							break;
						case 2: {
							asv.setType("婚姻期望");

						}

							break;
						case 3: {

							asv.setType("约会类型");
						}

							break;
						case 4: {
							asv.setType("生活习惯");

						}

							break;
						case 5: {
							asv.setType("个性描述");

						}

							break;
						case 6: {

							asv.setType("爱情观点");
						}

							break;

						default:
							break;
						}
						arr_sueveys.add(asv);
					}

				}

			} else {

				for (int i = 0; i < 7; i++) {
					AnSwerVo asv = new AnSwerVo();

					asv.setAnswer("暂时保密");

					switch (i) {
					case 0: {
						asv.setType("理想对象");

					}

						break;
					case 1: {
						asv.setType("婚后生活");

					}

						break;
					case 2: {
						asv.setType("婚姻期望");

					}

						break;
					case 3: {

						asv.setType("约会类型");
					}

						break;
					case 4: {
						asv.setType("生活习惯");

					}

						break;
					case 5: {
						asv.setType("个性描述");

					}

						break;
					case 6: {

						asv.setType("爱情观点");
					}

						break;

					default:
						break;
					}
					arr_sueveys.add(asv);

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		// adapter_hobby.notifyDataSetChanged();
	}

	void getAnList(JSONArray arr) {
		try {
			for (int i = 0; i < arr.length(); i++) {
				AnSwerVo asv = new AnSwerVo();
				JSONObject cell = arr.getJSONObject(i);
				String name = cell.getString("type_value");
				JSONArray cell_arr = cell.getJSONArray("question");
				StringBuilder sbcell = new StringBuilder("");
				for (int j = 0; j < cell_arr.length(); j++) {
					SubVo cv = new SubVo();
					String st = cell_arr.getJSONObject(j).getString("answer");
					cv.setValue(cell_arr.getJSONObject(j).getString(
							"option_value"));
					cv.setType_id(cell_arr.getJSONObject(j).getString(
							"option_id"));
					sbcell.append(st + " ");

				}
				asv.setType(name);
				asv.setAnswer(sbcell.toString());
				arr_sueveys.add(asv);

			}
		} catch (Exception e) {
			// showToast("读取信息出错！");
			e.printStackTrace();
		} finally {

		}

	}

	public class SubVo {
		String type_id = "";
		String value = "";

		public String getType_id() {
			return type_id;
		}

		public void setType_id(String type_id) {
			this.type_id = type_id;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	String getStrFrJson(JSONObject sn) {
		String str = "";
		try {
			str = sn.getString("value");
		} catch (JSONException e) {
			// showToast("解析生活信息数据出错！！" + e == null ? "" : e.getMessage());
			e.printStackTrace();
		}
		return str;
	}

	String getStrArr(JSONArray arr) throws JSONException {
		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < arr.length(); i++) {
			String va = arr.getJSONObject(i).getString("value");
			sb.append(va);

		}
		return sb.toString();
	}

	public String getDatajson() {
		return datajson;
	}

	public void setDatajson(String datajson) {
		this.datajson = datajson;
	}

	public PersonalAdapter(Context con) {
		this.con = con;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arr_sueveys.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup parent) {

		if (arr_sueveys == null || arr_sueveys.size() == 0) {
			return view;

		}
		ViewHolderP holder = null;
		// TextView per_left = null, per_right = null;
		// MyListView lines_txt = null;
		// View hide = null;
		if (view == null) {
			view = LayoutInflater.from(con).inflate(R.layout.person_cell_lines,
					null);
			holder = new ViewHolderP();
			holder.per_left = (TextView) view.findViewById(R.id.cell_left);
			holder.per_right = (TextView) view.findViewById(R.id.cell_right);
			holder.lines_txt = (MyListView) view.findViewById(R.id.lines_txt);
			holder.hide = view.findViewById(R.id.blank_cell_hiden);

			holder.bg = view.findViewById(R.id.btn_edit_cell);
			// .setVisibility(View.INVISIBLE);

			view.setTag(holder);
		}

		holder = (ViewHolderP) view.getTag();
		holder.bg.setVisibility(View.INVISIBLE);
		// per_left = (TextView) view.findViewById(R.id.cell_left);
		// per_right = (TextView) view.findViewById(R.id.cell_right);
		// lines_txt = (MyListView) view.findViewById(R.id.lines_txt);
		// hide = view.findViewById(R.id.blank_cell_hiden);
		//
		// view.findViewById(R.id.btn_edit_cell).setVisibility(View.INVISIBLE);

		List<AnSwerVo> ls_va = arr_sueveys;// (List<AnSwerVo>) objs;
		if (i == arr_sueveys.size() - 1) {
			holder.hide.setVisibility(View.VISIBLE);

		} else {
			holder.hide.setVisibility(View.GONE);

		}
		GeneralUtil.setValueToView(holder.per_left, ls_va.get(i).getType());
		String ans = ls_va.get(i).getAnswer();
		ArrayList<String> ls_arr = new ArrayList<String>();
		if (!TextUtils.isEmpty(ans) && !"null".equals(ans)) {
			if (ans.contains("；")) {

				String[] arrs = ans.split("；");
				for (int j = 0; j < arrs.length; j++) {
					if (arrs[j] != null && !TextUtils.isEmpty(arrs[j].trim())) {
						ls_arr.add(arrs[j]);

					}

				}
				if (holder.lines_txt != null) {

					holder.lines_txt.setAdapter(new line_adapter(ls_arr));
				}

			} else {

				if (holder.lines_txt != null) {
					ls_arr.add("暂时保密");
					holder.lines_txt.setAdapter(new line_adapter(ls_arr));
				}
			}

		} else {
			if (holder.lines_txt != null) {
				ls_arr.add("暂时保密");
				holder.lines_txt.setAdapter(new line_adapter(ls_arr));
			}
		}

		return view;
	}

	public static class ViewHolderP {
		public TextView per_left;
		public TextView per_right;
		public MyListView lines_txt;
		public View bg;
		public View hide;
	}

	class line_adapter extends BaseAdapter {
		ArrayList<String> arr = null;

		public line_adapter(ArrayList<String> arrs) {
			this.arr = arrs;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arr == null ? 0 : arr.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int po, View convertView, ViewGroup parent) {
			if (con == null) {
				return null;
			}
			CellViewHolder holderv = null;
			if (convertView == null) {
				holderv = new CellViewHolder();
				convertView = LayoutInflater.from(con).inflate(
						R.layout.lines_cell, null);
				holderv.txt = (TextView) convertView
						.findViewById(R.id.cell_right);
				convertView.setTag(holderv);
			}

			if (po % 2 == 1) {
				convertView.setBackgroundResource(R.color.white);
			} else {
				convertView.setBackgroundResource(R.color.gray_item);
			}

			holderv = (CellViewHolder) convertView.getTag();
			if (arr != null) {
				GeneralUtil.setValueToView(holderv.txt, arr.get(po));
			}
			return convertView;
		}

		class CellViewHolder {

			TextView txt;
		}

	}
}
