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
package org.laokou.auth.server.domain.sys.repository.service;

import org.laokou.auth.client.vo.SysMenuVO;
import org.laokou.auth.client.user.UserDetail;

import java.util.List;

/**
 * 菜单类
 * @author Kou Shenhai
 */
public interface SysMenuService {

    /**
     * 根据type获取资源
     * @param type
     * @return
     */
    List<SysMenuVO> getMenuList(Integer type);

    /**
     * 查询所有权限列表
     * @return
     */
    List<String> getPermissionsList();

    /**
     * 查询用户权限列表
     * @param userId
     * @return
     */
    List<String> getPermissionsListByUserId(Long userId);

    /**
     * 获取菜单列表
     * @param Authorization
     * @param userDetail
     * @param noCache
     * @param type
     * @return
     */
    List<SysMenuVO> getMenuList(String Authorization,UserDetail userDetail, boolean noCache, Integer type);

}
