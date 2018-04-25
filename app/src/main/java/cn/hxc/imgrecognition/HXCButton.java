package cn.hxc.imgrecognition;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.hxc.imgrecognitionSRI_OCR.R;

/**
 * Created by hxc on 2017/5/20.
 */

public class HXCButton extends RelativeLayout {
    private ImageView btn;
    private TextView title;

    private Drawable buttonBackground;
    private int buttonWidth;
    private int buttonHeight;
    private int buttonId;

    private String titleText;
    private float titleTextSize;
    private int titleTextColor;
    private int titleId;

    /*
     * �����ؼ��Ĳ�������
     */
    private LayoutParams btnLayoutParams;
    private LayoutParams textViewLayoutParams;

    private ButtonClickLisener lisener;

    public interface ButtonClickLisener{
        void myClick();
    }
   public void setButtonClickLisener(ButtonClickLisener lisener){
       this.lisener=lisener;
    }

    public HXCButton(Context context, AttributeSet attrs) {
        super(context, attrs);
//        LayoutInflater.from(context).inflate(R.layout.hxc_button,this);
//        title= (TextView) findViewById(R.id.title_tw);
//        btn= (Button) findViewById(R.id.top_btn);
        TypedArray attributes=context.obtainStyledAttributes(attrs, R.styleable.HXCButton);

        buttonBackground=attributes.getDrawable(R.styleable.HXCButton_buttonBackground);
        buttonWidth=(int)attributes.getDimension(R.styleable.HXCButton_buttonWidth,50);
        buttonHeight= (int) attributes.getDimension(R.styleable.HXCButton_buttonHeight,50);
        buttonId=(int)attributes.getDimension(R.styleable.HXCButton_buttonId,1);

        titleText=attributes.getString(R.styleable.HXCButton_titleText);
        titleTextSize=attributes.getDimension(R.styleable.HXCButton_titleTextSize,18);
        titleTextColor=attributes.getInt(R.styleable.HXCButton_titleTextColor,Color.BLACK);
        titleId= (int) attributes.getDimension(R.styleable.HXCButton_titleId,2);

        attributes.recycle();

        title=new TextView(context);
        btn=new ImageView(context);
        btn.setId(R.id.my_view);

        btn.setBackground(buttonBackground);
        title.setText(titleText);
        title.setTextSize(titleTextSize);
        title.setTextColor(titleTextColor);

        //ʵ������ť�Ĳ�������
        btnLayoutParams = new LayoutParams(buttonWidth,buttonHeight);
        btnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,TRUE);
        btnLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,TRUE);
        //����ť��ӵ����Զ���ؼ���
        addView(btn,btnLayoutParams);

        //ͬ��
        textViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,TRUE);
        textViewLayoutParams.addRule(RelativeLayout.BELOW,btn.getId());
        addView(title,textViewLayoutParams);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                btn.getBackground().setColorFilter(0x44000000, PorterDuff.Mode.SRC_ATOP);
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                btn.getBackground().setColorFilter(null);
                lisener.myClick();
                break;
        }
        return true;


    }
}
