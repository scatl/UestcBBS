package com.scatl.uestcbbs.custom.imageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;


public class RoundedImageView extends AppCompatImageView {

    float width, height;

    private String absolutePath;;

    private int img_round_angle = 20;

    public RoundedImageView(Context context) {
        this(context, null);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (width >= 100 && height > 30) {
            Path path = new Path();
            //四个圆角
            path.moveTo(img_round_angle, 0);
            path.lineTo(width - img_round_angle, 0);
            path.quadTo(width, 0, width, img_round_angle);
            path.lineTo(width, height - img_round_angle);
            path.quadTo(width, height, width - img_round_angle, height);
            path.lineTo(img_round_angle, height);
            path.quadTo(0, height, 0, height - img_round_angle);
            path.lineTo(0, img_round_angle);
            path.quadTo(0, 0, img_round_angle, 0);

            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

}
