/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
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
package org.laokou.admin.server.domain.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统角色部门
 * @author laokou
 * @version 1.0
 * @date 2022/7/27 0027 上午 9:20
 */
@Data
@TableName("boot_sys_role_dept")
@Schema(name = "SysRoleDeptDO",description = "系统角色部门实体类")
public class SysRoleDeptDO {

    /**
     * 角色id
     */
    @Schema(name = "roleId",description = "角色id",example = "666")
    private Long roleId;

    /**
     * 部门id
     */
    @Schema(name = "deptId",description = "部门id",example = "999")
    private Long deptId;

}
