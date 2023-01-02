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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.laokou.admin.server.application.service.SysSearchApplicationService;
import org.laokou.common.swagger.utils.HttpResult;
import org.laokou.elasticsearch.client.form.SearchForm;
import org.laokou.elasticsearch.client.vo.SearchVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
/**
 * 搜索管理控制器
 * @author laokou
 */
@RestController
@Tag(name = "Sys Search Api",description = "搜索管理API")
@RequestMapping("/sys/search/api")
@RequiredArgsConstructor
public class SysSearchApiController {

    private final SysSearchApplicationService sysSearchApplicationService;

    @PostMapping("/resource")
    @Operation(summary = "搜索管理>资源",description = "搜索管理>资源")
    @PreAuthorize("hasAuthority('sys:search:resource:query')")
    public HttpResult<SearchVO<Map<String,Object>>> searchResource(@RequestBody SearchForm form) {
        return new HttpResult<SearchVO<Map<String,Object>>>().ok(sysSearchApplicationService.searchResource(form));
    }

}
