package com.swater.meimeng.activity.adapterGeneral;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.vo.AnSwerVo;
import com.swater.meimeng.activity.adapterGeneral.vo.DataVo;
import com.swater.meimeng.activity.adapterGeneral.vo.HobbyVo;
import com.swater.meimeng.database.XmlSurveyData.SurveyVo;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.mygrid.MyListView;

/**
 * @category 用户通用适配器
 */
public class UserAdapter extends BaseAdapter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5640887851031257688L;
	User_Ada_Type type = User_Ada_Type.type_defalut;
	Context context = null;
	ListView listview = null;
	boolean isClikAble = true;
	boolean isHidden = false;
	boolean isUser = false;
	LinkedList<Integer> link_pos_ted = new LinkedList<Integer>();

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public void setClikAble(boolean isClikAble) {
		this.isClikAble = isClikAble;
	}

	/** 封装多选数据队列 */
	LinkedList<UserAdapterItem> quence_selected = new LinkedList<UserAdapterItem>();
	int pos_selected_pos = -1;

	public LinkedList<UserAdapterItem> getQuence_selected() {
		return quence_selected;
	}

	public void setQuence_selected(LinkedList<UserAdapterItem> quence_selected) {
		this.quence_selected = quence_selected;
	}

	/**
	 * @category 得到多选数据队列
	 * @return DataVo
	 */
	public DataVo getDataquence_selected() {
		LinkedList<UserAdapterItem> ls = this.getQuence_selected();
		if (ls.size() < 1) {
			return null;

		}
		StringBuilder sb_id = new StringBuilder("");
		StringBuilder sb_data = new StringBuilder("");
		for (UserAdapterItem cell : ls) {
			sb_id.append(cell.getId() + ",");
			sb_data.append(cell.getLeftStr() + ",");

		}
		DataVo dv = new DataVo();
		dv.setIds(sb_id.toString().substring(0, sb_id.toString().length() - 1));
		dv.setValues(sb_data.toString().substring(0,
				sb_data.toString().length() - 1));
		return dv;

	}

	public int getPos_selected_pos() {
		return pos_selected_pos;
	}

	public void setPos_selected_pos(int pos_selected_pos) {
		this.pos_selected_pos = pos_selected_pos;
	}

	public ListView getListview() {
		return listview;
	}

	EditText edit_right = null;
	EditText edit_right_height = null;

	public String getNickName() {

		return edit_right == null ? "" : edit_right.getEditableText()
				.toString();
	}

	public String getHeight() {

		return edit_right_height == null ? "" : edit_right_height
				.getEditableText().toString();
	}

	public void setListview(ListView listview) {
		this.listview = listview;
	}

	/** 自定义CELL点击事件！当onitem 失效时 */
	public interface cell_lay_click {
		void Cell_lay_Click(View v, int pos);

	}

	public UserAdapter getAda() {
		return this;
	}

	/** 自定义RadioCell点击事件！ */
	public interface Radio_lay_click {
		/**
		 * @category 点击单选问题组的radio-- 事件
		 */
		void Radio_lay_Click(View v, int pos, ListAdapter adapter);

	}

	private cell_lay_click click_cell_event = null;
	private Radio_lay_click radio_click_enent = null;

	public void setClick_cell_event(cell_lay_click click_cell_event) {
		this.click_cell_event = click_cell_event;
	}

	public void setRadio_click_enent(Radio_lay_click radio_click_enent) {
		this.radio_click_enent = radio_click_enent;
	}

	public UserAdapter(Context con) {
		this.context = con;
	}

	public User_Ada_Type getType() {
		return type;
	}

	public void setType(User_Ada_Type type) {
		this.type = type;
	}

	public List<?> getObjs() {
		return objs;
	}

	public void setObjs(List<?> objs) {
		this.objs = objs;
	}

	public enum User_Ada_Type {
		type_base_cell, type_new_user_cell, type_user_set_forbdden_user_cell, type_user_gallery_power, type_user_set, type_user_set_sys, type_hobby_cell, type_defalut, type_imgs, type_personlities, type_radio_listview, type_check_box_lay;
	}

	List<?> objs = null;

	@Override
	public int getCount() {
		return objs == null ? 0 : objs.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup arg2) {
		TextView txt = null;
		Button right = null;
		switch (type) {
		case type_defalut: {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.user_ada_cell, null);
			}

			if (i % 2 == 1) {
				view.setBackgroundResource(R.color.white);
			} else {
				view.setBackgroundResource(R.color.gray_item);
			}

			txt = (TextView) view.findViewById(R.id.cell_left);
			right = (Button) view.findViewById(R.id.cell_right);
			view.findViewById(R.id.cell_rly).setOnClickListener(
					new MyClick(i, view));
			if (objs == null || objs.size() == 0) {
				return view;

			}
			if ((objs.get(0)) instanceof UserAdapterItem) {
				List<UserAdapterItem> ls_va = (List<UserAdapterItem>) objs;
				GeneralUtil.setValueToView(txt, ls_va.get(i).getLeftStr());
				GeneralUtil.setValueToView(right, ls_va.get(i).getRightStr());

			}

		}

			break;
		case type_base_cell: {
			TextView txt_left = null, txt_right = null;
			if (view == null) {
				view = LayoutInflater.from(context).inflate(R.layout.base_cell,
						null);
			}

			if (i % 2 == 1) {
				view.setBackgroundResource(R.color.white);
			} else {
				view.setBackgroundResource(R.color.gray_item);
			}

			txt_left = (TextView) view.findViewById(R.id.cell_left);
			txt_right = (TextView) view.findViewById(R.id.txt_right);
			edit_right = (EditText) view.findViewById(R.id.edit_right);
			view.findViewById(R.id.cell_rly).setOnClickListener(
					new MyClick(i, view));
			if (objs == null || objs.size() == 0) {
				return view;

			}

			edit_right.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// edit_right.setText(s.toString());
				}
			});

			// if (i == 0) {
			// view.findViewById(R.id.cell_rly).setBackgroundResource(
			// R.drawable.list_item_top_bg_normal);
			//
			// } else if (i == objs.size() - 1) {
			// view.findViewById(R.id.cell_rly).setBackgroundResource(
			// R.drawable.list_item_bottom_bg_normal);
			//
			// } else {
			// view.findViewById(R.id.cell_rly).setBackgroundResource(
			// R.drawable.list_item_center_bg_normal);
			// }

			if ((objs.get(0)) instanceof UserAdapterItem) {
				List<UserAdapterItem> ls_va = (List<UserAdapterItem>) objs;

				if (i == 0) {
					GeneralUtil.setValueToView(txt_left, ls_va.get(i)
							.getLeftStr());
					GeneralUtil.setValueToView(edit_right, ls_va.get(i)
							.getRightStr());
					// edit_right.setEnabled(true);
					// edit_right.setFocusable(true);
					GeneralUtil.view_Show(edit_right);
					GeneralUtil.view_Hide(txt_right);
				} else if (i == 3) {
					edit_right_height = edit_right;
					GeneralUtil.setValueToView(txt_left, ls_va.get(i)
							.getLeftStr());
					GeneralUtil.setValueToView(edit_right, ls_va.get(i)
							.getRightStr());
					// edit_right.setEnabled(true);
					// edit_right.setFocusable(true);
					GeneralUtil.view_Show(edit_right);
					GeneralUtil.view_Hide(txt_right);

				} else {
					GeneralUtil.setValueToView(txt_left, ls_va.get(i)
							.getLeftStr());
					GeneralUtil.setValueToView(txt_right, ls_va.get(i)
							.getRightStr());
					GeneralUtil.view_Hide(edit_right);
					GeneralUtil.view_Show(txt_right);
					// edit_right.setEnabled(false);
					// edit_right.setFocusable(tr);
				}

			}

		}

			break;
		case type_new_user_cell: {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.cell_user_new_show, null);
			}

			if (i % 2 == 1) {
				view.setBackgroundResource(R.color.white);
			} else {
				view.setBackgroundResource(R.color.gray_item);
			}

			txt = (TextView) view.findViewById(R.id.cell_left);
			TextView right1 = (TextView) view.findViewById(R.id.cell_right);
			TextView right_multi = (TextView) view
					.findViewById(R.id.cell_right_multi);
			// view.findViewById(R.id.cell_rly).setOnClickListener(
			// new MyClick(i, view));
			if (objs == null || objs.size() == 0) {
				return view;

			}
			// if (i == 0) {
			// view.findViewById(R.id.cell_rly).setBackgroundResource(
			// R.drawable.list_item_top_bg_normal);
			//
			// }
			// if (i == objs.size() - 1) {
			// view.findViewById(R.id.cell_rly).setBackgroundResource(
			// R.drawable.list_item_bottom_bg_normal);
			//
			// }
			if ((objs.get(0)) instanceof UserAdapterItem) {
				List<UserAdapterItem> ls_va = (List<UserAdapterItem>) objs;
				if (ls_va.get(i).getLeftStr().equals("居住状况")
						|| ls_va.get(i).getLeftStr().equals("擅长生活技能")) {
					GeneralUtil.view_Hide(right1);
					GeneralUtil.view_Show(right_multi);
					GeneralUtil.setValueToView(right_multi, ls_va.get(i)
							.getRightStr());

				} else {
					GeneralUtil.view_Hide(right_multi);
					GeneralUtil.view_Show(right1);
					if (TextUtils.isEmpty(ls_va.get(i).getRightStr())
							|| ls_va.get(i).getRightStr().equals("null")) {
						ls_va.get(i).setRightStr("暂时保密");
						GeneralUtil.setValueToView(right1, ls_va.get(i)
								.getRightStr());

					} else {

						GeneralUtil.setValueToView(right1, ls_va.get(i)
								.getRightStr());

						// if (ls_va.get(i).getLeftStr().equals("擅长生活技能")) {
						// if (ls_va.get(i).getRightStr().length()>9) {
						//
						// right1.setText(ls_va);
						// }
						// }
					}

					// if (ls_va.get(i).getLeftStr().equals("擅长生活技能")) {
					// if (ls_va.get(i).getRightStr().l) {
					//
					// }
					// // right1.setText(text);
					// }
				}
				GeneralUtil
						.setValueToView(txt, ls_va.get(i).getLeftStr() + ":");

			}

		}

			break;
		case type_hobby_cell: {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.user_ada_hobby_cell, null);
			}

			if (i % 2 == 1) {
				view.setBackgroundResource(R.color.white);
			} else {
				view.setBackgroundResource(R.color.gray_item);
			}

			txt = (TextView) view.findViewById(R.id.cell_left);
			right = (Button) view.findViewById(R.id.cell_right);
			if (isClikAble) {
				view.findViewById(R.id.cell_rly).setOnClickListener(
						new MyClick(i, view));
			}

			if (objs == null || objs.size() == 0) {
				return view;

			}
			if ((objs.get(0)) instanceof HobbyVo) {
				List<HobbyVo> ls_va = (List<HobbyVo>) objs;
				GeneralUtil.setValueToView(txt, ls_va.get(i).getLeftName());
				GeneralUtil.setValueToView(right, ls_va.get(i).getData());

			}

		}

			break;
		case type_user_set: {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.cell_user_set, null);
			}

			if (i % 2 == 1) {
				view.setBackgroundResource(R.color.white);
			} else {
				view.setBackgroundResource(R.color.gray_item);
			}

			txt = (TextView) view.findViewById(R.id.cell_set_left);
			right = (Button) view.findViewById(R.id.cell_set_right);
			if (isClikAble) {
				view.findViewById(R.id.cell_rly).setOnClickListener(
						new MyClick(i, view));
			}

			if (objs == null || objs.size() == 0) {
				return view;

			}
			// if (i == 0) {
			// view.findViewById(R.id.cell_rly).setBackgroundResource(
			// R.drawable.list_item_top_bg_normal);
			//
			// }
			// if (i == objs.size() - 1) {
			// view.findViewById(R.id.cell_rly).setBackgroundResource(
			// R.drawable.list_item_bottom_bg_normal);
			//
			// }
			if ((objs.get(0)) instanceof UserAdapterItem) {
				UserAdapterItem it = (UserAdapterItem) objs.get(i);
				GeneralUtil.setValueToView(txt, it.getLeftStr());
				GeneralUtil.setValueToView(right, it.getRightStr());

			}

		}

			break;
		case type_user_set_sys: {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.cell_user_set, null);
			}

			if (i % 2 == 1) {
				view.setBackgroundResource(R.color.white);
			} else {
				view.setBackgroundResource(R.color.gray_item);
			}

			Button login_out = null;
			txt = (TextView) view.findViewById(R.id.cell_set_left);
			right = (Button) view.findViewById(R.id.cell_set_right);
			login_out = (Button) view.findViewById(R.id.cell_loginout);
			if (isClikAble) {
				view.findViewById(R.id.cell_rly).setOnClickListener(
						new MyClick(i, view));
			}

			if (objs == null || objs.size() == 0) {
				return view;

			}
			// if (i == 0) {
			// view.findViewById(R.id.cell_rly).setBackgroundResource(
			// R.drawable.list_item_top_bg_normal);
			//
			// }
			// if (i == objs.size() - 2) {
			// view.findViewById(R.id.cell_rly).setBackgroundResource(
			// R.drawable.list_item_bottom_bg_normal);
			//
			// }
			if (i == objs.size() - 1) {
				view.setBackgroundResource(R.color.gray_bg);
				GeneralUtil.view_Hide(txt, right);
				GeneralUtil.view_Show(login_out);
			}
			if ((objs.get(0)) instanceof UserAdapterItem) {
				UserAdapterItem it = (UserAdapterItem) objs.get(i);
				GeneralUtil.setValueToView(txt, it.getLeftStr());
				GeneralUtil.setValueToView(right, it.getRightStr());

			}

		}

			break;
		case type_user_set_forbdden_user_cell: {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.forbiden_cell, null);
			}

			if (i % 2 == 1) {
				view.setBackgroundResource(R.color.white);
			} else {
				view.setBackgroundResource(R.color.gray_item);
			}

			txt = (TextView) view.findViewById(R.id.forb_text);
			right = (Button) view.findViewById(R.id.forb_btn);
			if (isClikAble) {
				right.setOnClickListener(new MyClick(i, right));
			}

			if (objs == null || objs.size() == 0) {
				return view;

			}
			// if (i == 0) {
			// view.findViewById(R.id.cell_rly).setBackgroundResource(
			// R.drawable.list_item_top_bg_normal);
			//
			// }
			// if (i == objs.size() - 1) {
			// view.findViewById(R.id.cell_rly).setBackgroundResource(
			// R.drawable.list_item_bottom_bg_normal);
			//
			// }
			if ((objs.get(0)) instanceof UserAdapterItem) {
				UserAdapterItem it = (UserAdapterItem) objs.get(i);
				GeneralUtil.setValueToView(txt, it.getLeftStr());
				// GeneralUtil.setValueToView(right, it.getRightStr());

			}

		}

			break;
		case type_personlities: {
			TextView per_left = null, per_right = null;
			MyListView lines_txt = null;
			View hide = null;
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.person_cell_lines, null);
			}

			if (i % 2 == 1) {
				view.setBackgroundResource(R.color.white);
			} else {
				view.setBackgroundResource(R.color.gray_item);
			}

			per_left = (TextView) view.findViewById(R.id.cell_left);
			per_right = (TextView) view.findViewById(R.id.cell_right);
			lines_txt = (MyListView) view.findViewById(R.id.lines_txt);
			hide = view.findViewById(R.id.blank_cell_hiden);
			if (isHidden) {
				view.findViewById(R.id.btn_edit_cell).setVisibility(
						View.INVISIBLE);
			}

			if (isClikAble) {
				view.findViewById(R.id.btn_edit_cell).setOnClickListener(
						new MyClick(i, view.findViewById(R.id.btn_edit_cell)));
			}
			if (objs == null || objs.size() == 0) {
				return view;

			}
			if ((objs.get(0)) instanceof UserAdapterItem) {
				List<UserAdapterItem> ls_va = (List<UserAdapterItem>) objs;
				GeneralUtil.setValueToView(per_left, ls_va.get(i).getLeftStr());
				GeneralUtil.setValueToView(per_right, ls_va.get(i)
						.getRightStr());

			}

			if ((objs.get(0)) instanceof AnSwerVo) {
				List<AnSwerVo> ls_va = (List<AnSwerVo>) objs;
				if (i == objs.size() - 1) {
					hide.setVisibility(View.VISIBLE);

				} else {
					hide.setVisibility(View.GONE);

				}
				GeneralUtil.setValueToView(per_left, ls_va.get(i).getType());
				String ans = ls_va.get(i).getAnswer();
				ArrayList<String> ls_arr = new ArrayList<String>();
				if (!TextUtils.isEmpty(ans) && !"null".equals(ans)) {
					if (ans.contains("；")) {

						String[] arrs = ans.split("；");
						for (int j = 0; j < arrs.length; j++) {
							if (arrs[j] != null
									&& !TextUtils.isEmpty(arrs[j].trim())) {
								ls_arr.add(arrs[j]);

							}

						}
						if (lines_txt != null) {

							lines_txt.setAdapter(new line_adapter(ls_arr));
						}

					} else {

						if (lines_txt != null) {
							ls_arr.add("暂时保密");
							lines_txt.setAdapter(new line_adapter(ls_arr));
						}
					}

				} else {
					if (lines_txt != null) {
						ls_arr.add("暂时保密");
						lines_txt.setAdapter(new line_adapter(ls_arr));
					}
				}
				// GeneralUtil.setValueToView(per_right,
				// ls_va.get(i).getAnswer());

			}

		}
			break;
		// case type_personlities: {
		// TextView per_left = null, per_right = null;
		// View hide=null;
		// if (view == null) {
		// view = LayoutInflater.from(context).inflate(
		// R.layout.person_cell, null);
		// }
		// per_left = (TextView) view.findViewById(R.id.cell_left);
		// per_right = (TextView) view.findViewById(R.id.cell_right);
		// hide=view.findViewById(R.id.blank_cell_hiden);
		// if (isHidden) {
		// view.findViewById(R.id.btn_edit_cell).setVisibility(
		// View.INVISIBLE);
		// }
		//
		// if (isClikAble) {
		// view.findViewById(R.id.btn_edit_cell).setOnClickListener(
		// new MyClick(i, view.findViewById(R.id.btn_edit_cell)));
		// }
		// if (objs == null || objs.size() == 0) {
		// return view;
		//
		// }
		// if ((objs.get(0)) instanceof UserAdapterItem) {
		// List<UserAdapterItem> ls_va = (List<UserAdapterItem>) objs;
		// GeneralUtil.setValueToView(per_left, ls_va.get(i).getLeftStr());
		// GeneralUtil.setValueToView(per_right, ls_va.get(i)
		// .getRightStr());
		//
		// }
		//
		// if ((objs.get(0)) instanceof AnSwerVo) {
		// List<AnSwerVo> ls_va = (List<AnSwerVo>) objs;
		// if (i==objs.size()-1) {
		// hide.setVisibility(View.VISIBLE);
		//
		// }
		// GeneralUtil.setValueToView(per_left, ls_va.get(i).getType());
		// GeneralUtil.setValueToView(per_right, ls_va.get(i).getAnswer());
		//
		// }
		//
		// }
		// break;
		/** --问卷调查--多选适 */
		case type_radio_listview: {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.radio_cell, null);

			}

			if (i % 2 == 1) {
				view.setBackgroundResource(R.color.white);
			} else {
				view.setBackgroundResource(R.color.gray_item);
			}

			if (isClikAble) {
				view.findViewById(R.id.cell_rly).setOnClickListener(
						new MyRadioClick(i, view,
								this.getListview() == null ? null : this
										.getListview().getAdapter()));
			}

			RadioButton rb = (RadioButton) view.findViewById(R.id.cell_1);
			TextView txt_cell = (TextView) view.findViewById(R.id.cell_2);
			if (objs == null || objs.size() == 0) {
				return view;

			}

			if (objs.get(i) instanceof SurveyVo) {
				SurveyVo sv = (SurveyVo) objs.get(i);
				GeneralUtil.setValueToView(txt_cell, sv.getText_option());

			}
			if (i == this.pos_selected_pos) {
				((RadioButton) view.findViewById(R.id.cell_1)).setChecked(true);

			} else {
				((RadioButton) view.findViewById(R.id.cell_1))
						.setChecked(false);
			}

		}
			break;
		case type_user_gallery_power: {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.power_radio_cell, null);

			}

			if (i % 2 == 1) {
				view.setBackgroundResource(R.color.white);
			} else {
				view.setBackgroundResource(R.color.gray_item);
			}

			if (isClikAble) {
				view.findViewById(R.id.cell_rly).setOnClickListener(
						new MyRadioClick(i, view,
								this.getListview() == null ? null : this
										.getListview().getAdapter()));
			}

			RadioButton rb = (RadioButton) view.findViewById(R.id.cell_1);
			TextView txt_cell = (TextView) view.findViewById(R.id.cell_2);
			if (objs == null || objs.size() == 0) {
				return view;

			}

			if (objs.get(i) instanceof UserAdapterItem) {
				UserAdapterItem sv = (UserAdapterItem) objs.get(i);
				GeneralUtil.setValueToView(txt_cell, sv.getLeftStr());

			}
			if (i == this.pos_selected_pos) {
				((RadioButton) view.findViewById(R.id.cell_1)).setChecked(true);

			} else {
				((RadioButton) view.findViewById(R.id.cell_1))
						.setChecked(false);
			}

		}
			break;
		case type_check_box_lay: {
			TextView txtleft = null;
			CheckBox cb = null;
			// RadioButton rb = null;
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.multi_ch_cell, null);

			}

			if (i % 2 == 1) {
				view.setBackgroundResource(R.color.white);
			} else {
				view.setBackgroundResource(R.color.gray_item);
			}

			txtleft = (TextView) view.findViewById(R.id.cell_left);
			cb = (CheckBox) view.findViewById(R.id.cell_right);
			// cb.setTag(i);
			// rb = (RadioButton) view.findViewById(R.id.cell_right_1);

			if (objs == null || objs.size() == 0) {
				return view;

			}

			if (this.getObjs().get(i) instanceof UserAdapterItem) {
				GeneralUtil.setValueToView(txtleft,
						((UserAdapterItem) objs.get(i)).getLeftStr());

				cb.setTag(i);
				cb.setChecked(false);
				// --设置选中！防止scroll滑动的BUG
				if (getQuence_selected().size() > 0) {
					for (UserAdapterItem cell : getQuence_selected()) {
						if (String.valueOf(cell.getPos()).equals(i + "".trim())) {

							cb.setChecked(true);
						}

					}

				}

				// }
				view.findViewById(R.id.cell_rly).setOnClickListener(
						new MyRadioClick(i, view,
								this.getListview() == null ? null : this
										.getListview().getAdapter()));

				;

			}
			// if (i == this.pos_selected_pos) {
			// ((RadioButton) view.findViewById(R.id.cell_1)).setChecked(true);
			//
			// } else {
			// ((RadioButton) view.findViewById(R.id.cell_1))
			// .setChecked(false);
			// }

		}
			break;

		default:
			break;
		}
		return view;
	}

	public static class ViewHolder {
		public TextView textView;
	}

	class RadioCheck implements OnCheckedChangeListener {

		int index = 0;

		RadioCheck(int pos) {
			this.index = pos;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				// if ((Integer) buttonView.getTag() == index) {
				link_pos_ted.add(index);
				buttonView.setChecked(true);
				// }
				quence_selected.add((UserAdapterItem) objs.get(index));

			} else {
				// if ((Integer) buttonView.getTag() == index) {
				link_pos_ted.remove(index);

				buttonView.setChecked(false);
				// }
				quence_selected.remove((UserAdapterItem) objs.get(index));

			}

		}

	}

	class CheckListener implements OnCheckedChangeListener {
		int index = 0;

		CheckListener(int pos) {
			this.index = pos;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				// if ((Integer) buttonView.getTag() == index) {
				// link_pos_ted.add(index);
				// buttonView.setChecked(true);
				// }
				quence_selected.add((UserAdapterItem) objs.get(index));

			} else {
				// link_pos_ted.remove(index);
				// if ((Integer) buttonView.getTag() == index) {

				// buttonView.setChecked(false);
				// }
				quence_selected.remove((UserAdapterItem) objs.get(index));

			}
		}

	}

	public class ConvertCls {
		TextView txt = null;

	}

	class MyClick implements OnClickListener {
		int index = -1;
		View view = null;

		public MyClick(int pos, View v) {
			this.index = pos;
			this.view = v;
		}

		@Override
		public void onClick(View v) {
			if (null != click_cell_event) {

				click_cell_event.Cell_lay_Click(view, index);
			}

		}

	}

	/** radiolay事件 */
	class MyRadioClick implements OnClickListener {
		int index = -1;
		View view = null;
		ListAdapter ada = null;

		public MyRadioClick(int pos, View v, ListAdapter ad) {
			this.index = pos;
			this.view = v;
			this.ada = ad;
		}

		@Override
		public void onClick(View v) {
			if (null != radio_click_enent) {

				radio_click_enent.Radio_lay_Click(v, index, ada);
			}
		}

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
			if (context == null) {
				return null;
			}
			if (convertView == null) {

				convertView = LayoutInflater.from(context).inflate(
						R.layout.lines_cell, null);
			}

			if (po % 2 == 1) {
				convertView.setBackgroundResource(R.color.white);
			} else {
				convertView.setBackgroundResource(R.color.gray_item);
			}

			TextView txt = (TextView) convertView.findViewById(R.id.cell_right);
			if (arr != null) {
				GeneralUtil.setValueToView(txt, arr.get(po));
			}
			return convertView;
		}

	}

}
