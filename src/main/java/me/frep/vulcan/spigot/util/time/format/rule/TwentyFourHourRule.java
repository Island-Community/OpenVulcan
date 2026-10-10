package me.frep.vulcan.spigot.util.time.format.rule;

import java.util.Calendar;

public class TwentyFourHourRule implements NumberRule {

    private final NumberRule numberRule;

    public TwentyFourHourRule(NumberRule numberRule) {
        this.numberRule = numberRule;
    }

    public int estimateLength() {
        return numberRule.estimateLength();
    }

    public void appendTo(StringBuffer buffer, Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour == 0)
            hour = calendar.getMaximum(Calendar.HOUR_OF_DAY) + 1;
        numberRule.appendTo(buffer, hour);
    }

    public void appendTo(StringBuffer buffer, int value) {
        numberRule.appendTo(buffer, value);
    }
}
