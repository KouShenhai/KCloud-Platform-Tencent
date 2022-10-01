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
package org.laokou.admin.server.application.service.impl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.laokou.admin.server.application.service.SysLogApplicationService;
import org.laokou.admin.server.domain.sys.entity.SysLoginLogDO;
import org.laokou.admin.server.domain.sys.entity.SysOperateLogDO;
import org.laokou.admin.server.domain.sys.repository.service.SysLoginLogService;
import org.laokou.admin.server.domain.sys.repository.service.SysOperateLogService;
import org.laokou.admin.server.domain.sys.repository.service.SysUserService;
import org.laokou.admin.server.infrastructure.component.annotation.DataFilter;
import org.laokou.admin.server.interfaces.qo.LoginLogQO;
import org.laokou.admin.server.interfaces.qo.SysOperateLogQO;
import org.laokou.admin.client.vo.SysLoginLogVO;
import org.laokou.admin.client.vo.SysOperateLogVO;
import org.laokou.common.dto.LoginLogDTO;
import org.laokou.common.dto.OperateLogDTO;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.common.utils.ConvertUtil;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@GlobalTransactional(rollbackFor = Exception.class)
public class SysLogApplicationServiceImpl implements SysLogApplicationService {

    @Autowired
    private SysOperateLogService sysOperateLogService;

    @Autowired
    private SysLoginLogService sysLoginLogService;

    @Autowired
    private SysUserService sysUserService;

    @Override
    public Boolean insertOperateLog(OperateLogDTO dto) {
        SysOperateLogDO logDO = ConvertUtil.sourceToTarget(dto, SysOperateLogDO.class);
        final UserDetail userDetail = sysUserService.getUserDetail(logDO.getCreator());
        logDO.setDeptId(userDetail.getDeptId());
        return sysOperateLogService.save(logDO);
    }

    @Override
    public Boolean insertLoginLog(LoginLogDTO dto) {
        SysLoginLogDO logDO = ConvertUtil.sourceToTarget(dto, SysLoginLogDO.class);
        return sysLoginLogService.save(logDO);
    }

    @Override
    @DataFilter(tableAlias = "boot_sys_operate_log")
    public IPage<SysOperateLogVO> queryOperateLogPage(SysOperateLogQO qo) {
        IPage<SysOperateLogVO> page = new Page<>(qo.getPageNum(),qo.getPageSize());
        return sysOperateLogService.getOperateLogList(page,qo);
    }

    @Override
    public IPage<SysLoginLogVO> queryLoginLogPage(LoginLogQO qo) {
        IPage<SysLoginLogVO> page = new Page<>(qo.getPageNum(),qo.getPageSize());
        return sysLoginLogService.getLoginLogList(page,qo);
    }

}
