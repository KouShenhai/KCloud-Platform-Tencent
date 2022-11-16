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
import org.laokou.admin.server.interfaces.qo.DefinitionQo;
import org.laokou.admin.client.vo.DefinitionVO;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/7/6 0006 下午 6:10
 */
public interface WorkflowDefinitionApplicationService {

    String BPMN_FILE_SUFFIX = ".bpmn";

    /**
     * 导入流程文件
     * @param name
     * @param in
     * @return
     */
    Boolean importFile(String name, InputStream in);

    /**
     * 分页查询流程
     * @param qo
     * @return
     */
    IPage<DefinitionVO> queryDefinitionPage(DefinitionQo qo);

    /**
     * 查询流程图
     * @param definitionId
     * @param response
     */
    void imageProcess(String definitionId, HttpServletResponse response);

    /**
     * 删除流程
     * @param deploymentId
     * @return
     */
    Boolean deleteDefinition(String deploymentId);

    /**
     * 挂起流程
     * @param definitionId
     * @return
     */
    Boolean suspendDefinition(String definitionId);

    /**
     * 激活流程
     * @param definitionId
     * @return
     */
    Boolean activateDefinition(String definitionId);
}
