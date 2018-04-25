package cn.hxc.imgrecognition;

import android.graphics.drawable.Drawable;

public class Bean
{
    private String drawable;
    private String title;
    private String info;
    private boolean isChcked;

    public Bean(String drawable, String title, String info, boolean isChcked)
    {
        this.drawable = drawable;
        this.title = title;
        this.info = info;
        this.isChcked = isChcked;
    }

    /*public Bean(String drawable, String title, boolean isChcked)
    {
        this.drawable = drawable;
        this.title = title;
        this.isChcked = isChcked;
    }*/

    public void setIsChcked(boolean isChcked)
    {
        this.isChcked = isChcked;
    }

    public boolean getIsChcked()
    {
        return isChcked;
    }

    public String getInfo()
    {
        return info;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDrawable()
    {
        return drawable;
    }
}
