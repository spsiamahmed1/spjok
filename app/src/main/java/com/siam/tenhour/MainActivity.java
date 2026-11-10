package com.siam.tenhour;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;

public class MainActivity extends BaseActivity {
    private int selectedTarget = 10;
    private int startHour = 9, startMinute = 0;
    private EditText subjectInput;
    private TextView timeSummary;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedTarget = AppDatabase.target(this);
        startHour = AppDatabase.prefs(this).getInt("setup_hour", 9);
        startMinute = AppDatabase.prefs(this).getInt("setup_minute", 0);
        showHome();
        if (AppDatabase.active(this) && savedInstanceState == null) showResumeDialog();
    }

    private void showHome() {
        com.siam.tenhour.databinding.ActivityMainBinding binding = com.siam.tenhour.databinding.ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); setup(binding.getRoot(), false);
        LinearLayout top = row();
        LinearLayout greeting = new LinearLayout(this); greeting.setOrientation(LinearLayout.VERTICAL);
        greeting.addView(text("শুভ সকাল", 17, false));
        greeting.addView(text("১০ ঘণ্টা চ্যালেঞ্জ", 26, true));
        greeting.addView(text(Bengali.date(System.currentTimeMillis()), 13, false));
        top.addView(greeting, new LinearLayout.LayoutParams(0, -2, 1));
        ImageView bell = new ImageView(this); bell.setImageResource(R.drawable.ic_bell); top.addView(bell, new LinearLayout.LayoutParams(dp(28), dp(28)));
        bell.setOnClickListener(v -> navigate(SettingsActivity.class));
        ImageView user = new ImageView(this); user.setImageResource(R.drawable.ic_user);
        LinearLayout.LayoutParams up = new LinearLayout.LayoutParams(dp(28), dp(28)); up.leftMargin = dp(12); top.addView(user, up);
        user.setOnClickListener(v -> navigate(SettingsActivity.class));
        page.addView(top); gap(page, 20);

        LinearLayout targetCard = card();
        targetCard.addView(text("আজকের লক্ষ্য", 18, true));
        targetCard.addView(text("কত ঘণ্টা মনোযোগ দিয়ে পড়বেন?", 14, false)); gap(targetCard, 10);
        LinearLayout targets = row();
        int[] hours = {5, 8, 10, 12};
        for (int h : hours) {
            TextView chip = text(Bengali.digits(String.valueOf(h)) + " ঘণ্টা", 14, true);
            chip.setGravity(Gravity.CENTER); chip.setPadding(dp(6), dp(10), dp(6), dp(10));
            GradientDrawable shape = new GradientDrawable(); shape.setCornerRadius(dp(18));
            shape.setColor(selectedTarget == h ? 0x99E9D5FF : 0x55ffffff); shape.setStroke(dp(1), selectedTarget == h ? 0xffA855F7 : 0x88ffffff); chip.setBackground(shape);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, -2, 1); cp.setMargins(dp(3), dp(4), dp(3), dp(4)); targets.addView(chip, cp);
            chip.setOnClickListener(v -> { selectedTarget = h; showHome(); });
        }
        targetCard.addView(targets); gap(targetCard, 8);
        TextView startLabel = text("শুরুর সময়", 15, true); targetCard.addView(startLabel);
        TextView timeButton = outlineAction(Bengali.clock(startHour, startMinute) + "     সময় বাছুন");
        LinearLayout.LayoutParams tbp = new LinearLayout.LayoutParams(-1, -2); tbp.topMargin = dp(6); targetCard.addView(timeButton, tbp);
        timeButton.setOnClickListener(v -> new TimePickerDialog(this, (picker, h, m) -> {
            startHour = h; startMinute = m; AppDatabase.prefs(this).edit().putInt("setup_hour", h).putInt("setup_minute", m).apply(); showHome();
        }, startHour, startMinute, true).show());
        subjectInput = new EditText(this); subjectInput.setText(AppDatabase.prefs(this).getString("setup_subject", "রসায়ন — জৈব যৌগ"));
        subjectInput.setTextSize(16); subjectInput.setTextColor(ink()); subjectInput.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HindSiliguri-Regular.ttf"));
        subjectInput.setSingleLine(true); subjectInput.setHint("বিষয় লিখুন"); subjectInput.setPadding(dp(14), dp(10), dp(14), dp(10));
        subjectInput.setBackgroundResource(R.drawable.glass_card);
        LinearLayout.LayoutParams sip = new LinearLayout.LayoutParams(-1, -2); sip.topMargin = dp(10); targetCard.addView(subjectInput, sip);
        timeSummary = text("", 15, true); timeSummary.setTextColor(0xffA855F7); timeSummary.setPadding(0, dp(12), 0, 0); targetCard.addView(timeSummary);
        int endMins = (startHour * 60 + startMinute + selectedTarget * 60) % (24 * 60);
        timeSummary.setText("শুরু: " + Bengali.clock(startHour, startMinute) + "   ·   শেষ: " + Bengali.clock(endMins / 60, endMins % 60));
        page.addView(targetCard); gap(page, 14);
        TextView start = action("চ্যালেঞ্জ শুরু করুন"); page.addView(start, new LinearLayout.LayoutParams(-1, -2));
        start.setOnClickListener(v -> startChallenge());

        gap(page, 22); LinearLayout blockTitle = row(); blockTitle.addView(text("আজকের পড়ার ব্লক", 20, true), new LinearLayout.LayoutParams(0, -2, 1));
        blockTitle.addView(text("৩টি পরিকল্পিত বিষয়", 12, false)); page.addView(blockTitle); gap(page, 8);
        addStudyBlock("জীববিজ্ঞান", "কোষ ও জিনতত্ত্ব", "১ ঘণ্টা");
        addStudyBlock("পদার্থবিজ্ঞান", "গতি ও বল", "১ ঘণ্টা");
        addStudyBlock("রসায়ন", "জৈব যৌগ", "১ ঘণ্টা");

        gap(page, 18); LinearLayout todoCard = card(); LinearLayout todoHead = row();
        todoHead.addView(text("করণীয় তালিকা", 19, true), new LinearLayout.LayoutParams(0, -2, 1));
        ImageView plus = new ImageView(this); plus.setImageResource(R.drawable.ic_plus); todoHead.addView(plus, new LinearLayout.LayoutParams(dp(24), dp(24)));
        plus.setOnClickListener(v -> addTodo()); todoCard.addView(todoHead); gap(todoCard, 4);
        LinearLayout todos = new LinearLayout(this); todos.setOrientation(LinearLayout.VERTICAL); todoCard.addView(todos);
        TodoAdapter.render(this, todos, this::showHome);
        page.addView(todoCard);
        gap(page, 12);
        addBottomNav(v -> showHome(), v -> { if (AppDatabase.active(this)) resumeSession(); else startChallenge(); }, v -> navigate(ReportActivity.class), v -> navigate(SettingsActivity.class));
    }

    private void addStudyBlock(String title, String description, String duration) {
        LinearLayout box = card(); box.setOrientation(LinearLayout.HORIZONTAL); box.setGravity(Gravity.CENTER_VERTICAL); box.setPadding(dp(14), dp(11), dp(14), dp(11));
        ImageView icon = new ImageView(this); icon.setImageResource(R.drawable.ic_book); box.addView(icon, new LinearLayout.LayoutParams(dp(26), dp(26)));
        LinearLayout labels = new LinearLayout(this); labels.setOrientation(LinearLayout.VERTICAL); labels.setPadding(dp(12), 0, 0, 0);
        labels.addView(text(title, 16, true)); labels.addView(text(description, 13, false));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1); box.addView(labels, lp); box.addView(text(duration, 13, true));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, -2); bp.topMargin = dp(8); page.addView(box, bp);
    }

    private void addTodo() {
        EditText input = new EditText(this); input.setHint("নতুন কাজ লিখুন"); input.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HindSiliguri-Regular.ttf"));
        new AlertDialog.Builder(this).setTitle("করণীয় যোগ করুন").setView(input)
                .setNegativeButton("বাতিল", null).setPositiveButton("যোগ করুন", (d, w) -> {
                    String value = input.getText().toString().trim(); if (!value.isEmpty()) AppDatabase.addTodo(this, value); showHome();
                }).show();
    }

    private void startChallenge() {
        if (AppDatabase.active(this)) { showResumeDialog(); return; }
        String subject = subjectInput == null ? "পড়াশোনা" : subjectInput.getText().toString().trim();
        if (subject.isEmpty()) subject = "পড়াশোনা";
        AppDatabase.prefs(this).edit().putString("setup_subject", subject).apply();
        AppDatabase.startSession(this, selectedTarget, subject, startHour * 60L + startMinute);
        navigate(FocusActivity.class);
    }

    private void showResumeDialog() {
        if (isFinishing()) return;
        new AlertDialog.Builder(this).setTitle("চ্যালেঞ্জ চলছে")
                .setMessage("আপনার পড়ার সেশন সংরক্ষিত আছে। আবার শুরু করবেন?")
                .setNegativeButton("পরে", (d, w) -> {})
                .setPositiveButton("আবার শুরু করুন", (d, w) -> resumeSession()).show();
    }
    private void resumeSession() {
        if (AppDatabase.phase(this) == AppDatabase.PHASE_REST) navigate(RestActivity.class); else navigate(FocusActivity.class);
    }
}
