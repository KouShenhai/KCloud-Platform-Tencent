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
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.laokou.common.mybatisplus.entity.BaseDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
/**
 * 用户管理
 * @author  Kou Shenhai
 */
@Data
@TableName("boot_sys_user")
public class SysUserDO extends BaseDO {

    @NotBlank(message = "{sys.user.password.require}")
    @ApiModelProperty(value = "密码",name = "password",required = true,example = "123456")
    @Length(min = 6, max = 18, message = "密码长度必须在 {min} - {max} 之间")
    @TableField("password")
    private String password;

    /**
     * 用户名
     */
    @NotBlank(message = "{sys.user.username.require}")
    @ApiModelProperty(value = "用户名",name = "username",required = true,example = "admin")
    @TableField("username")
    private String username;

    /**
     * 超级管理员 0：否 1：是
     */
    @ApiModelProperty(value = "超级管理员 0：否 1：是",name = "superAdmin",example = "1")
    @TableField("super_admin")
    @JsonProperty("superAdmin")
    private Integer superAdmin;

    /**
     * 头像url
     */
    @ApiModelProperty(value = "头像url",name = "imgUrl",example = "https://pic.cnblogs.com/avatar/simple_avatar.gif")
    @TableField("img_url")
    @JsonProperty("imgUrl")
    private String imgUrl;

    /**
     * 电子邮箱
     */
    @ApiModelProperty(value = "电子邮箱",name = "email",example = "2413176044@qq.com")
    @TableField("email")
    @JsonProperty("email")
    private String email;

    /**
     * 状态 0停用 1正常
     */
    @ApiModelProperty(value = "状态 0停用 1正常",name = "status",example = "1")
    @TableField("status")
    @JsonProperty("status")
    private Integer status;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号",name = "mobile",example = "18974432576")
    @TableField("mobile")
    @JsonProperty("mobile")
    private String mobile;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id",name = "deptId",example = "0")
    @TableField("dept_id")
    @JsonProperty("deptId")
    private Long deptId;

}
