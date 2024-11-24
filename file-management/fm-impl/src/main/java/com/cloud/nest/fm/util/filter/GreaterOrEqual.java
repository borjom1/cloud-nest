package com.cloud.nest.fm.util.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see LessOrEqual
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GreaterOrEqual {

    /**
     * Specifies the name of field in the table for which {@code SQL-where} statement is intended to be constructed.
     */
    String value() default "";
}
