package com.swater.meimeng.activity.oomimg;

import java.io.IOException;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;

public class SlowImageCache extends ImageCache {
	private final Random r = new Random();

	public SlowImageCache(Context context) {
		super(context, CompressFormat.JPEG, 85);
	}

	@Override
	protected void downloadImage(String key, Uri uri)
			throws ClientProtocolException, IOException {
		try {
			Thread.sleep(r.nextInt(3000) + 500);
		} catch (final InterruptedException e) {

		}
		super.downloadImage(key, uri);

	}

}
