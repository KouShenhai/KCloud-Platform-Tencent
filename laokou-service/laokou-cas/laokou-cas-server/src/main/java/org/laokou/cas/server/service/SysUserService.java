package org.laokou.cas.server.service;

import org.laokou.cas.server.user.UserDetail;

/**
 * 用户类
 * @author Kou Shenhai
 */
public interface SysUserService {

    /**
     * 获取用户信息
     * @param id
     * @param username
     * @return
     */
    UserDetail getUserDetail(Long id, String username);


}
