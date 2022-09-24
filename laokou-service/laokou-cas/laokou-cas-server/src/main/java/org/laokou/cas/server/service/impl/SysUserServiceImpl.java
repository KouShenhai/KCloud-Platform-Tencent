package org.laokou.cas.server.service.impl;
import org.laokou.cas.server.mapper.SysUserMapper;
import org.laokou.cas.server.user.UserDetail;
import org.laokou.cas.server.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Kou Shenhai
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public UserDetail getUserDetail(Long id, String username) {
        return sysUserMapper.getUserDetail(id, username);
    }

}
