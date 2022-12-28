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
package org.laokou.admin.server.domain.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import org.laokou.common.mybatisplus.entity.BaseDO;
import lombok.Data;

/**
 * @author laokou
 * @version 1.0
 * @date 2022/8/11 0011 上午 8:57
 */
@Data
@TableName("boot_sys_oauth_client_details")
@EqualsAndHashCode(callSuper=true)
public class SysOauthDO extends BaseDO {

    /**
     * 应用id
     */
    private String clientId;

    /**
     * 资源集合
     */
    private String resourceIds;

    /**
     * 应用密钥
     */
    private String clientSecret;

    /**
     * 授权范围
     */
    private String scope;

    /**
     * 授权类型
     */
    private String authorizedGrantTypes;

    /**
     * 回调地址
     */
    private String webServerRedirectUri;

    /**
     * 权限
     */
    private String authorities;

    /**
     * 令牌秒数
     */
    private String accessTokenValidity;

    /**
     * 刷新秒数
     */
    private String refreshTokenValidity;

    /**
     * 附加说明
     */
    private String additionalInformation;

    /**
     * 自动授权
     */
    private String autoapprove;

    private Long deptId;

    private Integer sort;
}
