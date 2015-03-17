package com.swater.meimeng.activity.user;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import com.swater.meimeng.activity.adapterGeneral.dowmImg.Util;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.meimeng.app.R;
import com.swater.meimeng.activity.adapterGeneral.vo.VoPhoto;
import com.swater.meimeng.commbase.BaseTemplate;
import com.swater.meimeng.mutils.constant.MConstantUser;
import com.swater.meimeng.mutils.diagutil.DialogItem;
import com.swater.meimeng.mutils.imgs.ImageDispose;
import com.swater.meimeng.mutils.imgs.PhotoUtils;
import com.swater.meimeng.mutils.mygrid.GridViewDefine;
import com.swater.meimeng.mutils.net.HttpMultipartPost;
import com.swater.meimeng.mutils.net.RespVo;
import com.swater.meimeng.mutils.net.UploadNew;
import com.swater.meimeng.mutils.net.HttpMultipartPost.upload_call;
import com.swater.meimeng.mutils.remoteview.ImgMagnifyActivity;
import com.swater.meimeng.mutils.remoteview.RemoteImageView;
import com.swater.meimeng.mutils.remoteview.TouchImgMagnify;

/**
 * @category 用户相册管理
 */
public class UserGallery extends BaseTemplate implements upload_call {


    //图片的角度
    private int degrees;

    boolean isupload = false;
    public static final String IMGPATH = "/sdcard/Android/data/com.meimeng.app/cache/imgupload.jpg";
    //Environment.getExternalStorageDirectory().getAbsolutePath()

    private static final String ALBUM_PICTURE_LOCATION = "file:///sdcard/Android/data/com.meimeng.app/cache/imgupload.jpg";
    /**
     * michael
     * 从相册中选取的图片的保存地址，头像
     */
    private Uri albumUri;

    public static final int PHOTOZOOM = 96;
    public static final int CAMERA_WITH_DATA = 101;
    public static final int PHOTO_PICKED_WITH_DATA = 102;
    public static final int GALLERY_OK = 202;
    public static final int GALLERY_FAIL = 203;
    public static final int UpLOAD_OK = 204;
    public static final int UpLOAD_FAIL = 205;
    public static final int UpLOAD_EXCEPTION = 209;
    public static final int DELETE_OK = 206;
    public static final int DELETE_FAIL = 207;

    public static final int UPLOUD_ALBUM_CODE = 103;
    /**
     * 编辑模式
     */
    final static int TAG_EDIT = 1;
    /**
     * 详情模式
     */
    final static int TAG_DETAIL = 2;
    protected ArrayList<DialogItem> items_edit = new ArrayList<DialogItem>();
    protected ArrayList<DialogItem> items_add_new_photos = new ArrayList<DialogItem>();
    ImageView user_header_img;
    GallAdapter gallery_adapter = null;
    String head_url = "";// 头像URL
    String del_pid = "";// 删除图片ID
    GridViewDefine gd = null;
    /**
     * 是否是头像
     */
    boolean isHeader = false;

    private final String TAG = "UserGallery:";


    String globalPath = "";
    private byte[] picData;
    boolean isdoing_upload = false;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options = null;
    ImageView add_imv = null;
    private HttpMultipartPost post;

    //上传图片的路径
    private String uploadPicturePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mygallery);
        TempRun();
        getUserGalleryList();

        albumUri = Uri.parse(ALBUM_PICTURE_LOCATION);
    }

    File vFile = null;

    private void doCamera() {
        if (existSDcard()) { // 判断手机SD卡是否存在
            vFile = new File(IMGPATH);
            if (vFile.exists()) {
                vFile.delete();
            }
            if (!vFile.exists()) {
                File vDirPath = vFile.getParentFile();
                vDirPath.mkdirs();
            }
//            Uri uri = Uri.fromFile(vFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
            // Intent intent = new
            // Intent("android.media.action.IMAGE_CAPTURE");//android.media.action.IMAGE_CAPTURE
            intent.putExtra(MediaStore.EXTRA_OUTPUT, albumUri);//
            startActivityForResult(intent, CAMERA_WITH_DATA);



        } else {
            showToast("外部存储卡无法使用！");
        }
    }

    // void upload_by_camera(){
    // if (existSDcard()) { // 判断手机SD卡是否存在
    // File vFile = new File(IMGPATH);
    // if (!vFile.exists()) {
    // File vDirPath = vFile.getParentFile();
    // vDirPath.mkdirs();
    // }
    // Uri uri = Uri.fromFile(vFile);
    // Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//
    // startActivityForResult(intent, CAMERA_WITH_DATA);
    // } else {
    // showToast("外部存储卡无法使用！");
    // }
    // }
    Bitmap bmp = null;


    /**
     * michael
     * 从uri中解析图片，成为Bitmap类型的
     *
     * @param uri
     * @return
     */


    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 4;//图片高宽度都为原来的二分之一，即图片大小为原来的大小的四分之一
//            options.inTempStorage = new byte[16 * 1024]; //设置16MB的临时存储空间（不过作用还没看出来，待验证）
//            options.inJustDecodeBounds = false;
//            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

            String path = getPath(uri);
            bitmap = convertToBitmap(path);

            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    private Bitmap decodeCameraUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 4;//图片高宽度都为原来的二分之一，即图片大小为原来的大小的四分之一
