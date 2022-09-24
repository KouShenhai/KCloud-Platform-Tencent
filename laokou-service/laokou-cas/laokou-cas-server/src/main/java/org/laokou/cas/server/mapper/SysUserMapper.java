package org.laokou.cas.server.mapper;
import org.laokou.cas.server.user.UserDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户类
 * @author  Kou Shenhai
 */
@Mapper
@Repository
public interface SysUserMapper {

    /**
     * 获取用户信息
     * @param userId
     * @param username
     * @return
     */
    UserDetail getUserDetail(@Param("userId")Long userId, @Param("username")String username);

}
