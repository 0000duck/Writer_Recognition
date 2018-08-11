package cn.hxc.imgrecognition;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.graphics.Matrix;

import com.edmodo.cropper.CropImageView;

import cn.hxc.imgrecognitionSRI_OCR.R;

/**
 * Created by 刘欢 on 2018/4/13.
 */

public class blackList extends Activity{

    private Button btnPhone;
    private Button B_saveblist;
    private ImageView orignalView;
    private ImageView norView;
    private TextView orignalTxt;
    private TextView norTxt;
    Bitmap getBmpFormPhone;
    CropImageView cropImageView;
    Button cropButton;

    public ImageButton B_contrast;
    public ImageButton B_QueryLoc;
    public ImageButton B_QueryDB;
    public ImageButton B_set;
    public ImageButton B_blist;

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private final int IMAGE_CODE = 0; // 这里的IMAGE_CODE是自己任意定义的

    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.blacklist);//软件activity的布局

        B_contrast = (ImageButton) findViewById(R.id.B_contrast);
        B_QueryLoc = (ImageButton) findViewById(R.id.B_QueryLoc);
        B_QueryDB = (ImageButton) findViewById(R.id.B_QueryDB);
        B_set = (ImageButton) findViewById(R.id.B_set);
        B_blist = (ImageButton) findViewById(R.id.B_blist);

        B_contrast.setBackgroundResource(R.drawable.contrast);
        B_QueryLoc.setBackgroundResource(R.drawable.searchloc);
        B_QueryDB.setBackgroundResource(R.drawable.searchdb);
        B_set.setBackgroundResource(R.drawable.set);
        B_blist.setBackgroundResource(R.drawable.blacklist_change);

        btnPhone = (Button) findViewById(R.id.btnPhone);
        B_saveblist=(Button) findViewById(R.id.B_shotblist);
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropButton = (Button) findViewById(R.id.B_shotblist);

        File file1 = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS");

        if (!file1.exists())
        {
            file1.mkdirs();
        }

        btnPhone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //setImage1(); // 魅族显示风格：最新，照片，图库；华为：包含有相片的一组目录，
                // 小米：选择要使用的应用，最后没有结果

                setImage(); //魅族显示风格：图库，文件选择(图片文件) ；华为：最近的照片 小米：选择要使用的应用，最后没有结果
            }
        });

        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bitmap croppedImage = cropImageView.getCroppedImage();
                savePicToPhone(croppedImage);
                Intent intent = new Intent(blackList.this, BlistCompare.class);
                startActivity(intent);
            }
        });
    }

    private void setImage1() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        startActivityForResult(intent, IMAGE_CODE);
    }

    private void setImage() {
        // TODO Auto-generated method stub
        // 使用intent调用系统提供的相册功能，使用startActivityForResult是为了获取用户选择的图片

        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.setType(IMAGE_UNSPECIFIED);
        startActivityForResult(getAlbum, IMAGE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        getBmpFormPhone = null;
        // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getContentResolver();
        if (requestCode == IMAGE_CODE) {
            try {
                Uri originalUri = data.getData(); // 获得图片的uri
                getBmpFormPhone = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                int pictureSize=getBmpFormPhone.getHeight()*getBmpFormPhone.getWidth();
                cropImageView.setImageBitmap(getBmpFormPhone);
                cropButton.setVisibility(View.VISIBLE);
                    // 显得到bitmap图片
                    // imageView.setImageBitmap(bm);
                    String[] proj = { MediaStore.Images.Media.DATA };

                    // 好像是android多媒体数据库的封装接口，具体的看Android文档
                    Cursor cursor = managedQuery(originalUri, proj, null, null, null);

                    // 按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    // 最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);

            } catch (IOException e) {
                Log.e("TAG-->Error", e.toString());

            }
            finally {
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void savePicToPhone(Bitmap bitmap){
        try {
            File file1 = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "WR_LPAIS");
            FileOutputStream fos;
            //xyj
            fos = new FileOutputStream(file1 + File.separator
                    + "shotPic.jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void botmSet(View v){
        Intent intent = new Intent(this, set.class);
        startActivity(intent);
    }
    public void botmContrast(View v){
        Intent intent = new Intent(this, takePhoto.class);
        startActivity(intent);
    }
    public void botmBlacklist(View v){
        Intent intent = new Intent(this, blackList.class);
        startActivity(intent);
    }

    public void botmQueryLoc(View v){
        Intent intent = new Intent(this, queryLocInfor.class);
        startActivity(intent);
    }
    public void botmQueryDB(View v){
        Intent intent = new Intent(this, queryDBInfor.class);
        startActivity(intent);
    }
    public void onBackPressed(){
        Intent intent = new Intent(blackList.this, takePhoto.class);
        startActivity(intent);
    }

    public void savePicOfBlist(View v){
            Intent intent = new Intent(this, inputInformationBlist.class);
            startActivity(intent);
    }

}
