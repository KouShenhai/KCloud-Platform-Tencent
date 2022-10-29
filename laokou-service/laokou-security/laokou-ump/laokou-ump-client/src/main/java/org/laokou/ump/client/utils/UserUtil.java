package org.laokou.ump.client.utils;

import org.laokou.auth.client.user.UserDetail;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author 寇申海
 */
public class UserUtil {

    public static UserDetail userDetail() {
        return (UserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 获取用户id
     */
    public static Long getUserId() {
        return userDetail().getId();
    }

    /**
     * 获取用户名
     * @return
     */
    public static String getUsername() {
        return userDetail().getUsername();
    }

}
