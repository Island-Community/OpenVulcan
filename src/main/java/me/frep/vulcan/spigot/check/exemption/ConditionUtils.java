package me.frep.vulcan.spigot.check.exemption;

import java.util.Collection;
import java.util.function.Predicate;

public final class ConditionUtils {

    public static boolean anyMatch(Collection collection, Predicate predicate) {
        Predicate predicate2 = predicate;
        if (predicate == null) {
            return false;
        }
        for (Object value : collection) {
            if (predicate2.test(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean allMatch(Collection collection, Predicate predicate) {
        Predicate predicate2 = predicate;
        if (predicate == null) {
            return false;
        }
        for (Object value : collection) {
            if (!predicate2.test(value)) {
                return false;
            }
        }
        return true;
    }

    private ConditionUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
