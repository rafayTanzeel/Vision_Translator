
package com.translator.tester.visiontranslator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.translator.tester.visiontranslator.camera.GraphicOverlay;

import java.util.List;


public class OCR_GraphicOverlay extends GraphicOverlay.Graphic {

    private int graphicID;
    private static final float STROKE_THICKNESS = 5.0f;
    private static final float TEXT_SIZE = 70.0f;
    private static final int TEXT_COLOR = Color.WHITE;

    private static Paint sRectPaint;
    private static Paint sTextPaint;
    private final TextBlock textBlock;

    OCR_GraphicOverlay(GraphicOverlay overlay, TextBlock text) {
        super(overlay);

        textBlock = text;

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(TEXT_COLOR);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(STROKE_THICKNESS);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(TEXT_COLOR);
            sTextPaint.setTextSize(TEXT_SIZE);
        }
        postInvalidate();
    }

    public int getId() {
        return graphicID;
    }

    public void setId(int id) {
        this.graphicID = id;
    }

    public TextBlock getTextBlock() {
        return textBlock;
    }

    public boolean contains(float x, float y) {
        if (textBlock == null) {
            return false;
        }
        RectF rect = new RectF(textBlock.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        return (rect.left < x && rect.right > x && rect.top < y && rect.bottom > y);
    }

    @Override
    public void draw(Canvas canvas) {
        if (textBlock == null) {
            return;
        }

        RectF rect = new RectF(textBlock.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, sRectPaint);

        List<? extends Text> textComponents = textBlock.getComponents();
        for(Text currentText : textComponents) {
            float left = translateX(currentText.getBoundingBox().left);
            float bottom = translateY(currentText.getBoundingBox().bottom);
            canvas.drawText(currentText.getValue(), left, bottom, sTextPaint);
        }
    }
}
