package com.sprayme.teamrsm.analyticspraydown.uicomponents;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Region;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.sprayme.teamrsm.analyticspraydown.R;
import com.sprayme.teamrsm.analyticspraydown.models.Pyramid;
import com.sprayme.teamrsm.analyticspraydown.models.PyramidStep;
import com.sprayme.teamrsm.analyticspraydown.models.Tick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by climbak on 10/8/17.
 */

public class SprayamidView extends View {
  private Pyramid pyramid;
  private List<PyramidRegion> clickRegions = new ArrayList<>();

  public SprayamidView(Context context) {
    super(context);
  }

  public SprayamidView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  protected void onDraw(Canvas canvas) {
    if (pyramid == null || pyramid.getSteps() == null)
      return;

    clickRegions.clear();

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

    drawPyramid(canvas, centerX, centerY, vSteps, vStepSize, hStepSize, gradeLabelSize);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    switch(event.getAction())
    {
      case MotionEvent.ACTION_DOWN: {
        for (PyramidRegion region : clickRegions) {
          if (region.contains((int)event.getX(), (int)event.getY())) {
            Tick tick = region.getTick();
            if (tick != null && tick.getRoute() != null)
              Toast.makeText(getContext(), region.getTick().getRoute().getName(), Toast.LENGTH_SHORT).show();
          }
        }
      }
    }
    return true;
  }

  @Override
  public boolean performClick(){
    return false;
  }

  private void drawPyramid(Canvas canvas, int centerX, int centerY, int vStepCount, int vStepSize, int hStepSize, int labelSize) {
    // draw each level
    PyramidStep[] steps = pyramid.getSteps();
    for (int i = 0; i < vStepCount; i++) {
      int rectCount = steps[i].getSize();
      int y = i * vStepSize + 4;
      int startingX = centerX - (hStepSize * rectCount / 2);
      for (int j = 0; j < rectCount; j++) {
        int x = startingX + (j * hStepSize) + 4;
        int width = hStepSize - 4;
        int height = vStepSize - 4;
        ShapeDrawable rect = new ShapeDrawable(new RectShape());
        rect.setBounds(x, y, x + width, y + height);
        Tick tick = steps[i].getAt(j);
        if (steps[i].getAt(j) != null) {
          switch (tick.getType()) {
            case Redpoint: rect.getPaint().setColor(getResources().getColor(R.color.pyramidTickRedpoint)); break;
            case Onsight: rect.getPaint().setColor(getResources().getColor(R.color.pyramidTickOnsight)); break;
            case Flash: rect.getPaint().setColor(getResources().getColor(R.color.pyramidTickFlash)); break;
            case Toprope:
            case Follow: rect.getPaint().setColor(getResources().getColor(R.color.pyramidTickToprope)); break;
            case Fell: rect.getPaint().setColor(getResources().getColor(R.color.pyramidTickFell)); break;
            case Unknown: rect.getPaint().setColor(getResources().getColor(R.color.pyramidTickUnknown)); break;
            case Solo: rect.getPaint().setColor(getResources().getColor(R.color.pyramidTickSolo)); break;
            case Pinkpoint: rect.getPaint().setColor(getResources().getColor(R.color.pyramidTickPinkpoint)); break;
          }
        }
        rect.draw(canvas);
        clickRegions.add(new PyramidRegion(x, y, x + width, y + height, steps[i].getAt(j)));
      }

      // draw text labels
      String label = steps[i].getGrade().toShortString();
      //StaticLayout staticLayout = new StaticLayout(label, textPaint, (int) width, Layout.Alignment., 1.0f, 0, false);
      //staticLayout.draw(canvas);
      float labelX = centerX + (hStepSize * pyramid.getWidth() / (float) 2) + (hStepSize / (float) 2);
      float labelY = (i + (float) 0.7) * vStepSize;
      drawLabel(canvas, label, labelSize, labelX, labelY);
    }
  }

  private void drawLabel(Canvas canvas, String label, int labelSize, float labelX, float labelY) {
    TextPaint textPaint = new TextPaint();
    textPaint.setAntiAlias(true);
    textPaint.setTextSize(labelSize);
    textPaint.setColor(0xFF000000);

    int width = (int) textPaint.measureText(label);

    canvas.drawText(label, labelX, labelY, textPaint);
  }

  public void setPyramid(Pyramid pyramid) {
    this.pyramid = pyramid;
  }
}
