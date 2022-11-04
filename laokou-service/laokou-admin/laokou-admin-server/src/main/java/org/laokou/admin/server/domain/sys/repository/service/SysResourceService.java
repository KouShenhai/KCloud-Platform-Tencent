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
package org.laokou.admin.server.domain.sys.repository.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.laokou.admin.server.domain.sys.entity.SysResourceDO;
import org.laokou.admin.client.index.ResourceIndex;
import org.laokou.admin.server.interfaces.qo.SysResourceQO;
import org.laokou.admin.client.vo.SysResourceVO;

import java.util.List;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/8/19 0019 下午 4:11
 */
public interface SysResourceService extends IService<SysResourceDO> {
    IPage<SysResourceVO> getResourceList(IPage<SysResourceVO> page, SysResourceQO qo);

    SysResourceVO getResourceById(Long id);

    void deleteResource(Long id);

    Long getResourceTotal(String code);

    List<String> getResourceYMPartitionList(String code);

    List<ResourceIndex> getResourceIndexList(Integer pageSize, Integer pageIndex, String code);
}
