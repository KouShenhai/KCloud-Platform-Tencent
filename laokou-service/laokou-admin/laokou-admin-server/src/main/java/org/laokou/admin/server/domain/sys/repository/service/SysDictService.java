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
package org.laokou.admin.server.domain.sys.repository.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.laokou.admin.server.domain.sys.entity.SysDictDO;
import org.laokou.admin.server.interfaces.qo.SysDictQo;
import org.laokou.admin.client.vo.SysDictVO;

import java.util.List;

/**
 * @author laokou
 * @version 1.0
 * @date 2022/6/23 0023 上午 11:04
 */
public interface SysDictService extends IService<SysDictDO> {

    /**
     * 查询字典列表
     * @param qo
     * @return
     */
    List<SysDictVO> getDictList(SysDictQo qo);

    /**
     * 分页查询字典
     * @param page
     * @param qo
     * @return
     */
    IPage<SysDictVO> getDictList(IPage<SysDictVO> page, SysDictQo qo);

    /**
     * 根据id查询字典
     * @param id
     * @return
     */
    SysDictVO getDictById(Long id);

    /**
     * 根据id删除字典
     * @param id
     */
    void deleteDict(Long id);

}
