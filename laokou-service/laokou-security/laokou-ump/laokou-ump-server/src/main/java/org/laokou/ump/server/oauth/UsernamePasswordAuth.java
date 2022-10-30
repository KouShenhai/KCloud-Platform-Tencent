package org.laokou.ump.server.oauth;

import org.laokou.auth.client.dto.LoginDTO;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.common.constant.Constant;
import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.HttpResultUtil;
import org.laokou.common.utils.MessageUtil;
import org.laokou.ump.server.exception.RenOAuth2Exception;
import org.laokou.ump.server.feign.auth.AuthApiFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * @author Kou Shenhai
 */
@Component
public class UsernamePasswordAuth{

    @Autowired
    private AuthApiFeignClient authApiFeignClient;

    public UserDetail getUserDetail(LoginDTO loginDTO) {
        HttpResultUtil<UserDetail> result;
        try {
            result = authApiFeignClient.userDetail(loginDTO);
        } catch (Exception e) {
            throw new RenOAuth2Exception(ErrorCode.SERVICE_MAINTENANCE, MessageUtil.getMessage(ErrorCode.SERVICE_MAINTENANCE));
        }
        if (result.getCode() != Constant.SUCCESS) {
            throw new RenOAuth2Exception(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

}
