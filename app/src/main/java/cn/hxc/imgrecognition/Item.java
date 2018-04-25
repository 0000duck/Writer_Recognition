package cn.hxc.imgrecognition;

/**
 * Created by 刘欢 on 2018/4/21.
 */

public class Item {
    private String txtContent;  //显示的文本内容
    private String picPath;     //要显示的图片的地址

    //构造函数
    public Item(String txtContent, String picPath) {
        this.txtContent = txtContent;
        this.picPath = picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public void setTxtContent(String txtContent) {
        this.txtContent = txtContent;
    }

    public String getPicPath() {
        return picPath;
    }

    public String getTxtContent() {
        return txtContent;
    }
}
