package me.frep.vulcan.spigot.util.time.format.rule;

import java.util.Calendar;

public class CharacterLiteral implements FormatRule {

    private final char character;

    public CharacterLiteral(char character) {
        this.character = character;
    }

    public int estimateLength() {
        return 1;
    }

    public void appendTo(StringBuffer buffer, Calendar calendar) {
        buffer.append(character);
    }
}
