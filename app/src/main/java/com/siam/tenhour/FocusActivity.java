package com.siam.tenhour;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FocusActivity extends BaseActivity {
    private static final long HOUR = 60L * 60L * 1000L;
    private CountDownTimer timer;
    private TextView timeText, percentText, phaseText, cycleText, focusTotalText, restTotalText, pauseButton;
    private ProgressRingView ring;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        com.siam.tenhour.databinding.ActivityFocusBinding binding = com.siam.tenhour.databinding.ActivityFocusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); setup(binding.getRoot(), true);
        if (!AppDatabase.active(this)) { finish(); return; }
        buildUi(); startTimer();
    }

    private void buildUi() {
        TextView title = heading("ফোকাস চলছে"); title.setGravity(Gravity.CENTER); page.addView(title);
        TextView subject = text("বর্তমান: ঘণ্টা " + Bengali.digits(String.valueOf(AppDatabase.cycle(this) + 1)) + " · " + AppDatabase.subject(this), 16, true);
        subject.setGravity(Gravity.CENTER); page.addView(subject); gap(page, 12);
        FrameLayout ringBox = new FrameLayout(this); int size = Math.min(getResources().getDisplayMetrics().widthPixels - dp(62), dp(300));
        ring = new ProgressRingView(this); ringBox.addView(ring, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout center = new LinearLayout(this); center.setOrientation(LinearLayout.VERTICAL); center.setGravity(Gravity.CENTER);
        timeText = text("০১:০০:০০", 35, true); timeText.setGravity(Gravity.CENTER);
        percentText = text("০% সম্পূর্ণ", 16, false); percentText.setGravity(Gravity.CENTER);
        center.addView(timeText); center.addView(percentText);
        FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(-1, -1); ringBox.addView(center, cp);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(size, size); rp.gravity = Gravity.CENTER; page.addView(ringBox, rp);
        phaseText = text("বাকি আছে", 15, false); phaseText.setGravity(Gravity.CENTER); page.addView(phaseText); gap(page, 14);

        LinearLayout blocks = card(); blocks.addView(text("আজকের ফোকাস ব্লক", 17, true));
        LinearLayout strip = row(); strip.setGravity(Gravity.CENTER); int total = AppDatabase.target(this);
        for (int i = 0; i < total; i++) {
            TextView block = text(Bengali.digits(String.valueOf(i + 1)), 13, true); block.setGravity(Gravity.CENTER);
            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable(); bg.setCornerRadius(dp(10));
            if (i < AppDatabase.cycle(this)) { bg.setColor(0xffA855F7); block.setTextColor(0xffffffff); }
            else if (i == AppDatabase.cycle(this)) { bg.setColor(0x66ffffff); bg.setStroke(dp(2), 0xffA855F7); }
            else { bg.setColor(0x22ffffff); bg.setStroke(dp(1), 0x77777777); }
            block.setBackground(bg); LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(0, dp(38), 1); bp.setMargins(dp(2), dp(8), dp(2), 0); strip.addView(block, bp);
        }
        blocks.addView(strip); page.addView(blocks); gap(page, 12);

        LinearLayout stats = row(); LinearLayout focusCard = card(); focusCard.addView(text("ফোকাস", 14, false)); focusTotalText = text("০ ঘণ্টা", 20, true); focusCard.addView(focusTotalText);
        LinearLayout restCard = card(); restCard.addView(text("বিশ্রাম", 14, false)); restTotalText = text("০ মিনিট", 20, true); restCard.addView(restTotalText);
        stats.addView(focusCard, new LinearLayout.LayoutParams(0, -2, 1)); LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0, -2, 1); sp.leftMargin = dp(10); stats.addView(restCard, sp); page.addView(stats);
        gap(page, 18); LinearLayout actions = row(); pauseButton = outlineAction("বিরতি"); TextView stop = outlineAction("বন্ধ করুন");
        actions.addView(pauseButton, new LinearLayout.LayoutParams(0, dp(52), 1)); LinearLayout.LayoutParams stopP = new LinearLayout.LayoutParams(0, dp(52), 1); stopP.leftMargin = dp(10); actions.addView(stop, stopP);
        page.addView(actions); pauseButton.setOnClickListener(v -> togglePause()); stop.setOnClickListener(v -> confirmStop());
    }

    private void startTimer() {
        if (!AppDatabase.active(this) || AppDatabase.phase(this) != AppDatabase.PHASE_FOCUS) return;
        if (AppDatabase.paused(this)) { updateUi(AppDatabase.remaining(this)); pauseButton.setText("আবার শুরু"); return; }
        long left = Math.max(0, AppDatabase.deadline(this) - System.currentTimeMillis());
        if (left == 0) { onFocusFinished(); return; }
        TimerNotifications.schedule(this, AppDatabase.deadline(this));
        timer = new CountDownTimer(left, 1000) {
            @Override public void onTick(long remaining) { updateUi(remaining); }
            @Override public void onFinish() { updateUi(0); onFocusFinished(); }
        }.start();
        updateUi(left);
    }

    private void updateUi(long remaining) {
        if (timeText == null) return;
        timeText.setText(Bengali.duration(remaining));
        long spent = Math.max(0, HOUR - remaining);
        int pct = (int) Math.min(100, spent * 100 / HOUR);
        percentText.setText(Bengali.digits(String.valueOf(pct)) + "% সম্পূর্ণ"); ring.setProgress(spent / (float) HOUR);
        focusTotalText.setText(Bengali.digits(String.valueOf(AppDatabase.focusTotal(this) / 3_600_000L)) + " ঘণ্টা " + Bengali.digits(String.valueOf((AppDatabase.focusTotal(this) / 60_000L) % 60)) + " মিনিট");
        restTotalText.setText(Bengali.digits(String.valueOf(AppDatabase.restTotal(this) / 60_000L)) + " মিনিট");
    }

    private void togglePause() {
        if (timer != null) timer.cancel();
        if (AppDatabase.paused(this)) {
            long remain = AppDatabase.remaining(this); long deadline = System.currentTimeMillis() + remain;
            AppDatabase.setTimer(this, AppDatabase.PHASE_FOCUS, deadline, remain, false); pauseButton.setText("বিরতি"); startTimer();
        } else {
            long remain = Math.max(0, AppDatabase.deadline(this) - System.currentTimeMillis());
            AppDatabase.setTimer(this, AppDatabase.PHASE_FOCUS, 0L, remain, true); TimerNotifications.cancel(this);
            pauseButton.setText("আবার শুরু"); updateUi(remain);
        }
    }

    private void onFocusFinished() {
        TimerNotifications.cancel(this); AppDatabase.addFocus(this, HOUR);
        int next = AppDatabase.cycle(this) + 1; AppDatabase.setCycle(this, next);
        if (next >= AppDatabase.target(this)) {
            AppDatabase.finishSession(this, true); startActivity(new Intent(this, CompleteActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); finish();
        } else {
            long deadline = System.currentTimeMillis() + 120_000L;
            AppDatabase.setTimer(this, AppDatabase.PHASE_REST, deadline, 120_000L, false);
            startActivity(new Intent(this, RestActivity.class)); finish();
        }
    }

    private void confirmStop() {
        new AlertDialog.Builder(this).setTitle("চ্যালেঞ্জ বন্ধ করবেন?")
                .setMessage("এখন বন্ধ করলে চলতি চ্যালেঞ্জটি সম্পূর্ণ হিসেবে গণনা হবে না।")
                .setNegativeButton("চালিয়ে যাই", null).setPositiveButton("বন্ধ করুন", (d, w) -> stopSession()).show();
    }
    private void stopSession() {
        if (timer != null) timer.cancel(); TimerNotifications.cancel(this); AppDatabase.finishSession(this, false);
        Intent home = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(home); finish();
    }
    @Override public void onBackPressed() { confirmStop(); }
    @Override protected void onDestroy() { if (timer != null) timer.cancel(); super.onDestroy(); }
}
