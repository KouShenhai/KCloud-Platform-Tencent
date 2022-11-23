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
package org.laokou.rocketmq.consumer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.laokou.common.mybatisplus.entity.BaseDO;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/8/19 0019 下午 3:51
 */
@Data
@TableName("boot_sys_resource")
@EqualsAndHashCode(callSuper=true)
public class SysResourceDO extends BaseDO {

    private String title;
    private String author;
    private String uri;
    /**
     * 0待审核  1审核中 2审批拒绝 3审批通过
     */
    private Integer status;
    private String code;
    private String remark;
    private String tags;
    /**
     * 流程id
     */
    private String processInstanceId;
    private String md5;

}
