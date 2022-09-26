package org.laokou.common.jvm.classpath.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * 通配符类路径，继承CompositeEntry
 */
public class WildcardEntry extends CompositeEntry {

    public WildcardEntry(String pathList) {
        super(pathList);
    }

    private static String toPathList(String wildcardPath) {
        final String baseDir = wildcardPath.replace("*", "");
        try {
            return Files.walk(Paths.get(baseDir))
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(p -> p.endsWith(".jar") || p.endsWith(".JAR"))
                    .collect(Collectors.joining(File.pathSeparator));
        } catch (Exception e) {
            return "";
        }
    }

}
