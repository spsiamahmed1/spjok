package com.siam.tenhour;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.OutputStream;

public class CompleteActivity extends BaseActivity {
    private LinearLayout certificate;
    private EditText nameInput;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        com.siam.tenhour.databinding.ActivityCompleteBinding binding = com.siam.tenhour.databinding.ActivityCompleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); setup(binding.getRoot(), false);
        ImageView trophy = new ImageView(this); trophy.setImageResource(R.drawable.ic_trophy);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(dp(48), dp(48)); tp.gravity = Gravity.CENTER; page.addView(trophy, tp);
        TextView title = heading("চ্যালেঞ্জ সম্পূর্ণ"); title.setGravity(Gravity.CENTER); page.addView(title);
        TextView done = text(Bengali.digits(String.valueOf(AppDatabase.target(this))) + " ঘণ্টার ফোকাস শেষ!", 19, true); done.setGravity(Gravity.CENTER); page.addView(done); gap(page, 16);
        certificate = card(); certificate.setGravity(Gravity.CENTER); certificate.setPadding(dp(20), dp(22), dp(20), dp(22));
        TextView cap = text("অর্জনের সনদ", 18, true); cap.setGravity(Gravity.CENTER); certificate.addView(cap);
        TextView name = text("নাম", 13, false); name.setGravity(Gravity.CENTER); name.setPadding(0, dp(10), 0, 0); certificate.addView(name);
        nameInput = new EditText(this); nameInput.setSingleLine(true); nameInput.setHint("আপনার নাম লিখুন"); nameInput.setGravity(Gravity.CENTER);
        nameInput.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HindSiliguri-SemiBold.ttf")); nameInput.setTextColor(ink());
        nameInput.setBackgroundResource(R.drawable.glass_card); certificate.addView(nameInput, new LinearLayout.LayoutParams(-1, dp(48)));
        TextView award = text(Bengali.digits(String.valueOf(AppDatabase.target(this))) + " ঘণ্টা · " + Bengali.date(System.currentTimeMillis()), 16, true); award.setGravity(Gravity.CENTER); award.setPadding(0, dp(12), 0, dp(4)); certificate.addView(award);
        page.addView(certificate); gap(page, 16);
        TextView save = action("সনদ ডাউনলোড করুন"); page.addView(save, new LinearLayout.LayoutParams(-1, -2)); save.setOnClickListener(v -> saveCertificate());
        gap(page, 10); LinearLayout actions = row(); TextView share = outlineAction("শেয়ার করুন"); TextView home = outlineAction("হোমে ফিরুন");
        actions.addView(share, new LinearLayout.LayoutParams(0, dp(52), 1)); LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(0, dp(52), 1); hp.leftMargin = dp(10); actions.addView(home, hp); page.addView(actions);
        share.setOnClickListener(v -> shareCertificate()); home.setOnClickListener(v -> { Intent i = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(i); finish(); });
        FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(-1, dp(250)); fp.gravity = Gravity.TOP; root.addView(new ConfettiView(this), fp);
    }

    private void shareCertificate() {
        String name = nameInput.getText().toString().trim(); if (name.isEmpty()) name = "শিক্ষার্থী";
        Intent send = new Intent(Intent.ACTION_SEND); send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, name + " ১০ ঘণ্টার পড়ার চ্যালেঞ্জ সম্পূর্ণ করেছেন।");
        startActivity(Intent.createChooser(send, "সনদ শেয়ার করুন"));
    }

    private void saveCertificate() {
        if (Build.VERSION.SDK_INT < 29 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 41); return;
        }
        try {
            int w = Math.max(dp(300), certificate.getWidth()); int h = Math.max(dp(180), certificate.getHeight());
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888); Canvas canvas = new Canvas(bitmap); certificate.draw(canvas);
            String filename = "TenHourCertificate_" + System.currentTimeMillis() + ".png";
            ContentValues values = new ContentValues(); values.put(MediaStore.Images.Media.DISPLAY_NAME, filename); values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            if (Build.VERSION.SDK_INT >= 29) values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TenHourChallenge");
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri == null) throw new IllegalStateException("ফাইল তৈরি করা যায়নি");
            try (OutputStream stream = getContentResolver().openOutputStream(uri)) { if (stream == null) throw new IllegalStateException("ফাইলে লেখা যায়নি"); bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); }
            Toast.makeText(this, "সনদ ছবিতে সংরক্ষিত হয়েছে", Toast.LENGTH_LONG).show();
        } catch (Exception e) { Toast.makeText(this, "সনদ সংরক্ষণ করা যায়নি", Toast.LENGTH_LONG).show(); }
    }
    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == 41 && results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) saveCertificate();
    }
}
