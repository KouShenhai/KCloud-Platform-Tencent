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
package org.laokou.auth.server.domain.sys.repository.service.impl;
import org.laokou.auth.server.domain.sys.repository.mapper.SysMenuMapper;
import org.laokou.auth.server.domain.sys.repository.service.SysMenuService;
import org.laokou.auth.client.vo.SysMenuVO;
import org.laokou.common.enums.SuperAdminEnum;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.common.utils.RedisKeyUtil;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
/**
 * @author Kou Shenhai
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenuVO> getMenuList(Long userId, Integer type) {
        List<SysMenuVO> menuList;
        if (userId == null) {
            menuList = sysMenuMapper.getMenuList(type);
        } else {
            menuList = sysMenuMapper.getMenuListByUserId(userId,type);
        }
        return menuList;
    }

    @Override
    public List<String> getPermissionsList() {
        return sysMenuMapper.getPermissionsList();
    }

    @Override
    public List<String> getPermissionsListByUserId(Long userId) {
        return sysMenuMapper.getPermissionsListByUserId(userId);
    }

    @Override
    public List<SysMenuVO> getMenuList(String Authorization,UserDetail userDetail, boolean noCache, Integer type) {
        //region Description
        if (noCache) {
            return getMenuList(userDetail.getId(),userDetail.getSuperAdmin(),type);
        }
        String userResourceKey = RedisKeyUtil.getUserResourceKey(Authorization);
        final Object obj = redisUtil.get(userResourceKey);
        List<SysMenuVO> resourceList;
        if (null != obj) {
            resourceList = (List<SysMenuVO>) obj;
        } else {
            resourceList = getMenuList(userDetail.getId(),userDetail.getSuperAdmin(),type);
            redisUtil.set(userResourceKey,resourceList,RedisUtil.HOUR_ONE_EXPIRE);
        }
        return resourceList;
        //endregion
    }

    private List<SysMenuVO> getMenuList(Long userId, Integer superAdmin, Integer type) {
        //region Description
        if (SuperAdminEnum.YES.ordinal() == superAdmin) {
            return getMenuList(null,type);
        } else {
            return getMenuList(userId,type);
        }
        //endregion
    }
}
