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
package org.laokou.auth.server.domain.sys.repository.service.impl;

import lombok.RequiredArgsConstructor;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.server.domain.sys.repository.mapper.SysDeptMapper;
import org.laokou.auth.server.domain.sys.repository.service.SysDeptService;
import org.laokou.common.core.enums.SuperAdminEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
/**
 * @author laokou
 */
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class SysDeptServiceImpl implements SysDeptService {

    private final SysDeptMapper sysDeptMapper;

    @Override
    public List<Long> getDeptIds(Integer superAdmin,Long userId) {
        if (SuperAdminEnum.YES.ordinal() == superAdmin) {
            return sysDeptMapper.getDeptIds();
        }
        return sysDeptMapper.getDeptIdsByUserId(userId);
    }
}
