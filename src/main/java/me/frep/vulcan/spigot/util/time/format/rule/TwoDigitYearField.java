package me.frep.vulcan.spigot.util.time.format.rule;

import java.util.Calendar;

public class TwoDigitYearField implements NumberRule {

    public static final TwoDigitYearField INSTANCE = new TwoDigitYearField();

    public int estimateLength() {
        return 2;
    }

    public void appendTo(StringBuffer buffer, Calendar calendar) {
        appendTo(buffer, calendar.get(Calendar.YEAR) % 100);
    }

    public void appendTo(StringBuffer buffer, int value) {
        buffer.append((char) (value / 10 + '0'));
        buffer.append((char) (value % 10 + '0'));
    }
}
