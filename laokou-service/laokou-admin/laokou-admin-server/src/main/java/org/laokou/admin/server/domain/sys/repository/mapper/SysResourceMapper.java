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
package org.laokou.admin.server.domain.sys.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.laokou.admin.server.domain.sys.entity.SysResourceDO;
import org.laokou.admin.client.index.ResourceIndex;
import org.laokou.admin.server.interfaces.qo.SysResourceQo;
import org.laokou.admin.client.vo.SysResourceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/8/19 0019 下午 4:11
 */
@Mapper
@Repository
public interface SysResourceMapper extends BaseMapper<SysResourceDO> {

    /**
     * 分页查询资源
     * @param page
     * @param qo
     * @return
     */
    IPage<SysResourceVO> getResourceList(IPage<SysResourceVO> page, @Param("qo") SysResourceQo qo);

    /**
     * 根据id查询资源
     * @param id
     * @return
     */
    SysResourceVO getResourceById(@Param("id") Long id);

    /**
     * 根据id删除资源
     * @param id
     */
    void deleteResource(@Param("id") Long id);

    /**
     * 根据编码查询资源总数
     * @param code
     * @return
     */
    Long getResourceTotal(@Param("code")String code);

    /**
     * 根据编码查询资源的年分区列表
     * @param code
     * @return
     */
    List<String> getResourceYmPartitionList(@Param("code")String code);

    /**
     * 根据偏移量查询资源列表
     * @param pageSize
     * @param pageIndex
     * @param code
     * @return
     */
    List<ResourceIndex> getResourceIndexList(@Param("pageSize")Integer pageSize, @Param("pageIndex")Integer pageIndex, @Param("code")String code);

}
