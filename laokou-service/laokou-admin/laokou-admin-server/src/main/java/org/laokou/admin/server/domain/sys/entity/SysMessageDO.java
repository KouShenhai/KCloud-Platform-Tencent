/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
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
package org.laokou.admin.server.domain.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import org.laokou.common.mybatisplus.entity.BaseDO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 消息管理
 *
 * @author Kou Shenhai
 */
@Data
@TableName("boot_sys_message")
@ApiModel("消息")
@EqualsAndHashCode(callSuper=true)
public class SysMessageDO extends BaseDO {

    private String username;

    private String title;

    private String content;

    private String sendChannel;

    private Long deptId;

    private Integer type;

}
