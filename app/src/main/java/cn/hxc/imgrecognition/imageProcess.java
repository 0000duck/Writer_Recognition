
package cn.hxc.imgrecognition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.provider.MediaStore.Images;
import android.widget.AbsListView.RecyclerListener;

import java.io.File;

public class imageProcess {
    public Bitmap RGBToGrey(Bitmap bitmap) {

        Bitmap matrixBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(matrixBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        // 传入一个大于1的数字将增加饱和度，而传入一个0～1之间的数字会减少饱和度。0值将产生一幅灰度图像
        // Android ColorMatrix 默认的灰阶计算采用下面的BT709标准
        colorMatrix.setSaturation(0f);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(
                colorMatrix);
        paint.setColorFilter(colorMatrixColorFilter);
        canvas.drawBitmap(bitmap, 0f, 0f, paint);        
        return matrixBitmap;

    }

    public static Bitmap greyToArray(Bitmap bitmap) {
        int sgray[] = new int[256];
        for (int i = 0; i < 256; i++) {
            sgray[i] = 0;
        }

        double sum = 0;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Raster ra = bitmap.getData();
        /*
         * 图像的类型，看看它是多少位的.如果是32位 的要考虑aphal值通道,通过Raster对象来读取/写入像素， 它自动帮你处理成为32位的.
         */

        /*
         * Rectangle rect = ra.getBounds(); int w = (int) rect.getWidth(); int h
         * = (int) rect.getHeight();
         */
        // System.out.println(width + ":" + height);
        // System.out.println(w + ":" + h);

        int pixels[] = new int[width * height];
        int greyPixels[] = new int[width * height];
        // bitmap.getPixels(0, 0, width, height, pixels); //获得图片每个点的像素
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int k = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // int rgb = bi.getRGB(i, j);
                int rgb = pixels[i * height+j];

                /*
                 * 应为使用getRGB(i,j)获取的该点的颜色值是ARGB，
                 * 而在实际应用中使用的是RGB，所以需要将ARGB转化成RGB， 即bufImg.getRGB(i, j) &
                 * 0xFFFFFF。
                 */
                int r = (rgb & 0xff0000) >> 16;
                int g = (rgb & 0xff00) >> 8;
                int b = (rgb & 0xff);
              //  int gray = (int) (r * 0.3 + g * 0.59 + b * 0.11); // 计算灰度值
                int gray = (int) (r * 1 + g * 0 + b *0); // 计算灰度值
                int newcolor = 0xff<<24|(gray << 16) | (gray << 8) | (gray);
                greyPixels[i * height + j] = newcolor;
            }
        }
//        for (int i = 0; i < height; i++) {
//			for (int j = 0; j < width; j++) {
//				int gray = greyPixels[i * width + j];
//				int newcolor = (gray << 16) | (gray << 8) | (gray);
//				numPixels[i * width + j] = newcolor;
//			}
//		}

        Bitmap greyBit = Bitmap.createBitmap(greyPixels, 0, width, width, height,
                Bitmap.Config.RGB_565);
        return greyBit;
    }

    public void print(byte[] by) {

        for (int i = 0; i < 100; i++) {
            System.out.println(by[i]);
        }
    }

    public static void print(String s, int[] by) {
//        int k=0;
//        for (int i = 0; i < by.length-1;) {           
//           System.out.println(s + "sign " + by[i++]);
//           System.out.println(s + "minx " + by[i++]);
//           System.out.println(s + "maxx " + by[i++]);
//           System.out.println(s + "miny " + by[i++]);
//           System.out.println(s + "maxy " + by[i++]);
//           System.out.println(k+++"--------------------");
//        }
       for (int j = 0; j < by.length; j++) {
         System.out.println(j+"  "+by[j]);
       }
       
    }

    public static void noequl(String s, int i) {
        System.out.println(s + i);
    }

	public static void noequl(String s, double percent) {
		// TODO Auto-generated method stub
		System.out.println(s + percent);
	}
}

// public native void callcToArray(String path);

