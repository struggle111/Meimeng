package com.swater.meimeng.activity.adapterGeneral;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.meimeng.app.R;
import com.meimeng.app.R.color;
import com.swater.meimeng.activity.adapterGeneral.dowmImg.ImageDownloader;
import com.swater.meimeng.activity.adapterGeneral.dowmImg.OnImageDownload;
import com.swater.meimeng.activity.adapterGeneral.vo.PartyVo;
import com.swater.meimeng.activity.adapterGeneral.vo.UserSearchVo;
import com.swater.meimeng.activity.newtabMain.VipDownloadTask;
import com.swater.meimeng.activity.newtabMain.downquence.CellTaskMgr;
import com.swater.meimeng.commbase.MeiMengApp;
import com.swater.meimeng.mutils.NSlog.NSLoger;
import com.swater.meimeng.mutils.OMdown.ImageDownloadTask;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.remoteview.RemoteImageView;

/**
 * @category 搜索适配器
 */
public class AdapterSearch extends BaseAdapter {
	Context con = null;
	ImageDownloader mDownloader;
	List<?> data = null;
	ListView ls_view = null;
	Activity act = null;

	public Activity getAct() {
		return act;
	}

	public void setAct(Activity act) {
		this.act = act;
	}

	public ListView getLs_view() {
		return ls_view;
	}

	public void setLs_view(ListView ls_view) {
		this.ls_view = ls_view;
	}

	AdapterSearch_Type type_search;
	private Cell_Act_click cell_lay_click = null;
	LinkedList<ImageDownloadTask> downs_quences = new LinkedList<ImageDownloadTask>();
	CellTaskMgr mgr_task = null;

	// ImageLoader imageDownloader = ImageLoader.getInstance();
	// public void loadStop(){
	// imageDownloader.stop();
	// }
	// public void loadPause(){
	// imageDownloader.pause();
	// }
	// public void loadResume(){
	// imageDownloader.resume();
	// }
	//

	public CellTaskMgr getMgr_task() {
		return mgr_task;
	}

	public void setMgr_task(CellTaskMgr mgr_task) {
		this.mgr_task = mgr_task;
	}

	public void setCell_lay_click(Cell_Act_click cell_lay_click) {
		this.cell_lay_click = cell_lay_click;
	}

	public void setType_search(AdapterSearch_Type type_search) {
		this.type_search = type_search;
	}

	public AdapterSearch_Type getType_search() {
		return type_search;
	}

	public enum AdapterSearch_Type {
		type_private_invite, type_party, search_type_default, type_focus, search_type_default_11;
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

