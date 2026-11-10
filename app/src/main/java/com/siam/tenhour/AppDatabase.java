package com.siam.tenhour;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Lightweight local persistence; intentionally uses Android SharedPreferences, no database dependency. */
final class AppDatabase {
    static final int PHASE_FOCUS = 0;
    static final int PHASE_REST = 1;
    private static final String FILE = "ten_hour_challenge";
    private AppDatabase() {}
    static SharedPreferences prefs(Context c) { return c.getSharedPreferences(FILE, Context.MODE_PRIVATE); }
    static boolean active(Context c) { return prefs(c).getBoolean("active", false); }
    static int target(Context c) { return prefs(c).getInt("target", 10); }
    static int cycle(Context c) { return prefs(c).getInt("cycle", 0); }
    static int phase(Context c) { return prefs(c).getInt("phase", PHASE_FOCUS); }
    static long deadline(Context c) { return prefs(c).getLong("deadline", 0L); }
    static long remaining(Context c) { return prefs(c).getLong("remaining", 0L); }
    static boolean paused(Context c) { return prefs(c).getBoolean("paused", false); }
    static long focusTotal(Context c) { return prefs(c).getLong("focus_total", 0L); }
    static long restTotal(Context c) { return prefs(c).getLong("rest_total", 0L); }
    static long sessionStart(Context c) { return prefs(c).getLong("session_start", System.currentTimeMillis()); }
    static String subject(Context c) { return prefs(c).getString("subject", "পড়াশোনা"); }

    static void startSession(Context c, int hours, String subject, long startMinute) {
        long now = System.currentTimeMillis();
        prefs(c).edit().putBoolean("active", true).putBoolean("paused", false)
                .putInt("target", hours).putInt("cycle", 0).putInt("phase", PHASE_FOCUS)
                .putLong("session_start", now).putLong("start_minute", startMinute)
                .putString("subject", subject).putLong("focus_total", 0L).putLong("rest_total", 0L)
                .putInt("water", 0).putLong("remaining", 3_600_000L)
                .putLong("deadline", now + 3_600_000L).apply();
    }

    static void setTimer(Context c, int phase, long deadline, long remaining, boolean paused) {
        prefs(c).edit().putInt("phase", phase).putLong("deadline", deadline)
                .putLong("remaining", remaining).putBoolean("paused", paused).apply();
    }
    static void addFocus(Context c, long millis) { prefs(c).edit().putLong("focus_total", focusTotal(c) + Math.max(0, millis)).apply(); }
    static void addRest(Context c, long millis) { prefs(c).edit().putLong("rest_total", restTotal(c) + Math.max(0, millis)).apply(); }
    static void setCycle(Context c, int cycle) { prefs(c).edit().putInt("cycle", cycle).apply(); }
    static int water(Context c) { return prefs(c).getInt("water", 0); }
    static int addWater(Context c) { int count = water(c) + 1; prefs(c).edit().putInt("water", count).apply(); return count; }

    static void finishSession(Context c, boolean completed) {
        SharedPreferences p = prefs(c);
        if (completed) {
            String date = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
            long today = p.getLong("history_" + date, 0L) + focusTotal(c);
            p.edit().putLong("history_" + date, today).putLong("last_completion", System.currentTimeMillis())
                    .putInt("last_target", target(c)).apply();
        }
        p.edit().putBoolean("active", false).putBoolean("paused", false).putLong("deadline", 0L).apply();
    }

    static JSONArray todos(Context c) {
        try { return new JSONArray(prefs(c).getString("todos", "[]")); }
        catch (Exception e) { return new JSONArray(); }
    }
    static void saveTodos(Context c, JSONArray todos) { prefs(c).edit().putString("todos", todos.toString()).apply(); }
    static void addTodo(Context c, String title) {
        try {
            JSONArray old = todos(c), next = new JSONArray();
            for (int i = 0; i < old.length(); i++) next.put(old.optJSONObject(i));
            next.put(new JSONObject().put("title", title).put("done", false)); saveTodos(c, next);
        } catch (Exception ignored) { }
    }
    static void updateTodo(Context c, int index, boolean done) {
        try {
            JSONArray old = todos(c), next = new JSONArray();
            for (int i = 0; i < old.length(); i++) {
                JSONObject item = old.optJSONObject(i);
                if (item != null) { if (i == index) item.put("done", done); next.put(item); }
            }
            saveTodos(c, next);
        } catch (Exception ignored) { }
    }
    static void deleteTodo(Context c, int index) {
        JSONArray old = todos(c), next = new JSONArray();
        for (int i = 0; i < old.length(); i++) if (i != index && old.optJSONObject(i) != null) next.put(old.optJSONObject(i));
        saveTodos(c, next);
    }
    static long[] week(Context c) {
        long[] values = new long[7]; Calendar cal = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            cal.setTimeInMillis(System.currentTimeMillis()); cal.add(Calendar.DAY_OF_YEAR, -(6 - i));
            String key = new SimpleDateFormat("yyyyMMdd", Locale.US).format(cal.getTime()); values[i] = prefs(c).getLong("history_" + key, 0L);
        }
        return values;
    }
    static int streak(Context c) {
        int count = 0; Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 365; i++) {
            String key = new SimpleDateFormat("yyyyMMdd", Locale.US).format(cal.getTime());
            if (prefs(c).getLong("history_" + key, 0L) <= 0L) break;
            count++; cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        return count;
    }
    static void reset(Context c) { prefs(c).edit().clear().apply(); }
}
