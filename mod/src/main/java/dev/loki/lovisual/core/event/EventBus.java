package dev.loki.lovisual.core.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private static final Map<Class<?>, List<Listener>> listeners = new HashMap<>();

    public static void register(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventHandler.class)) continue;
            if (method.getParameterCount() != 1) continue;
            Class<?> eventType = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(eventType)) continue;
            method.setAccessible(true);
            listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                    .add(new Listener(object, method));
        }
    }

    public static void unregister(Object object) {
        for (List<Listener> list : listeners.values()) {
            list.removeIf(l -> l.obj == object);
        }
    }

    public static <T extends Event> T post(T event) {
        List<Listener> list = listeners.get(event.getClass());
        if (list == null) return event;
        for (Listener l : list) {
            try {
                l.method.invoke(l.obj, event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return event;
    }

    private record Listener(Object obj, Method method) {}
}