	public AdapterSearch(Context con) {
		this.con = con;
	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	/** 自定义CELL点击事件！当onitem 失效时 */
	public interface Cell_Act_click {
		void Cell_lay_Click(View v, int pos);

	}

	public void clearDownQuence() {

		if (downs_quences != null) {
			for (ImageDownloadTask task : downs_quences) {
				task.cancel(true);

			}
		}

	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		switch (type_search) {
		case type_focus: {
			HolderView holder = null;
			if (view == null) {
				view = LayoutInflater.from(con).inflate(
						R.layout.vip_cell_autoline_normal, null);
				holder = new HolderView();
				holder.user_name = (TextView) view
						.findViewById(R.id.cell_username);
				holder.user_age = (TextView) view
						.findViewById(R.id.cell_userage);
				holder.user_pos = (TextView) view
						.findViewById(R.id.cell_user_cityand_province);
				holder.user_heart_desc = (TextView) view
						.findViewById(R.id.cell_desc);
				holder.user_header = (RemoteImageView) view
						.findViewById(R.id.cell_header);

				view.setTag(holder);
			}
			if (data == null || data.size() < 1) {
				return view;

			}
			holder = (HolderView) view.getTag();
			holder.resetHolderView();
			/**
			 * RemoteImageView user_header = null; TextView user_name, user_age,
			 * user_height, user_pos; user_name = (TextView)
			 * view.findViewById(R.id.cell_username); user_age = (TextView)
			 * view.findViewById(R.id.cell_userage); user_height = (TextView)
			 * view.findViewById(R.id.cell_userheight); user_pos = (TextView)
			 * view .findViewById(R.id.cell_user_cityand_province); user_header
			 * = (RemoteImageView) view.findViewById(R.id.cell_header);
			 */
			if (data.get(0) instanceof UserSearchVo) {
				UserSearchVo usv = (UserSearchVo) data.get(position);
				String pos_Name = usv.getProvince() + usv.getCity();

				if (TextUtils.isEmpty(pos_Name) || "null".equals(pos_Name)) {
					GeneralUtil.setValueToView(holder.user_pos, "四川成都");
				} else {

					GeneralUtil.setValueToView(holder.user_pos, pos_Name);
				}
				if (TextUtils.isEmpty(usv.getHeart_desc())
						|| "null".equals(usv.getHeart_desc())) {
					GeneralUtil.setValueToView(holder.user_heart_desc,
							"我在美盟，你在哪儿？");
				} else {

					GeneralUtil.setValueToView(holder.user_heart_desc,
							usv.getHeart_desc());
				}
				if (TextUtils.isEmpty(usv.getNickName())
						|| "null".equals(usv.getNickName())) {
					GeneralUtil.setValueToView(holder.user_name, "小西"
							+ position);
				} else {

					GeneralUtil.setValueToView(holder.user_name,
							usv.getNickName());
				}
				GeneralUtil.setValueToView(holder.user_age, usv.getAge() + "岁");

				String url = usv.getPhotoUrl();
				if (usv.getSex().trim().equals("2")) {
					holder.user_header
							.setImageResource(R.drawable.female_default);

				} else {
					holder.user_header.setImageResource(R.drawable.man_default);
				}
				holder.user_header.setTag(url);

				try {
					if (mDownloader == null) {
						if (getAct() != null) {
							MeiMengApp app = (MeiMengApp) getAct()
									.getApplication();
							mDownloader = app.getLoad_mgr();
							if (null == mDownloader) {
								mDownloader = ImageDownloader.getInstance();

							}
						}
					}
				} catch (Exception e) {

				}

				mDownloader.imageDownload(url, (ImageView) holder.user_header,
						"/meimeng_vip", getAct(), new OnImageDownload() {
							@Override
							public void onDownloadSucc(Bitmap bitmap,
									String c_url, ImageView mimageView) {
								ImageView imageView = (ImageView) getLs_view()
										.findViewWithTag(c_url);
								if (imageView != null) {

									if (bitmap != null) {
										NSLoger.Log("--imageDownload-----"
												+ c_url);
										imageView.setImageBitmap(bitmap);
										imageView.setTag("");
									}
								}
							}
						});
				// ImageDownloadTask task = new ImageDownloadTask();
				// task.execute(url, holder.user_header);
				// downs_quences.add(task);

			}

		}

			break;

		case search_type_default_11: {
			HolderView holder = null;
			if (view == null) {
				view = LayoutInflater.from(con).inflate(
						R.layout.vip_cell_autoline_normal, null);
				holder = new HolderView();
				holder.user_name = (TextView) view
						.findViewById(R.id.cell_username);
				holder.user_age = (TextView) view
						.findViewById(R.id.cell_userage);
				holder.user_pos = (TextView) view
						.findViewById(R.id.cell_user_cityand_province);
				holder.user_heart_desc = (TextView) view
						.findViewById(R.id.cell_desc);
				holder.user_header = (RemoteImageView) view
						.findViewById(R.id.cell_header);

				view.setTag(holder);
			} else {
				holder = (HolderView) view.getTag();
				holder.resetHolderView();
			}
			if (data == null || data.size() < 1) {
				return view;

			}

			if (data.get(0) instanceof UserSearchVo) {
				UserSearchVo usv = (UserSearchVo) data.get(position);
				String pos_Name = usv.getProvince() + usv.getCity();

				if (TextUtils.isEmpty(pos_Name) || "null".equals(pos_Name)) {
					GeneralUtil.setValueToView(holder.user_pos, "四川成都");
				} else {

					GeneralUtil.setValueToView(holder.user_pos, pos_Name);
				}
				if (TextUtils.isEmpty(usv.getHeart_desc())
						|| "null".equals(usv.getHeart_desc())) {
					GeneralUtil.setValueToView(holder.user_heart_desc,
							"我在美盟，你在哪儿？");
				} else {

					GeneralUtil.setValueToView(holder.user_heart_desc,
							usv.getHeart_desc());
				}
				if (TextUtils.isEmpty(usv.getNickName())
						|| "null".equals(usv.getNickName())) {
					GeneralUtil.setValueToView(holder.user_name, "小西");
				} else {
					if (usv.getNickName().length() > 6) {
						holder.user_name.setTextSize(15);
					}

					GeneralUtil.setValueToView(holder.user_name,
							usv.getNickName());
				}
				GeneralUtil.setValueToView(holder.user_age, usv.getAge() + "岁");
				if (usv.getSex().trim().equals("2")) {
					holder.user_header
							.setImageResource(R.drawable.female_default);

				} else {
					holder.user_header.setImageResource(R.drawable.man_default);
				}
				// DisplayMetrics dm = con.getResources().getDisplayMetrics();
				// task.setDisplayHeight(dm.heightPixels);
				// task.setDisplayWidth(dm.widthPixels);
				// task.setImg_type(2);
				// task.setDisplayHeight(800);
				// task.setDisplayWidth(400);
				VipDownloadTask task = new VipDownloadTask(con);
				task.execute(usv.getPhotoUrl(), holder.user_header);
				// holder.user_header.setUrl(usv.getPhotoUrl());
				// downs_quences.add(task);
				// cell.addDownTask(new CellDownTask(url, imv));
				try {
					// if (mgr_task==null) {
					// mgr_task=CellTaskMgr.getInstance();
					// }
					// mgr_task.addDownTask(new CellDownTask(usv.getPhotoUrl(),
					// holder.user_header));
					Thread.sleep(10);
				} catch (Exception e) {
				}

			}

		}

			break;
		case search_type_default: {
			HolderView holder = null;
			if (view == null) {
				view = LayoutInflater.from(con).inflate(
						R.layout.vip_cell_autoline_normal, null);
				holder = new HolderView();
				holder.user_name = (TextView) view
						.findViewById(R.id.cell_username);
				holder.user_age = (TextView) view
						.findViewById(R.id.cell_userage);
				holder.user_pos = (TextView) view
						.findViewById(R.id.cell_user_cityand_province);
				holder.user_heart_desc = (TextView) view
						.findViewById(R.id.cell_desc);
				holder.user_header = (RemoteImageView) view
						.findViewById(R.id.cell_header);

				view.setTag(holder);
			} else {
				holder = (HolderView) view.getTag();
				if (mDownloader != null) {
					mDownloader.Force_Lease();
				}
				// holder.resetHolderView();
			}
			if (data == null || data.size() < 1) {
				return view;

			}

			if (data.get(0) instanceof UserSearchVo) {
				UserSearchVo usv = (UserSearchVo) data.get(position);
				String pos_Name = usv.getProvince() + usv.getCity();

				if (TextUtils.isEmpty(pos_Name) || "null".equals(pos_Name)) {
					GeneralUtil.setValueToView(holder.user_pos, "四川成都");
				} else {

					GeneralUtil.setValueToView(holder.user_pos, pos_Name);
				}
				if (TextUtils.isEmpty(usv.getHeart_desc())
						|| "null".equals(usv.getHeart_desc())) {
					GeneralUtil.setValueToView(holder.user_heart_desc,
							"我在美盟，你在哪儿？");
				} else {

					GeneralUtil.setValueToView(holder.user_heart_desc,
							usv.getHeart_desc());
				}
				if (TextUtils.isEmpty(usv.getNickName())
						|| "null".equals(usv.getNickName())) {
					GeneralUtil.setValueToView(holder.user_name, "小西");
				} else {
					if (usv.getNickName().length() > 6) {
						holder.user_name.setTextSize(15);
					}

					GeneralUtil.setValueToView(holder.user_name,
							usv.getNickName());
				}
				GeneralUtil.setValueToView(holder.user_age, usv.getAge() + "岁");
				if (usv.getSex().trim().equals("2")) {
					holder.user_header
							.setImageResource(R.drawable.female_default);

				} else {
					holder.user_header.setImageResource(R.drawable.man_default);
				}

				final String url = usv.getPhotoUrl();// /;URLS[position];
				NSLoger.Log("url--->" + url);
				holder.user_header.setTag(url);

				try {
					if (mDownloader == null) {
						if (getAct() != null) {
							MeiMengApp app = (MeiMengApp) getAct()
									.getApplication();
							mDownloader = app.getLoad_mgr();
							if (null == mDownloader) {
								mDownloader = ImageDownloader.getInstance();

							}
						}
					}
				} catch (Exception e) {
				}

				mDownloader.imageDownload(url, (ImageView) holder.user_header,
						"/meimeng_vip", getAct(), new OnImageDownload() {
							@Override
							public void onDownloadSucc(Bitmap bitmap,
									String c_url, ImageView mimageView) {
								ImageView imageView = (ImageView) getLs_view()
										.findViewWithTag(c_url);
								if (imageView != null) {

									if (bitmap != null) {
										NSLoger.Log("--imageDownload-----"
												+ c_url);
										imageView.setImageBitmap(bitmap);
										imageView.setTag("");
									}
								}
							}
						});

			}

		}

			break;
		case type_party: {
//			if (view == null) {
				view = LayoutInflater.from(con)
						.inflate(R.layout.act_cell, null);
//			}
			if (data == null || data.size() < 1) {
				return view;

			}

			RemoteImageView act_img = null;
			Button act_status = null;
			Button act_title = null;
			// TextView user_name, user_age, user_height, user_pos;
			// user_name = (TextView) view.findViewById(R.id.cell_username);
			// user_age = (TextView) view.findViewById(R.id.cell_userage);
			// user_height = (TextView) view.findViewById(R.id.cell_userheight);
			// user_pos = (TextView) view
			// .findViewById(R.id.a);
			view.findViewById(R.id.cell_bg_lay).setOnClickListener(
					new MyClick(position, view));
			act_img = (RemoteImageView) view.findViewById(R.id.act_img);
			act_title = (Button) view.findViewById(R.id.act_title);
			act_status = (Button) view.findViewById(R.id.act_status);
			if (data.get(0) instanceof PartyVo) {
				PartyVo pv = (PartyVo) data.get(position);
				act_img.setUrl(pv.getShow_pic());

				GeneralUtil.setValueToView(act_title, pv.getSubject());

				// imageDownloader.displayImage(pv.getShow_pic(), act_img);
				// -status:活动状态,int;1-未开始;2-进行中;3-已结束;
				switch (pv.getStatus()) {
				case 1: {
					GeneralUtil.setValueToView(act_status, "未开始");
					act_status.setTextColor(con.getResources().getColor(
							R.color.gold));
				}

					break;
				case 2: {
					GeneralUtil.setValueToView(act_status, "进行中");
					act_status.setTextColor(con.getResources().getColor(
							R.color.gold));
				}

					break;
				case 3: {
					GeneralUtil.setValueToView(act_status, "已结束");
					act_status.setTextColor(con.getResources().getColor(
							R.color.white));
					// act_status.setTextColor(con.getResources().getColor(R.color.gr))

				}

					break;

				default:
					break;
				}

			}

		}

			break;
		case type_private_invite: {
//			if (view == null) {
				view = LayoutInflater.from(con).inflate(
						R.layout.cell_private_invite, null);
//			}
			if (data == null || data.size() < 1) {
				return view;
			}

			RemoteImageView act_img = null;
			TextView party_name, party_status, party_place;
			party_name = (TextView) view.findViewById(R.id.party_name);
			party_place = (TextView) view.findViewById(R.id.party_place);
			party_status = (TextView) view.findViewById(R.id.party_status);

			act_img = (RemoteImageView) view.findViewById(R.id.act_img);

			if (data.get(0) instanceof PartyVo) {
				PartyVo pv = (PartyVo) data.get(position);
				act_img.setUrl(pv.getShow_pic());
				GeneralUtil.setValueToView(party_place, "" + pv.getPlace());
				GeneralUtil.setValueToView(party_name, pv.getSubject());
				int status = pv.getStatus();

				String sta = "进行中";
				if (status == 1) {
					sta = "未开始";
					party_status.setTextColor(con.getResources().getColor(
							R.color.gold));
				}
				if (status == 2) {
					sta = "进行中";
					party_status.setTextColor(con.getResources().getColor(
							R.color.gold));
				}
				if (status == 3) {
					sta = "已结束";
					party_status.setTextColor(con.getResources().getColor(
							R.color.white));
				}

				GeneralUtil.setValueToView(party_status, sta);

			}

		}

			break;

		default:
			break;
		}
		return view;
	}

	public class HolderView {
		RemoteImageView user_header = null;
		TextView user_name, user_age, user_pos, user_heart_desc;

		void clearView() {

			/** user_header.setUrl(""); */
			if (user_header != null) {
				// user_header.setImageBitmap(null);
				user_header.setImageResource(R.drawable.female_default);
			}
			// user_header.setImageResource(0);
			user_age.setText("");
			user_name.setText("");
			user_heart_desc.setText("");
		}

		public void resetHolderView() {
			this.clearView();
			cancelDownQuence();
		}

	}

	void cancelDownQuence() {

		if (downs_quences != null && downs_quences.size() > 0) {
			for (ImageDownloadTask task : downs_quences) {
				if (null != task) {

					task.cancel(true);
					NSLoger.Log("---取消下载队列----》");
				}

			}
		}
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
			if (null != cell_lay_click) {

				cell_lay_click.Cell_lay_Click(view, index);
			}

		}

	}
	private void setOrder(List<PartyVo> ls_p){
		/**
		 * 直接插入排序法
		 */
		int total = 1;
		for (int i = 1; i < ls_p.size(); i++) {
			PartyVo party = ls_p.get(i);
			String t1 = party.getTime();
			long a = getSecond(t1);
//                int a = numList.get(i);
			for (int j = total - 1; j >= 0; j--) {
				PartyVo party2 = ls_p.get(j);
				String t2 = party2.getTime();
				long b = getSecond(t2);
				if (a > b) {
					ls_p.set(j,party);
					ls_p.set(j+1,party2);
//                        numList.set(j, a);
//                        numList.set(j + 1, b);
				} else {
					break;
				}
			}
			total = total + 1;
		}
	}
	private long getSecond(String time) {

		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt2 = sdf.parse(time);
			//继续转换得到秒数的long型
			long lTime = dt2.getTime() / 1000;
			return lTime;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