//            options.inTempStorage = new byte[16 * 1024]; //设置16MB的临时存储空间（不过作用还没看出来，待验证）
//            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }


    public Bitmap convertToBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {


            return null;

        }

        options.inSampleSize = 1 * 1024 * 1024;

        options.inJustDecodeBounds = false;

        options.inDither = false;

        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isHeader) {// 头像
            if (resultCode == RESULT_OK) {
                Bitmap bmp = null;
                switch (requestCode) {
                    case CAMERA_WITH_DATA:// 照相
//                        File temp = new File(IMGPATH);
//                        startPhotoZoom(Uri.fromFile(temp));// 进入裁剪界面
                        isdoing_upload = true;
                        startPhotoZoom(albumUri);

                        break;
                    case PHOTO_PICKED_WITH_DATA:// 图片


//                        Uri uri = data.getData();
//                        Log.e("uri", uri.toString());
//                        startPhotoZoom(data.getData());// 进入裁剪界面
                        isupload = true;
//                        Bitmap bitmap = decodeUriAsBitmap(albumUri);
                        Uri uri = data.getData();
                        Bitmap bitmap = decodeUriAsBitmap(uri);
                        uploadPicturePath = getPath(uri);

                        if (bitmap == null) {
                            Log.e("------UserGallery:bitmap=", "null");
                        } else {
                            Log.e("--------UserGallery:bitmap!=", "null" + "; 上传图片的路径：" + uploadPicturePath);
                        }
                        Log.e("上上上事实上事实上UserGallery:", "图片上传，相册22222222");

                        saveFile(bitmap);

//                        String picturePath = decodeUriAsPath(albumUri);
//                        Bitmap bitmap = getimage(picturePath);
//                        saveFile(bitmap);

                        break;
                    case PHOTOZOOM:
//                        Bundle extras = data.getExtras();
//                        if (extras != null) {
//                            bmp = extras.getParcelable("data");
//
//                            user_header_img.setImageBitmap(bmp);
//                            if (bmp==null){
//                                Log.e("UserGallery:","bmp是空的");
//                            }
//                                saveFile(bmp);
//                        }

                        // michael
                        Bitmap bitmap2 = decodeCameraUriAsBitmap(albumUri);
                        if (bitmap2==null){
                            Log.e(TAG,"头像拍照为空？？？"+albumUri);
                        }else {
                            Log.e(TAG,"头像拍照不为空？？？"+albumUri);
                        }
                        saveFile(bitmap2);

                        break;
                    default:
                        break;
                }
            }
        } else {

            // ContentResolver resolver = getContentResolver();

            if (resultCode == RESULT_OK) {

                switch (requestCode) {
                    case CAMERA_WITH_DATA:// 照相
                        if (vFile == null) {
                            vFile = new File(IMGPATH);
                            if (!vFile.exists()) {
                                File vDirPath = vFile.getParentFile();
                                vDirPath.mkdirs();
                            }
                        }

                        if (vFile != null && vFile.exists()) {

                            try {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 2;

                                //获得图片的旋转角度（相对于正的图片）
                                degrees = readPictureDegree(vFile.getPath());

                                bmp = BitmapFactory.decodeFile(vFile.getPath(),
                                        options);


                                if (null == bmp) {

                                    Bundle bundle = data.getExtras();
                                    bmp = (Bitmap) bundle.get("data");

                                }

                                //旋转图片
                                bmp = rotaingImageView(degrees, bmp);

                                // Intent ins=new Intent(UserGallery.this,
                                // tempImgActivity.class);
                                // ins.putExtra("bmp", bmp);
                                // startActivity(ins);
                                // break;
                                globalPath = vFile.getPath();
                                handler.obtainMessage(88).sendToTarget();
                            } catch (Exception e) {
                                e.printStackTrace();
                                showToast("读取图片错误！" + e.getMessage());
                            }

                        }

                        // -----method--2---
                        /**
                         * { Bundle bundle = data.getExtras(); Bitmap photo=
                         * (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                         * ByteArrayOutputStream baos = new ByteArrayOutputStream();
                         * photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
                         * 把数据写入文件 Uri uri = data.getData(); Log.i("", "uri = "+
                         * uri); try { String[] pojo =
                         * {MediaStore.Images.Media.DATA}; Cursor cursor =
                         * managedQuery(uri, pojo, null, null,null);
                         * if(cursor!=null){ int colunm_index =
                         * cursor.getColumnIndexOrThrow
                         * (MediaStore.Images.Media.DATA); cursor.moveToFirst();
                         * String path = cursor.getString(colunm_index);
                         * if(path!=null){ globalPath=path; setTempUpload(photo); }
                         * } }catch (Exception e) { // TODO: handle exception
                         * e.printStackTrace(); showToast("解析图片错误"+e.getMessage());
                         * } }
                         */

                        /**
                         * BitmapFactory.Options options = new
                         * BitmapFactory.Options(); options.inJustDecodeBounds =
                         * true; bmp = BitmapFactory.decodeFile(IMGPATH, options);
                         * // 此时返回bm为空
                         *
                         * options.inJustDecodeBounds = false; // 缩放比 int be = (int)
                         * (options.outHeight / (float) 200); if (be <= 0) be = 1;
                         * options.inSampleSize = be; Log.v("--img--path-->>",
                         * IMGPATH); // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为
                         * false哦 bmp = BitmapFactory.decodeFile(IMGPATH, options);
                         *
                         * if (null == bmp) {
                         *
                         * Bundle bundle = data.getExtras(); bmp = (Bitmap)
                         * bundle.get("data"); }
                         *
                         *
                         * if (null != bmp) { // ImageDispose.Bitmap2Bytes(bmp); //
                         * globalPath = "/mnt" + SaveImg.saveMyBitmap(bmp);
                         * globalPath = IMGPATH; String path_save =
                         * SaveImg.saveMyBitmap(bmp); globalPath = path_save;
                         * NSLoger.Log(path_save + "-->>"); //
                         * setTempUploadFromCamera(bmp);
                         * handler.obtainMessage(88).sendToTarget();
                         *
                         * }
                         */

                        break;
                    case PHOTO_PICKED_WITH_DATA:// 图片
                        // try {


                        // 获得图片的uri
//                        Uri originalUri = data.getData();
//                        String[] proj = {MediaStore.Images.Media.DATA};
//                        Cursor cursor = managedQuery(originalUri, proj, null, null,
//                                null);
//                        int column_index = cursor
//                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);// 注意这里，当图片是0kb时抛异常
//
//                        // 将光标移至开头 ，这个很重要，不小心很容易引起越界
//
//                        cursor.moveToFirst();
//
//                        // 最后根据索引值获取图片路径
//                        globalPath = cursor.getString(column_index);

                        Log.e(TAG, "图片上传到这儿了，jjjjjjjjjjj");
                        Uri uri = data.getData();
                        globalPath = getPath(uri);

//                        globalPath = Environment.getExternalStorageDirectory()+"/Android/data/com.meimeng.app/cache/imgupload.jpg";
                        Log.v("-globalPath-file--path000-->>>", globalPath);


                        try {
                            BitmapFactory.Options options1 = new BitmapFactory.Options();
                            options1.inSampleSize = 4;
                            //options1.inTempStorage = new byte[5 * 1024];
                            options1.inJustDecodeBounds = false;
                            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options1);
                            setTempUpload(bmp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        bmp = PhotoUtils.getSingle().parselUriImg(albumUri,
//                                UserGallery.this);
//                        setTempUpload(bmp);


                        // 将图片内容解析成字节数组
                    /*
                     * mContent = readStream(resolver.openInputStream(Uri
					 * .parse(originalUri.toString()))); //
					 * 将字节数组转换为ImageView可调用的Bitmap对象 bmp =
					 * getPicFromBytes(mContent, null, originalUri); //
					 * //把得到的图片绑定在控件上显示 // get--path String[] proj = {
					 * MediaStore.Images.Media.DATA }; Cursor cursor =
					 * managedQuery(originalUri, proj, null, null, null); int
					 * column_index = cursor
					 * .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//
					 * 注意这里，当图片是0kb时抛异常
					 * 
					 * // 将光标移至开头 ，这个很重要，不小心很容易引起越界
					 * 
					 * cursor.moveToFirst();
					 * 
					 * // 最后根据索引值获取图片路径 globalPath =
					 * cursor.getString(column_index);
					 * Log.v("-globalPath-file--path000-->>>", globalPath); //
					 * setTempImagine(bmp); // tempPic.setImageBitmap(bmp); //
					 * tempPic.setVisibility(View.VISIBLE); //
					 * SaveImg.saveMyBitmap_new(bmp, "a");
					 * 
					 * // -lazy to -call-back--when upload-- picData =
					 * ImageDispose.Bitmap2Bytes(bmp); //
					 * bmp=BitUtils.getInstance().getBpCompress(IMGPATH); int lg
					 * = picData.length; int a = (int) lg / 1024; if (a > 1044)
					 * { Log.d("--图片有点大哦！---", "-----发图片--" + a + "kb");
					 * readImgFromGallery(originalUri, bmp); }
					 * setTempUpload(bmp); } catch (FileNotFoundException e) {
					 * showToast("图片找不到"); Log.e("FileNotFoundException",
					 * e.getMessage(), e); } catch (Exception e) {
					 * showToast("图片错误"); Log.e("Exception", e.getMessage(), e);
					 * }*
					 */

                        break;
                    default:
                        break;
                }

            }

            // --一般上传---

        }

    }

    void setTempUploadFromCamera(Bitmap bmps) {
        if (bmps != null) {

            gallery_adapter.setBmp_temp(bmps);
            gallery_adapter.notifyDataSetChanged();

            // int b_h= bmps.getHeight();
            // int b_w= bmps.getWidth();
            // 照片宽不低于1080px，高不低于720px
            // if (b_h<720||b_w<1080) {
            // showToast("图片错误！未达到"+"720*1080");
            // }else{
            uploadImgs("");
            // uploadPro();
            // }
            //

        } else {
            showToast("读取图片错误！");
        }

    }

    void setTempUpload(Bitmap bmps) {
        if (bmps != null) {
            // isdoing_upload = true;
            // add_imv.setImageBitmap(null);
            // add_imv.setBackgroundResource(0);
            // add_imv.setImageBitmap(bmps);

            gallery_adapter.setBmp_temp(bmps);
            gallery_adapter.notifyDataSetChanged();
            // if (bmps!=null) {

            // int b_h= bmps.getHeight();
            // int b_w= bmps.getWidth();
            // 照片宽不低于1080px，高不低于720px
            // if (b_h<720||b_w<1080) {
            // showToast("图片错误！未达到"+"720*1080");
            // }else{
            uploadImgs("");
            // uploadPro();
            // }
            //

        }

    }

    /* 处理大图 */
    void readImgFromGallery(Uri uri, Bitmap bp) {

        String[] proj = {MediaStore.Images.Media.DATA};
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
            // selectPicPath = path;
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
                Toast.makeText(this.getBaseContext(), "没有找到图片", Toast.LENGTH_SHORT).show();
            }
            options2.inJustDecodeBounds = false;
            picData = ImageDispose.Bitmap2Bytes(bp);

        } catch (Exception e) {
            showToast("图片错误！");
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

//    public void startPhotoZoom(Uri uri) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
//        intent.putExtra("crop", "true");
//        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", 240);
//        intent.putExtra("outputY", 240);
//        intent.putExtra("return-data", true);
//        startActivityForResult(intent, PHOTOZOOM);


    /**
     * michael
     * 剪切图片
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        //通知要裁剪
        intent.putExtra("crop", true);

        //X方向上的比例aspectX
        intent.putExtra("aspectX", 1);

        //Y方向上的比例
        intent.putExtra("aspectY", 1);

        //裁剪区的宽
        intent.putExtra("outputX", 240);

        //裁剪区的高
        intent.putExtra("outputY", 240);

        //按比例裁剪
        intent.putExtra("scale", true);

        //裁剪后的图片存放在此uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        //不以Bitmap的形式返回图片
        intent.putExtra("return-data", false);

        intent.putExtra("outputFormat", CompressFormat.JPEG.toString());

        intent.putExtra("noFaceDetection", true);

        startActivityForResult(intent, PHOTOZOOM);
    }

    public boolean existSDcard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    private void saveFile(Bitmap bm) {

        File file_temp = new File(Environment
                .getExternalStorageDirectory().getAbsolutePath()
                + "Android/data/com.meimeng.app/cache/imgupload.jpg");

        Log.e("UserGallery:", IMGPATH);
//        if (file_temp.isFile() && file_temp.exists()) {
//            file_temp.delete();
//
//        }

        try {
            FileOutputStream m_fileOutPutStream = new FileOutputStream(file_temp);
            isupload = true;
            bm.compress(CompressFormat.JPEG, 100, m_fileOutPutStream);

            m_fileOutPutStream.flush();
            m_fileOutPutStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


//        bm.compress(CompressFormat.JPEG, 100, m_fileOutPutStream);
//        try {
//            m_fileOutPutStream.flush();
//            m_fileOutPutStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

//    private void doPhoto() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
//    }

    private void doPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);

        intent.setType("image/*");

        intent.putExtra("crop", true);

        intent.putExtra("aspectX", 1);

        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 240);

        intent.putExtra("outputY", 240);

        intent.putExtra("scale", true);

        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, albumUri);

        intent.putExtra("outputFormat", CompressFormat.JPEG.toString());

        intent.putExtra("noFaceDetection", true);

        startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);

    }

    /**
     * 上传图片时的相册点击事件
     */
    private void albumButtonListener() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);

        intent.setType("image/*");

        startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);

    }


    /**
     * michael
     * 上传图片时的相机的点击事件
     */
    private void cameraButtonListener() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, albumUri);

        startActivityForResult(intent, CAMERA_WITH_DATA);
    }


    @Override
    public void iniView() {
        user_header_img = (ImageView) findViewById(R.id.gal_header);
//        ((BitmapDrawable)user_header_img.getDrawable()).getBitmap().recycle();

        showTitle("相册管理");
        showNavgationLeftBar("返回");
        findButton(R.id.home_right_btn).setTag(TAG_DETAIL);
        showNavgationRightBar("编辑");
        this.IniDiagShw();
        gd = (GridViewDefine) findViewById(R.id.gal_grid);
        gd.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gallery_adapter = new GallAdapter();
        gd.setAdapter(gallery_adapter);

    }

    void pullDiag(List<DialogItem> arr) {
        com.swater.meimeng.mutils.diagutil.Tools.createCustomDialog(t_context,
                arr, R.style.CustomDialogOld);

    }

    void IniDiagShw() {

        items_edit.add(new DialogItem(R.string.edit_item_1,
                R.layout.custom_dialog_special) {

            @Override
            public void onClick() {
                if (!TextUtils.isEmpty(del_pid)) {
                    deletePic(del_pid);
                }

            }
        });
        // gallery
        items_edit.add(new DialogItem(R.string.edit_item_2,
                R.layout.custom_dialog_normal) {
            @Override
            public void onClick() {
                super.onClick();
                albumButtonListener();
            }
        });
        // dofrom camera
        items_edit.add(new DialogItem(R.string.edit_item_3,
                R.layout.custom_dialog_normal) {
            @Override
            public void onClick() {
                super.onClick();

                if (isHeader) {
                    doCamera();
                } else {
                    cameraButtonListener();
                }


            }
        });

        items_edit.add(new DialogItem(R.string.edit_item_4,
                R.layout.custom_dialog_cancel));
        DialogItem title = new DialogItem(R.string.edit_title,
                R.layout.custom_dialog_title);
        items_add_new_photos.add(title);
        items_add_new_photos.addAll(items_edit.subList(1, items_edit.size()));
    }

    @Override
    public void bindClick() {
        bindNavgationEvent();
        gd.setOnItemClickListener(item_clik);
        setClickEvent(findImageView(R.id.gal_header));
    }

    /**
     * 判断是否是编辑模式
     */
    boolean isEditModu() {
        return (Integer) findButton(R.id.home_right_btn).getTag() != TAG_DETAIL ? true
                : false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gal_header: {

                if (isEditModu()) {
                    isHeader = true;
                    this.pullDiag(this.items_edit.subList(1, this.items_edit.size()));
                    // if (TextUtils.isEmpty(head_url)) {
                    //
                    // }else{
                    //
                    // showToast("暂时不能修改！后台未提供此接口");
                    // }

                } else {
                    Intent intent = new Intent(t_context, ImgMagnifyActivity.class);
                    if (!TextUtils.isEmpty(head_url)) {
                        intent.putExtra("url", head_url);
                        t_context.startActivity(intent);
                    }

                }

            }
            break;
            case R.id.home_left_btn: {
                if (isupload || isdoing_upload) {
                    showConfirm();

                } else {

                    sysback();
                }

            }

            break;
            case R.id.home_right_btn: {
                int tag = (Integer) findButton(R.id.home_right_btn).getTag();
                switch (tag) {
                    case TAG_DETAIL: {
                        showNavgationRightBar("保存");
                        if (isupload) {
                            // showToast("保存头像");
                            // 1 代表上传头像
                            uploadUserHeader();

                        }
                        if (isdoing_upload) {
                            // showToast("do上传照片");
                            uploadImgs(del_pid);

                        }
                        findButton(R.id.home_right_btn).setTag(TAG_EDIT);

                    }

                    break;
                    case TAG_EDIT: {

                        showNavgationRightBar("编辑");
                        findButton(R.id.home_right_btn).setTag(TAG_DETAIL);
                    }
                    break;

                    default:
                        break;
                }

            }

            break;

            default:
                break;
        }

    }

    String temp_header = "";
    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            ProDimiss();
            switch (msg.what) {
                case 88:
                    setTempUploadFromCamera(bmp);
                    break;
                case 909: {
                    uploadImgs("");
                }
                break;

                case DELETE_FAIL: {
                    showToast("删除失败！");

                }

                break;
                case DELETE_OK: {
                    // showToast("删除成功！");
                    gallery_adapter = null;

                    getUserGalleryList();

                }

                break;
                case Resp_action_ok: {
                    // showToast("修改头像成功！");
                    isupload = false;

                    ((BitmapDrawable) user_header_img.getDrawable()).getBitmap().recycle();

                }

                break;
                case UpLOAD_OK: {
                    showToast("上传成功！");
                    isdoing_upload = false;
                    gallery_adapter.setBmp_temp(null);
                    String res = resp_upload.getResp();
                    gallery_adapter = null;
                    // {"result":1,"error":"",
                    // "data":{"pid":"327","audit_url":"http:\/\/112.124.18.97\/attachment\/albumpic\/55\/20130916184815.jpg",
                    // "audit_thumb_url":"http:\/\/112.124.18.97\/attachment\/albumpic\/55\/20130916184815.jpg_240X240_.jpg"}}

                    try {

                        String url = new JSONObject(res).getJSONObject("data")
                                .getString("audit_url");
                        String url_big = new JSONObject(res).getJSONObject("data")
                                .getString("audit_thumb_url");
                        String pid = new JSONObject(res).getJSONObject("data")
                                .getString("pid");
                        VoPhoto add_vo = new VoPhoto();
                        if (!TextUtils.isEmpty(pid)) {

                            add_vo.setPid(Integer.valueOf(pid));
                        }
                        add_vo.setUrl(url);
                        add_vo.setThumb_url(url_big);
                        photos.add(add_vo);
                        if (null == gallery_adapter) {

                            gallery_adapter = new GallAdapter();
                            gd.setAdapter(gallery_adapter);
                        }
                        gallery_adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                    }

                    // getUserGalleryList();

                }

                break;
                case UpLOAD_EXCEPTION:
                case UpLOAD_FAIL: {
                    showToast("上传图片失败！！" + mg2String(msg));
                    isdoing_upload = false;
                    if (gallery_adapter != null) {

                        gallery_adapter.setBmp_temp(null);
                        gallery_adapter.notifyDataSetChanged();
                    }

                }

                break;
                case Resp_exception: {
                    showToast("上传异常" + mg2String(msg));
                    // gallery_adapter.notifyDataSetChanged();

                }

                break;
                case Resp_action_fail: {
                    showToast("上传失败！" + mg2String(msg));
                    // gallery_adapter.notifyDataSetChanged();

                }

                break;
                case GALLERY_FAIL: {
                    // gallery_adapter.notifyDataSetChanged();
                    showToast("获取用户相册列表失败！" + mg2String(msg));

                }
                case -8: {
                    // gallery_adapter.notifyDataSetChanged();
                    showToast("获取用户相册失败！");
                }
                break;
                case GALLERY_OK: {
                    // "result":1,"error":"","data":{"header":{"url":"http:\/\/112.124.18.97\/attachment\/tmp\/55\/header.jpg","audit_url":"http:\/\/192.168.1.104:112\/attachment\/albumpic\/55\/header\/20130913144431.jpg"},"photos":[{"pid":119,"url":"http:\/\/192.168.1.104:112\/attachment\/albumpic\/55\/20130828185130.jpg","thumb_url":"http:\/\/192.168.1.104:112\/attachment\/albumpic\/55\/20130828185130.jpg_240X240_.jpg","audit_url":null,"audit_thumb_url":""}
                    // "audit_url":"http:\/\/192.168.20.1:9090\/attachment\/albumpic\/55\/header\/20130819093700.jpg","url":"http:\/\/112.124.18.97\/attachment\/tmp\/55\/header.jpg
                    // showToast("上传失败！" + mg2String(msg));
                    parselJson(vo_resp.getResp());
                    if (null == gallery_adapter) {

                        gallery_adapter = new GallAdapter();
                        gd.setAdapter(gallery_adapter);
                    }
                    gallery_adapter.notifyDataSetChanged();
                    if (!TextUtils.isEmpty(temp_header)
                            && !temp_header.equals("null")) {
                        head_url = temp_header;

                    }

                    if (TextUtils.isEmpty(head_url)) {
                        head_url = shareUserInfo().getUserInfo().getHeader();
                    }
                    if (!TextUtils.isEmpty(head_url) && !head_url.equals("null")) {

                        options = new DisplayImageOptions.Builder()
                                .showImageForEmptyUri(R.drawable.photo_loading)
                                .showImageOnFail(R.drawable.photo_loading)
                                .resetViewBeforeLoading().cacheOnDisc()
                                .imageScaleType(ImageScaleType.EXACTLY)
                                .bitmapConfig(Bitmap.Config.RGB_565)
                                .displayer(new RoundedBitmapDisplayer(120)).build();
                        imageLoader.resume();
                        imageLoader
                                .displayImage(head_url, user_header_img, options);
                        // user_header_img.setRect(true);
                        // user_header_img.setOpenRect(true);
                        // user_header_img.setRectSize(10);
                        // user_header_img.setUrl(head_url);
                    } else {
                        if (shareUserInfo().getUserInfo().getSex() == 2) {

                            user_header_img
                                    .setImageResource(R.drawable.female_head);
                        } else if (shareUserInfo().getUserInfo().getSex() == 1) {

                            user_header_img.setImageResource(R.drawable.male_head);
                        } else {
                            user_header_img
                                    .setImageResource(R.drawable.default_head);
                        }
                    }

                }

                break;

                default:
                    break;
            }
        }

        ;
    };

    void uploadUserHeader() {
        Log.e(TAG,"图片上传调用了，啊哈哈哈哈");
        ProShow("正在上传头像...");
        poolThread.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    RespVo rp = null;

                    // RequestByPost.uploadUserHeader(
                    // MURL_user_header_upload, IMGPATH, shareUserInfo()
                    // .getUserid() + "", "");
                    // RespVo resp = null;

                    UploadNew instance = UploadNew.getInstance(t_context);
                    instance.setConnection(MURL_user_header_upload);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    BasicNameValuePair bvp1 = new BasicNameValuePair("uid",
                            shareUserInfo().getUserid() + "");
                    BasicNameValuePair bvp2 = new BasicNameValuePair("data",
                            IMGPATH);
                    BasicNameValuePair bvp3 = new BasicNameValuePair(
                            "filename", IMGPATH.substring(IMGPATH
                            .lastIndexOf("/")));
                    params.add(bvp1);
                    params.add(bvp2);
                    params.add(bvp3);

//                    rp = instance.getInputStream(params, IMGPATH);
                    rp = instance.getInputStream(params, uploadPicturePath);

                    if (rp.isHasError()) {
                        handler.obtainMessage(Resp_action_fail,
                                rp.getErrorDetail()).sendToTarget();
                    } else {
                        handler.obtainMessage(Resp_action_ok).sendToTarget();


                    }

                } catch (Exception e) {
                    handler.obtainMessage(Resp_exception).sendToTarget();
                }

            }
        });

    }

    /**
     * 专门为头像拍照上传图片写的
     */
    void uploadCameraHeader() {

        ProShow("正在上传头像...");
        poolThread.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    RespVo rp = null;

                    // RequestByPost.uploadUserHeader(
                    // MURL_user_header_upload, IMGPATH, shareUserInfo()
                    // .getUserid() + "", "");
                    // RespVo resp = null;

                    UploadNew instance = UploadNew.getInstance(t_context);
                    instance.setConnection(MURL_user_header_upload);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    BasicNameValuePair bvp1 = new BasicNameValuePair("uid",
                            shareUserInfo().getUserid() + "");
                    BasicNameValuePair bvp2 = new BasicNameValuePair("data",
                            IMGPATH);
                    BasicNameValuePair bvp3 = new BasicNameValuePair(
                            "filename", IMGPATH.substring(IMGPATH
                            .lastIndexOf("/")));
                    params.add(bvp1);
                    params.add(bvp2);
                    params.add(bvp3);

                    rp = instance.getInputStream(params, IMGPATH);
//                    rp = instance.getInputStream(params, uploadPicturePath);

                    if (rp.isHasError()) {
                        handler.obtainMessage(Resp_action_fail,
                                rp.getErrorDetail()).sendToTarget();
                    } else {
                        handler.obtainMessage(Resp_action_ok).sendToTarget();
                    }

                } catch (Exception e) {
                    handler.obtainMessage(Resp_exception).sendToTarget();
                }

            }
        });

    }

    private void showConfirm() {

        AlertDialog.Builder builder = new AlertDialog.Builder(t_context);
        builder.setTitle("提示");
        builder.setMessage("是否要放弃修改?");
        builder.setNegativeButton("放弃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isupload = false;
                onBackPressed();
            }
        });
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isdoing_upload) { //拍照时调用
                    Log.e("图片位置：","第一个");
                    isdoing_upload = false;
//                    uploadImgs(del_pid);
//                    uploadUserHeader();
                    uploadCameraHeader();
                }
                if (isupload) { //从相册中取时调用
                    Log.e("图片位置：","else");
                    isupload = false;
                    uploadUserHeader();

                }
                // onBackPressed();

            }
        });
        builder.show();

    }

    /**
     * @category 处理图片点击事件
     */
    OnItemClickListener item_clik = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int i, long arg3) {
            Integer tag = (Integer) findButton(R.id.home_right_btn).getTag();
            isHeader = false;
            if (photos.size() < 12 && i == photos.size()) {
                // showToast("添加图片");
                add_imv = (ImageView) v.findViewById(R.id.gal_cell_imv);
                pullDiag(items_add_new_photos);
                return;

            }
            switch (tag) {
                case TAG_DETAIL: {
                    Intent intent = new Intent(t_context, TouchImgMagnify.class);

                    if (i < photos.size()) {
                        intent.putExtra("url", photos.get(i).getUrl());
                        t_context.startActivity(intent);
                    } else {
                        pullDiag(items_add_new_photos);
                        // showToast("还没有图片哦！");
                    }

                }

                break;
                case TAG_EDIT: {
                    add_imv = (ImageView) v.findViewById(R.id.gal_cell_imv);
                    del_pid = photos.get(i).getPid() + "";
                    pullDiag(items_edit);

                }

                break;

                default:
                    break;
            }

        }
    };

    private void getUserGalleryList() {
        ProShow("加载数据中...");
        photos.clear();
        del_pid = "";
        try {
            poolThread.submit(new Runnable() {

                @Override
                public void run() {
                    req_map_param.put(MConstantUser.UserProperty.uid,
                            shareUserInfo().getUserid() + "");
                    req_map_param.put(MConstantUser.UserProperty.user_key,
                            key_server);
                    req_map_param.put("target_uid", shareUserInfo().getUserid()
                            + "");
                    vo_resp = sendReq(MURL_user_gallery_list, req_map_param);
                    if (null == vo_resp) {
                        handler.obtainMessage(-8).sendToTarget();
                    }
                    if (vo_resp.isHasError()) {
                        handler.obtainMessage(GALLERY_FAIL).sendToTarget();
                    } else {

                        handler.obtainMessage(GALLERY_OK).sendToTarget();
                    }

                }
            });

        } catch (Exception e) {
            handler.obtainMessage(Resp_exception).sendToTarget();
        }

    }

    List<VoPhoto> photos = new ArrayList<VoPhoto>();
    LinkedList<RemoteImageView> link_headers = new LinkedList<RemoteImageView>();

    @Override
    protected Object parselJson(String res) {
        try {
            JSONObject obj = new JSONObject(res).getJSONObject("data");

            JSONArray arr = obj.getJSONArray("photos");
            // obj.getJSONObject("header").getString("url");
            // if
            // (!TextUtils.isEmpty(obj.getJSONObject("header").getString("audit_url")))
            // {
            // head_url=obj.getJSONObject("header").getString("audit_url");
            // }else{
            //
            head_url = obj.getJSONObject("header").getString("url");
            temp_header = obj.getJSONObject("header").getString("audit_url");
            ;// obj.getString("header");//
            // }

            int total = obj.getInt("count");
            String url = "", thumb_url = "";
            int pid = 0;
            if (arr == null || arr.length() < 1) {
                return null;

            }
            for (int i = 0; i < arr.length(); i++) {
                // {"thumb_url":"http:\/\/192.168.1.104:112\/attachment\/albumpic\/55\/20130828185130.jpg_240X240_.jpg","pid":119,
                // "audit_url":null,"url":"http:\/\/192.168.1.104:112\/attachment\/albumpic\/55\/20130828185130.jpg","audit_thumb_url":""}
                VoPhoto vo = new VoPhoto();
                vo.setCountTotal(total);
                pid = arr.getJSONObject(i).getInt("pid");
                url = arr.getJSONObject(i).getString("url");
                thumb_url = arr.getJSONObject(i).getString("thumb_url");
                // url = arr.getJSONObject(i).getString("audit_url");
                // thumb_url =
                // arr.getJSONObject(i).getString("audit_thumb_url");
                vo.setPid(pid);
                vo.setThumb_url(thumb_url);
                vo.setUrl(url);
                photos.add(vo);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return super.parselJson(res);
    }

    class GallAdapter extends BaseAdapter {

        Bitmap bmp_temp = null;

        public Bitmap getBmp_temp() {
            return bmp_temp;
        }

        public void setBmp_temp(Bitmap bmp_temp) {
            this.bmp_temp = bmp_temp;
        }

        @Override
        public int getCount() {
            return photos.size() < 12 ? photos.size() + 1 : 12;
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


            HolderView holderView = null;
//            if (view == null) {
            view = LayoutInflater.from(t_context).inflate(
                    R.layout.mygallery_cell, null);
            holderView = new HolderView();
            // RemoteImageView imv =
            // (com.swater.meimeng.mutils.remoteview.RemoteImageView) view
            // .findViewById(R.id.gal_cell_imv);
            ImageView imv = (ImageView) view
                    .findViewById(R.id.gal_cell_imv);
            holderView.header = imv;
            // holderView.header.setOpenRect(true);
            // holderView.header.setRect(true);
            // holderView.header.setRectSize(10);

            view.setTag(holderView);

//            }else {
//                holderView = (HolderView) view.getTag();
//                resetHolderView(holderView);
//            }


            if (photos.size() < 1) {
                holderView.header.setImageBitmap(null);
                holderView.header.setImageResource(R.drawable.add_photo);

                holderView.header.setBackgroundResource(0);

                return view;
            } else {
                if (i < photos.size()) {
                    holderView.header
                            .setImageResource(R.drawable.photo_loading);
                    options = new DisplayImageOptions.Builder()
                            .showImageForEmptyUri(R.drawable.photo_loading)
                            .showImageOnFail(R.drawable.photo_loading)
                            .resetViewBeforeLoading().cacheOnDisc()
                            .imageScaleType(ImageScaleType.EXACTLY)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .displayer(new RoundedBitmapDisplayer(10)).build();

                    holderView.header.setBackgroundResource(0);
                    holderView.header
                            .setImageResource(R.drawable.photo_loading);
                    imageLoader.resume();
                    imageLoader.displayImage(photos.get(i).getThumb_url(),
                            holderView.header, options);
                    // link_headers.add(holderView.header);

                    Log.e("-----图片：", photos.get(i).getThumb_url());

                } else {


//                    // holderView.header.setUrl("");
                    if (bmp_temp != null) {
                        holderView.header.setImageBitmap(bmp_temp);
                        holderView.header.setBackgroundResource(0);
                    } else {

                        holderView.header.setImageBitmap(null);
                        // holderView.header
                        // .setImageResource(R.drawable.add_photo);
                        holderView.header
                                .setImageResource(R.drawable.add_photo);

                        holderView.header.setBackgroundResource(0);
                    }
                }

            }
            return view;
        }

    }

    public class HolderView {

        ImageView header = null;

        void resetHolderView() {
            header.setImageResource(R.drawable.add_photo);
        }

    }

    private void resetHolderView(HolderView holder) {
//        holder.header.setImageResource(0);
        holder.header.setImageBitmap(null);
    }

    /**
     * @category 上传图片
     * @param bytes
     */
    RespVo resp_upload = null;

    void uploadImgs(String pids) {
        ProShow("上传图片中...", true);
        final String pid = pids;
        poolThread.submit(new Runnable() {

            @Override
            public void run() {
                try {

                    UploadNew instance = UploadNew.getInstance(t_context);
                    instance.setConnection(MURL_user_uploadImg);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    BasicNameValuePair bvp1 = new BasicNameValuePair("uid",
                            shareUserInfo().getUserid() + "");
                    BasicNameValuePair bvp2 = new BasicNameValuePair("data",
                            globalPath);
                    BasicNameValuePair bvp3 = new BasicNameValuePair(
                            "filename", globalPath.substring(globalPath
                            .lastIndexOf("/")));
                    BasicNameValuePair bvp4 = null;
                    if (!TextUtils.isEmpty(pid)) {

                        bvp4 = new BasicNameValuePair("pid", pid);
                        params.add(bvp4);
                    }
                    params.add(bvp1);
                    params.add(bvp2);
                    params.add(bvp3);

                    resp_upload = instance.getInputStream(params, globalPath);

                    if (resp_upload == null) {
                        handler.obtainMessage(UpLOAD_EXCEPTION).sendToTarget();

                    }
                    if (!resp_upload.isHasError()) {
                        handler.obtainMessage(UpLOAD_OK).sendToTarget();

                    } else {
                        handler.obtainMessage(UpLOAD_FAIL,
                                resp_upload.getErrorDetail()).sendToTarget();

                    }

                } catch (Exception e) {
                    handler.obtainMessage(UpLOAD_EXCEPTION, e.getMessage())
                            .sendToTarget();
                }

            }
        });

    }

    void uploadPro() {
        // String filePath = et_filepath.getText().toString();
        File file = new File(globalPath);
        if (file.exists()) {
            post = new HttpMultipartPost(t_context, globalPath,
                    MURL_user_uploadImg, shareUserInfo().getUserid() + "");
            post.setCall(UserGallery.this);
            post.execute();

        } else {
            Toast.makeText(t_context, "file not exists", Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * @param pid
     * @category 删除照片
     */
    void deletePic(String pid) {
        ProShow("正在删除...");
        final String pids = pid;
        poolThread.submit(new Runnable() {

            @Override
            public void run() {
                try {

                    RespVo resp = null;
                    req_map_param.clear();
                    req_map_param.put("key", key_server);
                    req_map_param.put("uid", shareUserInfo().getUserid() + "");
                    req_map_param.put("pid", pids);
                    resp = sendReq(MURL_user_gallery_delete_pic, req_map_param);
                    Log.d("--RespVo---upload--imgs---->" + resp.toString(),
                            "---");
                    if (!resp.isHasError()) {
                        handler.obtainMessage(DELETE_OK).sendToTarget();

                    } else {
                        handler.obtainMessage(DELETE_OK).sendToTarget();

                    }

                } catch (Exception e) {
                    handler.obtainMessage(Resp_exception).sendToTarget();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (imageLoader != null) {
            imageLoader.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (imageLoader != null) {
            imageLoader.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (imageLoader != null) {
            imageLoader.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closePool();
        if (null != handler) {
            handler = null;
        }
        try {
            if (imageLoader != null) {
                imageLoader.stop();
            }
            // if (link_headers != null || link_headers.size() > 0) {
            // for (RemoteImageView cell : link_headers) {
            // if (null != cell) {
            // Log.d("---释放相册资源---", "---->>");
            // cell.Force_Close();
            // cell = null;
            // }
            //
            // }
            // link_headers.clear();
            // System.gc();
            //
            // }

        } catch (Exception e) {
        }

    }

    @Override
    public void uoload_call(RespVo resp_uploads) {
        try {
            resp_upload = resp_uploads;
            if (resp_uploads == null) {
                handler.obtainMessage(UpLOAD_EXCEPTION).sendToTarget();

            }
            if (!resp_uploads.isHasError()) {
                handler.obtainMessage(UpLOAD_OK).sendToTarget();

            } else {

                handler.obtainMessage(UpLOAD_FAIL,
                        resp_uploads.getErrorDetail()).sendToTarget();

            }
        } catch (Exception e) {

            showToat("upload_error-" + e.getMessage());
        }

    }

    /**
     * michael
     * 根据uri获取图片的路径
     * 不能解析自定义的路径的uri(自我的理解)
     *
     * @param uri
     * @return
     */
    private String decodeUriAsPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String path = cursor.getString(column_index);

        return path;
    }

    /**
     * Michael
     * 根据图片路径按比例压缩
     *
     * @param srcPath
     * @return
     */
    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 400;
        float ww = 400;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }

        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressQualityImage(bitmap);//压缩好比例大小后再进行质量压缩

        //直接返回，不用质量压缩也可以展示
//        return bitmap;
    }


    /**
     * michael
     * 质量压缩方法
     */
    private Bitmap compressQualityImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //100代表不压缩图片，把图片保存到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        int options = 100;

        while (baos.toByteArray().length / 1024 > 500) {

            //清空baos
            baos.reset();

            options -= 10;
            if (options < 0) {
                break;
            }
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }

        //把压缩后的图片放在inBm中
        ByteArrayInputStream inBm = new ByteArrayInputStream(baos.toByteArray());

        //生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(inBm, null, null);

        return bitmap;
    }


    /**
     * michael
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {

        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 根据Uri得到图片的路径
     *
     * @param uri
     * @return
     */
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
