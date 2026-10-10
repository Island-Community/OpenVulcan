package me.frep.vulcan.spigot.util.time.format.rule;

import java.util.Calendar;

public interface FormatRule {

    int estimateLength();

    void appendTo(StringBuffer buffer, Calendar calendar);
}
