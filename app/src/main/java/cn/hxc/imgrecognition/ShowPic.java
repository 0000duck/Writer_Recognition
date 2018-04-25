package cn.hxc.imgrecognition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.hxc.imgrecognitionSRI_OCR.R;

/**
 * Created by 刘欢 on 2017/12/19.
 */

public class ShowPic extends Activity{
    private ListView lv;
    private BeanAdapter mAdapter;
    private List<Bean> mList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_image);
        lv = (ListView) findViewById(R.id.lv);
        initData();
        mAdapter = new BeanAdapter(mList, this);
        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ViewHolder viewHolder = (ViewHolder)view.getTag();
                if(viewHolder.cb.isChecked())       //true -> false
                {
                    viewHolder.cb.setChecked(false);
                    mList.get(position).setIsChcked(false);
                }
                else
                {
                    viewHolder.cb.setChecked(true);
                    mList.get(position).setIsChcked(true);
                }
            }
        });
    }
    private void initData(){
        mList = new ArrayList<Bean>();
        Bean bean;
        File file = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS"+ File.separator + "Image");
        File[] files = file.listFiles();
        final int no_image = files.length;

       /* for(int j = 0; j < no_writer; j++)
        {
//            bean = new Bean(getResources().getDrawable(R.drawable.icon), "title" + j, "info" + j, false);
            String temps = file.toString()+ File.separator+iamgeID[j].ID+".jpg";
            bean = new Bean(temps, iamgeID[j].ID, iamgeID[j].name, false);
            mList.add(bean);
        }*/
    }
}
