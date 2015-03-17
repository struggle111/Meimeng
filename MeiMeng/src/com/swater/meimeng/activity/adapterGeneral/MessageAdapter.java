package com.swater.meimeng.activity.adapterGeneral;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.vo.MsgVo;
import com.swater.meimeng.activity.adapterGeneral.vo.PartyVo;
import com.swater.meimeng.activity.adapterGeneral.vo.UserSearchVo;
import com.swater.meimeng.mutils.OMdown.ImageDownloadTask;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.remoteview.RemoteImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @category 消息适配器
 */
public class MessageAdapter extends BaseAdapter {
	Context con = null;
	List<?> data = null;
	private Click_AGREE_BUTTON cell_lay_click = null;
	Msg_Cell_Click cell_click = null;
	ListView lisview = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public void loadStop() {
		if (imageLoader != null) {
			imageLoader.stop();
		}
	}

	public void loadPause() {
		if (imageLoader != null) {
			imageLoader.pause();
		}

	}

	public void loadResume() {
		if (imageLoader != null) {
			imageLoader.resume();
		}

	}

	public MessageAdapter() {

	}

	public ListView getLisview() {
		return lisview;
	}

	public void setLisview(ListView lisview) {
		this.lisview = lisview;
	}

	public Msg_Cell_Click getCell_click() {
		return cell_click;
	}

	public void setCell_click(Msg_Cell_Click cell_click) {
		this.cell_click = cell_click;
	}

	public interface Msg_Cell_Click {
		void Msg_Cell_Click(View v, int pos, ListAdapter ada, MSG_TYPE type,
				Object obj);

	}

	MSG_TYPE TYPE = MSG_TYPE.MSG_TYPE_SYS;

	public MSG_TYPE getTYPE() {
		return TYPE;
	}

	public void setTYPE(MSG_TYPE tYPE) {
		TYPE = tYPE;
	}

	public void setCell_lay_click(Click_AGREE_BUTTON cell_lay_click) {
		this.cell_lay_click = cell_lay_click;
	}

	public enum MSG_TYPE {
		MSG_TYPE_SYS, MSG_TYPE_USER, MSG_TYPE_APPLY;
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

