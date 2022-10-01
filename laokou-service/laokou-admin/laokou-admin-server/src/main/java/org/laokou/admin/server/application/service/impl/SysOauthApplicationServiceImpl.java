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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.laokou.admin.server.application.service.SysOauthApplicationService;
import org.laokou.admin.server.domain.sys.entity.SysOauthDO;
import org.laokou.admin.server.domain.sys.repository.service.SysOauthService;
import org.laokou.admin.server.domain.sys.repository.service.SysUserService;
import org.laokou.admin.client.dto.SysOauthDTO;
import org.laokou.admin.server.infrastructure.component.annotation.DataFilter;
import org.laokou.admin.server.interfaces.qo.SysOauthQO;
import org.laokou.admin.client.vo.SysOauthVO;
import org.laokou.common.constant.Constant;
import org.laokou.common.exception.CustomException;
import org.laokou.auth.client.user.SecurityUser;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.common.utils.ConvertUtil;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/8/11 0011 上午 9:47
 */
@Service
@GlobalTransactional(rollbackFor = Exception.class)
public class SysOauthApplicationServiceImpl implements SysOauthApplicationService {

    @Autowired
    private SysOauthService sysOauthService;

    @Autowired
    private SysUserService sysUserService;

    @Override
    @DataFilter(tableAlias = "boot_sys_oauth_client_details")
    public IPage<SysOauthVO> queryOauthPage(SysOauthQO qo) {
        IPage<SysOauthVO> page = new Page<>(qo.getPageNum(),qo.getPageSize());
        return sysOauthService.getOauthList(page,qo);
    }

    @Override
    public Boolean insertOauth(SysOauthDTO dto, HttpServletRequest request) {
        SysOauthDO sysOauthDO = ConvertUtil.sourceToTarget(dto, SysOauthDO.class);
        final int count = sysOauthService.count(Wrappers.lambdaQuery(SysOauthDO.class).eq(SysOauthDO::getDelFlag, Constant.NO).eq(SysOauthDO::getClientId, sysOauthDO.getClientId()));
        if (count > 0) {
            throw new CustomException("应用id已存在，请重新填写");
        }
        sysOauthDO.setCreator(SecurityUser.getUserId(request));
        final UserDetail userDetail = sysUserService.getUserDetail(sysOauthDO.getCreator());
        sysOauthDO.setDeptId(userDetail.getDeptId());
        return sysOauthService.save(sysOauthDO);
    }

    @Override
    public Boolean updateOauth(SysOauthDTO dto, HttpServletRequest request) {
        final SysOauthDO sysOauthDO = ConvertUtil.sourceToTarget(dto, SysOauthDO.class);
        final int count = sysOauthService.count(Wrappers.lambdaQuery(SysOauthDO.class).eq(SysOauthDO::getDelFlag, Constant.NO).eq(SysOauthDO::getClientId, sysOauthDO.getClientId()).ne(SysOauthDO::getId,dto.getId()));
        if (count > 0) {
            throw new CustomException("应用id已存在，请重新填写");
        }
        sysOauthDO.setEditor(SecurityUser.getUserId(request));
        return sysOauthService.updateById(sysOauthDO);
    }

    @Override
    public Boolean deleteOauth(Long id) {
        sysOauthService.deleteOauth(id);
        return true;
    }

    @Override
    public SysOauthVO getOauthById(Long id) {
        return sysOauthService.getOauthById(id);
    }
}
