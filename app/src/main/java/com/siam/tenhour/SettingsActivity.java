package com.siam.tenhour;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        com.siam.tenhour.databinding.ActivitySettingsBinding binding = com.siam.tenhour.databinding.ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); setup(binding.getRoot(), false);
        if (AppDatabase.prefs(this).getBoolean("dark", false)) root.setBackgroundColor(0xff1E1B2E);
        page.addView(heading("সেটিংস"));
        LinearLayout options = card();
        addSwitch(options, "ডার্ক মোড", "dark", false, enabled -> {
            if (enabled) root.setBackgroundColor(0xff1E1B2E); else root.setBackgroundResource(R.drawable.bg_gradient);
        });
        addSwitch(options, "বিরতির শব্দ", "sound", true, null);
        addSwitch(options, "বিরতির বিজ্ঞপ্তি", "notification", true, null);
        addSwitch(options, "কম্পন", "vibration", true, null);
        page.addView(options); gap(page, 14);
        LinearLayout resetCard = card(); resetCard.addView(text("ডেটা মুছুন", 18, true)); resetCard.addView(text("সংরক্ষিত লক্ষ্য, করণীয় ও প্রতিবেদন মুছে যাবে।", 14, false));
        TextView reset = outlineAction("সব তথ্য মুছে দিন"); LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, dp(50)); rp.topMargin = dp(12); resetCard.addView(reset, rp);
        reset.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("সব তথ্য মুছবেন?").setMessage("এই কাজ ফিরিয়ে আনা যাবে না।")
                .setNegativeButton("বাতিল", null).setPositiveButton("মুছে দিন", (d, w) -> { TimerNotifications.cancel(this); AppDatabase.reset(this); finish(); }).show());
        page.addView(resetCard); gap(page, 12);
        addBottomNav(v -> navigate(MainActivity.class), v -> { if (AppDatabase.active(this)) navigate(AppDatabase.phase(this) == AppDatabase.PHASE_REST ? RestActivity.class : FocusActivity.class); }, v -> navigate(ReportActivity.class), v -> {});
    }

    private void addSwitch(LinearLayout parent, String title, String key, boolean defaultValue, java.util.function.Consumer<Boolean> onChanged) {
        LinearLayout row = row(); row.setPadding(0, dp(6), 0, dp(6)); TextView label = text(title, 16, true); row.addView(label, new LinearLayout.LayoutParams(0, -2, 1));
        Switch toggle = new Switch(this); toggle.setChecked(AppDatabase.prefs(this).getBoolean(key, defaultValue)); toggle.setButtonTintList(android.content.res.ColorStateList.valueOf(0xffA855F7)); styleSwitch(toggle); row.addView(toggle);
        toggle.setOnCheckedChangeListener((button, checked) -> {
            AppDatabase.prefs(this).edit().putBoolean(key, checked).apply(); if (onChanged != null) onChanged.accept(checked);
            if ("notification".equals(key) && checked && android.os.Build.VERSION.SDK_INT >= 33 && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 32);
        });
        parent.addView(row);
    }
}
