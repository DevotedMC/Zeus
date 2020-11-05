package com.github.civcraft.zeus.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ZeusLoad {

	String description() default "";

	String name();

	String version();

	/**
	 * @return Space separated list of plugin names to be enabled before this one
	 */
	String dependencies() default "";

}