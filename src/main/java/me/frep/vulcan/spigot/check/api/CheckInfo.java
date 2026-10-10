package me.frep.vulcan.spigot.check.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CheckInfo {

    String name();

    char type();

    String complexType() default "N/A";

    boolean experimental() default false;

    boolean punish() default true;

    String description() default "No description.";
}
