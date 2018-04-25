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

    //创建一个Item格式的List
    private List<Item> list = new ArrayList<Item>();
    private ListView listView;
    private ItemAdapter itemAdapter;

    int[] by;

    public static float[] ixyj;

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private final int IMAGE_CODE = 0; // 这里的IMAGE_CODE是自己任意定义的

    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.blacklist);//软件activity的布局

        btnPhone = (Button) findViewById(R.id.btnPhone);
        B_saveblist=(Button) findViewById(R.id.B_saveblist);

        listView=(ListView)findViewById(R.id.Slistview);
        itemAdapter=new ItemAdapter(this,R.layout.listprocess,list);
        listView.setAdapter(itemAdapter);

        orignalView = (ImageView) findViewById(R.id.orignalView);
        norView=(ImageView) findViewById(R.id.norView);
        orignalTxt=(TextView) findViewById(R.id.orignalTxt);
        norTxt=(TextView) findViewById(R.id.norTxt);

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
    }
    public void norAndShow(Bitmap bitmap) {
        int height;
        int width;
        int NeedHeight = 250;
        int NeedWidth = 0;
        Bitmap greyBitmap;
        int[] pixels;
        int[] ThinNorpixels;
        int[] ThinNorBy;
        int[] step;
        height = bitmap.getHeight();
        width = bitmap.getWidth();
        Matrix matrix = new Matrix();
        greyBitmap = imageProcess.greyToArray(bitmap);
        pixels = new int[width * height];
        greyBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        by = new int[width * height];
        step = new int[width * height];
        // int[] by1 = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            by[i] = (0xff & pixels[i]);
        }
        int threshold = processActivity.otsu(by, width, height, 0, 0, width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (by[i + j * width] < threshold) {
                    by[i + j * width] = 0;
                } else {
                    by[i + j * width] = 255;
                }
            }
        }
        int BinaryPixels[] = new int[width * height];
        for (int p = 0; p < height; p++) {
            for (int q = 0; q < width; q++) {
                int gray = by[p * width + q];
                int newcolor = (gray << 16) | (gray << 8) | (gray);
                BinaryPixels[p * width + q] = newcolor;
            }
        }
        Bitmap binaryBitmap = Bitmap.createBitmap(BinaryPixels, 0, width, width, height, Bitmap.Config.RGB_565);
        //归一化begin
        Projection pro1 = new Projection();
        //创建一个数组用于进行归一化
        int[] Nor;

        int begin_y = pro1.ProExtractBegin(pro1.ProjectionY(by, height, width), height);//y投影的第一个行
        int end_y = pro1.ProExtractEnd(pro1.ProjectionY(by, height, width), height);//y投影的最后一行
        int begin_x = pro1.ProExtractBegin(pro1.ProjectionX(by, height, width), width);//x投影的第一列
        int end_x = pro1.ProExtractEnd(pro1.ProjectionX(by, height, width), width);//x投影的最后一列

        int NorHeight = end_y - begin_y + 1;//归一化之前的高度
        int NorWidth = end_x - begin_x + 1;//归一化之前的宽度

        //提取切割后的图像
        Nor = new int[NorHeight * NorWidth];
        for (int i = begin_y; i <= end_y; i++) {
            for (int j = begin_x; j <= end_x; j++) {
                Nor[(i - begin_y) * NorWidth + (j - begin_x)] = by[i * width + j];
            }
        }

        float YScale;
        float XScale;

        YScale = ((float) NeedHeight / NorHeight);

        if (NorWidth * YScale > width) {
            NorWidth = (int) (width / YScale);
        }

        NeedWidth = (int) (NorWidth * YScale);

        matrix.postScale(YScale, YScale); //长和宽放大缩小的比例

        Bitmap NorBitmap = Bitmap.createBitmap(binaryBitmap, begin_x, begin_y, NorWidth, NorHeight);
        //Bitmap a=new Bitmap();
        Bitmap resizeNorBitmap = Bitmap.createBitmap(NorBitmap, 0, 0, NorWidth, NorHeight, matrix, true);
//End归一化-------------------------------------------------------------------------------------------------------------------

