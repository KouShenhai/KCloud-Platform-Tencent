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
package org.laokou.admin.server.application.service;

import org.laokou.admin.client.vo.CacheVO;
import org.laokou.admin.server.infrastructure.server.Server;

/**
 * @author laokou
 * @version 1.0
 * @date 2022/7/27 0027 下午 3:17
 */
public interface SysMonitorApplicationService {

    /**
     * 获取缓存信息
     * @return
     */
    CacheVO getCacheInfo();

    /**
     * 获取服务器信息
     * @return
     * @throws Exception
     */
    Server getServerInfo() throws Exception;

}
