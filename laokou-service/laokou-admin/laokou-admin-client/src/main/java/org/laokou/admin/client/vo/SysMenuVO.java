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
package org.laokou.admin.client.vo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.laokou.common.core.utils.TreeUtil;

/**
 * 系统菜单VO
 * @author laokou
 * @version 1.0
 * @date 2021/1/16 0016 下午 9:08
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SysMenuVO extends TreeUtil.TreeNo<SysMenuVO> {

    /**
     * 图标
     */
    private String icon;

    /**
     * 类型   0：菜单   1：按钮
     */
    private Integer type;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 资源URL
     */
    private String url;
    /**
     * 权限标识
     */
    private String permission;

}
