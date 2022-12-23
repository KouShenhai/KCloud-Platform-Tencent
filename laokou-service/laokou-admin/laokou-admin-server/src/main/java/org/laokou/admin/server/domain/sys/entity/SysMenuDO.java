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
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import org.laokou.common.mybatisplus.entity.BaseDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 菜单管理
 *
 * @author laokou
 */
@Data
@TableName("boot_sys_menu")
@ApiModel("菜单")
@EqualsAndHashCode(callSuper=true)
public class SysMenuDO extends BaseDO {

	/**
	 * 父菜单ID，一级菜单为0
	 */
	@NotBlank(message = "{sys.menu.pid.require}")
	@TableField("pid")
	@ApiModelProperty(value = "父菜单ID",name = "pid",required = true,example = "0")
	private Long pid;

	/**
	 * 菜单名称
	 */
	@NotBlank(message = "{sys.menu.name.require}")
	@TableField("name")
	@ApiModelProperty(value = "菜单名称",name = "name",required = true,example = "用户管理")
	private String name;

	/**
	 * 菜单URL
	 */
	@TableField("url")
	@ApiModelProperty(value = "菜单URL",name = "url", example = "/sys/user/api/login")
	private String url;

	/**
	 * 授权
	 */
	@TableField("permission")
	@ApiModelProperty(value = "授权",name = "permission", example = "sys:user:query")
	private String permission;

	/**
	 * icon
	 */
	@TableField("icon")
	@ApiModelProperty(value = "图标",name = "icon",example = "user")
	private String icon;

	/**
	 * sort
	 */
	@TableField("sort")
	@ApiModelProperty(value = "排序",name = "sort",example = "1")
	private Integer sort;

	/**
	 * 类型   0：菜单   1：按钮
	 */
    @TableField("type")
    @ApiModelProperty(value = "类型   0：菜单   1：按钮",name = "type",example = "0")
	private Integer type;

}
