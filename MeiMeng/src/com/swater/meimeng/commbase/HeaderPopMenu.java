package com.swater.meimeng.commbase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.vo.UserInfo;
import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.database.XmlMedalData;
import com.swater.meimeng.database.ShareUtil.UserVo;
import com.swater.meimeng.fragment.recommend.MedalView;
import com.swater.meimeng.mutils.MedalUtil;
import com.swater.meimeng.mutils.constant.GeneralUtil;
import com.swater.meimeng.mutils.imgs.ImageDispose;
import com.swater.meimeng.mutils.remoteview.CachePoolException;
import com.swater.meimeng.mutils.remoteview.CachePoolManager;
import com.swater.meimeng.mutils.remoteview.RemoteImageView;

/**
 * 顶部菜单
 * 
 * @author chengshiyang
 * 
 */
public class HeaderPopMenu implements OnClickListener {
	protected ImageLoader imageLoader = null;
	DisplayImageOptions options = null;
	LinearLayout medal_group_linear = null;

	void iniImgloader() {
		if (imageLoader == null) {

			imageLoader = ImageLoader.getInstance();
		}
		if (options == null) {

			options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.default_head)
					.showImageOnFail(R.drawable.default_head)
					.resetViewBeforeLoading().cacheOnDisc()
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.displayer(new RoundedBitmapDisplayer(20)).build();

		}
	}

	/**
	 * @category 定义顶部菜单点击事件
	 */
	public interface OnHeaderItemClick {
		/**
		 * @category 传递点击事件
		 */
		public void onHeader_ItemClick(View v);
	}

	Map<String, Bitmap> weak_img = new HashMap<String, Bitmap>();
	final String WEAK_HEAD_URL = "WEAK_HEAD_URL";
	Bitmap user_bitmap = null;

	private Context context;
	private PopupWindow popupWindow;
	private OnHeaderItemClick listener;
	private LayoutInflater inflater;
	public View view = null;
	ShareUtil sh = null;
	public RemoteImageView header = null;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public RemoteImageView getHeader() {
		return header;
	}

	public void setHeader(RemoteImageView header) {
		this.header = header;
	}

	public interface setUserImg {

		void setUserImg(ImageView imv);
	}

	View txtnick = null;

	public HeaderPopMenu(Context context) {
		this.context = context;
		iniImgloader();

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		try {

			view = inflater.inflate(R.layout.pop_header_menu, null, true);
		} catch (Exception e) {
			return;
		}

		LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.cc16);
		LinearLayout visitorLayout = (LinearLayout)view.findViewById(R.id.cc17);
		if (MeiMengApp.isVisitor){
			visitorLayout.setVisibility(View.VISIBLE);
			linearLayout.setVisibility(View.GONE);
		}else {
			visitorLayout.setVisibility(View.GONE);
			linearLayout.setVisibility(View.VISIBLE);
		}
		view.findViewById(R.id.cc11).setOnClickListener(this);
		view.findViewById(R.id.cc12).setOnClickListener(this);
		view.findViewById(R.id.cc13).setOnClickListener(this);
		view.findViewById(R.id.cc14).setOnClickListener(this);
		view.findViewById(R.id.cc15).setOnClickListener(this);
		linearLayout.setOnClickListener(this);
		visitorLayout.setOnClickListener(this);

		view.findViewById(R.id.lay_person).setOnClickListener(this);
		// header = (RemoteImageView) view.findViewById(R.id.gallery_imv);
		header = (RemoteImageView) view.findViewById(R.id.gallery_imv);
		header.setOnClickListener(this);
		header.setOpenRect(true);
		header.setRect(true);
		header.setRectSize(120);

		sh = ShareUtil.getInstance(context);
		txtnick = view.findViewById(R.id.pop_user_nickname);
		medal_group_linear = (LinearLayout) view.findViewById(R.id.medal_group);

        //如果是游客
        if (MeiMengApp.isVisitor){
            txtnick.setVisibility(View.GONE);
        }
        //有账号的用户
        else {
            GeneralUtil
                    .setValueToView(txtnick, "" + sh.getUserInfo().getNickname());
        }


		// popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT,true);
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		// popupWindow.setAnimationStyle(R.style.shiyang_menu__pop);

		if (TextUtils.isEmpty(sh.getUserInfo().getHeader())
				|| sh.getUserInfo().getHeader().equals("null")) {
			if (sh.getUserInfo().getSex() == 2) {
				header.setImageResource(R.drawable.female_head);

			} else if (sh.getUserInfo().getSex() == 1) {

				header.setImageResource(R.drawable.male_head);
			} else {
				header.setImageResource(R.drawable.default_head);
			}

		} else {

			imageLoader.displayImage(sh.getUserInfo().getHeader(), header,
					options);
		}

        //如果是游客，就把头像设置为默认头像
        if (MeiMengApp.isVisitor){
            header.setImageResource(R.drawable.default_head);
        }

		getMedalInfo();

		popupWindow.setOutsideTouchable(true);
		// popupWindow.update();

	}

	ImageView imv_vip = null, imv_check = null;

	void getMedalInfo() {
		// active：是否激活，int；1-未激活；2-激活；
		// audit_type：认证类型，int；1-在线审核；2-面谈审核；
		// vip_level：vip等级，int；0-未付费;1-银牌；2-金牌;3-黑牌；
		imv_vip = (ImageView) view.findViewById(R.id.au_vip);
		imv_check = (ImageView) view.findViewById(R.id.au_check);
		if (sh.getUserInfo().getSex() == 1) {
			GeneralUtil.view_Hide(imv_check);

		} else {
			GeneralUtil.view_Show(imv_check);

		}
		if (ShareUtil.getInstance(context).getUserInfo().getActive() == 2) {
			int vip_type = ShareUtil.getInstance(context).getUserInfo()
					.getVip_level();
			switch (vip_type) {
			case 0: {
				imv_vip.setVisibility(view.GONE);
				// GeneralUtil.view_Hide(view.findViewById(R.id.medal_group));
			}

				break;
			case 1: {
				imv_vip.setImageResource(R.drawable.vip_silver);

			}

				break;
			case 2: {
				imv_vip.setImageResource(R.drawable.vip_golden);

			}

				break;
			case 3: {
				imv_vip.setImageResource(R.drawable.vip_black);

			}

				break;

			default:
				break;
			}
			int aud_type = ShareUtil.getInstance(context).getUserInfo()
					.getAudit_type();
			// ：认证类型，int；1-在线审核；2-面谈审核；
			switch (aud_type) {
			case 1: {
				imv_check.setImageResource(R.drawable.check_online);
			}

				break;
			case 2: {
				imv_check.setImageResource(R.drawable.check_face);
			}

				break;

			default:
				break;
			}

		}
		// getMedalInfo_user();
	}

	XmlMedalData medal_data = XmlMedalData.getSingle();

	void iniMedalData() {

		medal_data = XmlMedalData.getSingle();
		try {
			medal_data.init(context);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void getMedalInfo_user() {
		UserVo vo = sh.getUserInfo();
		if (!TextUtils.isEmpty(vo.getMedal()) && !vo.getMedal().equals("null")) {
			iniMedalData();
			if (vo.getMedal().contains(",")) {

				String[] medal_arr = vo.getMedal().split(",");
				if (medal_arr != null && medal_arr.length > 0) {

					for (int i = 0; i < medal_arr.length; i++) {
						String medal_Name = "a" + medal_arr[i];
						int medal_id = MedalUtil.getMedalIdByName(context,
								medal_Name);
						MedalView medal_cell = new MedalView(context, medal_id);
						String medalName = medal_data
								.getMedalNameById(medal_arr[i]);
						medal_cell.setMedalName(medalName);
						medal_group_linear.addView(medal_cell);

					}
				}
			} else {
				String medal_Name = "a" + vo.getMedal();
				int medal_id = MedalUtil.getMedalIdByName(context, medal_Name);
				MedalView medal_cell = new MedalView(context, medal_id);
				String medalName = medal_data.getMedalNameById(vo.getMedal());
				medal_cell.setMedalName(medalName);
				medal_group_linear.addView(medal_cell);
				// medal_1.setImageResource(medal_id);
				// view_Show(medal_1);

			}

		}

	}

	protected View getOverView() {
		return view == null ? new View(context) : view;

	}

	public void setOnHeaderItemClick(OnHeaderItemClick listener) {
		this.listener = listener;
	}

	public void showAsDropDown(View parent) {

		try {
			// getMedalInfo();
			if (null == parent) {
				return;
			}
			if (popupWindow == null || !popupWindow.isShowing()) {
				if (sh == null) {
					sh = ShareUtil.getInstance(context);
				}
				if (txtnick != null
						&& !TextUtils.isEmpty(sh.getUserInfo().getNickname())
						&& !"null".equals(sh.getUserInfo().getNickname())) {

					GeneralUtil.setValueToView(txtnick, ""
							+ sh.getUserInfo().getNickname());
				} else {
					GeneralUtil.setValueToView(txtnick, "" + sh.getUserid());// +sh.getUserInfo().getNickname()
				}

				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				popupWindow.showAsDropDown(parent, 10, context.getResources()
						.getDimensionPixelSize(R.dimen.popmenu_yoff));

				popupWindow.setFocusable(true);
				popupWindow.setOutsideTouchable(true);
				popupWindow.update();
			} else {
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void dismiss() {
		popupWindow.dismiss();
	}

	/**
	 * private void downLoadHeader(String headurl) { final String headUrl =
	 * headurl; if (TextUtils.isEmpty(headUrl)) { return; }
	 * Log.d("--user-header--url--->" + headurl, "---下载头像---》》》");
	 * 
	 * if (weak_img.containsKey(WEAK_HEAD_URL)) { user_bitmap =
	 * weak_img.get(WEAK_HEAD_URL); return;
	 * 
	 * } else { new Thread(new Runnable() {
	 * 
	 * @Override public void run() { byte[] byts_imgs; try { byts_imgs =
	 *           CachePoolManager.readByteByURL(headUrl); user_bitmap =
	 *           ImageDispose.getPicFromBytes(byts_imgs, null); if (user_bitmap
	 *           != null) {
	 * 
	 *           weak_img.put(WEAK_HEAD_URL, user_bitmap); } } catch (Exception
	 *           e) {
	 * 
	 *           e.printStackTrace(); }
	 * 
	 *           } }).start();
	 * 
	 *           }
	 * 
	 *           }
	 */

	@Override
	public void onClick(View v) {
		this.listener.onHeader_ItemClick(v);
	}
}
