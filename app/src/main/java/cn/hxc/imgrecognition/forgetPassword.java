package cn.hxc.imgrecognition;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URLEncoder;

import cn.hxc.imgrecognitionSRI_OCR.R;

/**
 * Created by 刘欢 on 2018/3/25.
 */

public class forgetPassword extends Activity {

    public static String forgetName;
    public String webforgetPath="http://101.132.159.49/PaisService.asmx/PaisFindSb?";
    public String backForgetPath;
    public static String result;
    MainActivity ma;
    inputInformation infor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_forgetpsw);//软件activity的布局

        //initData();
    }
    public void initView(){
        forgetName=((EditText)findViewById(R.id.forgte_username)).getText().toString();
    }

    public void forget_Confirm(View v){
        initView();
        ma=new MainActivity();
        infor=new inputInformation();
        if(!forgetName.isEmpty()){
            String temp="IMGMSG::"+forgetName+"::END";
            backForgetPath="Picinfo="+ URLEncoder.encode(temp);
            Thread sendThread=new Thread(new Runnable() {
                @Override
                public void run() {
                    result=infor.GETUtils(webforgetPath,backForgetPath);
                    result=ma.getContext(result);
                    judge(result);
                }
            });
            sendThread.start();
        }
        else if(forgetName.isEmpty()){
            Toast.makeText(getApplicationContext(), "请输入用户名！", Toast.LENGTH_LONG).show();
        }

    }

    //判断返回值是不是正确结果
    public void judge(String all){
        if(all.equals("[10400]")||all.equals("[10500]")||all.equals("[10600]")){
            handler.sendEmptyMessage(0);
        }
        else{
            result=result.substring(1,result.length()-1);
            Intent intent = new Intent(forgetPassword.this, findPassword.class);
            startActivity(intent);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "用户名不存在！", Toast.LENGTH_LONG).show();

        }
    };
}
