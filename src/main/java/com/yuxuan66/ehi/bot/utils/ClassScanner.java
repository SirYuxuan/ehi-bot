package com.yuxuan66.ehi.bot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Pattern;

/**
 * @author Sir丶雨轩
 * @date 2020/6/3
 */
public class ClassScanner {
    ;

    public static enum ProtocolTypes {
        http, https, file, jar;
    }

    /**
     * Find all classes in packages 扫描一或多个包下的所有Class，包含接口类
     *
     * @param scanBasePackages
     * @return
     */
    public static List<Class> scanPackages(String... scanBasePackages) {
        List<Class> classList = new LinkedList<Class>();
        if (scanBasePackages.length == 0) {
            return classList;
        }
        for (String pkg : scanBasePackages) {
            if (pkg != null && pkg.length() != 0) {
                classList.addAll(ClassScanner.scanOnePackage(pkg));
            }
        }
        return classList;
    }

    /**
     * Find all classes with given annotation in packages 扫描某个包下带有注解的Class
     *
     * @param anno
     * @param scanBasePackages
     * @return
     */
    public static List<Class> scanByAnno(Class<? extends Annotation> anno, String... scanBasePackages) {
        List<Class> classList = scanPackages(scanBasePackages);
        List<Class> result = new ArrayList<Class>();
        for (Class clz : classList) {
            Annotation clzAnno = clz.getAnnotation(anno);
            if (clzAnno != null) {
                result.add(clz);
            }
        }
        return result;
    }


    /**
     * find all classes in one package 扫描某个包下所有Class类
     *
     * @param pkg
     * @return Class
     */
    public static List<Class<?>> scanOnePackage(String pkg) {
        List<Class<?>> classList = new LinkedList<>();
        try {
            // 包名转化为路径名
            String pathName = package2Path(pkg);
            // 获取路径下URL
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(pathName);
            // 循环扫描路径
            classList = scanUrls(pkg, urls);
        } catch (IOException e) {
            System.err.println("Warning: Can not scan package：" + pkg);
        }

        return classList;
    }

