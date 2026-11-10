package com.siam.tenhour;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

final class ConfettiView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int[] colors = {0xffF472B6, 0xffA855F7, 0xffE9D5FF};
    private long start = System.currentTimeMillis();
    ConfettiView(Context c) { super(c); setClickable(false); }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas); long elapsed = (System.currentTimeMillis() - start) % 4500; float density = getResources().getDisplayMetrics().density;
        for (int i = 0; i < 22; i++) {
            float x = (i * 83f + (float)Math.sin(elapsed / 450.0 + i) * 20f) % Math.max(1, getWidth());
            float y = (elapsed * (0.035f + (i % 4) * .009f) + i * 37f) % Math.max(1, getHeight());
            paint.setColor(colors[i % colors.length]); paint.setAlpha(180); canvas.save(); canvas.rotate((elapsed / 18f + i * 19f) % 360, x, y);
            canvas.drawRoundRect(x, y, x + 5 * density, y + 10 * density, 2 * density, 2 * density, paint); canvas.restore();
        }
        if (elapsed < 3300) postInvalidateDelayed(90);
    }
}
