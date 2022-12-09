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
package org.laokou.admin.server.application.service.impl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.laokou.admin.server.application.service.SysLogApplicationService;
import org.laokou.admin.server.domain.sys.repository.service.SysLoginLogService;
import org.laokou.admin.server.domain.sys.repository.service.SysOperateLogService;
import org.laokou.admin.server.domain.sys.repository.service.SysUserService;
import org.laokou.admin.server.infrastructure.annotation.DataFilter;
import org.laokou.admin.server.interfaces.qo.LoginLogQo;
import org.laokou.admin.server.interfaces.qo.SysOperateLogQo;
import org.laokou.admin.client.vo.SysLoginLogVO;
import org.laokou.admin.client.vo.SysOperateLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Kou Shenhai
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
public class SysLogApplicationServiceImpl implements SysLogApplicationService {

    private final SysOperateLogService sysOperateLogService;

    private final SysLoginLogService sysLoginLogService;

    @Override
    @DataFilter(tableAlias = "boot_sys_operate_log")
    public IPage<SysOperateLogVO> queryOperateLogPage(SysOperateLogQo qo) {
        IPage<SysOperateLogVO> page = new Page<>(qo.getPageNum(),qo.getPageSize());
        return sysOperateLogService.getOperateLogList(page,qo);
    }

    @Override
    public IPage<SysLoginLogVO> queryLoginLogPage(LoginLogQo qo) {
        IPage<SysLoginLogVO> page = new Page<>(qo.getPageNum(),qo.getPageSize());
        return sysLoginLogService.getLoginLogList(page,qo);
    }

}
