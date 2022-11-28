package me.alpha432.oyvey.util;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ClassFinder {

    public static ArrayList<Class<?>> classesInFolder(final String folder) {
        try {
            return ReflectionUtil.getClassesForPackage("me.alpha432.oyvey.features.modules." + folder);
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    public static ArrayList<Class<?>> classesExtending(final ArrayList<Class<?>> classes, final Class<?> extendingClass) {
        return classes.stream().filter(extendingClass::isAssignableFrom).collect(Collectors.toCollection(ArrayList::new));
    }
}