package me.frep.vulcan.spigot.util.time.format.rule;

public interface NumberRule extends FormatRule {

    void appendTo(StringBuffer stringBuffer, int index);
}
