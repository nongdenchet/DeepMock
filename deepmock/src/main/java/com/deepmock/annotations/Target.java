package com.deepmock.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@java.lang.annotation.Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
public @interface Target {
}
