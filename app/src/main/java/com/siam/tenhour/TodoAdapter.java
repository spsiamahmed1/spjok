package com.siam.tenhour;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

final class TodoAdapter {
    private TodoAdapter() {}
    static void render(BaseActivity host, LinearLayout parent, Runnable refresh) {
        parent.removeAllViews(); JSONArray list = AppDatabase.todos(host);
        if (list.length() == 0) {
            TextView empty = host.text("আজকের কাজ এখানে যোগ করুন।", 14, false); empty.setPadding(0, host.dp(9), 0, host.dp(4)); parent.addView(empty); return;
        }
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.optJSONObject(i); if (item == null) continue;
            final int index = i; String title = item.optString("title", "কাজ"); boolean done = item.optBoolean("done", false);
            LinearLayout row = new LinearLayout(host); row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setPadding(host.dp(8), host.dp(2), host.dp(4), host.dp(2)); row.setBackgroundResource(R.drawable.glass_card);
            CheckBox check = new CheckBox(host); check.setButtonTintList(android.content.res.ColorStateList.valueOf(0xffA855F7)); check.setChecked(done);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(host.dp(42), host.dp(46)); row.addView(check, cp);
            TextView label = host.text(title, 15, false); if (done) label.setPaintFlags(label.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            row.addView(label, new LinearLayout.LayoutParams(0, -2, 1));
            ImageView delete = new ImageView(host); delete.setImageResource(R.drawable.ic_close); row.addView(delete, new LinearLayout.LayoutParams(host.dp(34), host.dp(34)));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2); p.topMargin = host.dp(6); parent.addView(row, p);
            check.setOnCheckedChangeListener((button, checked) -> { AppDatabase.updateTodo(host, index, checked); refresh.run(); });
            label.setOnClickListener(v -> check.setChecked(!check.isChecked()));
            delete.setOnClickListener(v -> { AppDatabase.deleteTodo(host, index); refresh.run(); });
        }
    }
}
