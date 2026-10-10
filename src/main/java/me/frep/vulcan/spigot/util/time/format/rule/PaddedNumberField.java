package me.frep.vulcan.spigot.util.time.format.rule;

import java.util.Calendar;

public class PaddedNumberField implements NumberRule {

    private final int calendarField;

    private final int minimumWidth;

    public PaddedNumberField(int calendarField, int minimumWidth) {
        if (minimumWidth < 3)
            throw new IllegalArgumentException();
        this.calendarField = calendarField;
        this.minimumWidth = minimumWidth;
    }

    public int estimateLength() {
        return 4;
    }

    public void appendTo(StringBuffer buffer, Calendar calendar) {
        appendTo(buffer, calendar.get(calendarField));
    }

    public void appendTo(StringBuffer buffer, int value) {
        if (value < 0)
            throw new IllegalArgumentException("Negative values should not be possible");
        String digits = Integer.toString(value);
        for (int padding = minimumWidth - digits.length(); padding > 0; padding--) buffer.append('0');
        buffer.append(digits);
    }
}
