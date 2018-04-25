package cn.hxc.imgrecognition;

import android.graphics.Bitmap;

import java.util.PriorityQueue;

/**
 * Created by 刘欢 on 2018/3/7.
 */

public class Icon {
    private String imgID;
    private String sxr;
    private String cjr;
    private String sctime;
    private String mypic;
    private Bitmap bm;

    public Icon(){}

    public Icon(String imgID,String sxr,String cjr,String sctime,String mypic,Bitmap bm){
        this.imgID=imgID;
        this.sxr=sxr;
        this.cjr=cjr;
        this.sctime=sctime;
        this.mypic=mypic;
        this.bm=bm;
    }

    public String getImgID() {
        return imgID;
    }

    public String getSxr() {
        return sxr;
    }

    public String getCjr() {
        return cjr;
    }

    public String getSctime() {
        return sctime;
    }

    public Bitmap getBm() {
        return bm;
    }

    public String getMypic() {
        return mypic;
    }

    public void setImgID(String imgID) {
        this.imgID = imgID;
    }

    public void setSctime(String sctime) {
        this.sctime = sctime;
    }

    public void setCjr(String cjr) {
        this.cjr = cjr;
    }

    public void setMypic(String mypic) {
        this.mypic = mypic;
    }

    public void setSxr(String sxr) {
        this.sxr = sxr;
    }

    public void setBm(Bitmap bm) {
        this.bm = bm;
    }
}




