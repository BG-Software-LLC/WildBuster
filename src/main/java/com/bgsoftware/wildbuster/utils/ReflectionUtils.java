package com.bgsoftware.wildbuster.utils;

import org.bukkit.Bukkit;

public final class ReflectionUtils {

    private static final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    public static Class<?> getClass(String clazz){
        try{
            return Class.forName(clazz.replace("VERSION", version));
        }catch(ClassNotFoundException ex){
            throw new RuntimeException(ex);
        }
    }

}
