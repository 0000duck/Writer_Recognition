package cn.hxc.imgrecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.hxc.imgrecognitionSRI_OCR.R;

/**
 * Created by 刘欢 on 2018/2/1.
 */

public class inputInformation extends Activity {

    public String sxname; //书写人名称
    public String sxnametemp;//按照指定方式存的书写人
    public String cjname; //采集人名称
    public String cjnametemp;//按照指定方式存的采集人
    public String titlesxname;//txt文本标题中书写人的名字
    public String titlenum;//txt文本标题中文本的序列
    public String txtID;//每个文本唯一的ID
    public String imageID;//每个图片唯一的ID
    public String nowTime;//获取的系统时间
    public String phoneID;//手机唯一的ID
    public String SendString;//手机向webAPI发送的字符串
    public String txtUrlWeb="http://101.132.159.49/PaisService.asmx/PaisUploadTxts?";//需要上传txt的URL
    public String imageUrlWeb="http://101.132.159.49/PaisService.asmx/PaisUploadImages?";//需要上传jpg的URL
    public String oracleUrl="http://101.132.159.49/PaisService.asmx/PaisInsertImages?";//需要上传数据库的URL
    public String oracleString;
    public String imageBackUrl;
    public String txtBackUrl;//完整的url，包括上传的字符串
    String featurePath=Environment.getExternalStorageDirectory()+ File.separator + "WR_LPAIS"+ File.separator + "txt"+ File.separator+"feature.txt";
    String AnotherfeaturePath=Environment.getExternalStorageDirectory()+ File.separator + "WR_LPAIS"+ File.separator + "txt"+ File.separator+"Anotherfeature.txt";
    String imgpath= Environment.getExternalStorageDirectory() + File.separator + "WR_LPAIS"+ File.separator + "ThinNorTemp.jpg";  //手机的根目录下存细化图片的地址
    Bitmap myimgae;  //手机根目录下存细化的图片
    String showimgpath= Environment.getExternalStorageDirectory() + File.separator + "WR_LPAIS"+ File.separator + "NorTemp.jpg";  //手机的根目录下存细化图片的地址
    Bitmap myshowimgae;  //手机根目录下存细化的图片
    byte[] imageData;//图片的数据
    public byte[] bytes;
    String sendtemp;
    String imageResult;//插入图片返回的结果
    String txtResult;//插入文本返回结果
    String databaseResult;//插入数据库返回的结果
    String toastResult;//上传完成后最后弹出的结果
    CheckBox ckb_save;
    CheckBox ckb_send;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.input_information);
        WindowManager wm = (WindowManager) this
                .getSystemService(this.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        ckb_save = (CheckBox) findViewById(R.id.ckb_save);
        ckb_send = (CheckBox) findViewById(R.id.ckb_send);
    }

    /*点击确定按钮执行的操作，将图片及其信息保存在指定文件夹下
    */
    public void inforYes(View v){
        //int a = saveimage();
        //saveTxt(a);
        sxname= ((EditText) findViewById(R.id.sxname)).getText().toString();
        cjname= ((EditText) findViewById(R.id.cjname)).getText().toString();

        //获取系统时间，格式是_年_月_日_时_分_秒，并记录到相应的TXT文本里
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());
        nowTime  =  formatter.format(curDate);

        //获取手机唯一的序列号，每一个手机都有一个唯一的序列号，并记录到相应的TXT文本里
        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        phoneID = tm.getDeviceId();//获取智能设备唯一编号

        if (!sxname.isEmpty()&&!cjname.isEmpty())
        {

            if(ckb_save.isChecked()&&ckb_send.isChecked()){
                saveimage();
                saveTxt();
                sendOut();
                savePictureData(featurePath);
                savePictureData(AnotherfeaturePath);
            }

            if(ckb_save.isChecked()&&!ckb_send.isChecked()){
                saveimage();
                saveTxt();
                savePictureData(featurePath);
                savePictureData(AnotherfeaturePath);
            }

            if(!ckb_save.isChecked()&&ckb_send.isChecked()){
                sendOut();
                savePictureData(AnotherfeaturePath);
            }


            inputInformation.this.finish();
            Intent intent = new Intent(inputInformation.this, takePhoto.class);
            startActivity(intent);
        }
        else
            Toast.makeText(this,"请将带*的内容填完！",Toast.LENGTH_LONG).show();

    }

    //点击取消键，取消当前的操作
    public void inforNo(View v){
        inputInformation.this.finish();
        Intent intent = new Intent(this, takePhoto.class);
        startActivity(intent);
    }

    //点击发送键，将图片及其信息先保存至本地然后发送到服务器
    public void sendOut(){
         int a = getFeatureData();

        //将书写人和序号按照规则存储
        sxnametemp=sxname;
        if(sxnametemp.length()<3){
            int b=3-sxnametemp.length();
            while(b!=0){
                sxnametemp+="__";
                b--;
            }
        }
        titlenum=String.valueOf(a);
        while(titlenum.length()<6){
            StringBuilder SB=new StringBuilder(titlenum);
            SB.insert(0,'0');
            titlenum=SB.toString();
        }

        //上传txt的操作
        txtID=sxnametemp+titlenum;
        imageID=txtID;
        SendString=txtID + "\r\n" + sxname + "\r\n" + cjname + "\r\n" +nowTime + "\r\n" + phoneID + "\r\n";
        String SendString64=Base64.encodeToString(SendString.getBytes(),0);
        txtBackUrl="base64string="+ URLEncoder.encode(SendString64)+"&orifilename="+URLEncoder.encode(txtID)+".txt";

        //上传图片的操作
        imageData=image2Bytes(showimgpath);
        sendtemp=Base64.encodeToString(imageData,0);
        imageBackUrl="base64string="+URLEncoder.encode(sendtemp)+"&orifilename="+URLEncoder.encode(imageID)+".jpg";

        //插入数据库的操作
        String oracleStringtemp="IMGMSG::" + imageID + "::" + sxname + "::" + cjname + "::" +nowTime + "::" + phoneID + "::END";
        oracleString="Picinfo="+URLEncoder.encode(oracleStringtemp);

        Thread sendThread=new Thread(new Runnable() {
            @Override
            public void run() {
                GETUtils(txtUrlWeb,txtBackUrl);  //上传TXT
                //GETUtils(imageUrlWeb,imageBackUrl);//上传图片
                GETUtilImgs(imageUrlWeb,imageBackUrl);
                GETUtils(oracleUrl,oracleString);//插入数据库
            }
        });
        sendThread.start();

        Toast.makeText(this,"上传成功！",Toast.LENGTH_LONG).show();

        TimerTask task = new TimerTask(){
            public void run(){
                //execute the task
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1000);
    }

    public byte[] image2Bytes(String imgPath)
    {
        try{
            FileInputStream fin = new FileInputStream(new File(imgPath));
            //可能溢出,简单起见就不考虑太多,如果太大就要另外想办法，比如一次传入固定长度byte[]
            bytes = new byte[fin.available()];
            //将文件内容写入字节数组，提供测试的case
            fin.read(bytes);

            fin.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return bytes;
    }

    //Andriod访问WebAPI
    public String GETUtils(String urlString,String inputLine){
        String msg="";
        try{
            String WholeString=urlString+inputLine;
            URL url = new URL(WholeString);
            //Toast.makeText(this,WholeString,Toast.LENGTH_LONG).show();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置请求方式
            conn.setRequestMethod("GET");
            //设置运行输入,输出:
            conn.setDoOutput(false);
            conn.setDoInput(true);
            //Post方式不能缓存,需手动设置为false
            conn.setUseCaches(true);
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(3000);
            conn.connect();
            int code =conn.getResponseCode();
            int dd=code;
            int a=dd;

            if (code==200)
            {
                BufferedReader read=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line=null;
                while ((line=read.readLine())!=null)
                {
                    msg+=line;
                }
                read.close();
            }

            conn.disconnect();

            Toast.makeText(this,msg,Toast.LENGTH_LONG).show();

        }catch (Exception e){
            e.printStackTrace();
        }
        return msg;
    }

    //上传图片
    public void GETUtilImgs(String urlString,String imgstr){
        try{
            String result=HttpUtils.doPost(urlString,imgstr);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    NAME######.JPG
    NAME是中文名称，用6个字节表示，2个字节表示一个汉字，不足三个字用两个下划线表示。
    ######是6位数字编号，从000001开始计数。
    * */
    public int saveimage()
    {
        titlesxname=sxname;
        myimgae = BitmapFactory.decodeFile(imgpath);
        myshowimgae = BitmapFactory.decodeFile(showimgpath);
//------------------将归一化后的图片保存下来--------------------
        File showfile = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS"+ File.separator + "ShowImage");

        if (!showfile.exists()) {
            showfile.mkdirs();
        }
//------------------------将细化后的图片保存下来------------------
        File file = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS"+ File.separator + "Image");

        if (!file.exists()) {
            file.mkdirs();
        }
        final int i = getFeatureData();
        int newname = 0;
        FileOutputStream fos = null;
        FileOutputStream showfos = null;

        if(titlesxname.length()<3){
            int a=3-titlesxname.length();
            while(a!=0){
                titlesxname+="__";
                a--;
            }
        }
        //数字数目补足为6位
        titlenum=String.valueOf(i);
        while(titlenum.length()<6){
            StringBuilder SB=new StringBuilder(titlenum);
            SB.insert(0,'0');
            titlenum=SB.toString();
        }

        //按规定格式保存图片
        try {
            newname=i;
            fos = new FileOutputStream(file + File.separator +titlesxname+ titlenum+".jpg");
            myimgae.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            showfos = new FileOutputStream(showfile + File.separator +titlesxname+ titlenum+".jpg");
            myshowimgae.compress(Bitmap.CompressFormat.JPEG, 100, showfos);

            fos.flush();
            fos.close();

            showfos.flush();
            showfos.close();
        } catch (IOException e) {
            Toast.makeText(this,"save faied!",Toast.LENGTH_LONG).show();
        }
        return newname;
    }

    /*保存txt文件，其中包括图片的信息
    * NAME######.txt
    NAME是中文名称，用6个字节表示，2个字节表示一个汉字，不足三个字用两个下划线表示。
    ######是6位数字编号，从000001开始计数。
     */
    public void saveTxt(){
        int a = getFeatureData();

            //人名不足3位的补'_'
            if(titlesxname.length()<3){
                int b=3-titlesxname.length();
                while(b!=0){
                    titlesxname+="__";
                    b--;
                }
            }
            //final String snewname = String.valueOf(a);
            File txtfile = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "WR_LPAIS" + File.separator + "txt");

            if (!txtfile.exists()) {
                txtfile.mkdirs();
            }
////////-----------------------------保存图片的信息，如书写人、采集人、手机号等信息--------------------------------------------
            //数字不足6位的前面补0
            titlenum=String.valueOf(a);
            while(titlenum.length()<6){
                StringBuilder SB=new StringBuilder(titlenum);
                SB.insert(0,'0');
                titlenum=SB.toString();
            }
            File sxf = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "WR_LPAIS" + File.separator + "txt" + File.separator + titlesxname+titlenum+".txt");
            if(!sxf.exists()){
                try{
                    sxf.createNewFile();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }

            FileWriter fs = null;
            try {
                fs = new FileWriter(sxf, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            PrintWriter sxw = new PrintWriter(fs);
            sxw.print(sxname + "\r\n");  //将书写人的名字记录到相应的TXT文本里
            sxw.print(cjname + "\r\n");  //将采集人的名字记录到相应的TXT文本里

            sxw.print(nowTime + "\r\n");

            sxw.print(phoneID + "\r\n");

            sxw.flush();

            try {
                fs.flush();
                sxw.close();
                fs.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
/////////---------------------------------保存图像的特征数组--------------------------------------------------------
    }

    public void savePictureData(String Path){
        File f = new File(Path);

        int a = getFeatureData();
        final String snewname = String.valueOf(a);

        FileWriter fw = null;
        try {
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter pw = new PrintWriter(fw);
        pw.print(snewname + " ");
        pw.print(sxname + " ");
        for (int ii = 0; ii < 920; ii++) {
            String temp = String.format("%-6f", processActivity.ixyj[ii]);
            pw.print(temp + " ");
        }
        pw.print("\r\n");
        pw.flush();

        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getFeatureData(){
        int num=0;
        try {
            num=processActivity.getTextLines(AnotherfeaturePath);
        }catch (IOException e){
            e.printStackTrace();
        }
        return num;
    }
}
