package com.siam.tenhour;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

abstract class BaseActivity extends Activity {
    protected FrameLayout root;
    protected LinearLayout page;
    private Typeface regular, semi;

    protected void setup(FrameLayout container, boolean fullscreen) {
        root = container;
        root.removeAllViews();
        if (fullscreen) getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        root.setBackgroundResource(R.drawable.bg_gradient);
        root.addView(new BubbleView(), new FrameLayout.LayoutParams(-1, -1));
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        root.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(20);
        page.setPadding(pad, fullscreen ? dp(24) : dp(18), pad, dp(28));
        scroll.addView(page, new ScrollView.LayoutParams(-1, -2));
        regular = Typeface.createFromAsset(getAssets(), "fonts/HindSiliguri-Regular.ttf");
        semi = Typeface.createFromAsset(getAssets(), "fonts/HindSiliguri-SemiBold.ttf");
    }

    protected int dp(float n) { return (int) (n * getResources().getDisplayMetrics().density + 0.5f); }
    protected int ink() { return getColor(R.color.ink); }
    protected TextView text(String value, float size, boolean bold) {
        TextView v = new TextView(this); v.setText(value); v.setTextSize(size); v.setTextColor(ink());
        v.setTypeface(bold ? semi : regular); v.setGravity(Gravity.CENTER_VERTICAL); return v;
    }
    protected TextView heading(String value) { TextView v = text(value, 24, true); v.setPadding(0, dp(4), 0, dp(8)); return v; }
    protected LinearLayout card() {
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(18), dp(16), dp(18), dp(16));
        box.setBackgroundResource(R.drawable.glass_card); box.setElevation(dp(8));
        return box;
    }
    protected LinearLayout row() { LinearLayout r = new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL); r.setGravity(Gravity.CENTER_VERTICAL); return r; }
    protected TextView action(String value) {
        TextView v = text(value, 17, true); v.setTextColor(0xffffffff); v.setGravity(Gravity.CENTER);
        v.setBackgroundResource(R.drawable.btn_gradient); v.setMinHeight(dp(52)); v.setPadding(dp(18), dp(8), dp(18), dp(8));
        v.setClickable(true); v.setFocusable(true); return v;
    }
    protected TextView outlineAction(String value) {
        TextView v = text(value, 15, true); v.setGravity(Gravity.CENTER); v.setPadding(dp(14), dp(10), dp(14), dp(10));
        GradientDrawable bg = new GradientDrawable(); bg.setColor(0x55ffffff); bg.setCornerRadius(dp(18)); bg.setStroke(dp(1), 0xaaffffff); v.setBackground(bg);
        v.setClickable(true); v.setFocusable(true); return v;
    }
    protected void gap(LinearLayout into, int h) { View s = new View(this); into.addView(s, new LinearLayout.LayoutParams(1, dp(h))); }
    protected void addBottomNav(View.OnClickListener home, View.OnClickListener timer, View.OnClickListener chart, View.OnClickListener profile) {
        LinearLayout nav = row(); nav.setGravity(Gravity.CENTER); nav.setPadding(dp(7), dp(8), dp(7), dp(8)); nav.setBackgroundResource(R.drawable.glass_card);
        int[] ids = {R.drawable.ic_home, R.drawable.ic_clock, R.drawable.ic_play, R.drawable.ic_chart, R.drawable.ic_user};
        String[] labels = {"হোম", "সময়", "শুরু", "রিপোর্ট", "প্রোফাইল"};
        View.OnClickListener[] listeners = {home, timer, timer, chart, profile};
        for (int i = 0; i < ids.length; i++) {
            LinearLayout item = new LinearLayout(this); item.setOrientation(LinearLayout.VERTICAL); item.setGravity(Gravity.CENTER);
            ImageView icon = new ImageView(this); icon.setImageResource(ids[i]);
            int sz = i == 2 ? dp(36) : dp(24); LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(sz, sz); item.addView(icon, ip);
            TextView label = text(labels[i], 11, true); label.setGravity(Gravity.CENTER); item.addView(label);
            if (i == 2) { GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffF472B6, 0xffA855F7}); g.setShape(GradientDrawable.OVAL); item.setBackground(g); label.setTextColor(0xffffffff); }
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(54), 1); nav.addView(item, p); item.setOnClickListener(listeners[i]);
        }
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(-1, -2); np.topMargin = dp(20); page.addView(nav, np);
    }
    protected void navigate(Class<?> cls) { startActivity(new android.content.Intent(this, cls)); }
    protected void styleSwitch(android.widget.CompoundButton v) { v.setTypeface(semi); v.setTextColor(ink()); }

    private class BubbleView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float phase = 0;
        BubbleView() { super(BaseActivity.this); setWillNotDraw(false); setClickable(false); }
        @Override protected void onDraw(Canvas c) {
            super.onDraw(c); float w = getWidth(), h = getHeight();
            paint.setColor(0x31ffffff);
            c.drawCircle(w * .14f + (float)Math.sin(phase) * dp(8), h * .09f, dp(54), paint);
            c.drawCircle(w * .88f, h * .22f + (float)Math.cos(phase) * dp(8), dp(32), paint);
            c.drawCircle(w * .76f, h * .78f, dp(46), paint);
            phase += .025f; postInvalidateDelayed(80);
        }
    }
}
