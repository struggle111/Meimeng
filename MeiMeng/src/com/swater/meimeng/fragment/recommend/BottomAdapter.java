package com.swater.meimeng.fragment.recommend;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.UserAdapterItem;
import com.swater.meimeng.activity.adapterGeneral.vo.HobbyVo;
import com.swater.meimeng.database.ShareUserConstant;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.constant.MConstantUser.UserAll_hobby_info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * @category 我的爱好
 */
public class BottomAdapter extends BaseAdapter implements ShareUserConstant {

	List<HobbyVo> arr_hobby = new ArrayList<HobbyVo>();
	Context con = null;
	String datajson = "";

	List<HobbyVo> getHobbyData(JSONArray arr, String lefttypeName) {
		HobbyVo hv = new HobbyVo();
		List<UserAdapterItem> lscell = new ArrayList<UserAdapterItem>();
		String id = "id";
		String value = "value";
		StringBuilder sb1 = new StringBuilder("");
		if (arr == null) {
			hv.setLeftName(lefttypeName);
			hv.setData("暂时保密");
			arr_hobby.add(hv);
			return arr_hobby;
		}

		for (int i = 0; i < arr.length(); i++) {
			UserAdapterItem cell = new UserAdapterItem();
			try {
				cell.setLeftStr(lefttypeName);
				cell.setId(arr.getJSONObject(i).getString(id));
				cell.setRightStr(arr.getJSONObject(i).getString(value));
				sb1.append(cell.getRightStr() + ",");

			} catch (JSONException e) {
				// showToast("解析爱好数据异常！");
				e.printStackTrace();
			} finally {

				lscell.add(cell);
				hv.setLeftName(lefttypeName);
				hv.setLsdata(lscell);
				String data = sb1.toString();
				if (data.length() > 2) {
					data = data.substring(0, data.length() - 1);

				}
				hv.setData(sb1.toString());
			}

		}
		arr_hobby.add(hv);
		return arr_hobby;

	}

	void parselData(String data) {
		try {
			JSONObject obj_hobby_info = new JSONObject(data);// obj.getJSONObject("hobby_info");
			// userinfo_vo.setJson_hobby_info(obj_hobby_info.toString());

			if (obj_hobby_info == null
					|| obj_hobby_info.isNull(UserAll_hobby_info.like_sports)) {
				getHobbyData(null, "喜欢的运动:");

				getHobbyData(null, "喜欢的食物:");
				getHobbyData(null, "喜欢的书籍:");
				getHobbyData(null, "喜欢的音乐:");
				getHobbyData(null, "喜欢的电影:");
				getHobbyData(null, "关注节目:");
				getHobbyData(null, "娱乐休闲:");
				getHobbyData(null, "业余爱好:");
				getHobbyData(null, "喜欢的旅游:");

			} else {

				getHobbyData(
						obj_hobby_info
								.getJSONArray(UserAll_hobby_info.like_sports),
						"喜欢的运动:");

				getHobbyData(
						obj_hobby_info
								.getJSONArray(UserAll_hobby_info.like_food),
						"喜欢的食物:");
				getHobbyData(
						obj_hobby_info
								.getJSONArray(UserAll_hobby_info.like_book),
						"喜欢的书籍:");
				getHobbyData(
						obj_hobby_info
								.getJSONArray(UserAll_hobby_info.like_music),
						"喜欢的音乐:");
				getHobbyData(
						obj_hobby_info
								.getJSONArray(UserAll_hobby_info.like_movie),
						"喜欢的电影:");
				getHobbyData(
						obj_hobby_info
								.getJSONArray(UserAll_hobby_info.attention_item),
						"关注节目:");
				getHobbyData(
						obj_hobby_info
								.getJSONArray(UserAll_hobby_info.entertainment),
						"娱乐休闲:");
				getHobbyData(
						obj_hobby_info
								.getJSONArray(UserAll_hobby_info.amateur_like),
						"业余爱好:");
				getHobbyData(
						obj_hobby_info.getJSONArray(UserAll_hobby_info.travel),
						"喜欢的旅游:");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		// adapter_hobby.notifyDataSetChanged();
	}

	public String getDatajson() {
		return datajson;
	}

	public void setDatajson(String datajson) {
		this.datajson = datajson;
	}

	public BottomAdapter(Context con) {
		this.con = con;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arr_hobby.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int i, View view, ViewGroup parent) {
		ViewHolder holder = null;
		if (view == null) {
			view = LayoutInflater.from(con).inflate(
					R.layout.user_ada_hobby_cell, null);
			holder = new ViewHolder();

			holder.txt = (TextView) view.findViewById(R.id.cell_left);
			holder.right = (Button) view.findViewById(R.id.cell_right);
			// .setBackgroundResource(0);
			view.setTag(holder);
		}

		if (i % 2 == 1) {
			view.setBackgroundResource(R.color.white);
		} else {
			view.setBackgroundResource(R.color.gray_item);
		}

		holder = (ViewHolder) view.getTag();

		if (arr_hobby == null || arr_hobby.size() == 0) {
			return view;

		}

		GeneralUtil.setValueToView(holder.txt, arr_hobby.get(i).getLeftName());
		GeneralUtil.setValueToView(holder.right, arr_hobby.get(i).getData());

		return view;
	}

	public static class ViewHolder {
		public TextView txt;
		public Button right;
	}

}
