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



public class chooseImage extends Activity {

    private ListView lv;
    private BeanAdapter mAdapter;
    private List<Bean> mList;

    private String name;
    private String num;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_image);
        lv = (ListView) findViewById(R.id.lv);
        initData();
        mAdapter = new BeanAdapter(mList, this);
        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                if (viewHolder.cb.isChecked())       //true -> false
                {
                    viewHolder.cb.setChecked(false);
                    mList.get(position).setIsChcked(false);
                } else {
                    viewHolder.cb.setChecked(true);
                    mList.get(position).setIsChcked(true);
                }
            }
        });
    }

    private void initData() {
        mList = new ArrayList<Bean>();
        Bean bean;
        File file = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS" + File.separator + "ShowImage");

        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        final int no_image = files.length;

        if (no_image == 0) {
            Toast.makeText(this, "数据库中没有图像！", Toast.LENGTH_LONG).show();
            return;
        }

        File f = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS" + File.separator + "txt" + File.separator + "feature.txt");

        if (!f.exists()) {
            Toast.makeText(this, "特征文件不存在，请清除所有数据！", Toast.LENGTH_LONG).show();
            return;
        }

        int no_writer = 0;
        try {
            no_writer = getTextLines(f.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (no_writer != no_image) {
            Toast.makeText(this, "数据库中图像和书写人信息不匹配，请清除所有数据！", Toast.LENGTH_LONG).show();
            return;
        }

        processActivity.IImage[] iamgeID = new processActivity.IImage[no_writer];
        for (int i = 0; i < no_writer; i++) {
            iamgeID[i] = new processActivity.IImage();
        }
        float[][] base = new float[no_writer][920];

        if (no_writer > 0) {
            try {
                getALLLines(f.toString(), no_writer, base, iamgeID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int j = 0; j < no_writer; j++) {
            name = iamgeID[j].name;
            num = iamgeID[j].ID;
            if (name.length() < 3) {
                int a = 3 - name.length();
                while (a != 0) {
                    name += "__";
                    a--;
                }
            }
            num = String.valueOf(Integer.valueOf(num));
            while (num.length() < 6) {
                StringBuilder SB = new StringBuilder(num);
                SB.insert(0, '0');
                num = SB.toString();
            }
            String temps = file.toString() + File.separator + name + num + ".jpg";
            //Toast.makeText(this,""+name+num,Toast.LENGTH_LONG).show();
            bean = new Bean(temps, iamgeID[j].ID, iamgeID[j].name, false);
            mList.add(bean);
        }
    }

    public static int getTextLines(String path) throws FileNotFoundException {

        FileReader fr = new FileReader(path);   //这里定义一个字符流的输入流的节点流，用于读取文件（一个字符一个字符的读取）
        BufferedReader br = new BufferedReader(fr);  // 在定义好的流基础上套接一个处理流，用于更加效率的读取文件（一行一行的读取）
        int x = 0;   // 用于统计行数，从0开始
        try {
            while (br.readLine() != null) { //  readLine()方法是按行读的，返回值是这行的内容
                x++;   // 每读一行，则变量x累加1
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return x;  //返回总的行数
    }

    public static int getALLLines(String path, int num, float[][] base, processActivity.IImage[] label) throws IOException {

        FileReader fr = new FileReader(path);   //这里定义一个字符流的输入流的节点流，用于读取文件（一个字符一个字符的读取）
        BufferedReader br = new BufferedReader(fr);  // 在定义好的流基础上套接一个处理流，用于更加效率的读取文件（一行一行的读取）
        int index = 0;

        String[][] rows = new String[num][930];
        String temp = null;
        while ((temp = br.readLine()) != null) { //  readLine()方法是按行读的，返回值是这行的内容
            rows[index] = temp.split("( )+");
            index++;
        }
        for (int i = 0; i < num; i++) {
            label[i].ID = (rows[i][0]);
            label[i].name = (rows[i][1]);
            for (int j = 0; j < 920; j++) {
                base[i][j] = Float.parseFloat(rows[i][j + 2]);
            }
        }
        fr.close();
        br.close();
        return 1;  //返回总的行数
    }

    public void PostToCom() {
    }

    public void OpenPic(View v) {
        if (mList.isEmpty()) {
            Intent intent = new Intent(chooseImage.this, MainActivity.class);
            startActivity(intent);
            return;
        }

        File file = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS" + File.separator + "Image");

        File[] files = file.listFiles();
        final int no_image = files.length;

    }

    public void shanchuwenjian(View v) {

        if (mList.isEmpty()) {
            Intent intent = new Intent(chooseImage.this, functionMain.class);
            startActivity(intent);
            return;
        }

        File file = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS" + File.separator + "Image");

        File Anotherfile = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS" + File.separator + "ShowImage");

        if (!file.exists()) {
            file.mkdirs();
        }

        File[] files = file.listFiles();
        final int no_image = files.length;

        if (no_image == 0) {
            Toast.makeText(this, "数据库中没有图像！", Toast.LENGTH_LONG).show();
            return;
        }

        File f = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator + "WR_LPAIS" + File.separator + "txt" + File.separator + "feature.txt");

        if (!f.exists()) {
            Toast.makeText(this, "特征文件不存在！", Toast.LENGTH_LONG).show();
            return;
        }

        int no_writer = 0;
        try {
            no_writer = getTextLines(f.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (no_writer != no_image | no_writer != mList.size()) {
            Toast.makeText(this, "数据库中图像和书写人信息不匹配，系统错误！", Toast.LENGTH_LONG).show();
            return;
        }

        processActivity.IImage[] iamgeID = new processActivity.IImage[no_writer];
        for (int i = 0; i < no_writer; i++) {
            iamgeID[i] = new processActivity.IImage();
        }
        float[][] base = new float[no_writer][920];

        if (no_writer > 0) {
            try {
                getALLLines(f.toString(), no_writer, base, iamgeID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        boolean[] flag = new boolean[no_writer];
        boolean newdosomething = false;
        for (int j = 0; j < no_writer; j++) {
//            bean = new Bean(getResources().getDrawable(R.drawable.icon), "title" + j, "info" + j, false);
            flag[j] = false;
            if (mList.get(j).getIsChcked() == true) {
                flag[j] = true;
                newdosomething = true;

                name = iamgeID[j].name;
                num = iamgeID[j].ID;
                if (name.length() < 3) {
                    int a = 3 - name.length();
                    while (a != 0) {
                        name += "__";
                        a--;
                    }
                }
                num = String.valueOf(Integer.valueOf(num));
                while (num.length() < 6) {
                    StringBuilder SB = new StringBuilder(num);
                    SB.insert(0, '0');
                    num = SB.toString();
                }
                String temps = file.toString() + File.separator + name + num + ".jpg";
                String Anothertemps=Anotherfile.toString() + File.separator + name + num + ".jpg";;

                    //String temps = file.toString()+File.separator+iamgeID[j].ID+".jpg";
                    //String temps = file.toString()+File.separator+iamgeID[j].ID+".jpg";
                    File dfile = new File(temps);
                    File Anotherdfile=new File(Anothertemps);
                    boolean aaflag = false;
                    if (dfile.isFile()) {
                        aaflag = dfile.delete();
                        Anotherdfile.delete();
                    }
                    if (!aaflag) {
                        Toast.makeText(this, files[j].getName() + "删除失败！", Toast.LENGTH_LONG).show();
                        flag[j] = false;
                    }
                }
            }
            if (newdosomething) {
                File txtfile = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator + "WR_LPAIS" + File.separator + "txt");

                if (!txtfile.exists()) {
                    txtfile.mkdirs();
                }

                File tf = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator + "WR_LPAIS" + File.separator + "txt" + File.separator + "feature.txt");
                FileWriter fw = null;
                try {
                    fw = new FileWriter(tf, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                PrintWriter pw = new PrintWriter(fw);
                for (int j = 0; j < no_writer; j++) {
                    if (!flag[j]) {
                        pw.print(iamgeID[j].ID + " ");
                        pw.print(iamgeID[j].name + " ");
                        for (int ii = 0; ii < 920; ii++) {
                            String temp = String.format("%-6f", base[j][ii]);
                            pw.print(temp + " ");
                        }
                        pw.print("\r\n");
                        pw.flush();
                    }
                }

                try {
                    fw.flush();
                    pw.close();
                    fw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            Toast.makeText(this, "完成删除！", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(chooseImage.this, functionMain.class);
            startActivity(intent);
        }
    }
