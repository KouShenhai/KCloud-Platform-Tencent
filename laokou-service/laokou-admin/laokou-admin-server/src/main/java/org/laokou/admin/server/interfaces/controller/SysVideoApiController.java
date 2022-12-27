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
import org.laokou.common.core.exception.CustomException;
import org.laokou.common.core.utils.HttpResultUtil;
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
//@Api(value = "视频管理API",protocols = "http",tags = "视频管理API")
@RequestMapping("/sys/resource/video/api")
public class SysVideoApiController {

    @Autowired
    private SysResourceApplicationService sysResourceApplicationService;

    @Autowired
    private WorkflowTaskApplicationService workflowTaskApplicationService;

    @PostMapping("/upload")
//    @ApiOperation("视频管理>上传")
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
        return new HttpResultUtil<UploadVO>().ok(sysResourceApplicationService.uploadResource("video",fileName,inputStream,fileSize));
    }

    @PostMapping("/query")
//    @ApiOperation("视频管理>查询")
    @PreAuthorize("hasAuthority('sys:resource:video:query')")
    public HttpResultUtil<IPage<SysResourceVO>> query(@RequestBody SysResourceQo qo) {
        return new HttpResultUtil<IPage<SysResourceVO>>().ok(sysResourceApplicationService.queryResourcePage(qo));
    }

    @PostMapping("/syncIndex")
//    @ApiOperation("视频管理>同步索引")
    @OperateLog(module = "视频管理",name = "索引同步")
    @PreAuthorize("hasAuthority('sys:resource:video:syncIndex')")
    @Lock4j(key = "video_sync_index_lock",scope = LockScope.DISTRIBUTED_LOCK)
    public HttpResultUtil<Boolean> sync(@RequestParam("code") String code) throws InterruptedException {
        return new HttpResultUtil<Boolean>().ok(sysResourceApplicationService.syncResourceIndex(code));
    }

    @PostMapping("/createIndex")
//    @ApiOperation("视频管理>创建索引")
    @OperateLog(module = "视频管理",name = "创建索引")
    @PreAuthorize("hasAuthority('sys:resource:video:createIndex')")
    @Lock4j(key = "video_create_index_lock",scope = LockScope.DISTRIBUTED_LOCK)
    public HttpResultUtil<Boolean> createIndex(@RequestParam("code") String code) {
        return new HttpResultUtil<Boolean>().ok(sysResourceApplicationService.createResourceIndex(code));
    }

    @DeleteMapping("/deleteIndex")
//    @ApiOperation("视频管理>创建索引")
    @OperateLog(module = "视频管理",name = "删除索引")
    @PreAuthorize("hasAuthority('sys:resource:video:deleteIndex')")
    @Lock4j(key = "video_delete_index_lock",scope = LockScope.DISTRIBUTED_LOCK)
    public HttpResultUtil<Boolean> deleteIndex(@RequestParam("code") String code) {
        return new HttpResultUtil<Boolean>().ok(sysResourceApplicationService.deleteResourceIndex(code));
    }

    @GetMapping(value = "/detail")
//    @ApiOperation("视频管理>详情")
    @PreAuthorize("hasAuthority('sys:resource:video:detail')")
    public HttpResultUtil<SysResourceVO> detail(@RequestParam("id") Long id) {
        return new HttpResultUtil<SysResourceVO>().ok(sysResourceApplicationService.getResourceById(id));
    }

    @PostMapping(value = "/insert")
//    @ApiOperation("视频管理>新增")
    @OperateLog(module = "视频管理",name = "视频新增")
    @PreAuthorize("hasAuthority('sys:resource:video:insert')")
    public HttpResultUtil<Boolean> insert(@RequestBody SysResourceDTO dto) throws IOException {
        return new HttpResultUtil<Boolean>().ok(sysResourceApplicationService.insertResource(dto));
    }

    @PutMapping(value = "/update")
//    @ApiOperation("视频管理>修改")
    @OperateLog(module = "视频管理",name = "视频修改")
    @PreAuthorize("hasAuthority('sys:resource:video:update')")
    public HttpResultUtil<Boolean> update(@RequestBody SysResourceDTO dto) throws IOException {
        return new HttpResultUtil<Boolean>().ok(sysResourceApplicationService.updateResource(dto));
    }

    @DeleteMapping(value = "/delete")
//    @ApiOperation("视频管理>删除")
    @OperateLog(module = "视频管理",name = "视频删除")
    @PreAuthorize("hasAuthority('sys:resource:video:delete')")
    public HttpResultUtil<Boolean> delete(@RequestParam("id") Long id) {
        return new HttpResultUtil<Boolean>().ok(sysResourceApplicationService.deleteResource(id));
    }

    @GetMapping(value = "/diagram")
//    @ApiOperation(value = "视频管理>流程图")
    @PreAuthorize("hasAuthority('sys:resource:video:diagram')")
    public void diagram(@RequestParam("processInstanceId")String processInstanceId, HttpServletResponse response) throws IOException {
        workflowTaskApplicationService.diagramProcess(processInstanceId, response);
    }

    @GetMapping("/auditLog")
//    @ApiOperation("视频管理>审批日志")
    @PreAuthorize("hasAuthority('sys:resource:video:auditLog')")
    public HttpResultUtil<List<SysAuditLogVO>> auditLog(@RequestParam("businessId") Long businessId) {
        return new HttpResultUtil<List<SysAuditLogVO>>().ok(sysResourceApplicationService.queryAuditLogList(businessId));
    }

}
