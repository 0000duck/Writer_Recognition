package cn.hxc.imgrecognition;

import java.util.Arrays;

/**
 * Created by 刘欢 on 2017/11/25.
 */

public class Projection {

    //X轴方向的投影
    public int[] ProjectionX(int[] by, int height, int width) {
        int[] projectionx = new int[width];
        Arrays.fill(projectionx, 0);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (by[j * width + i] == 0) {
                    projectionx[i]++;
                }
            }
        }
        return projectionx;
    }

    //Y轴方向的投影
    public int[] ProjectionY(int[] by, int height, int width) {
        int[] projectiony = new int[height];
        Arrays.fill(projectiony, 0);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (by[i * width + j] == 0) {
                    projectiony[i]++;
                }
            }
        }
        return projectiony;
    }

    /*public int[] ProExtract(int[] Projectiony,int[] Projectionx,int height,int width){
        int begin_y=0;
        int end_y=height-1;
        int begin_x=0;
        int end_x=width-1;
        int[] newPic;
        for(int i=0;i<height;i++){
            if(Projectiony[i]>0)
                begin_y=i;
        }
        for(int j=height-1;j>=0;j++){
            if(Projectiony[j]>0)
                end_y=j;
        }

        for(int i=0;i<width;i++){
            if(Projectionx[i]>0)
                begin_x=i;
        }
        for(int j=width-1;j>=0;j++){
            if(Projectionx[j]>0)
                end_x=j;
        }
        newPic=new int[(end_x-begin_x+1)*(end_y-begin_y+1)];
        return newPic;
    }*/
    public int ProExtractBegin(int[] ProArray,int length){
        int num=0;
        for(int i=0;i<length;i++){
            if(ProArray[i]>0){
                num=i;
                break;
            }
        }
        return num;
    }

    public int ProExtractEnd(int[] ProArray,int length){
        int num=length-1;
        for(int j=length-1;j>=0;j--){
            if(ProArray[j]>0){
                num=j;
                break;
            }
        }
        return num;
    }
}
