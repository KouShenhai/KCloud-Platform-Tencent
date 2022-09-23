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
package org.laokou.admin.domain.sys.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.laokou.admin.domain.sys.entity.SysOauthDO;
import org.laokou.admin.interfaces.qo.SysOauthQO;
import org.laokou.admin.interfaces.vo.SysOauthVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/8/11 0011 上午 9:52
 */
@Mapper
@Repository
public interface SysOauthMapper extends BaseMapper<SysOauthDO> {

    void deleteOauth(@Param("id") Long id);

    SysOauthVO getOauthById(@Param("id") Long id);

    IPage<SysOauthVO> getOauthList(IPage<SysOauthVO> page, @Param("qo") SysOauthQO qo);

}
