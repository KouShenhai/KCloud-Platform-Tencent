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
package org.laokou.admin.server.interfaces.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.laokou.common.mybatisplus.entity.BasePage;

/**
 * @author laokou
 * @version 1.0
 * @date 2022/8/19 0019 下午 3:46
 */
@Data
@EqualsAndHashCode(callSuper=true)
@Schema(name = "SysResourceQo",description = "资源实体类")
public class SysResourceQo extends BasePage {
    @Schema(name = "title",description = "标题")
    private String title;
    @Schema(name = "code",description = "编码")
    private String code;
    @Schema(name = "id",description = "编号")
    private Long id;

}
