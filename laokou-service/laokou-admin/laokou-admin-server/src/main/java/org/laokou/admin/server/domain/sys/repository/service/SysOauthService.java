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
import org.laokou.admin.server.domain.sys.entity.SysOauthDO;
import org.laokou.admin.server.interfaces.qo.SysOauthQo;
import org.laokou.admin.client.vo.SysOauthVO;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/8/11 0011 上午 9:49
 */
public interface SysOauthService extends IService<SysOauthDO> {

    /**
     * 根据id删除认证配置
     * @param id
     */
    void deleteOauth(Long id);

    /**
     * 根据id查询认证配置
     * @param id
     * @return
     */
    SysOauthVO getOauthById(Long id);

    /**
     * 分页查询认证配置
     * @param page
     * @param qo
     * @return
     */
    IPage<SysOauthVO> getOauthList(IPage<SysOauthVO> page, SysOauthQo qo);

}
