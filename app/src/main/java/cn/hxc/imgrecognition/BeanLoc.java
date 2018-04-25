package cn.hxc.imgrecognition;

/**
 * Created by 刘欢 on 2018/3/7.
 */

public class BeanLoc {
    private String imgID;
    private String sxr;
    private String cjr;
    private String sctime;
    private String imgPath;

    public BeanLoc(String imgID,String sxr,String cjr,String sctime,String imgPath){
        this.imgID=imgID;
        this.sxr=sxr;
        this.cjr=cjr;
        this.sctime=sctime;
        this.imgPath=imgPath;
    }

    public String getSxr() {
        return sxr;
    }

    public String getCjr() {
        return cjr;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getImgID() {
        return imgID;
    }

    public String getSctime() {
        return sctime;
    }
}
