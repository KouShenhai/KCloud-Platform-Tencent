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
package org.laokou.log.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.laokou.common.core.utils.ConvertUtil;
import org.laokou.log.client.dto.AuditLogDTO;
import org.laokou.log.server.entity.SysAuditLogDO;
import org.laokou.log.server.mapper.SysAuditLogMapper;
import org.laokou.log.server.service.SysAuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author laokou
 * @version 1.0
 * @date 2022/8/26 0026 下午 5:35
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysAuditLogServiceImpl extends ServiceImpl<SysAuditLogMapper, SysAuditLogDO> implements SysAuditLogService {

    @Override
    public Boolean insertAuditLog(AuditLogDTO dto) {
        SysAuditLogDO auditDO = ConvertUtil.sourceToTarget(dto, SysAuditLogDO.class);
        baseMapper.insert(auditDO);
        return true;
    }

}