	public MessageAdapter(Context con) {
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

	public interface Click_AGREE_BUTTON {
		void Click_AGREE_BUTTON(View v, int pos);

	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		switch (TYPE) {
		case MSG_TYPE_SYS: {
			if (view == null) {
				view = LayoutInflater.from(con)
						.inflate(R.layout.msg_cell, null);
			}
			if (data == null || data.size() < 1) {
				return view;

			}

			if (position % 2 == 1) {
				view.setBackgroundResource(R.drawable.selector_item_white);
			} else {
				view.setBackgroundResource(R.drawable.selector_item_gray);
			}

			// RemoteImageView msg_heder = null;
			ImageView msg_heder = null;
			TextView msg_content, msg_time, msg_user;
			msg_content = (TextView) view.findViewById(R.id.msg_content);
			msg_time = (TextView) view.findViewById(R.id.msg_time);
			msg_user = (TextView) view.findViewById(R.id.msg_name);

			msg_heder = (ImageView) view.findViewById(R.id.msg_header);
			view.findViewById(R.id.cell_rly).setOnClickListener(
					new CellClick(position, view, this.getLisview()
							.getAdapter(), getTYPE()));
			if (data.get(0) instanceof MsgVo) {
				MsgVo usv = (MsgVo) data.get(position);
				GeneralUtil.setValueToView(msg_content, usv.getContent());
				GeneralUtil.setValueToView(msg_time, usv.getTime());

				String title = usv.getUser_title() == null
						|| usv.getUser_title().equals("") ? "云红娘" : usv
						.getUser_title();

				GeneralUtil.setValueToView(msg_user, title);
				msg_heder.setImageResource(R.drawable.matchmaker);
				// msg_heder.setUrl(usv.getPic_url());
				// if (usv.getStatus() == 1) {
				// GeneralUtil.view_Show(view.findViewById(R.id.isreadTag));
				//
				// } else {
				GeneralUtil.view_Hide(view.findViewById(R.id.isreadTag));
				//
				// }

			}

		}

			break;
		case MSG_TYPE_APPLY: {
			if (view == null) {
				view = LayoutInflater.from(con).inflate(R.layout.apple_cell,
						null);
			}
			if (data == null || data.size() < 1) {
				return view;

			}

			if (position % 2 == 1) {
				view.setBackgroundResource(R.drawable.selector_item_white);
			} else {
				view.setBackgroundResource(R.drawable.selector_item_gray);
			}

			view.setOnClickListener(new CellClick(position, view, this
					.getLisview().getAdapter(), getTYPE()));

			ImageView msg_heder = null;
			TextView msg_content, msg_time, msg_user;
			msg_content = (TextView) view.findViewById(R.id.msg_content);
			msg_time = (TextView) view.findViewById(R.id.msg_time);
			msg_user = (TextView) view.findViewById(R.id.msg_name);

			msg_heder = (ImageView) view.findViewById(R.id.msg_header);
			try {
				//

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (data.get(0) instanceof MsgVo) {
				MsgVo usv = (MsgVo) data.get(position);

				// -agreed:是否已同意;1-未同意;2-已同意;
				if (usv.getIsagreed() == 1) {

					GeneralUtil.setValueToView(
							view.findViewById(R.id.msg_app_btn), "未同意");
					if (null != view) {
						view.findViewById(R.id.msg_app_btn).setOnClickListener(
								new MyClick(position, (Button) view
										.findViewById(R.id.msg_app_btn)));
					}
				} else {
					GeneralUtil.setValueToView(
							view.findViewById(R.id.msg_app_btn), "已同意");

				}
				GeneralUtil.setValueToView(msg_content, usv.getContent());
				GeneralUtil.setValueToView(msg_time, usv.getTime());
				GeneralUtil.setValueToView(msg_user, usv.getUser_title());
				// msg_heder.setBackgroundResource(0);
				// msg_heder.setImageBitmap(null);
				// msg_heder.setUrl(usv.getPic_url());

				// ImageDownloadTask task=new ImageDownloadTask();
				// DisplayMetrics dm=con.getResources().getDisplayMetrics();
				// task.setDisplayHeight(dm.heightPixels);
				// task.setDisplayWidth(dm.widthPixels);
				// task.execute(usv.getPic_url(),msg_heder);
				// if (imageLoader != null) {
				// imageLoader.displayImage(usv.getPic_url(), msg_heder,
				// options);
				// }
				DisplayImageOptions options = null;

				if (usv.getSex() == 2) {

					msg_heder.setImageResource(R.drawable.female_head);
					options = new DisplayImageOptions.Builder()
							.showImageForEmptyUri(R.drawable.female_head)
							.showImageOnFail(R.drawable.female_head)
							.resetViewBeforeLoading().cacheOnDisc()
							.imageScaleType(ImageScaleType.EXACTLY)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new RoundedBitmapDisplayer(120)).build();
				} else if (usv.getSex() == 1) {

					msg_heder.setImageResource(R.drawable.male_head);
					options = new DisplayImageOptions.Builder()
							.showImageForEmptyUri(R.drawable.male_head)
							.showImageOnFail(R.drawable.male_head)
							.resetViewBeforeLoading().cacheOnDisc()
							.imageScaleType(ImageScaleType.EXACTLY)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new RoundedBitmapDisplayer(120)).build();
				} else {
					msg_heder.setImageResource(R.drawable.default_head);
					options = new DisplayImageOptions.Builder()
							.showImageForEmptyUri(R.drawable.default_head)
							.showImageOnFail(R.drawable.default_head)
							.resetViewBeforeLoading().cacheOnDisc()
							.imageScaleType(ImageScaleType.EXACTLY)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new RoundedBitmapDisplayer(120)).build();
				}

				int isopen = usv.getOpen_to_me();
				// int;1-不开放;2-开放;*/
				if (isopen == 2) {
					imageLoader.resume();
					imageLoader.displayImage(usv.getPic_url(), msg_heder,
							options);
				}

			}

		}

			break;
		case MSG_TYPE_USER: {
			if (view == null) {
				view = LayoutInflater.from(con)
						.inflate(R.layout.msg_cell, null);
			}
			if (data == null || data.size() < 1) {
				return view;
			}
			ImageView msg_heder = null;
			TextView msg_content, msg_time, msg_user;
			msg_content = (TextView) view.findViewById(R.id.msg_content);
			msg_time = (TextView) view.findViewById(R.id.msg_time);
			msg_user = (TextView) view.findViewById(R.id.msg_name);
			view.setOnClickListener(new CellClick(position, view, this
					.getLisview().getAdapter(), getTYPE()));

			if (position % 2 == 1) {
				view.setBackgroundResource(R.drawable.selector_item_white);
			} else {
				view.setBackgroundResource(R.drawable.selector_item_gray);
			}

			msg_heder = (ImageView) view.findViewById(R.id.msg_header);

			if (data.get(0) instanceof MsgVo) {
				MsgVo usv = (MsgVo) data.get(position);
				GeneralUtil.setValueToView(msg_content, usv.getContent());
				GeneralUtil.setValueToView(msg_time, usv.getTime());
				GeneralUtil.setValueToView(msg_user, usv.getUser_title());

				msg_heder.setImageBitmap(null);
				DisplayImageOptions options = null;

				if (usv.getSex() == 2) {

					msg_heder.setImageResource(R.drawable.female_head);
					options = new DisplayImageOptions.Builder()
							.showImageForEmptyUri(R.drawable.female_head)
							.showImageOnFail(R.drawable.female_head)
							.resetViewBeforeLoading().cacheOnDisc()
							.imageScaleType(ImageScaleType.EXACTLY)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new RoundedBitmapDisplayer(120)).build();
				} else if (usv.getSex() == 1) {

					msg_heder.setImageResource(R.drawable.male_head);
					options = new DisplayImageOptions.Builder()
							.showImageForEmptyUri(R.drawable.male_head)
							.showImageOnFail(R.drawable.male_head)
							.resetViewBeforeLoading().cacheOnDisc()
							.imageScaleType(ImageScaleType.EXACTLY)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new RoundedBitmapDisplayer(120)).build();
				} else {
					msg_heder.setImageResource(R.drawable.default_head);
					options = new DisplayImageOptions.Builder()
							.showImageForEmptyUri(R.drawable.default_head)
							.showImageOnFail(R.drawable.default_head)
							.resetViewBeforeLoading().cacheOnDisc()
							.imageScaleType(ImageScaleType.EXACTLY)
							.bitmapConfig(Bitmap.Config.RGB_565)
							.displayer(new RoundedBitmapDisplayer(120)).build();
				}

				// msg_heder.setUrl(usv.getPic_url());

				if (usv.getOpen_to_me() == 2) {

					imageLoader.resume();
					imageLoader.displayImage(usv.getPic_url(), msg_heder,
							options);
				}

				/**
				 * if (usv.getIsagreed() == 1) {
				 * 
				 * GeneralUtil.setValueToView(
				 * view.findViewById(R.id.msg_app_btn), "同意"); if (null != view)
				 * { view.findViewById(R.id.msg_app_btn).setOnClickListener( new
				 * MyClick(position, (Button) view
				 * .findViewById(R.id.msg_app_btn))); } } else {
				 * GeneralUtil.setValueToView(
				 * view.findViewById(R.id.msg_app_btn), "已同意");
				 * 
				 * }
				 */

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
		TextView user_name, user_age, user_height, user_pos;

		void clearView() {

			user_header.setUrl("");
			user_age.setText("");
			user_name.setText("");
			user_height.setText("");
		}

	}

	// cell点击事件
	class CellClick implements OnClickListener {
		int index = -1;
		View view = null;
		ListAdapter ada = null;
		MSG_TYPE type = null;

		public CellClick(int pos, View v, ListAdapter ada, MSG_TYPE msgtype) {
			this.index = pos;
			this.view = v;
			this.ada = ada;
			this.type = msgtype;
		}

		@Override
		public void onClick(View v) {
			if (null != cell_click) {

				cell_click.Msg_Cell_Click(view, index, ada, type,
						data == null ? null : data.get(index));
			}

		}

	}

	/**
	 * @category 同意点击事件
	 */
	class MyClick implements OnClickListener {
		int index = -1;
		View view = null;

		public MyClick(int pos, View v) {
			this.index = pos;
			this.view = v;
		}

		@Override
		public void onClick(View v) {
			if (v == null) {
				return;
			}
			if (null != cell_lay_click) {

				cell_lay_click.Click_AGREE_BUTTON(view, index);
			}

		}

	}

}
