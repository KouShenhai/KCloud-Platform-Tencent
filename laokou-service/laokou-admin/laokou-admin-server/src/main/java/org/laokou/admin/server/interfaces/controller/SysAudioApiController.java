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
package org.laokou.admin.server.interfaces.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletResponse;
import org.laokou.admin.server.application.service.SysResourceApplicationService;
import org.laokou.admin.server.application.service.WorkflowTaskApplicationService;
import org.laokou.admin.client.dto.SysResourceDTO;
import org.laokou.admin.server.interfaces.qo.SysResourceQo;
import org.laokou.admin.client.vo.SysAuditLogVO;
import org.laokou.admin.client.vo.SysResourceVO;
import org.laokou.common.swagger.exception.CustomException;
import org.laokou.common.swagger.utils.HttpResult;
import org.laokou.oss.client.vo.UploadVO;
import org.laokou.admin.server.infrastructure.annotation.OperateLog;
import org.laokou.redis.annotation.Lock4j;
import org.laokou.redis.enums.LockScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
/**
 * @author laokou
 * @version 1.0
 * @date 2022/8/19 0019 下午 3:56
 */
@RestController
//@Api(value = "音频管理API",protocols = "http",tags = "音频管理API")
@RequestMapping("/sys/resource/audio/api")
public class SysAudioApiController {

    @Autowired
    private SysResourceApplicationService sysResourceApplicationService;

    @Autowired
    private WorkflowTaskApplicationService workflowTaskApplicationService;

    @GetMapping("/auditLog")
//    @ApiOperation("音频管理>审批日志")
    @PreAuthorize("hasAuthority('sys:resource:audio:auditLog')")
    public HttpResult<List<SysAuditLogVO>> auditLog(@RequestParam("businessId") Long businessId) {
        return new HttpResult<List<SysAuditLogVO>>().ok(sysResourceApplicationService.queryAuditLogList(businessId));
    }

    @PostMapping("/syncIndex")
//    @ApiOperation("音频管理>同步索引")
    @OperateLog(module = "音频管理",name = "同步索引")
    @Lock4j(key = "audio_sync_index_lock", scope = LockScope.DISTRIBUTED_LOCK)
    @PreAuthorize("hasAuthority('sys:resource:audio:syncIndex')")
    public HttpResult<Boolean> syncIndex(@RequestParam("code") String code) throws InterruptedException {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.syncResourceIndex(code));
    }

    @PostMapping("/createIndex")
//    @ApiOperation("音频管理>创建索引")
    @OperateLog(module = "音频管理",name = "创建索引")
    @PreAuthorize("hasAuthority('sys:resource:audio:createIndex')")
    @Lock4j(key = "audio_create_index_lock", scope = LockScope.DISTRIBUTED_LOCK)
    public HttpResult<Boolean> createIndex(@RequestParam("code") String code) {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.createResourceIndex(code));
    }

    @DeleteMapping("/deleteIndex")
//    @ApiOperation("音频管理>删除索引")
    @OperateLog(module = "音频管理",name = "删除索引")
    @PreAuthorize("hasAuthority('sys:resource:audio:deleteIndex')")
    @Lock4j(key = "audio_delete_index_lock", scope = LockScope.DISTRIBUTED_LOCK)
    public HttpResult<Boolean> deleteIndex(@RequestParam("code") String code) {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.deleteResourceIndex(code));
    }

    @PostMapping("/upload")
//    @ApiOperation("音频管理>上传")
    public HttpResult<UploadVO> upload(@RequestPart("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new CustomException("上传的文件不能为空");
        }
        //文件名
        final String fileName = file.getOriginalFilename();
        //文件流
        final InputStream inputStream = file.getInputStream();
        //文件大小
        final Long fileSize = file.getSize();
        return new HttpResult<UploadVO>().ok(sysResourceApplicationService.uploadResource("audio",fileName,inputStream,fileSize));
    }

    @PostMapping("/query")
//    @ApiOperation("音频管理>查询")
    @PreAuthorize("hasAuthority('sys:resource:audio:query')")
    public HttpResult<IPage<SysResourceVO>> query(@RequestBody SysResourceQo qo) {
        return new HttpResult<IPage<SysResourceVO>>().ok(sysResourceApplicationService.queryResourcePage(qo));
    }

    @GetMapping(value = "/detail")
//    @ApiOperation("音频管理>详情")
    @PreAuthorize("hasAuthority('sys:resource:audio:detail')")
    public HttpResult<SysResourceVO> detail(@RequestParam("id") Long id) {
        return new HttpResult<SysResourceVO>().ok(sysResourceApplicationService.getResourceById(id));
    }

    @PostMapping(value = "/insert")
//    @ApiOperation("音频管理>新增")
    @OperateLog(module = "音频管理",name = "音频新增")
    @PreAuthorize("hasAuthority('sys:resource:audio:insert')")
    public HttpResult<Boolean> insert(@RequestBody SysResourceDTO dto) throws IOException {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.insertResource(dto));
    }

    @PutMapping(value = "/update")
//    @ApiOperation("音频管理>修改")
    @OperateLog(module = "音频管理",name = "音频修改")
    @PreAuthorize("hasAuthority('sys:resource:audio:update')")
    public HttpResult<Boolean> update(@RequestBody SysResourceDTO dto) throws IOException {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.updateResource(dto));
    }

    @DeleteMapping(value = "/delete")
//    @ApiOperation("音频管理>删除")
    @OperateLog(module = "音频管理",name = "音频删除")
    @PreAuthorize("hasAuthority('sys:resource:audio:delete')")
    public HttpResult<Boolean> delete(@RequestParam("id") Long id) {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.deleteResource(id));
    }

    @GetMapping(value = "/diagram")
//    @ApiOperation(value = "音频管理>流程图")
    @PreAuthorize("hasAuthority('sys:resource:audio:diagram')")
    public void diagram(@RequestParam("processInstanceId")String processInstanceId, HttpServletResponse response) throws IOException {
        workflowTaskApplicationService.diagramProcess(processInstanceId, response);
    }
}
