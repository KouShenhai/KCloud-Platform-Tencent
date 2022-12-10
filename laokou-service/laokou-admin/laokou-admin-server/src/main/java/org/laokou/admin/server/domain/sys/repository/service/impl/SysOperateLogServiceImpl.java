/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
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
package org.laokou.admin.server.domain.sys.repository.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.laokou.admin.server.domain.sys.entity.SysOperateLogDO;
import org.laokou.admin.server.domain.sys.repository.mapper.SysOperateLogMapper;
import org.laokou.admin.server.domain.sys.repository.service.SysOperateLogService;
import org.laokou.admin.server.interfaces.qo.SysOperateLogQo;
import org.laokou.admin.client.vo.SysOperateLogVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Kou Shenhai
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysOperateLogServiceImpl extends ServiceImpl<SysOperateLogMapper, SysOperateLogDO> implements SysOperateLogService {
    @Override
    public IPage<SysOperateLogVO> getOperateLogList(IPage<SysOperateLogVO> page, SysOperateLogQo qo) {
        return this.baseMapper.getOperateLogList(page,qo);
    }

    @Override
    public List<SysOperateLogVO> getOperateLogList(SysOperateLogQo qo) {
        return this.baseMapper.getOperateLogList(qo);
    }
}
