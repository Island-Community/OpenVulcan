package me.frep.vulcan.spigot.util.time.format.rule;

import java.util.Calendar;

public class TwoDigitCalendarField implements NumberRule {

    private final int calendarField;

    public TwoDigitCalendarField(int calendarField) {
        this.calendarField = calendarField;
    }

    public int estimateLength() {
        return 2;
    }

    public void appendTo(StringBuffer buffer, Calendar calendar) {
        appendTo(buffer, calendar.get(calendarField));
    }

    public void appendTo(StringBuffer buffer, int value) {
        if (value < 100) {
            buffer.append((char) (value / 10 + '0'));
            buffer.append((char) (value % 10 + '0'));
        } else
            buffer.append(value);
    }
}
