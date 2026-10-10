package me.frep.vulcan.spigot.util.time.format.rule;

import java.util.Calendar;

public class StringRule implements FormatRule {

    private final String literal;

    public StringRule(String literal) {
        this.literal = literal;
    }

    public int estimateLength() {
        return literal.length();
    }

    public void appendTo(StringBuffer buffer, Calendar calendar) {
        buffer.append(literal);
    }
}
