/**
 * Copyright (c) 2022 KCloud-Platform-Netflix Authors. All Rights Reserved.
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
import org.laokou.admin.server.application.service.SysDictApplicationService;
import org.laokou.admin.client.dto.SysDictDTO;
import org.laokou.admin.server.interfaces.qo.SysDictQO;
import org.laokou.admin.client.vo.SysDictVO;
import org.laokou.common.utils.HttpResultUtil;
import org.laokou.log.annotation.OperateLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
/**
 * 系统字典控制器
 * @author Kou Shenhai
 */
@RestController
@Api(value = "系统字典API",protocols = "http",tags = "系统字典API")
@RequestMapping("/sys/dict/api")
public class SysDictApiController {

    @Autowired
    private SysDictApplicationService sysDictApplicationService;

    @PostMapping(value = "/query")
    @ApiOperation("系统字典>查询")
    @PreAuthorize("hasAuthority('sys:dict:query')")
    public HttpResultUtil<IPage<SysDictVO>> query(@RequestBody SysDictQO qo) {
        return new HttpResultUtil<IPage<SysDictVO>>().ok(sysDictApplicationService.queryDictPage(qo));
    }

    @GetMapping(value = "/detail")
    @ApiOperation("系统字典>详情")
    public HttpResultUtil<SysDictVO> detail(@RequestParam("id") Long id) {
        return new HttpResultUtil<SysDictVO>().ok(sysDictApplicationService.getDictById(id));
    }

    @PostMapping(value = "/insert")
    @ApiOperation("系统字典>新增")
    @OperateLog(module = "系统字典",name = "字典新增")
    @PreAuthorize("hasAuthority('sys:dict:insert')")
    public HttpResultUtil<Boolean> insert(@RequestBody SysDictDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysDictApplicationService.insertDict(dto));
    }

    @PutMapping(value = "/update")
    @ApiOperation("系统字典>修改")
    @OperateLog(module = "系统字典",name = "字典修改")
    @PreAuthorize("hasAuthority('sys:dict:update')")
    public HttpResultUtil<Boolean> update(@RequestBody SysDictDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysDictApplicationService.updateDict(dto));
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation("系统字典>删除")
    @OperateLog(module = "系统字典",name = "字典删除")
    @PreAuthorize("hasAuthority('sys:dict:delete')")
    public HttpResultUtil<Boolean> delete(@RequestParam("id") Long id) {
        return new HttpResultUtil<Boolean>().ok(sysDictApplicationService.deleteDict(id));
    }

}
