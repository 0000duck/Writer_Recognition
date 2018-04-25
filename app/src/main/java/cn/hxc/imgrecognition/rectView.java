package cn.hxc.imgrecognition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

public class rectView extends View {

	private Paint linePaint;
	private Paint rectPaint;
	public rectView(Context context) {
		super(context);

		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(Color.BLUE);
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeWidth(3f);
		linePaint.setAlpha(80);
		
		
		rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rectPaint.setColor(Color.YELLOW);
		rectPaint.setStyle(Style.FILL);
		rectPaint.setAlpha(20);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		
		canvas.drawRect(0,0,50,50, this.rectPaint);		
		//canvas.drawRect(w, h, w+maskWidth, h+maskHeight, this.linePaint);
		
		super.onDraw(canvas);
	}

}
