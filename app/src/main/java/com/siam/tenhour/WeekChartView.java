package com.siam.tenhour;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

final class WeekChartView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final long[] hours;
    private final String[] days = {"সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র", "শনি", "রবি"};
    WeekChartView(Context c, long[] hours) {
        super(c); this.hours = hours;
        p.setTypeface(android.graphics.Typeface.createFromAsset(c.getAssets(), "fonts/HindSiliguri-Regular.ttf"));
    }
    @Override protected void onDraw(Canvas c) {
        super.onDraw(c); float w = getWidth(), h = getHeight(); float left = 12, top = 16, bottom = h - 32;
        p.setColor(0x55A855F7); p.setStrokeWidth(1.5f);
        for (int i = 1; i <= 4; i++) { float y = top + (bottom - top) * i / 4f; c.drawLine(left, y, w - left, y, p); }
        long max = 10 * 60 * 60 * 1000L; float slot = (w - 2 * left) / 7f;
        for (int i = 0; i < 7; i++) {
            float x = left + i * slot + slot * .20f; float bw = slot * .58f;
            float ratio = Math.min(1f, hours[i] / (float) max); float bh = (bottom - top) * ratio;
            p.setColor(i == 6 ? 0xffA855F7 : 0xccF472B6);
            c.drawRoundRect(new RectF(x, bottom - bh, x + bw, bottom), bw / 2, bw / 2, p);
            p.setColor(0xff1E1B2E); p.setTextSize(10 * getResources().getDisplayMetrics().density); p.setTextAlign(Paint.Align.CENTER);
            String day = new String[]{"সো", "মঙ্গ", "বু", "বৃহ", "শু", "শনি", "রবি"}[i]; c.drawText(day, x + bw / 2, h - 8, p);
        }
    }
}
