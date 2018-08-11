package cn.hxc.imgrecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hxc.imgrecognitionSRI_OCR.R;

/**
 * Created by 刘欢 on 2018/3/5.
 */

class ShowInfor{
    String ImageID; //图片唯一的ID
    String sxname;  //书写人名字
    String cjname;  //采集人名字
    String scTime;  //上传时间
    String phoneID; //手机唯一的标识
    //Image myPic;    //对应要显示的图片
    String ImagePath; //对应要显示图片的地址
    Bitmap bm;
}

public class queryLocInfor extends Activity{
    static int txtLineNum=4;
    static int FileLineNum;
    String thinImagePath=Environment.getExternalStorageDirectory()+ File.separator + "WR_LPAIS"+ File.separator + "Image";
    String txtPath=Environment.getExternalStorageDirectory()+ File.separator + "WR_LPAIS"+ File.separator + "txt";
    String norImagePath=Environment.getExternalStorageDirectory()+ File.separator + "WR_LPAIS"+ File.separator + "ShowImage";
    String subNorImgPath=Environment.getExternalStorageDirectory()+ File.separator + "WR_LPAIS"+ File.separator + "ShowImage"+ File.separator;
    String subtxtPath=Environment.getExternalStorageDirectory()+ File.separator + "WR_LPAIS"+ File.separator + "txt"+File.separator;

    public Context mContent;
    public GridView grid_photo;
    public TextView result;
    public BaseAdapter mAdapter=null;
    public ArrayList<Icon> mData=null;
    public String sxr; //书写人的名字
    public String cjr; //采集人的名字
    public ShowInfor[] SIF; //存每个显示条目的结构
    public int fileLength; //查询到的文件的个数
    public String[] row=new String[txtLineNum];

