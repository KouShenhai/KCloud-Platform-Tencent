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
package org.laokou.admin.server.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.laokou.admin.server.interfaces.qo.LoginLogQo;
import org.laokou.admin.server.interfaces.qo.SysOperateLogQo;
import org.laokou.admin.client.vo.SysLoginLogVO;
import org.laokou.admin.client.vo.SysOperateLogVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Kou Shenhai
 */
public interface SysLogApplicationService {

    /**
     * 分页查询操作日志
     * @param qo
     * @return
     */
    IPage<SysOperateLogVO> queryOperateLogPage(SysOperateLogQo qo);

    /**
     * 导出全部
     * @param qo
     * @param response
     * @throws IOException
     */
    void exportOperateLog(SysOperateLogQo qo, HttpServletResponse response) throws IOException;

    /**
     * 分页查询登录日志
     * @param qo
     * @return
     */
    IPage<SysLoginLogVO> queryLoginLogPage(LoginLogQo qo);
}
