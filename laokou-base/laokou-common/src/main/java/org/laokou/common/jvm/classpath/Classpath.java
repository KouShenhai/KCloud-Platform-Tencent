package org.laokou.common.jvm.classpath;

import org.laokou.common.jvm.classpath.impl.WildcardEntry;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 类路径
 */
public class Classpath {

    /**
     * 启动类路径
     */
    private Entry bootstrapClasspath;

    /**
     * 扩展类路径
     */
    private Entry extensionClasspath;

    /**
     * 用户类路径
     */
    private Entry userClasspath;

    public Classpath(String jreOption,String cpOption) {
        //启动类&扩展类 "C:\Program Files\Java\jre1.8.0_212"
        bootstrapAndExtensionClasspath(jreOption);
        //用户类 "D:\koushenhai\project\KCloud-Platform-Netflix\laokou-cloud\laokou-register\target\classes\org\laokou\register\RegisterApplication.class"
        parseUserClasspath(cpOption);
    }

    private void parseUserClasspath(String cpOption) {
        if (cpOption == null) {
            cpOption = ".";
        }
        userClasspath = Entry.create(cpOption);
    }

    private void bootstrapAndExtensionClasspath(String jreOption) {
        final String jreDir = getJreDir(jreOption);

        // ..jre/lib/*
        final String jreLibPath = Paths.get(jreDir, "lib") + File.separator + "*";
        bootstrapClasspath = new WildcardEntry(jreLibPath);

        // ..jre/lin/ext/*
        final String jreExtPath = Paths.get(jreDir, "lib", "ext") + File.separator + "*";
        extensionClasspath = new WildcardEntry(jreExtPath);
    }

    private static String getJreDir(String jreOption) {
        if (jreOption != null && Files.exists(Paths.get(jreOption))) {
            return jreOption;
        }
        if (Files.exists(Paths.get("./jre"))) {
            return "./jre";
        }
        final String jh = System.getenv("JAVA_HOME");
        if (jh != null) {
            return Paths.get(jh,"jre").toString();
        }
        throw new RuntimeException("Can not find JRE folder!");
    }

    public byte[] readClass(String className) throws Exception {
        className += ".class";

        //[readClass]启动类路径
        try {
            final byte[] bytes = bootstrapClasspath.readClass(className);
            System.out.println("bootstrapClasspath -> length" + bytes.length);
            return bytes;
        } catch (Exception ignored) {
            //ignored
        }

        //[readClass]扩展类路径
        try {
            final byte[] bytes = extensionClasspath.readClass(className);
            System.out.println("extensionClasspath -> length" + bytes.length);
            return bytes;
        } catch (Exception ignored) {
            //ignored
        }

        //[readClass]用户类路径
        final byte[] bytes = userClasspath.readClass(className);
        System.out.println("userClasspath -> length" + bytes.length);
        return bytes;
    }

}
