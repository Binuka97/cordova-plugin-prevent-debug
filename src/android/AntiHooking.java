package com.plugin.security;

import java.lang.reflect.Method;

public class AntiHooking {
    public static boolean isHooked() {
        try {
            for (Method method : ClassLoader.class.getDeclaredMethods()) {
                if (method.getName().equals("findLoadedClass")) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
