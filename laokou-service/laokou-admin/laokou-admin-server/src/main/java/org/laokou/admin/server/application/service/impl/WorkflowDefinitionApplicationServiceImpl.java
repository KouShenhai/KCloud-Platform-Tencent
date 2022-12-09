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
package org.laokou.admin.server.application.service.impl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.laokou.admin.server.application.service.WorkflowDefinitionApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.laokou.admin.server.interfaces.qo.DefinitionQo;
import org.laokou.flowable.client.vo.DefinitionVO;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/7/6 0006 下午 6:11
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowDefinitionApplicationServiceImpl implements WorkflowDefinitionApplicationService {


    @Override
    public Boolean insertDefinition(String name, InputStream in) {
        return null;
    }

    @Override
    public IPage<DefinitionVO> queryDefinitionPage(DefinitionQo qo) {
        return null;
    }

    @Override
    public void diagramDefinition(String definitionId, HttpServletResponse response) {

    }

    @Override
    public Boolean deleteDefinition(String deploymentId) {
        return null;
    }

    @Override
    public Boolean suspendDefinition(String definitionId) {
        return null;
    }

    @Override
    public Boolean activateDefinition(String definitionId) {
        return null;
    }
//
//    @Autowired
//    private RepositoryService repositoryService;
//
//    @Override
//
//    public Boolean importFile(String name, InputStream in) {
//        String processName = name + BPMN_FILE_SUFFIX;
//        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
//                .name(processName)
//                .key(name)
//                .addInputStream(processName, in);
//        deploymentBuilder.deploy();
//        return true;
//    }
//
//    @Override
//    public IPage<DefinitionVO> queryDefinitionPage(DefinitionQo qo) {
//        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
//                .latestVersion()
//                .orderByProcessDefinitionKey().asc();
//        if (StringUtil.isNotEmpty(qo.getProcessName())) {
//            processDefinitionQuery = processDefinitionQuery.processDefinitionNameLike("%" + qo.getProcessName() + "%");
//        }
//        long pageTotal = processDefinitionQuery.count();
//        Integer pageNum = qo.getPageNum();
//        Integer pageSize = qo.getPageSize();
//        IPage<DefinitionVO> page = new Page<>(pageNum,pageSize,pageTotal);
//        int pageIndex = pageSize * (pageNum - 1);
//        List<ProcessDefinition> definitionList = processDefinitionQuery.listPage(pageIndex, pageSize);
//        List<DefinitionVO> definitions = new ArrayList<>(definitionList.size());
//        for (ProcessDefinition processDefinition : definitionList) {
//            DefinitionVO vo = new DefinitionVO();
//            vo.setDefinitionId(processDefinition.getId());
//            vo.setProcessKey(processDefinition.getKey());
//            vo.setProcessName(processDefinition.getName());
//            vo.setDeploymentId(processDefinition.getDeploymentId());
//            vo.setSuspended(processDefinition.isSuspended());
//            definitions.add(vo);
//        }
//        page.setRecords(definitions);
//        return page;
//    }
//
//    @Override
//    public void imageProcess(String definitionId, HttpServletResponse response) {
//        //获取图片流
//        DefaultProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
//        //输出为图片
//        InputStream inputStream = diagramGenerator.generateDiagram(
//                bpmnModel,
//                "png",
//                Collections.emptyList(),
//                Collections.emptyList(),
//                "宋体",
//                "宋体",
//                "宋体",
//                null,
//                1.0,
//                false);
//        try(ServletOutputStream os = response.getOutputStream()) {
//            BufferedImage image = ImageIO.read(inputStream);
//            if (null != image) {
//                response.setHeader("Cache-Control", "no-store, no-cache");
//                response.setContentType("image/png");
//                ImageIO.write(image,"png",os);
//            }
//        } catch (IOException e) {
//            log.error("错误信息：{}",e.getMessage());
//        }
//    }
//
//    @Override
//    public Boolean deleteDefinition(String deploymentId) {
//        // true允许级联删除 不设置会导致数据库关联异常
//        repositoryService.deleteDeployment(deploymentId,true);
//        return true;
//    }
//
//    @Override
//    public Boolean suspendDefinition(String definitionId) {
//        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(definitionId).singleResult();
//        if (processDefinition.isSuspended()) {
//            throw new CustomException("挂起失败，流程已挂起");
//        } else {
//            // 挂起
//            repositoryService.suspendProcessDefinitionById(definitionId, true, null);
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean activateDefinition(String definitionId) {
//        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(definitionId).singleResult();
//        if (processDefinition.isSuspended()) {
//            // 激活
//            repositoryService.activateProcessDefinitionById(definitionId, true, null);
//        } else {
//            throw new CustomException("激活失败，流程已激活");
//        }
//        return true;
//    }

}
