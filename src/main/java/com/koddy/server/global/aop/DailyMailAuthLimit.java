package com.koddy.server.global.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DailyMailAuthLimit {
    int maxTry() default 3;

    int banTime() default 30;

    TimeUnit banTimeUnit() default TimeUnit.MINUTES;
}
