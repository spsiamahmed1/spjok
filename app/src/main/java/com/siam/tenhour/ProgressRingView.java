package com.siam.tenhour;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class ProgressRingView extends View {
    private final Paint track = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint arc = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float progress;
    public ProgressRingView(Context context) {
        super(context); setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        track.setStyle(Paint.Style.STROKE); track.setStrokeWidth(dp(12)); track.setColor(0x66ffffff); track.setStrokeCap(Paint.Cap.ROUND);
        arc.setStyle(Paint.Style.STROKE); arc.setStrokeWidth(dp(12)); arc.setStrokeCap(Paint.Cap.ROUND);
        arc.setShader(new android.graphics.LinearGradient(0, 0, dp(220), dp(220), 0xffF472B6, 0xffA855F7, android.graphics.Shader.TileMode.CLAMP));
    }
    private float dp(float n) { return n * getResources().getDisplayMetrics().density; }
    public void setProgress(float fraction) { progress = Math.max(0f, Math.min(1f, fraction)); invalidate(); }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float pad = dp(18); RectF r = new RectF(pad, pad, getWidth() - pad, getHeight() - pad);
        canvas.drawArc(r, -90, 360, false, track); canvas.drawArc(r, -90, 360 * progress, false, arc);
    }
}
