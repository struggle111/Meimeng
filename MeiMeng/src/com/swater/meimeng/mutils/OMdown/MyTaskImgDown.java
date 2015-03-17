package com.swater.meimeng.mutils.OMdown;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class MyTaskImgDown extends UserTask<Object, Object, Bitmap> {

	private ImageView imageView = null;
	LinkedList<Bitmap> down_bitmaps = new LinkedList<Bitmap>();

	int img_type = 1;

	public MyTaskImgDown(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public int getImg_type() {
		return img_type;
	}

	public void setImg_type(int img_type) {
		this.img_type = img_type;
	}

	public LinkedList<Bitmap> getDown_bitmaps() {
		return down_bitmaps;
	}

	public void setDown_bitmaps(LinkedList<Bitmap> down_bitmaps) {
		this.down_bitmaps = down_bitmaps;
	}

	public void setDisplayWidth(int width) {
		_displaywidth = width;
	}

	public int getDisplayWidth() {
		return _displaywidth;
	}

	public void setDisplayHeight(int height) {
		_displayheight = height;
	}

	public int getDisplayHeight() {
		return _displayheight;
	}

	public int getDisplayPixels() {
		return _displaypixels;
	}

	private int _displaywidth = 480;
	private int _displayheight = 800;
	private int _displaypixels = _displaywidth * _displayheight;

	@Override
	protected void onTaskPrepare() {
		// TODO Auto-generated method stub
		super.onTaskPrepare();
	}

	@Override
	protected void onErrorHandle(Context context, Exception error) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onTaskFinished(Context context, Bitmap result) {

	}

	class mainTest {
		MyTaskImgDown task_inst = new MyTaskImgDown(null);

		// task.

	}

}
