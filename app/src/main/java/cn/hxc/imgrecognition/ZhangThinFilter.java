package cn.hxc.imgrecognition;

/**
 * Created by 刘欢 on 2017/11/18.
 */

/*
P9   P2   P3

P8   P1   P4

P7   P6   P5
 */
public class ZhangThinFilter {

    //Zhang算法的步骤一
    public boolean ZhangStep1(int[] by, int[] step,int height, int width) {
        boolean goon=false;
        int np;   //中心点P1临近8点中为1的个数
        int sp;   //P2->P9->P2中从0—1的变化次数  0表示背景(255)，1表示前景(0)
        int[] neighbor = new int[8]; //存储邻域
        for(int i=0;i<height;i++)
            for(int j=0;j<width;j++){
                step[i*width+j]=by[i*width+j];
            }

        for (int i = 1; i < height - 1; i++){
            for (int j = 1; j < width-1; j++) {
                np = 0;
                sp = 0;

                //中心点必须是前景点
                if (by[i*width+j]==255) continue;

                //将8个邻域复制
                neighbor[0] = by[(i-1)*width+j];       //P2
                neighbor[1] = by[(i-1)*width+j+1];   //P3
                neighbor[2] = by[i*width+j+1];       //P4
                neighbor[3] = by[(i+1)*width+j+1];   //P5
                neighbor[4] = by[(i+1)*width+j];       //P6
                neighbor[5] = by[(i+1)*width+j-1];   //P7
                neighbor[6] = by[i*width+j-1];       //P8
                neighbor[7] = by[(i-1)*width+j-1];   //P9

                //计算中心点邻域一共有几个像素
                for (int m = 0; m < 8; m++){
                    if(neighbor[m]==0)
                        np ++;
                }

                if (np < 2 || np > 6) continue;

                //计算P2->P9中0-1像素转换的数目
                for (int m = 0; m < 7; m++) {
                    if (neighbor[m] - neighbor[m + 1] == 255)
                        sp++;
                }

                if (neighbor[7] - neighbor[0] == 255)
                    sp++;

                if (sp != 1) continue;

                //条件34

                if (neighbor[0]==0 && neighbor[2]==0 && neighbor[4] == 0) continue;
                if (neighbor[2]==0 && neighbor[4]==0 && neighbor[6] == 0) continue;

                step[i*width+j]=255;
                goon=true;
            }
        }
        return goon;
    }

    //Zhang算法的步骤二
    public boolean ZhangStep2(int[] by, int[] step,int height, int width){
        boolean goon=false;
        int np;                           //中心点P1临近8点中为1的个数
        int sp;                           //P2->P9中从0—1的变化次数
        int[] neighbor = new int[8];      //存储邻域
        for(int i=0;i<height;i++)
            for(int j=0;j<width;j++){
            step[i*width+j]=by[i*width+j];
        }

        for (int i = 1; i < height - 1; i++){
            for (int j = 1; j < width-1; j++) {
                np = 0;
                sp = 0;
                if (step[i*width+j] == 255) continue;

                neighbor[0] = step[(i-1)*width+j];       //P2
                neighbor[1] = step[(i-1)*width+j+1];   //P3
                neighbor[2] = step[i*width+j+1];       //P4
                neighbor[3] = step[(i+1)*width+j+1];   //P5
                neighbor[4] = step[(i+1)*width+j];       //P6
                neighbor[5] = step[(i+1)*width+j-1];   //P7
                neighbor[6] = step[i*width+j-1];       //P8
                neighbor[7] = step[(i-1)*width+j-1];   //P9

                for (int m = 0; m < 8; m++){
                    if(neighbor[m]==0)
                        np ++;
                }

                if (np < 2 || np > 6) continue;

                for (int m = 0; m < 7; m++) {
                    if (neighbor[m] - neighbor[m + 1] == 255)
                        sp++;
                }

                if (neighbor[7] - neighbor[0] == 255)
                    sp++;

                if (sp != 1) continue;

                if (neighbor[0]==0 && neighbor[2]==0 && neighbor[6] == 0) continue;
                if (neighbor[0]==0 && neighbor[4]==0 && neighbor[6] == 0) continue;

                step[i*width+j]=255;
                goon=true;
            }
        }
        return goon;
    }

    //将标记的可删除的点全部删除
    public void deletepixel(int[] by,int[] step,int height,int width){
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                if(step[i*width+j]==255)
                    by[i*width+j]=255;
            }
        }
    }
    //------------------------------------------------
}

