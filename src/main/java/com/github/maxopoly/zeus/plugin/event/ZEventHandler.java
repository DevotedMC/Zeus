package com.github.maxopoly.zeus.plugin.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark methods, which want to listen for specific
 * events
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ZEventHandler {

	/**
	 * Event listeners will be processed in descending order of their priority,
	 * meaning the ones with highest priority go first. Priority should be within
	 * [0,1000], where listeners below 100 never cancel events
	 * 
	 * @return Priority for ordering of event listener execution
	 */
	int priority() default 0;

	/**
	 * @return Should this event listener be run even if the event is already
	 *         cancelled
	 */
	boolean runWhenCancelled() default false;
}
