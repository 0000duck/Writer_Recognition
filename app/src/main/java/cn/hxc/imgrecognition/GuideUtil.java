package cn.hxc.imgrecognition;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;



/**

 */public class GuideUtil {
    private Context context;
    private ImageView imgView;
    private WindowManager windowManager;
    private static GuideUtil instance = null;
    /** �Ƿ��һ�ν���ó��� **/
    private boolean isFirst = true;

    /**����˽�еķ�ʽ��ֻ��֤����ͨ�����������ã�ͬʱ��֤������󲻻���ڶ��**/
    private GuideUtil() {
    }

    /**���õ��������ģʽ��ͬʱ����ͬ����**/
    public static GuideUtil getInstance() {
        synchronized (GuideUtil.class) {
            if (null == instance) {
                instance = new GuideUtil();
            }
        }
        return instance;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case 1:
                // ����LayoutParams����
                final LayoutParams params = new WindowManager.LayoutParams();
                // ������ʾ�����ͣ�TYPE_PHONEָ�������绰��ʱ��ᱻ���ǣ�����ʱ�������ǰ�ˣ���ʾλ����stateBar���棬���������ֵ������ĵ�
                params.type = WindowManager.LayoutParams.TYPE_PHONE;
                // ������ʾ��ʽ
                params.format = PixelFormat.RGBA_8888;
                // ���ö��뷽ʽ
                params.gravity = Gravity.LEFT | Gravity.TOP;
                // ���ÿ��
                WindowManager wm = (WindowManager) context  
                        .getSystemService(Context.WINDOW_SERVICE);  
                DisplayMetrics outMetrics = new DisplayMetrics();  
                wm.getDefaultDisplay().getMetrics(outMetrics);  
                  
                params.width = outMetrics.widthPixels;
                params.height = outMetrics.heightPixels;
                // ���ö���
               // params.windowAnimations = R.style.view_anim;

                // ��ӵ���ǰ�Ĵ�����
                windowManager.addView(imgView, params);
                break;
            }
        };
    };/**
     * @����˵��:��ʼ��
     * @��������:initGuide
     * @param context
     * @param drawableRourcesId������ͼƬ����ԴId
     * @����ֵ:void
     */
    public void initGuide(Activity context, int drawableRourcesId) {
        /**������ǵ�һ�ν���ý���**/
        if (!isFirst) {
            return;
        }
        this.context = context;
        windowManager = context.getWindowManager();

        /** ��̬��ʼ��ͼ��**/
        imgView = new ImageView(context);
        imgView.setLayoutParams(new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        imgView.setScaleType(ScaleType.FIT_XY);
        imgView.setImageResource(drawableRourcesId);
        /**��������������һ��handler�ӳ���ʾ���棬��Ҫ��Ϊ�˽����������ܿ���������ö���Ч������Ȼ�Ļ������������ֱ����ʾ����**/
        handler.sendEmptyMessageDelayed(1, 1000);

        imgView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                /** ���ͼ��֮�󣬽�ͼ���Ƴ�**/
                windowManager.removeView(imgView);
            }
        });
    }

    public boolean isFirst() {
        return isFirst;
    }/**
     * @����˵��:�����Ƿ��һ�ν���ó���
     * @��������:setFirst
     * @param isFirst
     * @����ֵ:void
     */
    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }
}

