/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.laokou.oss.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.laokou.common.core.constant.Constant;
import org.laokou.oss.server.entity.SysOssLogDO;
import org.laokou.oss.server.mapper.SysOssLogMapper;
import org.laokou.oss.server.service.SysOssLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author laokou
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysOssLogServiceImpl extends ServiceImpl<SysOssLogMapper, SysOssLogDO> implements SysOssLogService {

    @Override
    @Async
    public void insertLog(String url, String md5,String fileName,Long fileSize) {
        SysOssLogDO sysOssLogDO = new SysOssLogDO();
        sysOssLogDO.setUrl(url);
        sysOssLogDO.setMd5(md5);
        sysOssLogDO.setFileName(fileName);
        sysOssLogDO.setFileSize(fileSize);
        this.baseMapper.insert(sysOssLogDO);
    }

    @Override
    public SysOssLogDO getLogByMd5(String md5) {
        LambdaQueryWrapper<SysOssLogDO> queryWrapper = Wrappers.lambdaQuery(SysOssLogDO.class)
                .select(SysOssLogDO::getUrl)
                .eq(SysOssLogDO::getMd5, md5)
                .eq(SysOssLogDO::getDelFlag, Constant.NO);
        return this.baseMapper.selectOne(queryWrapper);
    }

}
