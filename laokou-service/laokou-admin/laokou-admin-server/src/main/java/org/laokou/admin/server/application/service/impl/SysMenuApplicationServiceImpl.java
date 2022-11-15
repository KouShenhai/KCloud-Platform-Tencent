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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.laokou.admin.server.application.service.SysMenuApplicationService;
import org.laokou.admin.server.domain.sys.entity.SysMenuDO;
import org.laokou.admin.server.domain.sys.repository.service.SysMenuService;
import org.laokou.admin.server.interfaces.qo.SysMenuQo;
import org.laokou.admin.client.vo.SysMenuVO;
import org.laokou.admin.client.dto.SysMenuDTO;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.client.utils.UserUtil;
import org.laokou.common.core.constant.Constant;
import org.laokou.common.core.exception.CustomException;
import org.laokou.common.core.utils.ConvertUtil;
import org.laokou.common.core.utils.RedisKeyUtil;
import org.laokou.common.core.utils.TreeUtil;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * @author Kou Shenhai
 */
@Service
public class SysMenuApplicationServiceImpl implements SysMenuApplicationService {

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public SysMenuVO getMenuList() {
        UserDetail userDetail = UserUtil.userDetail();
        Long userId = userDetail.getUserId();
        String resourceTreeKey = RedisKeyUtil.getResourceTreeKey(userId);
        Object obj = redisUtil.get(resourceTreeKey);
        if (obj != null) {
            return (SysMenuVO) obj;
        }
        List<SysMenuVO> menuList = sysMenuService.getMenuList(userDetail,0);
        SysMenuVO sysMenuVO = buildMenu(menuList);
        redisUtil.set(resourceTreeKey,sysMenuVO,RedisUtil.HOUR_ONE_EXPIRE);
        return sysMenuVO;
    }

    @Override
    public List<SysMenuVO> queryMenuList(SysMenuQo qo) {
        return sysMenuService.queryMenuList(qo);
    }

    @Override
    public SysMenuVO getMenuById(Long id) {
        return sysMenuService.getMenuById(id);
    }

    @Override
    public Boolean updateMenu(SysMenuDTO dto) {
        SysMenuDO menuDO = ConvertUtil.sourceToTarget(dto, SysMenuDO.class);
        long count = sysMenuService.count(Wrappers.lambdaQuery(SysMenuDO.class).eq(SysMenuDO::getName, menuDO.getName()).eq(SysMenuDO::getDelFlag, Constant.NO).ne(SysMenuDO::getId,menuDO.getId()));
        if (count > 0) {
            throw new CustomException("菜单已存在，请重新填写");
        }
        menuDO.setEditor(UserUtil.getUserId());
        return sysMenuService.updateById(menuDO);
    }

    @Override
    public Boolean insertMenu(SysMenuDTO dto) {
        SysMenuDO menuDO = ConvertUtil.sourceToTarget(dto, SysMenuDO.class);
        long count = sysMenuService.count(Wrappers.lambdaQuery(SysMenuDO.class).eq(SysMenuDO::getName, menuDO.getName()).eq(SysMenuDO::getDelFlag, Constant.NO));
        if (count > 0) {
            throw new CustomException("菜单已存在，请重新填写");
        }
        menuDO.setCreator(UserUtil.getUserId());
        return sysMenuService.save(menuDO);
    }

    @Override
    public Boolean deleteMenu(Long id) {
        sysMenuService.deleteMenu(id);
        UserDetail userDetail = UserUtil.userDetail();
        Long userId = userDetail.getUserId();
        String resourceTreeKey = RedisKeyUtil.getResourceTreeKey(userId);
        redisUtil.delete(resourceTreeKey);
        return true;
    }

    @Override
    public SysMenuVO treeMenu(Long roleId) {
        List<SysMenuVO> menuList;
        if (null == roleId) {
            menuList = queryMenuList(new SysMenuQo());
        } else {
            menuList = sysMenuService.getMenuListByRoleId(roleId);
        }
        return buildMenu(menuList);
    }

    @Override

    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return sysMenuService.getMenuIdsByRoleId(roleId);
    }

    /**
     * 组装树菜单
     * @param menuList
     * @return
     */
    private SysMenuVO buildMenu(List<SysMenuVO> menuList) {
        TreeUtil.TreeNo<TreeUtil.TreeNo> rootNode = TreeUtil.rootRootNode();
        SysMenuVO rootMenuNode = ConvertUtil.sourceToTarget(rootNode, SysMenuVO.class);
        return TreeUtil.buildTreeNode(menuList,rootMenuNode);
    }
}
