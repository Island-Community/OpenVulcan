package me.frep.vulcan.api.check;

public interface Check {

    String getCategory();

    String getName();

    char getType();

    char getDisplayType();

    int getMaxVl();

    int getVl();

    double getMaxBuffer();

    boolean isExperimental();

    double getBufferDecay();

    double getBufferMultiple();

    int getMinimumVlToNotify();

    int getAlertInterval();

    double getBuffer();

    void setVl(int index);

    boolean isPunishable();

    void setBuffer(double amount);

    String getDescription();

    String getComplexType();

    String getDisplayName();
}