//Begin细化-----------------------------------------------------------------------------------------------------------------------
        ZhangThinFilter zhang1 = new ZhangThinFilter();
        boolean goon = true;
        Arrays.fill(step, 0);
        //int i=5;

        //执行Zhang-Sue算法
        while (goon) {
            goon = false;
            boolean s1 = zhang1.ZhangStep1(by, step, height, width);
            zhang1.deletepixel(by, step, height, width);
            Arrays.fill(step, 0);
            // step two
            boolean s2 = zhang1.ZhangStep2(by, step, height, width);
            zhang1.deletepixel(by, step, height, width);
            Arrays.fill(step, 0);
            if (s1 && s2) {
                goon = true;
            }
        }
        int numPixels[] = new int[width * height];
        for (int p = 0; p < height; p++) {
            for (int q = 0; q < width; q++) {
                int gray = by[p * width + q];
                int newcolor = (gray << 16) | (gray << 8) | (gray);
                numPixels[p * width + q] = newcolor;
            }
        }
        //ixyj = xyj(width, height, 1);
        Bitmap ThinngBitmap = Bitmap.createBitmap(numPixels, 0, width,
                width, height, Bitmap.Config.RGB_565);
/////
        //begin细化+归一化-------------------------------------------------------------------------------------------------------------
        Bitmap NorThinBitmap = Bitmap.createBitmap(ThinngBitmap, begin_x, begin_y, NorWidth, NorHeight);
        Bitmap resizeNorThinBitmap = Bitmap.createBitmap(NorThinBitmap, 0, 0, NorWidth, NorHeight, matrix, true);

        pixels = new int[NeedHeight * NeedWidth];
        resizeNorThinBitmap.getPixels(pixels, 0, NeedWidth, 0, 0, NeedWidth, NeedHeight);
        by = new int[NeedWidth * NeedHeight];

        for (int i = 0; i < NeedHeight * NeedWidth; i++) {
            by[i] = (0xff & pixels[i]);
        }
        ixyj = xyj(NeedWidth, NeedHeight, 1);
        norTxt.setText("归一化之后的图");
        norView.setImageBitmap(resizeNorBitmap);

        FileOutputStream fos1;

        try {
            File file1 = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "WR_LPAIS");
            //xyj
            fos1 = new FileOutputStream(file1 + File.separator
                    + "ThinNorTemp.jpg");
            resizeNorThinBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos1);
            fos1.flush();
            fos1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File file1 = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "WR_LPAIS");

            //xyj
            fos1 = new FileOutputStream(file1 + File.separator
                    + "NorTemp.jpg");
            resizeNorBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos1);
            fos1.flush();
            fos1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File file1 = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "WR_LPAIS");

            //xyj
            fos1 = new FileOutputStream(file1 + File.separator
                    + "binaryTemp.jpg");
            binaryBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos1);
            fos1.flush();
            fos1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File file1 = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "WR_LPAIS");

            //xyj
            fos1 = new FileOutputStream(file1 + File.separator
                    + "greyTemp.jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos1);
            fos1.flush();
            fos1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File txtfile = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS" + File.separator + "txt");

        if (!txtfile.exists()) {
            txtfile.mkdirs();
            Toast.makeText(this, "数据库中没有图像！", Toast.LENGTH_LONG).show();
            return;
        }

        File f = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS" + File.separator + "txt" + File.separator + "feature.txt");

        if (!f.exists()) {
            Toast.makeText(this, "数据库中没有图像！", Toast.LENGTH_LONG).show();
            return;
        }

        File f2 = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS" + File.separator + "txt" + File.separator + "Anotherfeature.txt");

        if (!f2.exists()) {
            Toast.makeText(this, "数据库中没有图像！", Toast.LENGTH_LONG).show();
            return;
        }
        int datanum = 0;
        try {
            datanum = processActivity.getTextLines(f.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (datanum < 3) {
            Toast.makeText(this, "数据库中只有" + datanum + "幅图像，数量过少（至少需要三幅图像）！", Toast.LENGTH_LONG).show();
            return;
        }

        int Threshold = 3;
        File tf = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS" + File.separator + "txt" + File.separator + "threshold.txt");
        if (!tf.exists()) {
            Threshold = 3;
        } else {
            try{
                FileReader fr = new FileReader(tf.toString());   //这里定义一个字符流的输入流的节点流，用于读取文件（一个字符一个字符的读取）
                BufferedReader br = new BufferedReader(fr);  // 在定义好的流基础上套接一个处理流，用于更加效率的读取文件（一行一行的读取）
                String stemp = null;
                stemp = br.readLine();
                if (stemp == null) {
                    Threshold = 3;
                    fr.close();
                    br.close();
                } else {
                        Threshold = Integer.parseInt(stemp);
                    fr.close();
                    br.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        float[] yuzhi = {0.00001f, 0.00005f, 0.0001f, 1f};
        processActivity.IImage[] iamgeID = new processActivity.IImage[datanum];
        for (int i = 0; i < datanum; i++) {
            iamgeID[i] = new processActivity.IImage();
        }
        float[][] base = new float[datanum][920];

        if (datanum > 0) {
            try{
                processActivity.getALLLines(f.toString(), datanum, base, iamgeID);
            }catch (IOException e){
                e.printStackTrace();
            }

        }

        for (int i = 0; i < datanum; i++) {
            iamgeID[i].distanse = processActivity.distance_xyj(ixyj, base[i]);
        }

        Arrays.sort(iamgeID, new processActivity.MyComprator());

        int flag = 0;
        int NOcandidate = 3;
        File cf = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS" + File.separator + "txt" + File.separator + "NO_candidate.txt");
        if (!cf.exists()) {
            NOcandidate = 3;
        } else {
            try {
                FileReader fr = new FileReader(cf.toString());   //这里定义一个字符流的输入流的节点流，用于读取文件（一个字符一个字符的读取）
                BufferedReader br = new BufferedReader(fr);  // 在定义好的流基础上套接一个处理流，用于更加效率的读取文件（一行一行的读取）
                String stemp = null;
                stemp = br.readLine();
                if (stemp == null) {
                    NOcandidate = 3;
                    try{
                        fr.close();
                        br.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                } else {
                    NOcandidate = Integer.parseInt(stemp);
                }
                fr.close();
                br.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        String name;
        String num;

        for (int i = 0; i < NOcandidate && i < datanum; i++) {
            if (iamgeID[i].distanse > yuzhi[Threshold]) {
                break;
            }
            //将书写人名字和序号改写成规定格式
            name = iamgeID[i].name;
            if (name.length() < 3) {
                int a = 3 - name.length();
                while (a != 0) {
                    name += "__";
                    a--;
                }
            }
            num = String.valueOf(Integer.valueOf(iamgeID[i].ID));
            while (num.length() < 6) {
                StringBuilder SB = new StringBuilder(num);
                SB.insert(0, '0');
                num = SB.toString();
            }

            String temp;
            String picPath = Environment.getExternalStorageDirectory() + File.separator + "WR_LPAIS" + File.separator + "ShowImage" + File.separator + name + num + ".jpg";
            temp = "第" + (i + 1) + "候选人姓名：" + iamgeID[i].name + "  相似距离：" + iamgeID[i].distanse;
            Item item = new Item(temp, picPath); //picPath[i]为第i张图片的地址
            list.add(item);  //添加item
        }
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
                B_saveblist.setVisibility(View.VISIBLE);
                orignalTxt.setText("原图");
                orignalView.setImageBitmap(getBmpFormPhone);  //使用系统的一个工具类，参数列表为 Bitmap Width,Height  这里使用压缩后显示，否则在华为手机上ImageView 没有显示
                norAndShow(getBmpFormPhone);
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

    float[] xyj(int width, int height, int flag) {
        int i, j;
        int[] mfeature1 = new int[8];
        int[] mfeature2 = new int[16];
        int[] mfeature3 = new int[24];
        int[] mfeature4 = new int[32];
//		int[] mfeature5 = new int[40];
//		int[] mfeature6 = new int[48];
//		int[] mfeature7 = new int[56];

        int pixel;
        float[] feature1 = new float[28];
        float[] feature2 = new float[120];
        float[] feature3 = new float[276];
        float[] feature4 = new float[496];
        if (height < 9 || width < 9) {
            return new float[1];
        }
        for (i = 4; i < height - 4; i++) {
            for (j = 4; j < width - 4; j++) {
                pixel = j + i * width;
                if (by[pixel] == 0) {
                    mfeature1[0] = by[pixel + 1];
                    mfeature1[1] = by[pixel - width - 1];
                    mfeature1[2] = by[pixel - width];
                    mfeature1[3] = by[pixel - width - 1];
                    mfeature1[4] = by[pixel - 1];
                    mfeature1[5] = by[pixel + width - 1];
                    mfeature1[6] = by[pixel + width];
                    mfeature1[7] = by[pixel + width + 1];

                    mfeature2[0] = by[pixel + 2];
                    mfeature2[1] = by[pixel - 1 * width + 2];
                    mfeature2[2] = by[pixel - 2 * width + 2];
                    mfeature2[3] = by[pixel - 2 * width + 1];
                    mfeature2[4] = by[pixel - 2 * width];
                    mfeature2[5] = by[pixel - 2 * width - 1];
                    mfeature2[6] = by[pixel - 2 * width - 2];
                    mfeature2[7] = by[pixel - 1 * width - 2];
                    mfeature2[8] = by[pixel - 2];
                    mfeature2[9] = by[pixel + 1 * width - 2];
                    mfeature2[10] = by[pixel + 2 * width - 2];
                    mfeature2[11] = by[pixel + 2 * width - 1];
                    mfeature2[12] = by[pixel + 2 * width];
                    mfeature2[13] = by[pixel + 2 * width + 1];
                    mfeature2[14] = by[pixel + 2 * width + 2];
                    mfeature2[15] = by[pixel + 1 * width + 2];

                    mfeature3[0] = by[pixel + 3];
                    mfeature3[1] = by[pixel - 1 * width + 3];
                    mfeature3[2] = by[pixel - 2 * width + 3];
                    mfeature3[3] = by[pixel - 3 * width + 3];
                    mfeature3[4] = by[pixel - 3 * width + 2];
                    mfeature3[5] = by[pixel - 3 * width + 1];
                    mfeature3[6] = by[pixel - 3 * width + 0];
                    mfeature3[7] = by[pixel - 3 * width - 1];
                    mfeature3[8] = by[pixel - 3 * width - 2];
                    mfeature3[9] = by[pixel - 3 * width - 3];
                    mfeature3[10] = by[pixel - 2 * width - 3];
                    mfeature3[11] = by[pixel - 1 * width - 3];
                    mfeature3[12] = by[pixel - 0 * width - 3];
                    mfeature3[13] = by[pixel + 1 * width - 3];
                    mfeature3[14] = by[pixel + 2 * width - 3];
                    mfeature3[15] = by[pixel + 3 * width - 3];
                    mfeature3[16] = by[pixel + 3 * width - 2];
                    mfeature3[17] = by[pixel + 3 * width - 1];
                    mfeature3[18] = by[pixel + 3 * width - 0];
                    mfeature3[19] = by[pixel + 3 * width + 1];
                    mfeature3[20] = by[pixel + 3 * width + 2];
                    mfeature3[21] = by[pixel + 3 * width + 3];
                    mfeature3[22] = by[pixel + 2 * width + 3];
                    mfeature3[23] = by[pixel + 1 * width + 3];

                    mfeature4[0] = by[pixel + 4];
                    mfeature4[1] = by[pixel - 1 * width + 4];
                    mfeature4[2] = by[pixel - 2 * width + 4];
                    mfeature4[3] = by[pixel - 3 * width + 4];
                    mfeature4[4] = by[pixel - 4 * width + 4];
                    mfeature4[5] = by[pixel - 4 * width + 3];
                    mfeature4[6] = by[pixel - 4 * width + 2];
                    mfeature4[7] = by[pixel - 4 * width + 1];
                    mfeature4[8] = by[pixel - 4 * width + 0];
                    mfeature4[9] = by[pixel - 4 * width - 1];
                    mfeature4[10] = by[pixel - 4 * width - 2];
                    mfeature4[11] = by[pixel - 4 * width - 3];
                    mfeature4[12] = by[pixel - 4 * width - 4];
                    mfeature4[13] = by[pixel - 3 * width - 4];
                    mfeature4[14] = by[pixel - 2 * width - 4];
                    mfeature4[15] = by[pixel - 1 * width - 4];
                    mfeature4[16] = by[pixel - 0 * width - 4];
                    mfeature4[17] = by[pixel + 1 * width - 4];
                    mfeature4[18] = by[pixel + 2 * width - 4];
                    mfeature4[19] = by[pixel + 3 * width - 4];
                    mfeature4[20] = by[pixel + 4 * width - 4];
                    mfeature4[21] = by[pixel + 4 * width - 3];
                    mfeature4[22] = by[pixel + 4 * width - 2];
                    mfeature4[23] = by[pixel + 4 * width - 1];
                    mfeature4[24] = by[pixel + 4 * width - 0];
                    mfeature4[25] = by[pixel + 4 * width + 1];
                    mfeature4[26] = by[pixel + 4 * width + 2];
                    mfeature4[27] = by[pixel + 4 * width + 3];
                    mfeature4[28] = by[pixel + 4 * width + 4];
                    mfeature4[29] = by[pixel + 3 * width + 4];
                    mfeature4[30] = by[pixel + 2 * width + 4];
                    mfeature4[31] = by[pixel + 1 * width + 4];

                    int a, b, c, d;
                    int flag2 = 0;
                    int flag3 = 0;
                    int flag41 = 0;
                    int flag42 = 0;

                    for (a = 0; a < 8; a++) {
                        if (mfeature1[a] == 0) {
                            for (b = a + 1; b < 8; b++) {
                                if (mfeature1[b] == 0) {
                                    feature1[a * (15 - a) / 2 + b - a - 1] = feature1[a * (15 - a) / 2 + b - a - 1] + 1;
                                    a = b;
                                    if (flag == 1) {
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    for (a = 0; a < 16; a++) {
                        if (mfeature2[a] == 0) {
                            for (b = a + 1; b < 16; b++) {
                                if (mfeature2[b] == 0) {
                                    for (c = 0; c < 8; c++) {
                                        if (a == 2 * c) {
                                            for (d = 0; d < 8; d++) {
                                                if (b == 2 * d) {
                                                    feature1[c * (15 - c) / 2 + d - c - 1] = feature1[c * (15 - c) / 2 + d - c - 1] + 1;
                                                    a = b;
                                                    flag2 = 1;
                                                    if (flag == 1) {
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (flag2 == 0) {
                                        feature2[a * (31 - a) / 2 + b - a - 1] = feature2[a * (31 - a) / 2 + b - a - 1] + 1;
                                        a = b;
                                        if (flag == 1) {
                                            break;
                                        }
                                    }
                                    flag2 = 0;
                                }
                            }
                        }
                    }

                    for (a = 0; a < 24; a++) {
                        if (mfeature3[a] == 0) {
                            for (b = a + 1; b < 24; b++) {
                                if (mfeature3[b] == 0) {
                                    for (c = 0; c < 8; c++) {
                                        if (a == 3 * c) {
                                            for (d = 0; d < 8; d++) {
                                                if (b == 3 * d) {
                                                    feature1[c * (15 - c) / 2 + d - c - 1] = feature1[c * (15 - c) / 2 + d - c - 1] + 1;
                                                    a = b;
                                                    flag3 = 1;
                                                    if (flag == 1) {
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (flag3 == 0) {
                                        feature3[a * (47 - a) / 2 + b - a - 1] = feature3[a * (47 - a) / 2 + b - a - 1] + 1;
                                        a = b;
                                        if (flag == 1) {
                                            break;
                                        }
                                    }
                                    flag3 = 0;
                                }
                            }
                        }
                    }

                    for (a = 0; a < 32; a++) {
                        if (mfeature4[a] == 0) {
                            for (b = a + 1; b < 32; b++) {
                                if (mfeature4[b] == 0) {
                                    for (c = 0; c < 8; c++) {
                                        if (a == 4 * c) {
                                            for (d = 0; d < 8; d++) {
                                                if (b == 4 * d) {
                                                    feature1[c * (15 - c) / 2 + d - c - 1] = feature1[c * (15 - c) / 2 + d - c - 1] + 1;
                                                    a = b;
                                                    flag41 = 1;
                                                    if (flag == 1) {
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    for (c = 0; c < 16; c++) {
                                        if (a == 2 * c) {
                                            for (d = 0; d < 16; d++) {
                                                if (b == 2 * d) {
                                                    if (flag41 == 0) {
                                                        feature2[c * (31 - c) / 2 + d - c - 1] = feature2[c * (31 - c) / 2 + d - c - 1] + 1;
                                                        a = b;
                                                        flag42 = 1;
                                                        if (flag == 1) {
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (flag41 == 0 && flag42 == 0) {
                                        feature4[a * (63 - a) / 2 + b - a - 1] = feature4[a * (63 - a) / 2 + b - a - 1] + 1;
                                        a = b;
                                        if (flag == 1) {
                                            break;
                                        }
                                    }
                                    flag41 = 0;
                                    flag42 = 0;
                                }
                            }
                        }
                    }
                }
            }
        }

        int a = 0;
        float sum = 0;
        for (a = 0; a < 28; a++) {
            sum = sum + feature1[a];
        }
        for (a = 0; a < 120; a++) {
            sum = sum + feature2[a];
        }
        for (a = 0; a < 276; a++) {
            sum = sum + feature3[a];
        }
        for (a = 0; a < 496; a++) {
            sum = sum + feature4[a];
        }

        float[] probability = new float[920];

        for (a = 0; a < 28; a++) {
            //para[a]=feature1[a]/sum;
            probability[a] = feature1[a] / sum;
        }
        for (a = 0; a < 120; a++) {
            //para[a]=feature1[a]/sum;
            probability[a + 28] = feature2[a] / sum;
        }

        for (a = 0; a < 276; a++) {
            //para[a]=feature1[a]/sum;
            probability[a + 28 + 120] = feature3[a] / sum;
        }
        for (a = 0; a < 496; a++) {
            //para[a]=feature1[a]/sum;
            probability[a + 28 + 120 + 276] = feature4[a] / sum;
        }

        return probability;
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
