/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.common.core.utils;
/**
 * @author laokou
 * @version 1.0
 * @date 2021/7/11 0011 上午 10:03
 */
public class HashUtil {

    /**
     * {@link java.util.HashMap#hash(Object)}
     * 获取hash值
     */
    public static int getHash(String key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

}
