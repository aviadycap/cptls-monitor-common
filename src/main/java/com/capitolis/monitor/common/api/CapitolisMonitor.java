package com.capitolis.monitor.common.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CapitolisMonitor {

    /**
     *
     * @return the Monitor event description. keep it small and informative.
     */
    String taskDescription();

}
