package com.siam.tenhour;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        com.siam.tenhour.databinding.ActivityReportBinding binding = com.siam.tenhour.databinding.ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); setup(binding.getRoot(), false);
        TextView title = heading("সাপ্তাহিক প্রতিবেদন"); page.addView(title);
        long[] week = AppDatabase.week(this); long total = 0; for (long h : week) total += h;
        int target = AppDatabase.target(this); int percent = (int) Math.min(100, total * 100 / Math.max(1L, target * 3_600_000L));
        LinearLayout stats = row();
        LinearLayout complete = card(); complete.addView(text("এই সপ্তাহে ফোকাস", 14, false)); complete.addView(text(Bengali.digits(String.format(java.util.Locale.US, "%.1f ঘণ্টা", total / 3_600_000.0)), 21, true));
        LinearLayout streak = card(); streak.addView(text("ধারাবাহিক দিন", 14, false)); streak.addView(text(Bengali.digits(String.valueOf(AppDatabase.streak(this))) + " দিন", 21, true));
        stats.addView(complete, new LinearLayout.LayoutParams(0, -2, 1)); LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0, -2, 1); sp.leftMargin = dp(10); stats.addView(streak, sp); page.addView(stats);
        gap(page, 14); LinearLayout chartCard = card(); chartCard.addView(text("সোমবার থেকে রবিবার", 18, true));
        WeekChartView chart = new WeekChartView(this, week); LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, dp(220)); cp.topMargin = dp(10); chartCard.addView(chart, cp); page.addView(chartCard);
        gap(page, 14); LinearLayout summary = card(); summary.addView(text("লক্ষ্য পূরণ", 16, true)); summary.addView(text(Bengali.digits(String.valueOf(percent)) + "% সম্পূর্ণ হয়েছে", 20, true));
        summary.addView(text("এখানে শুধু অ্যাপে সংরক্ষিত সম্পন্ন ফোকাস ঘণ্টা দেখানো হয়।", 13, false)); page.addView(summary);
        gap(page, 12); addBottomNav(v -> navigate(MainActivity.class), v -> { if (AppDatabase.active(this)) navigate(AppDatabase.phase(this) == AppDatabase.PHASE_REST ? RestActivity.class : FocusActivity.class); }, v -> {}, v -> navigate(SettingsActivity.class));
    }
}
