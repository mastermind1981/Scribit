package scribit.sjbodzo.com.scribit;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


public class ChallengeTracker extends View {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = Color.BLACK;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    private int sWidth, sHeight;
    private long startTime;

    //timing for animation
    int framesPerSecond = 60;
    long animationDuration = 10000;

    public ChallengeTracker(Context context, AttributeSet attrs) {
        super(context, attrs);
        //getScreenResolution(context);
        //setupDrawing();

        //this.startTime = System.currentTimeMillis();
        //this.postInvalidate();
    }
/**
    private void getScreenResolution(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        sWidth = metrics.widthPixels;
        sHeight = metrics.heightPixels;
        Log.d("METRICS", "width: " + sWidth + " height: " + sHeight);
    }

    private void setupDrawing(){
        //get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth((float).01*sWidth);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long elapsedTime = System.currentTimeMillis() - startTime;

        //draw view
        //canvas.drawPath(drawPath, drawPaint);

        //draw central line initially
        drawPath.moveTo((float) .50*sWidth, (float) .15*sHeight);
        drawPath.lineTo((float) .50*sWidth, (float) .20*sHeight);
        drawPaint.setColor(0xFF00FF00);
        drawPath.lineTo((float) .50*sWidth, (float) .30*sHeight);
        drawPath.addCircle((float)( .50*sWidth),
                (float)( .335*sHeight),
                (float)(.05*sWidth), Path.Direction.CW);

        //draw left branch off central path
        drawPath.moveTo((float) .50*sWidth, (float) .20*sHeight);
        drawPaint.setColor(0xFFFF0000);
        drawPath.lineTo((float) .20*sWidth, (float) .20*sHeight);
        drawPath.lineTo((float) .20*sWidth, (float) .30*sHeight);
        drawPath.addCircle((float)( .20*sWidth),
                (float)( .335*sHeight),
                (float)(.05*sWidth), Path.Direction.CW);

        //draw right branch off central path
        drawPath.moveTo((float) .50*sWidth, (float) .20*sHeight);
        drawPaint.setColor(0xFF0000FF);
        drawPath.lineTo((float).80*sWidth, (float) .20*sHeight);
        drawPath.lineTo((float).80*sWidth, (float) .30*sHeight);
        drawPath.addCircle((float)( .80*sWidth),
                (float)( .335*sHeight),
                (float)(.05*sWidth), Path.Direction.CW);

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        drawPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("METRICS", "MOVING TO " + sWidth/2 + ", " + sHeight/10);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("METRICS", "LINE TO " + sWidth / 2 + ", " + sHeight / 10);
                break;
            case MotionEvent.ACTION_UP:
                Log.e("METRICS", "DRAWING TO " + sWidth / 2 + ", " + sHeight / 10);
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }
    **/
}
