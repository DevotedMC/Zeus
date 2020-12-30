package com.github.maxopoly.zeus.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ZCommand {

	public static final int DEFAULT_ARG_NUM = -1;

	String id();

	String altIds() default "";

	String description();

	int args() default 0;

	int minArgs() default DEFAULT_ARG_NUM;

	int maxArgs() default DEFAULT_ARG_NUM;

}