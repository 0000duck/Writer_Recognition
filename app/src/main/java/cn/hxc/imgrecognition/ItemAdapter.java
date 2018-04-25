package cn.hxc.imgrecognition;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import cn.hxc.imgrecognitionSRI_OCR.R;

/**
 * Created by 刘欢 on 2018/4/21.
 */

public class ItemAdapter extends ArrayAdapter<Item> {
    private int layoutId;

    public ItemAdapter(Context context, int layoutId, List<Item> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.item_img);
        TextView textView = (TextView) view.findViewById(R.id.item_text);
        //根据图片的地址decode出图片并转化成Bitmap格式并绑定。
        imageView.setImageBitmap(BitmapFactory.decodeFile(item.getPicPath()));
        textView.setText(item.getTxtContent());

        return view;
    }
}
