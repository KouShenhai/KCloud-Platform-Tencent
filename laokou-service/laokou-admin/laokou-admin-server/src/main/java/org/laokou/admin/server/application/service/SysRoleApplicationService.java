/**
 * Copyright (c) 2022 KCloud-Platform-Netflix Authors. All Rights Reserved.
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

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.laokou.admin.client.dto.SysRoleDTO;
import org.laokou.admin.server.interfaces.qo.SysRoleQO;
import org.laokou.admin.client.vo.SysRoleVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface SysRoleApplicationService {

    IPage<SysRoleVO> queryRolePage(SysRoleQO qo);

    List<SysRoleVO> getRoleList(SysRoleQO qo);

    SysRoleVO getRoleById(Long id);

    Boolean insertRole(SysRoleDTO dto);

    Boolean updateRole(SysRoleDTO dto);

    Boolean deleteRole(Long id);
}
