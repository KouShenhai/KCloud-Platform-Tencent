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
package org.laokou.admin.server.domain.sys.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.laokou.admin.server.domain.sys.entity.SysRoleDeptDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/7/27 0027 上午 9:21
 */
@Repository
@Mapper
public interface SysRoleDeptMapper extends BaseMapper<SysRoleDeptDO> {
}
