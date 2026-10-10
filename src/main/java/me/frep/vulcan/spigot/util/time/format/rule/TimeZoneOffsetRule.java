package me.frep.vulcan.spigot.util.time.format.rule;

import java.util.Calendar;

public class TimeZoneOffsetRule implements FormatRule {

    public static final TimeZoneOffsetRule WITH_COLON = new TimeZoneOffsetRule(true);

    public static final TimeZoneOffsetRule WITHOUT_COLON = new TimeZoneOffsetRule(false);

    private final boolean colon;

    public TimeZoneOffsetRule(boolean colon) {
        this.colon = colon;
    }

    public int estimateLength() {
        return 5;
    }

    public void appendTo(StringBuffer buffer, Calendar calendar) {
        int offset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
        buffer.append(offset < 0 ? '-' : '+');
        offset = Math.abs(offset);
        int hours = offset / 3600000;
        buffer.append((char) (hours / 10 + '0')).append((char) (hours % 10 + '0'));
        if (colon)
            buffer.append(':');
        int minutes = offset / 60000 - hours * 60;
        buffer.append((char) (minutes / 10 + '0')).append((char) (minutes % 10 + '0'));
    }
}
