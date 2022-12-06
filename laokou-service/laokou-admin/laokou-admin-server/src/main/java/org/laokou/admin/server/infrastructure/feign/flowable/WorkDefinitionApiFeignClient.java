package org.laokou.admin.server.infrastructure.feign.flowable;
import org.laokou.admin.server.infrastructure.feign.flowable.dto.DefinitionDTO;
import org.laokou.admin.server.infrastructure.feign.flowable.factory.WorkTaskApiFeignClientFallbackFactory;
import org.laokou.admin.server.infrastructure.feign.flowable.vo.DefinitionVO;
import org.laokou.admin.server.infrastructure.feign.flowable.vo.PageVO;
import org.laokou.common.core.constant.ServiceConstant;
import org.laokou.common.core.utils.HttpResultUtil;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Kou Shenhai
 */
@FeignClient(name = ServiceConstant.LAOKOU_FLOWABLE,path = "/work/definition/api", fallback = WorkTaskApiFeignClientFallbackFactory.class)
@Service
public interface WorkDefinitionApiFeignClient {

    /**
     * 新增流程
     * @param name
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/insert")
    HttpResultUtil<Boolean> insert(@RequestParam("name")String name, @RequestPart("file") MultipartFile file);

    /**
     * 查询流程
     * @param dto
     * @return
     */
    @PostMapping(value = "/query")
    HttpResultUtil<PageVO<DefinitionVO>> query(@RequestBody DefinitionDTO dto);

    /**
     * 流程图
     * @param definitionId
     * @param response
     */
    @GetMapping(value = "/diagram")
    void diagram(@RequestParam("definitionId")String definitionId, HttpServletResponse response);

    /**
     * 删除流程
     * @param deploymentId
     * @return
     */
    @DeleteMapping(value = "/delete")
    HttpResultUtil<Boolean> delete(@RequestParam("deploymentId")String deploymentId);

    /**
     * 挂起流程
     * @param definitionId
     * @return
     */
    @PutMapping(value = "/suspend")
    HttpResultUtil<Boolean> suspend(@RequestParam("definitionId")String definitionId);

    /**
     * 激活流程
     * @param definitionId
     * @return
     */
    @PutMapping(value = "/activate")
    HttpResultUtil<Boolean> activate(@RequestParam("definitionId")String definitionId);

}
