package com.example.adoption_pet.UI.Widget.qrCode;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.example.adoption_pet.R;


public class ScannerOverlay extends ViewGroup {
    private float mEndY;
    private float mLeft;
    private float mRight;
    private float mTop;
    private float mMarginLeft;
    private float mMarginRight;
    private float mRectWidth;
    private float mRectHeight;
    private int mFrames;
    private boolean mAnimation;
    private final Paint mEraser = new Paint();
    private final Paint mLine = new Paint();

    public ScannerOverlay(Context context) {
        super(context);
    }

    public ScannerOverlay(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScannerOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ScannerOverlay,
                0, 0);
        mRectWidth = a.getDimensionPixelSize(R.styleable.ScannerOverlay_square_width, 0);
        mRectHeight = a.getDimensionPixelSize(R.styleable.ScannerOverlay_square_height, 0);
        mMarginLeft = a.getDimensionPixelSize(R.styleable.ScannerOverlay_square_margin_left, 0);
        mMarginRight = a.getDimensionPixelSize(R.styleable.ScannerOverlay_square_margin_right, 0);
        int lineColor = a.getColor(R.styleable.ScannerOverlay_line_color,
                ContextCompat.getColor(context, R.color.pink));
        int lineWidth = a.getInteger(R.styleable.ScannerOverlay_line_width, 4);
        mFrames = a.getInteger(R.styleable.ScannerOverlay_line_speed, 5);

        mEraser.setAntiAlias(true);
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mLine.setColor(lineColor);
        mLine.setStrokeWidth((float) lineWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        if (mRectWidth == -1) {
            mRectWidth = widthMeasureSpec;
        }
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mLeft = (w - mRectWidth) / 2;
        mRight = w - mMarginRight;
        mTop = (h - mRectHeight) / 2;
        mEndY = mTop;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw transparent rect
        int cornerRadius = 0;
        canvas.drawRoundRect(mMarginLeft, mTop, mRight, mRectHeight + mTop,
                (float) cornerRadius, (float) cornerRadius, mEraser);

        // draw the line to product animation
        if (mEndY >= mTop + mRectHeight + mFrames) {
            mAnimation = true;
        } else if (mEndY == mTop + mFrames) {
            mAnimation = false;
        }

        // check if the line has reached to bottom
        if (mAnimation) {
            mEndY -= mFrames;
        } else {
            mEndY += mFrames;
        }
        canvas.drawLine(mMarginLeft, mEndY, mLeft * 2 - mMarginRight, mEndY, mLine);
        invalidate();
    }
}