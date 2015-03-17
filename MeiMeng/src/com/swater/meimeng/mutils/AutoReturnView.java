package com.swater.meimeng.mutils;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.meimeng.app.R;

/**
 * 自定义文本标签，自动换行 MyView 2012-9-12 下午09:30:47
 * 
 * @author md
 * @version 1.0.0
 */
public class AutoReturnView extends View {
	private Paint mPaint = new Paint();
	private String content = "";
	private int size = 14;
	private int color = Color.BLACK;
	private int maxLines = 0;

	
	
	
	public Paint getmPaint() {
		return mPaint;
	}

	public void setmPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public String getContent() {
		return content;
	}

	public int getMaxLines() {
		return maxLines;
	}

	public AutoReturnView(Context context) {
		this(context, null);
		
	}

	public AutoReturnView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.text_view);
		String textColor = array.getString(R.styleable.text_view_color);
		String textSize = array.getString(R.styleable.text_view_size);
		String textMaxLines = array.getString(R.styleable.text_view_maxLines);
		String textContent = array.getString(R.styleable.text_view_content);
		array.recycle();
		if (textColor != null) {
			color = Color.parseColor(textColor);
		}
		if (textSize != null) {
			size = Integer.parseInt(textSize);
		}
		if (textMaxLines != null) {
			maxLines = Integer.parseInt(textMaxLines);
		}
		if (textContent != null) {
			content = textContent;
		}
		init();

	}

	public AutoReturnView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public void setContent(String str) {
		this.content = str;
		init();
	
		
	}

	private void init() {
		mPaint.setAntiAlias(true);
		mPaint.setColor(color);
		mPaint.setStyle(Style.STROKE);
		mPaint.setTextSize(getRawSize(TypedValue.COMPLEX_UNIT_DIP, size));

	}

	/**
	 * 设置字体大小 setTextSise
	 * 
	 * @param textSize
	 *            显示内容字体大小(int类型)
	 * @return void
	 * @since 1.0.0
	 */
	public void setTextSise(int textSize) {
		size = textSize;
		init();
	}

	/**
	 * 
	 * 设置字体颜色 setTextColor
	 * 
	 * @param cor
	 *            16进制颜色编码(#ffffff)
	 * @return void
	 * @since 1.0.0
	 */
	public void setTextColor(int cor) {
		color = cor;
		init();
	}

	/**
	 * 
	 * 设置显示内容最大行数，超过显示行数截断用...省略 setMaxLines
	 * 
	 * @param max
	 *            最大行数 int
	 * @return void
	 * @since 1.0.0
	 */
	public void setMaxLines(int max) {
		maxLines = max;
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		FontMetrics fm = mPaint.getFontMetrics();

		float baseline = fm.descent - fm.ascent;
		float x = 0;
		float y = baseline;

		String[] texts = autoSplit(content, mPaint, getWidth() - 5);

		for (String text : texts) {
			if (texts.length == 1) {
				canvas.drawText(text, x, y + 10, mPaint);
			} else {
				canvas.drawText(text, x, y, mPaint);
			}
			y += baseline + fm.leading;
		}
	invalidate();
	}

	/**
	 * 自动分割文本
	 * 
	 * 
	 * @pocketState content 需要分割的文本
	 * @pocketState p 画笔，用来根据字体测量文本的宽度
	 * @pocketState width 指定的宽度
	 * @return 一个字符串数组，保存每行的文本
	 */
	private String[] autoSplit(String content, Paint p, float width) {
		int length = content.length();
		int lastSpilt = 0;
		float textWidth = p.measureText(content);
		if (textWidth <= width) {
			return new String[] { content };
		}

		int start = 0, end = 1, i = 0;
		int lines = (int) Math.ceil(textWidth / width); // 计算行数
		if (lines > 1 && maxLines != 0) {
			lines = maxLines;
		}
		String[] lineTexts = new String[lines];
		while (start < length) {
			if (p.measureText(content, start, end) > width) { // 文本宽度超出控件宽度时
				String result = (String) content.subSequence(start, end - 1);
				start = end - 1;

				if (i == (maxLines - 1) && maxLines != 0) {
					lastSpilt = result.length();
					if (result.length() > (lastSpilt - 1)) {
						lineTexts[i] = result.substring(0,
								(result.length() - 1)) + "...";
					} else {
						lineTexts[i] = result;
					}
					break;
				} else {
					lineTexts[i] = result;
				}
				i = i + 1;
			}
			if (end == length) { // 不足一行的文本
				lineTexts[i] = (String) content.subSequence(start, end);
				break;
			}
			end += 1;
		}
		return lineTexts;
	}

	/**
	 * 获取指定单位对应的原始大小（根据设备信息） px,dip,sp -> px
	 * 
	 * Paint.setTextSize()单位为px
	 * 
	 * 代码摘自：TextView.setTextSize()
	 * 
	 * @pocketState unit TypedValue.COMPLEX_UNIT_*
	 * @pocketState size
	 * @return
	 */
	public float getRawSize(int unit, float size) {
		Context c = getContext();
		Resources r;

		if (c == null)
			r = Resources.getSystem();
		else
			r = c.getResources();

		return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
	}
}
