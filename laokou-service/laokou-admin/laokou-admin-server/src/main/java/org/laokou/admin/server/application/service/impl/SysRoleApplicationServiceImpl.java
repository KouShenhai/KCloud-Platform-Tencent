/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
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
import org.laokou.admin.server.application.service.SysRoleApplicationService;
import org.laokou.admin.server.domain.sys.entity.SysRoleDO;
import org.laokou.admin.server.domain.sys.entity.SysRoleDeptDO;
import org.laokou.admin.server.domain.sys.entity.SysRoleMenuDO;
import org.laokou.admin.server.domain.sys.repository.service.SysRoleDeptService;
import org.laokou.admin.server.domain.sys.repository.service.SysRoleMenuService;
import org.laokou.admin.server.domain.sys.repository.service.SysRoleService;
import org.laokou.admin.server.infrastructure.component.annotation.DataFilter;
import org.laokou.admin.server.interfaces.qo.SysRoleQo;
import org.laokou.admin.client.dto.SysRoleDTO;
import org.laokou.admin.client.vo.SysRoleVO;
import org.apache.commons.collections.CollectionUtils;
import org.laokou.auth.client.utils.UserUtil;
import org.laokou.common.core.constant.Constant;
import org.laokou.common.core.exception.CustomException;
import org.laokou.common.core.utils.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
/**
 * @author Kou Shenhai
 */
@Service
public class SysRoleApplicationServiceImpl implements SysRoleApplicationService {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Autowired
    private SysRoleDeptService sysRoleDeptService;

    @Override
    @DataFilter(tableAlias = "boot_sys_role")
    public IPage<SysRoleVO> queryRolePage(SysRoleQo qo) {
        IPage<SysRoleVO> page = new Page<>(qo.getPageNum(),qo.getPageSize());
        return sysRoleService.getRolePage(page,qo);
    }

    @Override
    public List<SysRoleVO> getRoleList(SysRoleQo qo) {
        return sysRoleService.getRoleList(qo);
    }

    @Override
    public SysRoleVO getRoleById(Long id) {
        return sysRoleService.getRoleById(id);
    }

    @Override
    public Boolean insertRole(SysRoleDTO dto) {
        SysRoleDO roleDO = ConvertUtil.sourceToTarget(dto, SysRoleDO.class);
        long count = sysRoleService.count(Wrappers.lambdaQuery(SysRoleDO.class).eq(SysRoleDO::getName, roleDO.getName()).eq(SysRoleDO::getDelFlag, Constant.NO));
        if (count > 0) {
            throw new CustomException("角色已存在，请重新填写");
        }
        roleDO.setCreator(UserUtil.getUserId());
        roleDO.setDeptId(UserUtil.getDeptId());
        sysRoleService.save(roleDO);
        List<Long> menuIds = dto.getMenuIds();
        saveOrUpdate(roleDO.getId(),menuIds,dto.getDeptIds());
        return true;
    }

    private Boolean saveOrUpdate(Long roleId,List<Long> menuIds,List<Long> deptIds) {
        if (CollectionUtils.isNotEmpty(menuIds)) {
            List<SysRoleMenuDO> roleMenuList = new ArrayList<>(menuIds.size());
            for (Long menuId : menuIds) {
                SysRoleMenuDO roleMenuDO = new SysRoleMenuDO();
                roleMenuDO.setMenuId(menuId);
                roleMenuDO.setRoleId(roleId);
                roleMenuList.add(roleMenuDO);
            }
            sysRoleMenuService.saveBatch(roleMenuList);
        }
        if (CollectionUtils.isNotEmpty(deptIds)) {
            List<SysRoleDeptDO> roleDeptList = new ArrayList<>(deptIds.size());
            for (Long deptId : deptIds) {
                SysRoleDeptDO roleDeptDO = new SysRoleDeptDO();
                roleDeptDO.setDeptId(deptId);
                roleDeptDO.setRoleId(roleId);
                roleDeptList.add(roleDeptDO);
            }
            sysRoleDeptService.saveBatch(roleDeptList);
        }
        return true;
    }

    @Override
    public Boolean updateRole(SysRoleDTO dto) {
        SysRoleDO roleDO = ConvertUtil.sourceToTarget(dto, SysRoleDO.class);
        long count = sysRoleService.count(Wrappers.lambdaQuery(SysRoleDO.class).eq(SysRoleDO::getName, roleDO.getName()).eq(SysRoleDO::getDelFlag, Constant.NO).ne(SysRoleDO::getId,roleDO.getId()));
        if (count > 0) {
            throw new CustomException("角色已存在，请重新填写");
        }
        Long userId = UserUtil.getUserId();
        roleDO.setEditor(userId);
        sysRoleService.updateById(roleDO);
        //删除中间表
        sysRoleMenuService.remove(Wrappers.lambdaQuery(SysRoleMenuDO.class).eq(SysRoleMenuDO::getRoleId,dto.getId()));
        sysRoleDeptService.remove(Wrappers.lambdaQuery(SysRoleDeptDO.class).eq(SysRoleDeptDO::getRoleId,dto.getId()));
        saveOrUpdate(roleDO.getId(),dto.getMenuIds(),dto.getDeptIds());
        return true;
    }

    @Override
    public Boolean deleteRole(Long id) {
        sysRoleService.deleteRole(id);
        return true;
    }

}
