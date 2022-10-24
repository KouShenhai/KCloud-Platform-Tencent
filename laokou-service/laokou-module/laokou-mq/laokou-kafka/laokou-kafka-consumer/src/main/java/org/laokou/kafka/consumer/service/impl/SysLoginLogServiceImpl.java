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
package org.laokou.kafka.consumer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.laokou.kafka.client.dto.LoginLogDTO;
import org.laokou.common.utils.ConvertUtil;
import org.laokou.kafka.consumer.entity.SysLoginLogDO;
import org.laokou.kafka.consumer.mapper.SysLoginLogMapper;
import org.laokou.kafka.consumer.service.SysLoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Kou Shenhai
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLogDO> implements SysLoginLogService {

    @Override
    public void insertLoginLog(LoginLogDTO dto) {
        SysLoginLogDO logDO = ConvertUtil.sourceToTarget(dto, SysLoginLogDO.class);
        baseMapper.insert(logDO);
    }
}
