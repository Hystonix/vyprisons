package com.glitchedturtle.vyprisons.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfKey {
    String value();
    boolean required() default true;
    boolean translateColourCodes() default true;

}
