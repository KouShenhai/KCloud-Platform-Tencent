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
package org.laokou.admin.server.domain.sys.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.laokou.admin.server.domain.sys.entity.SysDeptDO;
import org.laokou.admin.server.interfaces.qo.SysDeptQo;
import org.laokou.admin.client.vo.SysDeptVO;

import java.util.List;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/7/26 0026 下午 4:12
 */
public interface SysDeptService extends IService<SysDeptDO> {

    /**
     * 获取部门
     * @param qo
     * @return
     */
    List<SysDeptVO> getDeptList(SysDeptQo qo);

    /**
     * 删除部门
     * @param id
     */
    void deleteDept(Long id);

    /**
     * 通过id查询部门
     * @param id
     * @return
     */
    SysDeptVO getDept(Long id);

    /**
     * 通过角色id获取部门ids
     * @param roleId
     * @return
     */
    List<Long> getDeptIdsByRoleId(Long roleId);

    /**
     * 根据id修改部门路径
     * @param id
     * @param pid
     */
    void updateDeptPath1ById(Long id,Long pid);

    /**
     * 根据id修改部门路径
     * @param id
     * @param pid
     */
    void updateDeptPath2ById(Long id,Long pid);
}
