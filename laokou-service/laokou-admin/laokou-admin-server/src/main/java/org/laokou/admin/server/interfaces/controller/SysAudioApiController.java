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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.laokou.admin.client.constant.CacheConstant;
import org.laokou.admin.client.enums.CacheEnum;
import org.laokou.admin.server.application.service.SysResourceApplicationService;
import org.laokou.admin.server.application.service.WorkflowTaskApplicationService;
import org.laokou.admin.client.dto.SysResourceDTO;
import org.laokou.admin.server.infrastructure.annotation.DataCache;
import org.laokou.admin.server.interfaces.qo.SysResourceQo;
import org.laokou.admin.client.vo.SysAuditLogVO;
import org.laokou.admin.client.vo.SysResourceVO;
import org.laokou.common.swagger.utils.HttpResult;
import org.laokou.admin.server.infrastructure.annotation.OperateLog;
import org.laokou.oss.client.vo.UploadVO;
import org.laokou.redis.annotation.Lock4j;
import org.laokou.redis.enums.LockScope;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
/**
 * @author laokou
 * @version 1.0
 * @date 2022/8/19 0019 ?????? 3:56
 */
@RestController
@Tag(name = "Sys Resource Audio API",description = "????????????API")
@RequestMapping("/sys/resource/audio/api")
@RequiredArgsConstructor
public class SysAudioApiController {

    private final SysResourceApplicationService sysResourceApplicationService;

    private final WorkflowTaskApplicationService workflowTaskApplicationService;

    @GetMapping("/auditLog")
    @Parameter(name = "businessId",description = "??????id", required = true, example = "123")
    @Operation(summary = "????????????>????????????",description = "????????????>????????????")
    @PreAuthorize("hasAuthority('sys:resource:audio:auditLog')")
    public HttpResult<List<SysAuditLogVO>> auditLog(@RequestParam("businessId") Long businessId) {
        return new HttpResult<List<SysAuditLogVO>>().ok(sysResourceApplicationService.queryAuditLogList(businessId));
    }

    @PostMapping("/syncIndex")
    @Operation(summary = "????????????>????????????",description = "????????????>????????????")
    @OperateLog(module = "????????????",name = "????????????")
    @Lock4j(key = "audio_sync_index_lock", scope = LockScope.DISTRIBUTED_LOCK)
    @PreAuthorize("hasAuthority('sys:resource:audio:syncIndex')")
    @Parameter(name = "code",description = "??????",example = "audio")
    public HttpResult<Boolean> syncIndex(@RequestParam("code") String code) throws InterruptedException {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.syncResourceIndex(code));
    }

    @PostMapping("/createIndex")
    @Operation(summary = "????????????>????????????",description = "????????????>????????????")
    @Parameter(name = "code",description = "??????",example = "audio")
    @OperateLog(module = "????????????",name = "????????????")
    @PreAuthorize("hasAuthority('sys:resource:audio:createIndex')")
    @Lock4j(key = "audio_create_index_lock", scope = LockScope.DISTRIBUTED_LOCK)
    public HttpResult<Boolean> createIndex(@RequestParam("code") String code) {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.createResourceIndex(code));
    }

    @DeleteMapping("/deleteIndex")
    @Operation(summary = "????????????>????????????",description = "????????????>????????????")
    @Parameter(name = "code",description = "??????",example = "audio")
    @OperateLog(module = "????????????",name = "????????????")
    @PreAuthorize("hasAuthority('sys:resource:audio:deleteIndex')")
    @Lock4j(key = "audio_delete_index_lock", scope = LockScope.DISTRIBUTED_LOCK)
    public HttpResult<Boolean> deleteIndex(@RequestParam("code") String code) {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.deleteResourceIndex(code));
    }

    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "????????????>??????",description = "????????????>??????")
    public HttpResult<UploadVO> upload(@RequestPart("file") MultipartFile file, @RequestParam("md5")String md5) throws Exception {
        return new HttpResult<UploadVO>().ok(sysResourceApplicationService.uploadResource("audio",file,md5));
    }

    @PostMapping("/query")
    @Operation(summary = "????????????>??????",description = "????????????>??????")
    @PreAuthorize("hasAuthority('sys:resource:audio:query')")
    public HttpResult<IPage<SysResourceVO>> query(@RequestBody SysResourceQo qo) {
        return new HttpResult<IPage<SysResourceVO>>().ok(sysResourceApplicationService.queryResourcePage(qo));
    }

    @GetMapping(value = "/detail")
    @Operation(summary = "????????????>??????",description = "????????????>??????")
    @PreAuthorize("hasAuthority('sys:resource:audio:detail')")
    @DataCache(name = CacheConstant.AUDIO,key = "#id")
    public HttpResult<SysResourceVO> detail(@RequestParam("id") Long id) {
        return new HttpResult<SysResourceVO>().ok(sysResourceApplicationService.getResourceById(id));
    }

    @PostMapping(value = "/insert")
    @Operation(summary = "????????????>??????",description = "????????????>??????")
    @OperateLog(module = "????????????",name = "????????????")
    @PreAuthorize("hasAuthority('sys:resource:audio:insert')")
    @DataCache(name = CacheConstant.AUDIO,key = "#dto.id",type = CacheEnum.DEL)
    public HttpResult<Boolean> insert(@RequestBody SysResourceDTO dto) throws IOException {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.insertResource(dto));
    }

    @PutMapping(value = "/update")
    @Operation(summary = "????????????>??????",description = "????????????>??????")
    @OperateLog(module = "????????????",name = "????????????")
    @PreAuthorize("hasAuthority('sys:resource:audio:update')")
    @DataCache(name = CacheConstant.AUDIO,key = "#dto.id",type = CacheEnum.DEL)
    public HttpResult<Boolean> update(@RequestBody SysResourceDTO dto) throws IOException {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.updateResource(dto));
    }

    @DeleteMapping(value = "/delete")
    @Operation(summary = "????????????>??????",description = "????????????>??????")
    @OperateLog(module = "????????????",name = "????????????")
    @PreAuthorize("hasAuthority('sys:resource:audio:delete')")
    @DataCache(name = CacheConstant.AUDIO,key = "#id",type = CacheEnum.DEL)
    public HttpResult<Boolean> delete(@RequestParam("id") Long id) {
        return new HttpResult<Boolean>().ok(sysResourceApplicationService.deleteResource(id));
    }

    @GetMapping(value = "/diagram")
    @Operation(summary = "????????????>?????????",description = "????????????>?????????")
    @PreAuthorize("hasAuthority('sys:resource:audio:diagram')")
    public void diagram(@RequestParam("processInstanceId")String processInstanceId, HttpServletResponse response) throws IOException {
        workflowTaskApplicationService.diagramProcess(processInstanceId, response);
    }
}
