package scribit.sjbodzo.com.scribit;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ChallengeCategoryWidget extends View {
    int strokeColor; //int hex color storage
    int radius; //radius of circle drawn
    boolean hasTextComponent; //flag for text title accompaniment
    String catName, catDesc;
    Typeface catTypeFace;

    public ChallengeCategoryWidget(Context c, AttributeSet attrs) {
        super(c, attrs);
        AssetManager asm = c.getAssets();
        catTypeFace = Typeface.createFromAsset(asm, "fonts/RobotoThin.ttf");
        Log.d("STYLE?", catTypeFace.getStyle() + "");
        TypedArray attributeSet = c.obtainStyledAttributes(attrs, R.styleable.ChallengeCategoryWidget);
        strokeColor = attributeSet.getInteger(R.styleable.ChallengeCategoryWidget_strokeColor, 0xFF0FFF00);
        radius = attributeSet.getInteger(R.styleable.ChallengeCategoryWidget_radius, 22);
        hasTextComponent = attributeSet.getBoolean(R.styleable.ChallengeCategoryWidget_hasTextComponent, true);
        catName = attributeSet.getString(R.styleable.ChallengeCategoryWidget_categoryName);
        catDesc = attributeSet.getString(R.styleable.ChallengeCategoryWidget_categoryDesc);
        attributeSet.recycle(); //free back up for GC
        setWillNotDraw(false); //toggle off to override onDraw properly according to SDK specs

        /**
        if (hasTextComponent) {
            //logic implementation for accompanying TextView
            TextView titleView = new TextView(c); //WHERE DOES THIS ADD BY DEFAULT?!
            titleView.setText(attributeSet.getString(R.styleable.ChallengeCategoryWidget_categoryName));
            titleView.setAllCaps(true);
            titleView.setGravity(2);
         }
         **/
    }

    @Override
    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(strokeColor);
        paint.setTextSize(40);
        paint.setTypeface(catTypeFace);
        paint.setTextScaleX((float)1.3);
        paint.setAntiAlias(true);
        Log.d("GETLEFT, ", getLeft() + "");
        Log.d("GETTOP, ", getTop() + "");
        Log.d("RADIUS", radius + "");
        Log.d("PADDLEFT", getPaddingLeft() + "");
        Log.d("PADDTOP", getPaddingTop() + "");
        Log.d("?????????????", "");
        Log.d("lkjljlj", (getPaddingLeft() + getLeft() + radius) + ".....");
        Log.d("lkjlkjlkj", (getPaddingTop() + getTop() + radius) + ".....");
        canvas.drawCircle(getPaddingLeft() + getLeft() + radius,
                          getPaddingTop() + getTop() + radius, radius, paint); //written this way to respect padding
        paint.setStrokeWidth(3);
        canvas.drawText(catName, (float) 2.4 * radius + getLeft() + getPaddingLeft(),
                                 (float) 0.5 * radius + getTop() + getPaddingTop(), paint);
        paint.setTextSize(30);
        paint.setStrokeWidth(1);
        canvas.drawText(catDesc, (float) 2.4 * radius + getLeft() + getPaddingLeft(),
                                 (float) 1.2 * radius + getTop() + getPaddingTop(), paint);
    }

    protected void onMeasure(int wSpec, int hSpec) {
        int measuredHeight = MeasureSpec.getSize(hSpec);
        int measuredWidth = MeasureSpec.getSize(wSpec);
        Log.d("MeasureSpec.getSize(hSpec)\t", "hSpec = " + hSpec + ", method call : " + measuredHeight);
        Log.d("MeasureSpec.getSize(wSpec)\t", "wSpec = " + wSpec + ", method call : " + measuredWidth);
        Log.d("getSuggMinHeight:  ", getSuggestedMinimumHeight() + "");
        Log.d("getSuggMinWidth:  ", getSuggestedMinimumWidth() + "");
        Log.d("h/w scaled vals : ", (int)(getWidth()*0.8) + ", " + (int)(getHeight()*0.25));
        Log.d("h = 0.2w --->", "..." + " " + (int)measuredWidth*0.2);
        //Log.d("COMP.EXP: ", (radius*2+2*5 > 0.2*measuredWidth) + "");
        //radius = (radius*2+2*5 > 0.2*measuredWidth) ? (int)0.16*measuredWidth : radius; //ensure radius fits
        if (radius > 0.17*measuredWidth) radius = (int)0.17*measuredWidth;
        setMeasuredDimension(measuredWidth, (int)(measuredWidth*0.2));
    }
}


