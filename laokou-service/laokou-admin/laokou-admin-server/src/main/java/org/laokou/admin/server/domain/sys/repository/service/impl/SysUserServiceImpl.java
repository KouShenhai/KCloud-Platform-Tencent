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
package org.laokou.admin.server.domain.sys.repository.service.impl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.laokou.admin.client.dto.SysUserDTO;
import org.laokou.admin.server.interfaces.qo.SysUserQO;
import org.laokou.admin.client.vo.OptionVO;
import org.laokou.admin.client.vo.SysUserVO;
import org.laokou.admin.server.domain.sys.entity.SysUserDO;
import org.laokou.admin.server.domain.sys.repository.mapper.SysUserMapper;
import org.laokou.auth.client.password.PasswordUtil;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.admin.server.domain.sys.repository.service.SysUserService;
import org.laokou.common.utils.RedisKeyUtil;
import org.laokou.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
/**
 * @author Kou Shenhai
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserDO> implements SysUserService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void updateUser(SysUserDTO dto) {
        String password = dto.getPassword();
        if (StringUtils.isNotBlank(password)) {
            dto.setPassword(PasswordUtil.encode(password));
        }
        this.baseMapper.updateUser(dto);
        //删除用户缓存数据
        redissonClient.getKeys().delete(RedisKeyUtil.getUserInfoKey(dto.getId()));
    }

    @Override
    public UserDetail getUserDetail(Long userId) {
        //region Description
        String userInfoKey = RedisKeyUtil.getUserInfoKey(userId);
        final RBucket<Object> bucket = redissonClient.getBucket(userInfoKey);
        UserDetail userDetail;
        if (redisUtil.hasKey(userInfoKey)) {
            userDetail = (UserDetail) bucket.get();
        } else {
            userDetail = this.baseMapper.getUserDetail(userId,null);
        }
        return userDetail;
        //endregion
    }

    @Override
    public String getUsernameByOpenid(String zfbOpenid) {
        return this.baseMapper.getUsernameByOpenid(zfbOpenid);
    }

    @Override
    public IPage<SysUserVO> getUserPage(IPage<SysUserVO> page, SysUserQO qo) {
        return this.baseMapper.getUserPage(page, qo);
    }

    @Override
    public void deleteUser(Long id) {
        this.baseMapper.deleteUser(id);
    }

    @Override
    public List<SysUserVO> getUserList() {
        return this.baseMapper.getUserList();
    }

    @Override
    public List<SysUserVO> getUserListByUserId(Long id) {
        return this.baseMapper.getUserListByUserId(id);
    }

    @Override
    public List<OptionVO> getOptionList() {
        return this.baseMapper.getOptionList();
    }

}
