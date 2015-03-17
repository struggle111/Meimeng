package com.swater.meimeng.activity.reg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.meimeng.app.R;
import com.swater.meimeng.activity.newtabMain.IndexActivity;
import com.swater.meimeng.activity.user.WaitActive;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.database.ShareUtil;
import com.swater.meimeng.mutils.diagutil.DialogItem;
import com.swater.meimeng.mutils.imgs.ImageDispose;
import com.swater.meimeng.mutils.imgs.SaveImg;
import com.swater.meimeng.mutils.net.RespVo;
import com.swater.meimeng.mutils.net.UploadNew;

public class RegUploadImg extends BaseTemplate {
	protected ArrayList<DialogItem> mItems = new ArrayList<DialogItem>();
	public static final int PHOTO_PICKED_WITH_DATA = 102;
	public static final int CAMERA_WITH_DATA = 101;

	public static final String IMGPATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/meimeng/temp/img.jpg";
	ImageView tempPic;
	private byte[] picData;
	String selectPicPath = "";
	String globalPath = "";
	int userid = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_upload);
		this.TempRun();

	}

	@Override
	public void iniView() {
		view_Show(findButton(R.id.home_right_btn));
		setValueToView(findViewById(R.id.home_right_btn), "提交");
		view_Show(findViewById(R.id.home_left_btn));
		setValueToView(findViewById(R.id.home_left_btn), "返回");
		tempPic = findImageView(R.id.upload_bg);
		userid = getIntent().getIntExtra("uid", 0);
		this.IniDiagShw();

	}

	@Override
	public void bindClick() {
		setClickEvent(findViewById(R.id.home_left_btn),
				findButton(R.id.home_right_btn), findViewById(R.id.upload_bg));
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.home_right_btn: {
			// Intent in = new Intent(t_context, MainActivity.class);
			// in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// t_context.startActivity(in);
			uploadImgs(picData);

		}
			break;
		case R.id.home_left_btn: {
			onBackPressed();
			// finish();
		}
			break;
		case R.id.upload_bg: {

			// showToast("click--bg--");
			this.pullDiag();

		}

			break;

		default:
			break;
		}
	};

	void pullDiag() {
		com.swater.meimeng.mutils.diagutil.Tools.createCustomDialog(t_context,
				mItems, R.style.CustomDialogOld);

	}

	void IniDiagShw() {

		mItems.add(new DialogItem(R.string.pull_item_title,
				R.layout.custom_dialog_title));
		// 照相机
		mItems.add(new DialogItem(R.string.pull_item_camera,
				R.layout.custom_dialog_normal) {

			@Override
			public void onClick() {

				if (existSDcard()) { // 判断手机SD卡是否存在
					File vFile = new File(IMGPATH);
					if (!vFile.exists()) {
						File vDirPath = vFile.getParentFile();
						vDirPath.mkdirs();
					}
					Uri uri = Uri.fromFile(vFile);
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//
					startActivityForResult(intent, CAMERA_WITH_DATA);
				} else {
					showToast("外部存储卡无法使用！");
				}

			}
		});

		// 相册
		mItems.add(new DialogItem(R.string.pull_item_gallery,
				R.layout.custom_dialog_normal) {
			@Override
			public void onClick() {
				super.onClick();
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

				intent.setType("image/*");

				startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);

			}
		});

		// 取消
		mItems.add(new DialogItem(R.string.pull_item_cancel,
				R.layout.custom_dialog_cancel));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ContentResolver resolver = getContentResolver();

		if (resultCode == RESULT_OK) {
			Bitmap bmp = null;
			switch (requestCode) {
			case CAMERA_WITH_DATA:// 照相

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				bmp = BitmapFactory.decodeFile(IMGPATH, options); // 此时返回bm为空

				options.inJustDecodeBounds = false;
				// 缩放比
				int be = (int) (options.outHeight / (float) 200);
				if (be <= 0)
					be = 1;
				options.inSampleSize = be;
				Log.v("--img--path-->>", IMGPATH);
				// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
				bmp = BitmapFactory.decodeFile(IMGPATH, options);

				// bmp=cutImgs(bmp);
				if (bmp != null) {

					tempPic.setImageBitmap(bmp);
					tempPic.setBackgroundResource(0);
					tempPic.setVisibility(View.VISIBLE);
				} else {
					showToast("图片解析错误！");
				}
				/*
				 * //-lazy to -call-back--when upload-- call back when call
				 * upload function by chengshiyang!!! picData =
				 * ImageDispose.Bitmap2Bytes(bmp); globalPath = "/mnt" +
				 * SaveImg.saveMyBitmap(bmp);
				 */
				break;
			case PHOTO_PICKED_WITH_DATA:// 图片
				byte[] mContent;
				try {

					// 获得图片的uri
					Uri originalUri = data.getData();
					// 将图片内容解析成字节数组
					mContent = readStream(resolver.openInputStream(Uri
							.parse(originalUri.toString())));
					// 将字节数组转换为ImageView可调用的Bitmap对象
					bmp = getPicFromBytes(mContent, null, originalUri);
					// //把得到的图片绑定在控件上显示
					// get--path
					String[] proj = { MediaStore.Images.Media.DATA };
					Cursor cursor = managedQuery(originalUri, proj, null, null,
							null);
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);// 注意这里，当图片是0kb时抛异常

					// 将光标移至开头 ，这个很重要，不小心很容易引起越界

					cursor.moveToFirst();

					// 最后根据索引值获取图片路径
					globalPath = cursor.getString(column_index);
					Log.v("-globalPath-file--path000-->>>", globalPath);

					if (null != bmp) {

						tempPic.setImageBitmap(bmp);
						tempPic.setVisibility(View.VISIBLE);
						tempPic.setBackgroundResource(0);
					}
					// SaveImg.saveMyBitmap_new(bmp, "a");

					// -lazy to -call-back--when upload--
					/**
					 * picData = ImageDispose.Bitmap2Bytes(bmp); int lg =
					 * picData.length; int a = (int) lg / 1024; if (a > 1044) {
					 * Log.d("--图片有点大哦！---", "-----发图片--" + a + "kb");
					 * readImgFromGallery(originalUri, bmp); }
					 */
				} catch (FileNotFoundException e) {
					Toast.makeText(RegUploadImg.this, "图片找不到",
							Toast.LENGTH_SHORT).show();
					Log.e("FileNotFoundException", e.getMessage(), e);
				} catch (Exception e) {
					Toast.makeText(RegUploadImg.this, "图片错误",
							Toast.LENGTH_SHORT).show();
					Log.e("Exception", e.getMessage(), e);
				}

				break;
			default:
				break;
			}
		}
	}

	private Bitmap cutImgs(Bitmap bitmap) {
		// DisplayMetrics dm = getResources().getDisplayMetrics();
		// mScreenWidth = dm.widthPixels;//屏幕宽
		// mScreenHeight = dm.heightPixels;//屏幕高
		int wid = 240;
		int height = 240;
		// Bitmap bmp =
		// ((BitmapDrawable)getResources().getDrawable(R.drawable.show)).getBitmap();
		Bitmap mBitmap = Bitmap.createScaledBitmap(bitmap, wid, height, true);
		SaveImg.saveMyBitmap(bitmap);
		return mBitmap;
	}

	/* 处理大图 */
	void readImgFromGallery(Uri uri, Bitmap bp) {

		String[] proj = { MediaStore.Images.Media.DATA };
		// 好像是android多媒体数据库的封装接口，具体的看Android文档

		Cursor cursor = managedQuery(uri, proj, null, null, null);

		// 按我个人理解 这个是获得用户选择的图片的索引值

		try {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);// 注意这里，当图片是0kb时抛异常

			// 将光标移至开头 ，这个很重要，不小心很容易引起越界

			cursor.moveToFirst();

			// 最后根据索引值获取图片路径

			String path = cursor.getString(column_index);
			Log.v("-gethahahhaha-file--path000-->>>", path);
			selectPicPath = path;
			BitmapFactory.Options options2 = new BitmapFactory.Options();
			options2.inJustDecodeBounds = true;
			bp = BitmapFactory.decodeFile(IMGPATH, options2); // 此时返回bm为空

			options2.inJustDecodeBounds = false;
			// 设置最大压缩比
			int Maxcom = 4;
			// 缩放比
			int b2e = (int) (options2.outHeight / (float) 200);
			if (b2e <= 0)
				b2e = Maxcom;
			options2.inSampleSize = b2e;
			Log.v("--img--path-->>", path);
			// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦

			if (path != null) {

				bp = BitmapFactory.decodeFile(path, options2);
			} else {
				Toast.makeText(this.getBaseContext(), "没有找到图片", 2).show();
			}
			options2.inJustDecodeBounds = false;
			picData = ImageDispose.Bitmap2Bytes(bp);

		} catch (Exception e) {
			Toast.makeText(RegUploadImg.this, "图片错误", Toast.LENGTH_SHORT)
					.show();
			Log.e("Exception", e.getMessage(), e);
		}
	}

	public static Bitmap getPicFromBytes(byte[] bytes,
			BitmapFactory.Options opts, Uri url) {
		int totalsize = (int) bytes.length / 1024;
		if (bytes != null) {

			if (totalsize > 1044) {
				opts = new BitmapFactory.Options();
				opts.inSampleSize = 4;
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			} else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

		} else
			return null;
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ProDimiss();
			switch (msg.what) {
			case Resp_action_ok: {

				// parselJson(resp.getResp());
				// regPush();
				Intent intent = new Intent();

				if (ShareUtil.getInstance(t_context).getUserInfo().getActive() == 2) {

					intent.setClass(t_context, IndexActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					t_context.startActivity(intent);
					finish();
				} else {
					intent.setClass(t_context, WaitActive.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					t_context.startActivity(intent);
					finish();
				}
				break;

				// showToat("上传成功！");
				// jumpOtherActivity(MainActivity.class);
				// finish();
			}

			case Resp_action_fail: {
				showToat("上传失败！！" + mg2String(msg));
				Intent intent = new Intent();

				if (ShareUtil.getInstance(t_context).getUserInfo().getActive() == 2) {

					intent.setClass(t_context, IndexActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					t_context.startActivity(intent);
					finish();
				} else {
					intent.setClass(t_context, WaitActive.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					t_context.startActivity(intent);
					finish();
				}
				break;

			}

			case Resp_exception: {
				showToat("上传异常！！");
				Intent intent = new Intent();

				if (ShareUtil.getInstance(t_context).getUserInfo().getActive() == 2) {

					intent.setClass(t_context, IndexActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					t_context.startActivity(intent);
					finish();
				} else {
					intent.setClass(t_context, WaitActive.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					t_context.startActivity(intent);
					finish();
				}
				break;

			}


			default:
				break;
			}

		};

	};

	/**
	 * @category 上传图片
	 * @param bytes
	 */
	void uploadImgs(byte[] imgs) {
		ProShow("上传中...");
		req_map_param.put(UserGobal.USER_ID, userid + "");

		poolThread.submit(new Runnable() {

			@Override
			public void run() {
				try {
					RespVo resp = null;
					// resp = RequestByPost.uploadUserFile(MURL_user_uploadImg,
					// globalPath, userid + "", "","");

					UploadNew instance = UploadNew.getInstance(t_context);
					instance.setConnection(MURL_user_uploadImg);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
//					BasicNameValuePair bvp1 = new BasicNameValuePair("uid",
//							shareUserInfo().getUserid() + "");
					BasicNameValuePair bvp1 = new BasicNameValuePair("uid",
							userid + "");
					// BasicNameValuePair bvp2 = new BasicNameValuePair("data",
					// globalPath);
					BasicNameValuePair bvp3 = new BasicNameValuePair(
							"filename", globalPath.substring(globalPath
									.lastIndexOf("/")));
					params.add(bvp1);
					// params.add(bvp2);
					params.add(bvp3);

					resp = instance.getInputStream(params, globalPath);

					Log.d("--RespVo---upload--imgs---->" + resp.toString(),
							"---");
					if (!resp.isHasError()) {
						handler.obtainMessage(Resp_action_ok).sendToTarget();

					} else {
						handler.obtainMessage(Resp_action_fail,
								resp.getErrorDetail()).sendToTarget();

					}

				} catch (Exception e) {
					handler.obtainMessage(Resp_exception).sendToTarget();
				}

			}
		});

	}

}
