/**
 * Copyright (c) 2022 KCloud-Platform-Netflix Authors. All Rights Reserved.
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
package org.laokou.admin.server.application.service.impl;
import org.laokou.admin.server.application.service.SysMonitorApplicationService;
import org.laokou.admin.client.vo.CacheVO;
import org.laokou.admin.server.infrastructure.component.server.Server;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/7/27 0027 下午 3:18
 */
@Service
public class SysMonitorApplicationServiceImpl implements SysMonitorApplicationService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public CacheVO getCacheInfo() {
        CacheVO vo = new CacheVO();
        vo.setCommandStats(redisUtil.getCommandStatus());
        vo.setInfo(redisUtil.getInfo());
        vo.setKeysSize(redisUtil.getKeysSize());
        return vo;
    }

    @Override
    public Server getServerInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return server;
    }
}
