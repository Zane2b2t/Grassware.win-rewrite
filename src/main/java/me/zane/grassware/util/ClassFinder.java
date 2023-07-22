package me.zane.grassware.util;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ClassFinder {

    public static ArrayList<Class<?>> classesInFolder(final String folder) {
        try {
            return ReflectionUtil.getClassesForPackage("me.zane.grassware.features.modules." + folder);
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    public static ArrayList<Class<?>> classesExtending(final ArrayList<Class<?>> classes, final Class<?> extendingClass) {
        return classes.stream().filter(extendingClass::isAssignableFrom).collect(Collectors.toCollection(ArrayList::new));
    }
}