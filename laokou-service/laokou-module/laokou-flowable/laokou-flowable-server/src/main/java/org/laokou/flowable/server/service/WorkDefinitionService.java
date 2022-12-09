package org.laokou.flowable.server.service;
import org.laokou.flowable.client.dto.DefinitionDTO;
import org.laokou.flowable.client.dto.FileDTO;
import org.laokou.flowable.client.vo.DefinitionVO;
import org.laokou.flowable.client.vo.PageVO;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
/**
 * @author Kou Shenhai
 */
public interface WorkDefinitionService {

    /**
     * 新增流程文件
     * @param dto
     * @return
     */
    Boolean insertDefinition(FileDTO dto);

    /**
     * 分页查询流程
     * @param dto
     * @return
     */
    PageVO<DefinitionVO> queryDefinitionPage(DefinitionDTO dto);

    /**
     * 查看流程图
     * @param definitionId
     * @param response
     */
    void diagramDefinition(String definitionId, HttpServletResponse response);

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
