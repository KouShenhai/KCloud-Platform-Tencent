package org.laokou.ump.server.oauth;

import org.laokou.auth.client.dto.LoginDTO;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.client.vo.LoginVO;
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

    public String getToken(LoginDTO loginDTO) {
        HttpResultUtil<LoginVO> result;
        try {
            result = authApiFeignClient.login(loginDTO);
        } catch (Exception e) {
            throw new RenOAuth2Exception(ErrorCode.SERVICE_MAINTENANCE, MessageUtil.getMessage(ErrorCode.SERVICE_MAINTENANCE));
        }
        if (result.getCode() != Constant.SUCCESS) {
            throw new RenOAuth2Exception(result.getCode(), result.getMsg());
        }
        return result.getData().getToken();
    }

    public UserDetail getUserDetail(String token) {
        HttpResultUtil<UserDetail> result;
        try {
            result = authApiFeignClient.userDetail(token);
        } catch (Exception e) {
            throw new RenOAuth2Exception(ErrorCode.SERVICE_MAINTENANCE, MessageUtil.getMessage(ErrorCode.SERVICE_MAINTENANCE));
        }
        if (result.getCode() != Constant.SUCCESS) {
            throw new RenOAuth2Exception(result.getCode(), result.getMsg());
        }
        return result.getData();
    }

}
