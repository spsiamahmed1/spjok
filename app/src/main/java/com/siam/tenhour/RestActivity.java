package com.siam.tenhour;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RestActivity extends BaseActivity {
    private static final long REST = 120_000L;
    private CountDownTimer timer;
    private TextView countdown, waterLabel;
    private ProgressRingView ring;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        com.siam.tenhour.databinding.ActivityRestBinding binding = com.siam.tenhour.databinding.ActivityRestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); setup(binding.getRoot(), true);
        if (!AppDatabase.active(this)) { finish(); return; }
        buildUi(); runTimer();
    }

    private void buildUi() {
        TextView title = heading("বিরতির সময়"); title.setGravity(Gravity.CENTER); page.addView(title);
        TextView message = text("একটি পড়ার ঘণ্টা শেষ হয়েছে। ২ মিনিট বিশ্রাম নিন। পানি পান করুন, চোখকে বিশ্রাম দিন, একটু হাঁটুন।", 16, false);
        message.setGravity(Gravity.CENTER); page.addView(message); gap(page, 20);
        FrameLayout ringBox = new FrameLayout(this); int size = Math.min(getResources().getDisplayMetrics().widthPixels - dp(72), dp(270));
        ring = new ProgressRingView(this); ringBox.addView(ring, new FrameLayout.LayoutParams(-1, -1));
        countdown = text("০২:০০", 36, true); countdown.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(-1, -1); ringBox.addView(countdown, cp);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(size, size); rp.gravity = Gravity.CENTER; page.addView(ringBox, rp);
        gap(page, 18); LinearLayout water = card(); LinearLayout head = row();
        android.widget.ImageView drop = new android.widget.ImageView(this); drop.setImageResource(R.drawable.ic_drop); head.addView(drop, new LinearLayout.LayoutParams(dp(26), dp(26)));
        head.addView(text("পানি পান করেছেন?", 17, true)); water.addView(head);
        waterLabel = text("আজকে " + Bengali.digits(String.valueOf(AppDatabase.water(this))) + " গ্লাস", 15, false); waterLabel.setPadding(0, dp(6), 0, dp(10)); water.addView(waterLabel);
        TextView yes = action("হ্যাঁ, পানি পান করেছি"); water.addView(yes, new LinearLayout.LayoutParams(-1, -2));
        yes.setOnClickListener(v -> { int n = AppDatabase.addWater(this); waterLabel.setText("আজকে " + Bengali.digits(String.valueOf(n)) + " গ্লাস"); });
        page.addView(water); gap(page, 16);
        TextView skip = outlineAction("রেস্ট শেষ করুন"); page.addView(skip, new LinearLayout.LayoutParams(-1, dp(52))); skip.setOnClickListener(v -> endRest());
    }

    private void runTimer() {
        if (AppDatabase.phase(this) != AppDatabase.PHASE_REST) { endRest(); return; }
        if (AppDatabase.paused(this)) { long paused = AppDatabase.remaining(this); countdown.setText(Bengali.digits(String.format(java.util.Locale.US, "%02d:%02d", paused / 60_000L, (paused / 1000L) % 60))); return; }
        long left = Math.max(0L, AppDatabase.deadline(this) - System.currentTimeMillis());
        if (left == 0) { endRest(); return; }
        timer = new CountDownTimer(left, 1000) {
            @Override public void onTick(long millis) {
                long sec = millis / 1000L; countdown.setText(Bengali.digits(String.format(java.util.Locale.US, "%02d:%02d", sec / 60, sec % 60)));
                ring.setProgress(1f - millis / (float) REST);
            }
            @Override public void onFinish() { endRest(); }
        }.start();
    }
    private void endRest() {
        if (timer != null) timer.cancel(); TimerNotifications.cancel(this); AppDatabase.addRest(this, REST);
        long deadline = System.currentTimeMillis() + 3_600_000L;
        AppDatabase.setTimer(this, AppDatabase.PHASE_FOCUS, deadline, 3_600_000L, false);
        startActivity(new Intent(this, FocusActivity.class)); finish();
    }
    @Override public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("বিরতি ছেড়ে যাবেন?").setMessage("আপনি চাইলে বিরতিটি শেষ করে ফোকাসে ফিরতে পারেন।")
                .setNegativeButton("থাকুন", null).setPositiveButton("রেস্ট শেষ করুন", (d, w) -> endRest()).show();
    }
    @Override protected void onDestroy() { if (timer != null) timer.cancel(); super.onDestroy(); }
}
