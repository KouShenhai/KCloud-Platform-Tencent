/**
 * Copyright (c) 2022 KCloud-Platform Authors. All Rights Reserved.
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
package org.laokou.auth.client.user;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/8/5 0005 下午 3:56
 */
@Data
@Builder
@ApiModel(value = "基础用户信息VO")
@AllArgsConstructor
@NoArgsConstructor
public class BaseUserVO implements Serializable {

    @ApiModelProperty(name = "userId",value = "用户编号",required = true,example = "1341620898007281665")
    private Long userId;

    @ApiModelProperty(name = "username",value = "用户名",required = true,example = "admin")
    private String username;

}
