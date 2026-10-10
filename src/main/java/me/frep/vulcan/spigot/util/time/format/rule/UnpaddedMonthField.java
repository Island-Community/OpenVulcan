package me.frep.vulcan.spigot.util.time.format.rule;

import java.util.Calendar;

public class UnpaddedMonthField implements NumberRule {

    public static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();

    public int estimateLength() {
        return 2;
    }

    public void appendTo(StringBuffer buffer, Calendar calendar) {
        appendTo(buffer, calendar.get(Calendar.MONTH) + 1);
    }

    public void appendTo(StringBuffer buffer, int value) {
        buffer.append(value);
    }
}
