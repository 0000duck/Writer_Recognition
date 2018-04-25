package cn.hxc.imgrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.hxc.imgrecognitionSRI_OCR.R;

public class BeanAdapter extends BaseAdapter
{
    private List<Bean> mList;
    private Context mContext;

    public BeanAdapter(List<Bean> mList, Context mContext)
    {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item, null);
            holder = new ViewHolder();
            holder.icon = (ImageView)convertView.findViewById(R.id.item_icon);
            holder.cb = (CheckBox)convertView.findViewById(R.id.item_cb);


            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        final Bean bean = mList.get(position);
        Bitmap b3 = BitmapFactory.decodeFile(bean.getDrawable());
        holder.icon.setImageBitmap(b3);

        holder.cb.setChecked(bean.getIsChcked());
        holder.cb.setText(bean.getTitle()+" "+bean.getInfo());
        return convertView;
    }

    public View getView(final int position, View convertView){

        return convertView;
    }
}

class ViewHolder
{
    public ImageView icon;
    public CheckBox cb;
}

