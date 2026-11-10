package com.siam.tenhour;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class Bengali {
    private static final char[] DIGITS = {'০','১','২','৩','৪','৫','৬','৭','৮','৯'};
    private Bengali() {}

    static String digits(String value) {
        StringBuilder out = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            out.append(c >= '0' && c <= '9' ? DIGITS[c - '0'] : c);
        }
        return out.toString();
    }

    static String duration(long millis) {
        long seconds = Math.max(0, millis / 1000);
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return digits(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, secs));
    }

    static String clock(int hour, int minute) {
        return digits(String.format(Locale.US, "%02d:%02d", hour, minute));
    }

    static String date(long time) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("bn", "BD"));
        return digits(format.format(new Date(time)));
    }

    static String shortDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("d MMM", new Locale("bn", "BD"));
        return digits(format.format(new Date(time)));
    }
}
