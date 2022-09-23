package org.laokou.cas.service;

import org.laokou.cas.user.UserDetail;

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
