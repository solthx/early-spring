package com.earlyspring.commons.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarFile;

/**
 * @author czf
 * @Date 2020/5/8 8:03 下午
 */
@Slf4j
public class ClassUtils {
    /**
     * 扫描包
     * @param packageName 包名
     * @param clazzesHasRegistered 注册过的class对象，防止重复注册造成死循环
     * @return 指定包名下所有类的class对象
     */
    public static Set<Class<?>> scanPackage(String packageName, Set<Class<?>> clazzesHasRegistered) {
        Set<Class<?>> clazzSet = new HashSet<>();
        // 1. 获取类加载器
        ClassLoader classLoader = getClassLoader();
        // 2. 通过类加载器获取该包在os下的url
        URL url = classLoader.getResource(packageName.replace(".", "/"));
        if ( url==null ||  (!url.getProtocol().equals("file") && !url.getProtocol().equals("jar")) ){
            // print log
            // System.out.println("包路径不正确...");
            log.warn("包路径不正确..."+packageName);
            return null;
        }
        String path = null;
        URLConnection connection = null;
        // 得到了包在os下对应的目录
        File AbsolutePackageDir = null;

        try {
            connection = url.openConnection();
            if(connection instanceof JarURLConnection) {
                JarFile jarFile = ((JarURLConnection) connection).getJarFile();
                path = jarFile.getName();
                int separator = path.indexOf("!/");
                if (separator > 0) {
                    path = path.substring(0, separator);
                }
            } else {
                path = url.toURI().getPath();
            }
            AbsolutePackageDir = new File(path);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        if ( AbsolutePackageDir == null ){
            log.warn(packageName+"扫描失败...");
            return clazzSet;
        }

        // 扫描该目录及其子目录下的所有满足条件的File对象
        Set<File> clazzFileSet = collectFileFromDir(AbsolutePackageDir, (f) -> {
            return f.getName().endsWith(".class") ;
        });
        // 将扫描得到的file给转成class对象
        transFileToClazz(clazzFileSet, clazzSet, packageName, clazzesHasRegistered);
        return clazzSet;
    }

    /**
     * 把file文件转成class对象，添加到clazzSet里，packageName是包名
     * @param clazzFileSet  集合内为class文件
     * @param clazzSet   集合内为class对象
     * @param packageName  class所在包
     * @param clazzesHasRegistered 已经注册过的clazz集合
     */
    private static void transFileToClazz(Set<File> clazzFileSet, Set<Class<?>> clazzSet, String packageName, Set<Class<?>> clazzesHasRegistered) {
        if ( clazzFileSet==null )
            return;
        for ( File f:clazzFileSet ){
            String path = f.getPath().replace(File.separator, ".");
            String clazzName = path.substring(path.indexOf(packageName));
            clazzName = clazzName.substring(0, clazzName.indexOf(".class"));
            // 此时clazzName就是全类名了
            try {
                Class<?> klazz = Class.forName(clazzName);
                if (!clazzesHasRegistered.contains(klazz)) {
                    clazzSet.add(klazz);
                    clazzesHasRegistered.add(klazz);
                }
            } catch (ClassNotFoundException e) {
                // print log
                e.printStackTrace();
                log.warn("未找到"+f.getName());
            }
        }
    }

    /**
     * 扫描指定目录下，满足pattern条件的文件
     * @param dir  待扫描的目录
     * @param pattern 用于判断是否是我们想要的文件的匹配器
     * @return
     */
    public static Set<File> collectFileFromDir(File dir, Predicate<File> pattern) {
        Set<File> filesSet = new HashSet<>();
        collectFileFromDir(filesSet, dir, pattern);
        return filesSet;
    }

    /**
     * 递归的去扫描目录，将匹配成功的文件存入fileSet中
     * @param fileSet
     * @param dir
     * @param pattern
     */
    private static void collectFileFromDir(Set<File> fileSet, File dir,Predicate<File> pattern) {
        if ( !dir.isDirectory() ) return;
        File[] dirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if ( pathname.isDirectory() )
                    return true;
                if ( pattern.test(pathname) )
                    fileSet.add(pathname);
                return false;
            }
        });
        if (dirs!=null)
            for ( File f:dirs )
                collectFileFromDir(fileSet, f, pattern);
    }

    /**
     * 获取类加载器
     * @return
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 给指定对象的指定field进行赋值
     * @param field  指定对象的Field
     * @param targetObject  被赋值的对象
     * @param value   要去赋的值
     * @param accessible  field的setAccessible传入参数
     */
    public static void setField(Field field, Object targetObject, Object value, boolean accessible){
        field.setAccessible(accessible);
        try {
            field.set(targetObject, value);
        } catch (IllegalAccessException e) {
            // to replace
            e.printStackTrace();
        }
    }
}
