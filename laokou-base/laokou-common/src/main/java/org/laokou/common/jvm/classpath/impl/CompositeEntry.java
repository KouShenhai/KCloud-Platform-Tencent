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
package org.laokou.common.jvm.classpath.impl;

import com.google.common.collect.Lists;
import org.laokou.common.jvm.classpath.Entry;

import java.io.File;
import java.io.IOException;
import java.util.List;
/**
 * @author Kou Shenhai
 */
public class CompositeEntry implements Entry {

    private final List<Entry> entryList = Lists.newArrayList();

    public CompositeEntry(String pathList) {
        String[] paths = pathList.split(File.pathSeparator);
        for (String path : paths) {
            entryList.add(Entry.create(path));
        }
    }

    @Override
    public byte[] readClass(String className) throws IOException {
        for (Entry entry : entryList) {
            try {
                return entry.readClass(className);
            } catch (Exception ignored) {
                //ignored
            }
        }
        throw new IOException("class not found " + className);
    }

    @Override
    public String toString() {
        final String[] strs = new String[entryList.size()];
        for (int i = 0; i < strs.length; i++) {
            strs[i] = entryList.get(i).toString();
        }
        return String.join(File.pathSeparator,strs);
    }
}
