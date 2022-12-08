package org.laokou.flowable.server.service;

import org.laokou.flowable.server.dto.AuditDTO;
import org.laokou.flowable.server.dto.ProcessDTO;
import org.laokou.flowable.server.dto.TaskDTO;
import org.laokou.flowable.server.vo.AssigneeVO;
import org.laokou.flowable.server.vo.PageVO;
import org.laokou.flowable.server.vo.TaskVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Kou Shenhai
 */
public interface WorkTaskService {

    /**
     * 审批任务
     * @param dto
     * @return
     */
    AssigneeVO auditTask(AuditDTO dto);

    /**
     * 开始任务
     * @param dto
     * @return
     */
    AssigneeVO startTask(ProcessDTO dto);

    /**
     * 任务分页
     * @param dto
     * @return
     */
    PageVO<TaskVO> queryTaskPage(TaskDTO dto);

    /**
     * 任务流程图
     * @param processInstanceId
     * @param response
     * @throws IOException
     */
    void diagramTask(String processInstanceId, HttpServletResponse response) throws IOException;

}