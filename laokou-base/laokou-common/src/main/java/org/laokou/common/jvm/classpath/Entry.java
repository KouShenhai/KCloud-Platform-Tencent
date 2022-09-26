/**
 * Copyright (c) 2022 KCloud-Platform Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.common.jvm.classpath;

import org.laokou.common.jvm.classpath.impl.CompositeEntry;
import org.laokou.common.jvm.classpath.impl.DirEntry;
import org.laokou.common.jvm.classpath.impl.WildcardEntry;
import org.laokou.common.jvm.classpath.impl.ZipEntry;

import java.io.File;
import java.io.IOException;

/**
 * 类路径接口
 * @author Kou Shenhai
 */
public interface Entry {

    /**
     * 读取class文件
     * @param className
     * @return
     * @throws IOException
     */
    byte[] readClass(String className) throws IOException;

    /**
     * 创建类路径接口
     * @param path
     * @return
     */
    public static Entry create(String path) {
        //File.pathSeparator 路径分隔符(win\linux)
        if (path.contains(File.pathSeparator)) {
            return new CompositeEntry(path);
        }
        if (path.endsWith("*")) {
            return new WildcardEntry(path);
        }
        if (path.endsWith(".jar") || path.endsWith(".JAR")
           || path.endsWith(".zip") || path.endsWith(".ZIP")) {
            return new ZipEntry(path);
        }
        return new DirEntry(path);
    }

}
