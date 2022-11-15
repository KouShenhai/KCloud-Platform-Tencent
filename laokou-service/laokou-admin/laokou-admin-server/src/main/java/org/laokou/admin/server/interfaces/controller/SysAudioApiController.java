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
package org.laokou.admin.server.interfaces.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.laokou.admin.server.application.service.SysResourceApplicationService;
import org.laokou.admin.server.application.service.WorkflowTaskApplicationService;
import org.laokou.admin.client.dto.SysResourceDTO;
import org.laokou.admin.server.interfaces.qo.SysResourceQO;
import org.laokou.admin.client.vo.SysResourceAuditLogVO;
import org.laokou.admin.client.vo.SysResourceVO;
import org.laokou.common.core.exception.CustomException;
import org.laokou.common.core.utils.HttpResultUtil;
import org.laokou.oss.client.vo.UploadVO;
import org.laokou.admin.server.infrastructure.component.annotation.OperateLog;
import org.laokou.redis.annotation.Lock4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.laokou.redis.enums.LockScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/8/19 0019 下午 3:56
 */
@RestController
@Api(value = "音频管理API",protocols = "http",tags = "音频管理API")
@RequestMapping("/sys/resource/audio/api")
public class SysAudioApiController {

    @Autowired
    private SysResourceApplicationService sysResourceApplicationService;

    @Autowired
    private WorkflowTaskApplicationService workflowTaskApplicationService;

    @GetMapping("/auditLog")
    @ApiOperation("音频管理>审批日志")
    @PreAuthorize("hasAuthority('sys:resource:audio:auditLog')")
    public HttpResultUtil<List<SysResourceAuditLogVO>> auditLog(@RequestParam("resourceId") Long resourceId) {
        return new HttpResultUtil<List<SysResourceAuditLogVO>>().ok(sysResourceApplicationService.queryAuditLogList(resourceId));
    }

    @PostMapping("/sync")
    @ApiOperation("音频管理>同步")
    @Lock4j(key = "audio_sync_lock",scope = LockScope.DISTRIBUTED_LOCK)
    @OperateLog(module = "音频管理",name = "音频同步")
    @PreAuthorize("hasAuthority('sys:resource:audio:sync')")
    public HttpResultUtil<Boolean> sync(@RequestParam("code") String code) throws InterruptedException {
        return new HttpResultUtil<Boolean>().ok(sysResourceApplicationService.syncAsyncBatchResource(code));
    }

    @PostMapping("/upload")
    @ApiOperation("音频管理>上传")
    public HttpResultUtil<UploadVO> upload(@RequestPart("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new CustomException("上传的文件不能为空");
        }
        //文件名
        final String fileName = file.getOriginalFilename();
        //文件流
        final InputStream inputStream = file.getInputStream();
        //文件大小
        final Long fileSize = file.getSize();
        return new HttpResultUtil<UploadVO>().ok(sysResourceApplicationService.uploadResource("audio",fileName,inputStream,fileSize));
    }

    @PostMapping("/query")
    @ApiOperation("音频管理>查询")
    @PreAuthorize("hasAuthority('sys:resource:audio:query')")
    public HttpResultUtil<IPage<SysResourceVO>> query(@RequestBody SysResourceQO qo) {
        return new HttpResultUtil<IPage<SysResourceVO>>().ok(sysResourceApplicationService.queryResourcePage(qo));
    }

    @GetMapping(value = "/detail")
    @ApiOperation("音频管理>详情")
    @PreAuthorize("hasAuthority('sys:resource:audio:detail')")
    public HttpResultUtil<SysResourceVO> detail(@RequestParam("id") Long id) {
        return new HttpResultUtil<SysResourceVO>().ok(sysResourceApplicationService.getResourceById(id));
    }

    @PostMapping(value = "/insert")
    @ApiOperation("音频管理>新增")
    @OperateLog(module = "音频管理",name = "音频新增")
    @PreAuthorize("hasAuthority('sys:resource:audio:insert')")
    public HttpResultUtil<Boolean> insert(@RequestBody SysResourceDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysResourceApplicationService.insertResource(dto));
    }

    @PutMapping(value = "/update")
    @ApiOperation("音频管理>修改")
    @OperateLog(module = "音频管理",name = "音频修改")
    @PreAuthorize("hasAuthority('sys:resource:audio:update')")
    public HttpResultUtil<Boolean> update(@RequestBody SysResourceDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysResourceApplicationService.updateResource(dto));
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation("音频管理>删除")
    @OperateLog(module = "音频管理",name = "音频删除")
    @PreAuthorize("hasAuthority('sys:resource:audio:delete')")
    public HttpResultUtil<Boolean> delete(@RequestParam("id") Long id) {
        return new HttpResultUtil<Boolean>().ok(sysResourceApplicationService.deleteResource(id));
    }

    @GetMapping(value = "/diagram")
    @ApiOperation(value = "音频管理>流程图")
    @PreAuthorize("hasAuthority('sys:resource:audio:diagram')")
    public void diagram(@RequestParam("processInstanceId")String processInstanceId, HttpServletResponse response) throws IOException {
        workflowTaskApplicationService.diagramProcess(processInstanceId, response);
    }
}
