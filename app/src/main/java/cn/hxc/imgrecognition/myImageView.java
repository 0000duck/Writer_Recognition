
package cn.hxc.imgrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class myImageView extends ImageView {
    private float newbottom;
    private float newtop;
    private float old_x;
    private float old_y;
    private float new_x;
    private float new_y;
    private boolean isInRect;
    private Canvas canvas;
    private Rect rect;
    private Bitmap bm;

    public myImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnTouchListener(new OnTouchListenerImp());
    }

    private class OnTouchListenerImp implements OnTouchListener {

        @Override
        public boolean onTouch(View arg0, MotionEvent event) {
            // TODO Auto-generated method stub

            return false;
        }

    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        // TODO Auto-generated method stub
        super.setImageBitmap(bm);
        this.bm = bm;

        canvas = new Canvas(bm);
        rect = canvas.getClipBounds();
        newbottom = bm.getHeight() * 3 / 5;
        newtop = bm.getHeight() * 2 / 5;

        drawImage(newtop, newbottom);

    }

    // @Override
    // protected void onDraw(Canvas canvas) {
    // // TODO Auto-generated method stub
    // super.onDraw(canvas);
    // Paint paint=new Paint();
    // paint.setColor(Color.YELLOW);
    // paint.setStrokeWidth(2);
    // paint.setStyle(Paint.Style.STROKE);
    // this.canvas.drawRect(rect, paint);
    //
    // }
    private void drawImage(float newtop2,
            float newbottom2) {
        Canvas canvas11 = new Canvas(bm);
        Rect rect = canvas11.getClipBounds();
        rect.bottom = (int) newbottom2;
        rect.top = (int) newtop2;
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        canvas11.drawRect(rect, paint);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        // TODO Auto-generated method stub
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                onTouchUp(event);
                break;
        }

        return true;
    }

    private void onTouchUp(MotionEvent event) {
        // TODO Auto-generated method stub

    }

    private void onTouchMove(MotionEvent event) {
        if (isInRect == true) {
            // new_x=event.getX();
            new_y = event.getY();
            imageProcess.noequl("move", (int) new_y);
            drawImage(newtop + new_y - old_y, newbottom + new_y - old_y);

        }

    }

    private void onTouchDown(MotionEvent event) {
        // old_x=event.getX();
        old_y = event.getY();

        if (old_y > newtop && old_y < newbottom) {
            isInRect = true;
        }
        else {
            isInRect = false;
        }

    }

}
