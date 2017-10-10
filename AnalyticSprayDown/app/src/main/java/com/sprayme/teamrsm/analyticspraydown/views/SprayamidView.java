package com.sprayme.teamrsm.analyticspraydown.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
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
        int hStepSize = canvasWidth / (hSteps + 3);
        int gradeLabelSize = (int) (vStepSize * 0.5);

        // draw each level
        PyramidStep[] steps = pyramid.getSteps();
        for (int i=0; i<vSteps; i++){
            int rectCount = steps[i].getSize();
            int y = i * vStepSize + 4;
            int startingX = centerX - (hStepSize * rectCount / 2);
            for (int j=0; j<rectCount; j++) {
                int x = startingX + (j * hStepSize) + 4;
                int width = hStepSize - 4;
                int height = vStepSize - 4;
                ShapeDrawable rect = new ShapeDrawable(new RectShape());
                rect.setBounds(x, y, x+width, y+height);
                if (steps[i].getAt(j) != null)
                    rect.getPaint().setColor(0xff74AC23);
                rect.draw(canvas);
            }

            // draw text labels
            String label = steps[i].getGrade().toShortString();
            TextPaint textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(gradeLabelSize);
            textPaint.setColor(0xFF000000);

            int width = (int) textPaint.measureText(label);
            //StaticLayout staticLayout = new StaticLayout(label, textPaint, (int) width, Layout.Alignment., 1.0f, 0, false);
            //staticLayout.draw(canvas);
            float labelX = centerX + (hStepSize * pyramid.getWidth() / 2) + (hStepSize / 2);
            float labelY = (i+(float)0.7) * vStepSize;
            canvas.drawText(label, labelX, labelY, textPaint);
        }
    }

    public void setPyramid(Pyramid pyramid){
        this.pyramid = pyramid;
    }
}
