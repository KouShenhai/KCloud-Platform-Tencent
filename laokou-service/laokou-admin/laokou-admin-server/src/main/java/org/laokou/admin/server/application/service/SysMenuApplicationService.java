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
package org.laokou.admin.server.application.service;

import org.laokou.admin.client.dto.SysMenuDTO;
import org.laokou.admin.server.interfaces.qo.SysMenuQo;
import org.laokou.admin.client.vo.SysMenuVO;
import java.util.List;

/**
 * @author Kou Shenhai
 */
public interface SysMenuApplicationService {

    SysMenuVO getMenuList();

    List<SysMenuVO> queryMenuList(SysMenuQo dto);

    SysMenuVO getMenuById(Long id);

    Boolean updateMenu(SysMenuDTO dto);

    Boolean insertMenu(SysMenuDTO dto);

    Boolean deleteMenu(Long id);

    SysMenuVO treeMenu(Long roleId);

    List<Long> getMenuIdsByRoleId(Long roleId);
}
