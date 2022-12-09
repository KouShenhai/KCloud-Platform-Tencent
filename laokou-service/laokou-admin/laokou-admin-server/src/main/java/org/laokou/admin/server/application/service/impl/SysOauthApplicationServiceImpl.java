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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.laokou.admin.server.application.service.SysOauthApplicationService;
import org.laokou.admin.server.domain.sys.entity.SysOauthDO;
import org.laokou.admin.server.domain.sys.repository.service.SysOauthService;
import org.laokou.admin.client.dto.SysOauthDTO;
import org.laokou.admin.server.infrastructure.annotation.DataFilter;
import org.laokou.admin.server.interfaces.qo.SysOauthQo;
import org.laokou.admin.client.vo.SysOauthVO;
import org.laokou.auth.client.utils.UserUtil;
import org.laokou.common.core.constant.Constant;
import org.laokou.common.core.exception.CustomException;
import org.laokou.common.core.utils.ConvertUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/8/11 0011 上午 9:47
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SysOauthApplicationServiceImpl implements SysOauthApplicationService {

    private final SysOauthService sysOauthService;

    @Override
    @DataFilter(tableAlias = "boot_sys_oauth_client_details")
    public IPage<SysOauthVO> queryOauthPage(SysOauthQo qo) {
        IPage<SysOauthVO> page = new Page<>(qo.getPageNum(),qo.getPageSize());
        return sysOauthService.getOauthList(page,qo);
    }

    @Override
    public Boolean insertOauth(SysOauthDTO dto) {
        SysOauthDO sysOauthDO = ConvertUtil.sourceToTarget(dto, SysOauthDO.class);
        final long count = sysOauthService.count(Wrappers.lambdaQuery(SysOauthDO.class).eq(SysOauthDO::getDelFlag, Constant.NO).eq(SysOauthDO::getClientId, sysOauthDO.getClientId()));
        if (count > 0) {
            throw new CustomException("应用id已存在，请重新填写");
        }
        sysOauthDO.setCreator(UserUtil.getUserId());
        sysOauthDO.setDeptId(UserUtil.getDeptId());
        return sysOauthService.save(sysOauthDO);
    }

    @Override
    public Boolean updateOauth(SysOauthDTO dto) {
        final SysOauthDO sysOauthDO = ConvertUtil.sourceToTarget(dto, SysOauthDO.class);
        final long count = sysOauthService.count(Wrappers.lambdaQuery(SysOauthDO.class).eq(SysOauthDO::getDelFlag, Constant.NO).eq(SysOauthDO::getClientId, sysOauthDO.getClientId()).ne(SysOauthDO::getId,dto.getId()));
        if (count > 0) {
            throw new CustomException("应用id已存在，请重新填写");
        }
        sysOauthDO.setEditor(UserUtil.getUserId());
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
