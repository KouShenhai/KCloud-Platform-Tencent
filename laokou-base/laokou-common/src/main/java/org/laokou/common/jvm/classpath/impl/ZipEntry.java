package org.laokou.common.jvm.classpath.impl;

import org.laokou.common.jvm.classpath.Entry;

import java.io.IOException;
import java.nio.file.*;

/**
 * zip/zar文件形式类路径
 */
public class ZipEntry implements Entry {

    private Path absolutePath;

    public ZipEntry(String path) {
        //获取绝对路径
        this.absolutePath = Paths.get(path).toAbsolutePath();
    }

    @Override
    public byte[] readClass(String className) throws IOException {
        try (FileSystem fileSystem = FileSystems.newFileSystem(absolutePath,null)) {
            return Files.readAllBytes(fileSystem.getPath(className));
        }
    }

    @Override
    public String toString() {
        return absolutePath.toString();
    }
}
