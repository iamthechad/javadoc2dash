package com.megatome.j2d.sample.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Sample annotation
 */
@Target(METHOD)
@Retention(CLASS)
@Documented
public @interface SampleAnnotation {
    /**
     * Sample value.
     *
     * @return Description
     */
    String value() default "";
}
