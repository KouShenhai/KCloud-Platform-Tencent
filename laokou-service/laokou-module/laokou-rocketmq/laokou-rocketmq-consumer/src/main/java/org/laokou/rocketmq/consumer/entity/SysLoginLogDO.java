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
 */
@Data
@TableName("boot_sys_login_log")
@EqualsAndHashCode(callSuper=true)
public class SysLoginLogDO extends BaseDO {

    /**
     * 登录用户
     */
    private String loginName;

    /**
     * ip地址
     */
    private String requestIp;
    /**
     * 操作地点
     */
     private String requestAddress;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 状态  0：成功   1：失败
     */
    private Integer requestStatus;

    /**
     * 提示信息
     */
    private String msg;


}