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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.laokou.admin.server.application.service.SysResourceApplicationService;
import org.laokou.admin.server.application.service.WorkflowTaskApplicationService;
import org.laokou.admin.client.dto.SysResourceDTO;
import org.laokou.admin.server.interfaces.qo.SysResourceQo;
import org.laokou.admin.client.vo.SysAuditLogVO;
import org.laokou.admin.client.vo.SysResourceVO;
import org.laokou.common.swagger.utils.HttpResult;
import org.laokou.admin.server.infrastructure.annotation.OperateLog;
import org.laokou.redis.annotation.Lock4j;
import org.laokou.redis.enums.LockScope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.*;
/**
 * @author laokou
 * @version 1.0
 * @date 2022/8/19 0019 下午 3:56
 */
@RestController
@Tag(name = "Sys Resource Image API",description = "图片管理API")
@RequestMapping("/sys/resource/image/api")
@RequiredArgsConstructor
public class SysImageApiController {

    private final SysResourceApplicationService sysResourceApplicationService;

    private final WorkflowTaskApplicationService workflowTaskApplicationService;

//    @PostMapping("/upload")
////    @ApiOperation("图片管理>上传")
//    public HttpResult<UploadVO> upload(@RequestPart("file") MultipartFile file) throws Exception {
//        if (file.isEmpty()) {
//            throw new CustomException("上传的文件不能为空");
//        }
//        //文件名
//        final String fileName = file.getOriginalFilename();
//        //文件流
//        final InputStream inputStream = file.getInputStream();
//        //文件大小
//        final Long fileSize = file.getSize();
//        return new HttpResult<UploadVO>().ok(sysResourceApplicationService.uploadResource("image",fileName,inputStream,fileSize));
//    }

    @PostMapping("/syncIndex")
    @Operation(summary = "图片管理>同步索引",description = "图片管理>同步索引")
    @OperateLog(module = "图片管理",name = "索引同步")
    @PreAuthorize("hasAuthority('sys:resource:image:syncIndex')")
    @Lock4j(key = "image_sync_index_lock", scope = LockScope.DISTRIBUTED_LOCK)
    public HttpResult<Boolean> sync(@RequestParam("code") String code) throws InterruptedException {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.syncResourceIndex(code));
    }

    @PostMapping("/createIndex")
    @Operation(summary = "图片管理>创建索引",description = "图片管理>创建索引")
    @OperateLog(module = "图片管理",name = "创建索引")
    @PreAuthorize("hasAuthority('sys:resource:image:createIndex')")
    @Lock4j(key = "image_create_index_lock", scope = LockScope.DISTRIBUTED_LOCK)
    public HttpResult<Boolean> create(@RequestParam("code") String code) {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.createResourceIndex(code));
    }

    @DeleteMapping("/deleteIndex")
    @Operation(summary = "图片管理>创建索引",description = "图片管理>创建索引")
    @OperateLog(module = "图片管理",name = "删除索引")
    @PreAuthorize("hasAuthority('sys:resource:image:deleteIndex')")
    @Lock4j(key = "image_delete_index_lock", scope = LockScope.DISTRIBUTED_LOCK)
    public HttpResult<Boolean> deleteIndex(@RequestParam("code") String code) {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.deleteResourceIndex(code));
    }

    @PostMapping("/query")
    @Operation(summary = "图片管理>查询",description = "图片管理>查询")
    @PreAuthorize("hasAuthority('sys:resource:image:query')")
    public HttpResult<IPage<SysResourceVO>> query(@RequestBody SysResourceQo qo) {
        return new HttpResult<IPage<SysResourceVO>>().ok(sysResourceApplicationService.queryResourcePage(qo));
    }

    @GetMapping(value = "/detail")
    @Operation(summary = "图片管理>详情",description = "图片管理>详情")
    @PreAuthorize("hasAuthority('sys:resource:image:detail')")
    public HttpResult<SysResourceVO> detail(@RequestParam("id") Long id) {
        return new HttpResult<SysResourceVO>().ok(sysResourceApplicationService.getResourceById(id));
    }

    @PostMapping(value = "/insert")
    @Operation(summary = "图片管理>新增",description = "图片管理>新增")
    @OperateLog(module = "图片管理",name = "图片新增")
    @PreAuthorize("hasAuthority('sys:resource:image:insert')")
    public HttpResult<Boolean> insert(@RequestBody SysResourceDTO dto) throws IOException {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.insertResource(dto));
    }

    @PutMapping(value = "/update")
    @Operation(summary = "图片管理>修改",description = "图片管理>修改")
    @OperateLog(module = "图片管理",name = "图片修改")
    @PreAuthorize("hasAuthority('sys:resource:image:update')")
    public HttpResult<Boolean> update(@RequestBody SysResourceDTO dto) throws IOException {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.updateResource(dto));
    }

    @DeleteMapping(value = "/delete")
    @Operation(summary = "图片管理>删除",description = "图片管理>删除")
    @OperateLog(module = "图片管理",name = "图片删除")
    @PreAuthorize("hasAuthority('sys:resource:image:delete')")
    public HttpResult<Boolean> delete(@RequestParam("id") Long id) {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.deleteResource(id));
    }

    @GetMapping(value = "/diagram")
    @Operation(summary = "图片管理>流程图",description = "图片管理>流程图")
    @PreAuthorize("hasAuthority('sys:resource:image:diagram')")
    public void diagram(@RequestParam("processInstanceId")String processInstanceId, HttpServletResponse response) throws IOException {
        workflowTaskApplicationService.diagramProcess(processInstanceId, response);
    }

    @GetMapping("/auditLog")
    @Operation(summary = "图片管理>审批日志",description = "图片管理>审批日志")
    @PreAuthorize("hasAuthority('sys:resource:image:auditLog')")
    public HttpResult<List<SysAuditLogVO>> auditLog(@RequestParam("businessId") Long businessId) {
        return new HttpResult<List<SysAuditLogVO>>().ok(sysResourceApplicationService.queryAuditLogList(businessId));
    }

}
