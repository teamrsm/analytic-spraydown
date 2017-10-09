package com.sprayme.teamrsm.analyticspraydown.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.View;

import com.sprayme.teamrsm.analyticspraydown.models.Pyramid;
import com.sprayme.teamrsm.analyticspraydown.models.PyramidStep;

/**
 * Created by climbak on 10/8/17.
 */

public class SprayamidView extends View {
//    private ShapeDrawable mDrawable;
    private Pyramid pyramid;

    public SprayamidView(Context context) {
        super(context);

//        int x = 10;
//        int y = 10;
//        int width = 300;
//        int height = 50;

//        mDrawable = new ShapeDrawable(new RectShape());
//        // If the color isn't set, the shape uses black as the default.
//        mDrawable.getPaint().setColor(0xff74AC23);
//        // If the bounds aren't set, the shape can't be drawn.
//        mDrawable.setBounds(x, y, x + width, y + height);
    }

    public SprayamidView(Context context, AttributeSet attrs){
        super(context, attrs);

        int x = 10;
        int y = 10;
        int width = 300;
        int height = 50;

//        mDrawable = new ShapeDrawable(new RectShape());
//        // If the color isn't set, the shape uses black as the default.
//        mDrawable.getPaint().setColor(0xff74AC23);
//        // If the bounds aren't set, the shape can't be drawn.
//        mDrawable.setBounds(x, y, x + width, y + height);
    }

    protected void onDraw(Canvas canvas) {
//        mDrawable.draw(canvas);

        if (pyramid == null)
            return;

        // calculate pyramid drawing parameters
        int canvasHeight = canvas.getHeight();
        int canvasWidth = canvas.getWidth();
        int centerX = canvasWidth / 2;
        int centerY = canvasHeight / 2;
        int vSteps = pyramid.getHeight();
        int hSteps = pyramid.getWidth();

        int vStepSize = canvasHeight / vSteps;
        int hStepSize = canvasWidth / hSteps;

        // draw each level
        PyramidStep[] steps = pyramid.getSteps();
        for (int i=0; i<vSteps; i++){
            int rectCount = steps[i].getSize();
            int startingX = centerX - (hStepSize * rectCount / 2);
            int startingY = i * vStepSize;
            for (int j=0; j<rectCount; j++) {
                int x = startingX + (j * hStepSize) + 1;
                int y = startingY + (j * hStepSize) + 1;
                int width = hStepSize - 1;
                int height = vStepSize - 1;
                ShapeDrawable rect = new ShapeDrawable(new RectShape());
                rect.setBounds(x, y, x+width, y+height);
                rect.getPaint().setColor(0xff74AC23);
                rect.draw(canvas);
            }
        }
    }

    public void setPyramid(Pyramid pyramid){
        this.pyramid = pyramid;
    }
}