    /**
     * find all classes in urls 扫描多个Url路径，找出符合包名的Class类
     *
     * @param pkg
     * @param urls
     * @return Class
     * @throws IOException
     */
    private static List<Class<?>> scanUrls(String pkg, Enumeration<URL> urls) throws IOException {
        List<Class<?>> classList = new LinkedList<>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            // 获取协议
            String protocol = url.getProtocol();

            if (ProtocolTypes.file.name().equals(protocol)) {
                // 文件
                String path = URLDecoder.decode(url.getFile(), "UTF-8");
                classList.addAll(recursiveScan4Path(pkg, path,""));

            } else if (ProtocolTypes.jar.name().equals(protocol)) {
                // jar包
                String jarPath = getJarPathFormUrl(url);
                classList.addAll(recursiveScan4Jar(pkg, jarPath));
            }
        }
        return classList;
    }

    /**
     * get real path from url 从url中获取jar真实路径
     * <p>
     * jar文件url示例如下：
     * <p>
     * jar:file:/Users/cent/.gradle/caches/modules-2/files-2.1/org.projectlombok/lombok/1.18.4/7103ab519b1cdbb0642ad4eaf1db209d905d0f96/lombok-1.18.4.jar!/org
     *
     * @param url
     * @return
     */
    private static String getJarPathFormUrl(URL url) {
        String file = url.getFile();
        String jarRealPath = file.substring(0, file.lastIndexOf("!")).replaceFirst("file:", "");
        return jarRealPath;
    }

    /**
     * recursive scan for path 递归扫描指定文件路径下的Class文件
     *
     * @param pkg
     * @param filePath
     * @return Class列表
     */
    public static List<Class<?>> recursiveScan4Path(String pkg, String filePath, String removePrefix) {
        List<Class<?>> classList = new LinkedList<>();

        File file = new File(filePath);
        if (!file.exists() || !file.isDirectory()) {
            return classList;
        }

        // 处理类文件
        File[] classes = file.listFiles(child -> isClass(child.getName()));
        for (File child : classes) {
            String className = classFile2SimpleClass(
                    pkg + "." + child.getName());
            className = className.replace(removePrefix,"");
            try {
                Class clz = Thread.currentThread().getContextClassLoader().loadClass(className);
                classList.add(clz);
            } catch (ClassNotFoundException | LinkageError e) {
                System.err.println("Warning: Can not load class:" + className);
            }

        }

        // 处理目录

        File[] dirs = file.listFiles(f -> f.isDirectory());

        for (File child : dirs) {
            String childPackageName = pkg + "." + child.getName();
            String childPath = filePath + "/" + child.getName();
            classList.addAll(recursiveScan4Path(childPackageName, childPath, removePrefix));
        }

        return classList;
    }
    /**
     * Recursive scan 4 jar 递归扫描Jar文件内的Class类
     *
     * @param pkg
     * @param jarPath
     * @return Class列表
     * @throws IOException
     */
    private static List<Class<?>> recursiveScan4Jar(String pkg, String jarPath) throws IOException {
        List<Class<?>> classList = new LinkedList<>();

        JarInputStream jin = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jin.getNextJarEntry();
        while (entry != null) {
            String name = entry.getName();
            entry = jin.getNextJarEntry();

            if (!name.contains(package2Path(pkg))) {
                continue;
            }
            if (isClass(name)) {
                if (isAnonymousInnerClass(name)) {
                    // 是匿名内部类，跳过不作处理
                    continue;
                }

                String className = classFile2SimpleClass(path2Package(name));
                try {
                    Class clz = Thread.currentThread().getContextClassLoader().loadClass(className);
                    classList.add(clz);
                } catch (ClassNotFoundException | LinkageError e) {
                    System.err.println("Warning: Can not load class:" + className);
                }
            }
        }

        return classList;
    }




    public static Set<Class<?>> loadClasses(String rootClassPath) throws Exception {
        Set<Class<?>> classSet = new HashSet<>();
        // 设置class文件所在根路径
        File clazzPath = new File(rootClassPath);

        // 记录加载.class文件的数量
        int clazzCount = 0;

        if (clazzPath.exists() && clazzPath.isDirectory()) {
            // 获取路径长度
            int clazzPathLen = clazzPath.getAbsolutePath().length() + 1;

            Stack<File> stack = new Stack<>();
            stack.push(clazzPath);

            // 遍历类路径
            while (!stack.isEmpty()) {
                File path = stack.pop();
                File[] classFiles = path.listFiles(pathname -> {
                    //只加载class文件
                    return pathname.isDirectory() || pathname.getName().endsWith(".class");
                });
                if (classFiles == null) {
                    break;
                }
                for (File subFile : classFiles) {
                    if (subFile.isDirectory()) {
                        stack.push(subFile);
                    } else {
                        if (clazzCount++ == 0) {
                            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                            boolean accessible = method.isAccessible();
                            try {
                                if (!accessible) {
                                    method.setAccessible(true);
                                }
                                // 设置类加载器
                                URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                                // 将当前类路径加入到类加载器中
                                method.invoke(classLoader, clazzPath.toURI().toURL());
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                method.setAccessible(accessible);
                            }
                        }
                        // 文件名称
                        String className = subFile.getAbsolutePath();
                        className = className.substring(clazzPathLen, className.length() - 6);
                        //将/替换成. 得到全路径类名
                        className = className.replace(File.separatorChar, '.');
                        // 加载Class类
                        Class<?> aClass = Class.forName(className);
                        classSet.add(aClass);
                    }
                }
            }
        }
        return classSet;
    }


    // ===== Inside used static tool methods =====
    private static final Pattern ANONYMOUS_INNER_CLASS_PATTERN = Pattern.compile("^[\\s\\S]*\\${1}\\d+\\.class$");

    private static String package2Path(String packageName) {
        return packageName.replace(".", "/");
    }

    private static String path2Package(String pathName) {
        return pathName.replaceAll("/", ".");
    }

    private static boolean isClass(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return false;
        }
        return fileName.endsWith(".class");
    }

    private static String classFile2SimpleClass(String classFileName) {
        return classFileName.replace(".class", "");
    }

    private static boolean isAnonymousInnerClass(String className) {
        return ANONYMOUS_INNER_CLASS_PATTERN.matcher(className).matches();
    }

}
