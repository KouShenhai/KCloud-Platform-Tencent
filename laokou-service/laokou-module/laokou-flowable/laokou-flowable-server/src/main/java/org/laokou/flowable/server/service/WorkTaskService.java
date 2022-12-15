package org.laokou.flowable.server.service;
import jakarta.servlet.http.HttpServletResponse;
import org.laokou.flowable.client.dto.AuditDTO;
import org.laokou.flowable.client.dto.ProcessDTO;
import org.laokou.flowable.client.dto.TaskDTO;
import org.laokou.flowable.client.vo.AssigneeVO;
import org.laokou.flowable.client.vo.PageVO;
import org.laokou.flowable.client.vo.TaskVO;
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
