package com.github.maxopoly.zeus.plugin.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;

public class EventBroadcaster {

	private static final class MethodListenerTuple {
		private Method method;
		private ZeusListener listener;

		private MethodListenerTuple(Method m, ZeusListener listener) {
			this.method = m;
			this.listener = listener;
		}
	}

	private Map<Class<? extends ZeusEvent>, List<MethodListenerTuple>> listenerMapping;

	private Logger logger;

	public EventBroadcaster(Logger logger) {
		this.listenerMapping = new ConcurrentHashMap<>();
		this.logger = logger;
	}

	public void broadcast(ZeusEvent e) {
		List<MethodListenerTuple> listeners = listenerMapping.get(e.getClass());
		if (listeners == null) {
			return;
		}
		for (MethodListenerTuple tuple : listeners) {
			try {
				tuple.method.invoke(tuple.listener, e);
			} catch (Exception ex) {
				// catching just any kind of exception isnt nice behavior, but this is where
				// code outside of Zeus will run and we dont want the exceptions of that code to
				// mess with Zeus
				logger.error("Executing listener in class " + tuple.listener.getClass() + " threw exception ", ex);
			}
		}
	}

	private void internalRegister(Class<? extends ZeusEvent> eventClass, Method method, ZeusListener listener) {
		List<MethodListenerTuple> existingListeners = listenerMapping.get(eventClass);
		if (existingListeners == null) {
			existingListeners = new ArrayList<>();
			listenerMapping.put(eventClass, existingListeners);
		}
		method.setAccessible(true);
		existingListeners.add(new MethodListenerTuple(method, listener));
	}

	@SuppressWarnings("unchecked")
	public void registerListener(ZeusListener listener) {
		for (Method method : listener.getClass().getMethods()) {
			if (!Modifier.isPublic(method.getModifiers())) {
				// method should be public for any outside use case
				continue;
			}
			if (!method.getReturnType().equals(Void.TYPE)) {
				// method should not return anything
				continue;
			}
			boolean eventHandlerAnnotationFound = false;
			for (Annotation annotation : method.getAnnotations()) {
				if (annotation instanceof ZEventHandler) {
					eventHandlerAnnotationFound = true;
					break;
				}
			}
			if (!eventHandlerAnnotationFound) {
				// method is missing the annotation, we dont care about it
				continue;
			}
			if (method.getParameterCount() != 1) {
				// parameter should only be the event object
				continue;
			}
			Class<?> eventClass = method.getParameterTypes()[0];
			if (!ZeusEvent.class.isAssignableFrom(method.getParameterTypes()[0])) {
				// only parameter is not a subtype of the event class
				continue;
			}
			// we found a valid listener method at this point
			internalRegister((Class<? extends ZeusEvent>) eventClass, method, listener);
		}
	}

	public void unregisterListener(ZeusListener lis) {
		for (Entry<Class<? extends ZeusEvent>, List<MethodListenerTuple>> entry : listenerMapping.entrySet()) {
			List<MethodListenerTuple> currList = entry.getValue();
			for (int i = 0; i < currList.size(); i++) {
				MethodListenerTuple curr = currList.get(i);
				if (curr.listener == lis) {
					currList.remove(curr);
					i--;
				}
			}
		}
	}

}
