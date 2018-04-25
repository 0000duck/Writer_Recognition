package cn.hxc.imgrecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.hxc.imgrecognitionSRI_OCR.R;

/**
 * Created by 刘欢 on 2018/3/25.
 */

public class findPassword extends Activity {
    public String getPsw;
    public String yourAns;
    public String input_answer;
    public LinearLayout linearLayout_showpsw;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.findpsw);//软件activity的布局

        String[] temp=forgetPassword.result.split("::");
        yourAns=temp[0];
        getPsw=temp[1];

        linearLayout_showpsw=(LinearLayout) findViewById(R.id.layout_showpsw);
        linearLayout_showpsw.setVisibility(View.INVISIBLE);
    }

    public void initView(){
        input_answer=((TextView)findViewById(R.id.input_answer)).getText().toString();
    }

    public void complete(View v){
        Intent intent = new Intent(findPassword.this, MainActivity.class);
        startActivity(intent);
    }

    public void findPswConfir(View v){
        initView();
        if(input_answer.equals(yourAns)){
            linearLayout_showpsw.setVisibility(View.VISIBLE);
            TextView textView = (TextView) findViewById(R.id.find_psw);
            textView.setText("密码是："+getPsw);
        }
        //String temp=
    }
}
