package me.frep.vulcan.spigot.util.minecraft;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes.Property;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes.PropertyModifier;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes.PropertyModifier.Operation;
import java.util.ArrayList;

public final class AttributeUtils {

    public static double getAttributeValue(Property property) {
        ArrayList<PropertyModifier> modifiers = new ArrayList<>(property.getModifiers());
        double baseValue = property.getValue();
        for (PropertyModifier modifier : modifiers) {
            if (modifier.getOperation() != Operation.ADDITION)
                continue;
            baseValue += modifier.getAmount();
        }
        double modifiedValue = baseValue;
        for (PropertyModifier modifier : modifiers) {
            if (modifier.getOperation() != Operation.MULTIPLY_BASE)
                continue;
            modifiedValue += baseValue * modifier.getAmount();
        }
        for (PropertyModifier modifier : modifiers) {
            if (modifier.getOperation() != Operation.MULTIPLY_TOTAL)
                continue;
            modifiedValue *= 1.0 + modifier.getAmount();
        }
        return modifiedValue;
    }

    public static double getMovementSpeedWithoutSprint(Property property) {
        ArrayList<PropertyModifier> modifiers = new ArrayList<>(property.getModifiers());
        modifiers.removeIf(AttributeUtils::isSprintModifier);
        double baseValue = property.getValue();
        for (PropertyModifier modifier : modifiers) {
            if (modifier.getOperation() != Operation.ADDITION)
                continue;
            baseValue += modifier.getAmount();
        }
        double modifiedValue = baseValue;
        for (PropertyModifier modifier : modifiers) {
            if (modifier.getOperation() != Operation.MULTIPLY_BASE)
                continue;
            modifiedValue += baseValue * modifier.getAmount();
        }
        for (PropertyModifier modifier : modifiers) {
            if (modifier.getOperation() != Operation.MULTIPLY_TOTAL)
                continue;
            modifiedValue *= 1.0 + modifier.getAmount();
        }
        return modifiedValue;
    }

    private AttributeUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static boolean isSprintModifier(PropertyModifier modifier) {
        return modifier.getUUID().toString().equals("662a6b8d-da3e-4c1c-8813-96ea6097278d") || modifier.getUUID().toString().equals("19156c30-b575-3f85-a869-c0a29ccaa5fa") || modifier.getName().toString().toLowerCase().contains("sprint");
    }
}