    public ImageButton B_contrast;
    public ImageButton B_QueryLoc;
    public ImageButton B_QueryDB;
    public ImageButton B_set;
    public ImageButton B_blist;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ������
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// ����ȫ��
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.queryloc);

        B_contrast = (ImageButton) findViewById(R.id.B_contrast);
        B_QueryLoc = (ImageButton) findViewById(R.id.B_QueryLoc);
        B_QueryDB = (ImageButton) findViewById(R.id.B_QueryDB);
        B_set = (ImageButton) findViewById(R.id.B_set);
        B_blist = (ImageButton) findViewById(R.id.B_blist);

        B_contrast.setBackgroundResource(R.drawable.contrast);
        B_QueryLoc.setBackgroundResource(R.drawable.searchloc_change);
        B_QueryDB.setBackgroundResource(R.drawable.searchdb);
        B_set.setBackgroundResource(R.drawable.set);
        B_blist.setBackgroundResource(R.drawable.blacklist);

        mContent = queryLocInfor.this;
        grid_photo = (GridView) findViewById(R.id.grid_photo);
        result=(TextView)findViewById(R.id.result);

        sxr = ((EditText) findViewById(R.id.edit_sxr)).getText().toString();
        cjr = ((EditText) findViewById(R.id.edit_cjr)).getText().toString();
        mData = new ArrayList<Icon>();

        try{
            queryAllInfor(norImagePath);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    /*
    将所有信息存储到自定义的结构体内，方面显示
    具体操作是先从存储归一化Image的文件夹内获取每个图片的名字，然后根据名字将对应的txt的内容读取出来然后存储
    */
    public void queryAllInfor(String FilePath)throws IOException{
        mData.clear();
        sxr = ((EditText) findViewById(R.id.edit_sxr)).getText().toString();
        cjr = ((EditText) findViewById(R.id.edit_cjr)).getText().toString();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.Options opts = processActivity.getOption(options);
        //Bitmap bmp = BitmapFactory.decodeFile(path, options);

        File file=new File(FilePath);
        if(!file.exists()){
            Toast.makeText(this,"本地无存储图片！",Toast.LENGTH_LONG).show();
        }
        else{
            //-------------------------------------------------------------------------------
            File[] files=file.listFiles();
            int filelength=files.length;

            ShowInfor[] SIF=new ShowInfor[filelength];
            ShowInfor[] SIF1=new ShowInfor[filelength];
            ShowInfor[] SIF2=new ShowInfor[filelength];
            ShowInfor[] SIF3=new ShowInfor[filelength];

            for(int i=0;i<filelength;i++){
                SIF[i]=new ShowInfor();
                SIF1[i]=new ShowInfor();
                SIF2[i]=new ShowInfor();
                SIF3[i]=new ShowInfor();

                String name=files[i].getName().substring(0,files[i].getName().length()-4);
                String a=subtxtPath+name+".txt";
                row=ReadTxtLine(a);
                SIF[i].ImageID=files[i].getName();
                SIF[i].sxname=row[0];
                SIF[i].cjname=row[1];
                SIF[i].scTime=row[2];
                SIF[i].ImagePath=subNorImgPath+files[i].getName();
                SIF[i].bm=BitmapFactory.decodeFile(SIF[i].ImagePath, opts);
            }

            if(sxr.isEmpty()&&cjr.isEmpty()){

                fileLength=filelength;

                //显示结果总数
                result.setText("结果总数："+fileLength);

                for(int i=0;i<fileLength;i++){
                    mData.add(new Icon("   图像ID："+SIF[i].ImageID,"   书写人："+SIF[i].sxname,"   上传人："+SIF[i].cjname,"   上传时间："+SIF[i].scTime,""+SIF[i].ImagePath, SIF[i].bm));
                }

                mAdapter=new MyAdapter<Icon>(mData,R.layout.item_grid_icon)
                {
                    public void bindView(ViewHolder holder,Icon obj){
                        holder.setText(R.id.imageid,obj.getImgID());
                        holder.setText(R.id.sxr,obj.getSxr());
                        holder.setText(R.id.cjr,obj.getCjr());
                        holder.setText(R.id.sctime,obj.getSctime());
                        //holder.setImageResource(R.id.myimg,);
                        holder.setImageResource(R.id.myimg,obj.getBm());
                    }
                };
                grid_photo.setAdapter(mAdapter);
            }
            //------------------------------------------------------
            else if(!sxr.isEmpty()&&cjr.isEmpty()){
                int index=0;
                for(int i=0;i<filelength;i++)
                    if(SIF[i].sxname.equals(sxr))
                    {
                        SIF1[index]=SIF[i];
                        index++;
                    }
                fileLength=index;
                result.setText("结果总数："+fileLength);
                for(int i=0;i<fileLength;i++){
                    mData.add(new Icon("   图像ID："+SIF1[i].ImageID,"   书写人："+SIF1[i].sxname,"   上传人："+SIF1[i].cjname,"   上传时间："+SIF1[i].scTime,""+SIF1[i].ImagePath,SIF1[i].bm));
                }

                mAdapter=new MyAdapter<Icon>(mData,R.layout.item_grid_icon)
                {
                    public void bindView(ViewHolder holder,Icon obj){
                        holder.setText(R.id.imageid,obj.getImgID());
                        holder.setText(R.id.sxr,obj.getSxr());
                        holder.setText(R.id.cjr,obj.getCjr());
                        holder.setText(R.id.sctime,obj.getSctime());
                        holder.setImageResource(R.id.myimg,obj.getBm());
                    }
                };
                grid_photo.setAdapter(mAdapter);
            }
//------------------------------------------------------------------------------------------
            else if(sxr.isEmpty()&&!cjr.isEmpty()){
                int index=0;
                for(int i=0;i<filelength;i++)
                    if(SIF[i].cjname.equals(cjr))
                    {
                        SIF2[index]=SIF[i];
                        index++;
                    }
                fileLength=index;
                result.setText("结果总数："+fileLength);
                for(int i=0;i<fileLength;i++){
                    mData.add(new Icon("   图像ID："+SIF2[i].ImageID,"   书写人："+SIF2[i].sxname,"   上传人："+SIF2[i].cjname,"   上传时间："+SIF2[i].scTime,""+SIF2[i].ImagePath,SIF2[i].bm));
                }

                mAdapter=new MyAdapter<Icon>(mData,R.layout.item_grid_icon)
                {
                    public void bindView(ViewHolder holder,Icon obj){
                        holder.setText(R.id.imageid,obj.getImgID());
                        holder.setText(R.id.sxr,obj.getSxr());
                        holder.setText(R.id.cjr,obj.getCjr());
                        holder.setText(R.id.sctime,obj.getSctime());
                        holder.setImageResource(R.id.myimg,obj.getBm());
                    }
                };
                grid_photo.setAdapter(mAdapter);
            }
//--------------------------------------------------------------------------
            else if(!sxr.isEmpty()&&!cjr.isEmpty()){
                int index=0;
                for(int i=0;i<filelength;i++)
                    if(SIF[i].sxname.equals(sxr)&&SIF[i].cjname.equals(cjr))
                    {
                        SIF3[index]=SIF[i];
                        index++;
                    }
                fileLength=index;
                result.setText("结果总数："+fileLength);
                for(int i=0;i<fileLength;i++){
                    mData.add(new Icon("   图像ID："+SIF3[i].ImageID,"   书写人："+SIF3[i].sxname,"   上传人："+SIF3[i].cjname,"   上传时间："+SIF3[i].scTime,""+SIF3[i].ImagePath,SIF3[i].bm));
                }

                mAdapter=new MyAdapter<Icon>(mData,R.layout.item_grid_icon)
                {
                    public void bindView(ViewHolder holder,Icon obj){
                        holder.setText(R.id.imageid,obj.getImgID());
                        holder.setText(R.id.sxr,obj.getSxr());
                        holder.setText(R.id.cjr,obj.getCjr());
                        holder.setText(R.id.sctime,obj.getSctime());
                        holder.setImageResource(R.id.myimg,obj.getBm());
                    }
                };
                grid_photo.setAdapter(mAdapter);
            }
        }
    }
        //读取每个txt文本里面的内容
        public String[] ReadTxtLine(String txtPath)throws IOException{
        FileReader fr = new FileReader(txtPath);   //这里定义一个字符流的输入流的节点流，用于读取文件（一个字符一个字符的读取）
        BufferedReader br = new BufferedReader(fr);  // 在定义好的流基础上套接一个处理流，用于更加效率的读取文件（一行一行的读取）

        int index=0;
        String[] rows = new String[txtLineNum];

        String temp=null;

        while((temp=br.readLine()) != null) { //  readLine()方法是按行读的，返回值是这行的内容
            rows[index] = temp;
            index++;
        }
        fr.close();
        br.close();

        return rows;
    }

    //按下查询本地页面的查询键执行的操作
    public void SearchDatabase(View v){
        try{
            queryAllInfor(norImagePath);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //点击屏幕顶部的返回按钮执行的操作
    public void searchLocBack(View v){
        super.onBackPressed();
    }

    //在查询本地界面执行退出按钮执行的操作
    public void LocSignOut(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
        Intent intent = new Intent(queryLocInfor.this, takePhoto.class);
        startActivity(intent);
    }
}
