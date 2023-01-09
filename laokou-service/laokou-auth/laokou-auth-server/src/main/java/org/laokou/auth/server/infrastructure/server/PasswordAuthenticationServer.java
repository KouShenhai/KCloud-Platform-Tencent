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
package org.laokou.auth.server.infrastructure.server;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.laokou.auth.client.constant.AuthConstant;
import org.laokou.auth.server.domain.sys.repository.service.SysCaptchaService;
import org.laokou.auth.server.domain.sys.repository.service.SysDeptService;
import org.laokou.auth.server.domain.sys.repository.service.SysMenuService;
import org.laokou.auth.server.domain.sys.repository.service.impl.SysUserServiceImpl;
import org.laokou.auth.server.infrastructure.log.LoginLogUtil;
import org.laokou.common.core.enums.ResultStatusEnum;
import org.laokou.common.swagger.exception.CustomException;
import org.laokou.common.swagger.exception.ErrorCode;
import org.laokou.common.core.utils.MessageUtil;
import org.laokou.common.core.utils.StringUtil;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import java.awt.*;
/**
 * 密码登录
 * @author laokou
 */
@Component
@Slf4j
public class PasswordAuthenticationServer extends AbstractAuthenticationServer {

    private final SysCaptchaService sysCaptchaService;
    public static final String GRANT_TYPE = "password";

    public PasswordAuthenticationServer(SysUserServiceImpl sysUserService
            , SysMenuService sysMenuService
            , SysDeptService sysDeptService
            , SysCaptchaService sysCaptchaService
            , LoginLogUtil loginLogUtil
            , PasswordEncoder passwordEncoder
            , RedisUtil redisUtil) {
        super(sysUserService, sysMenuService, sysDeptService,loginLogUtil,redisUtil,passwordEncoder);
        this.sysCaptchaService = sysCaptchaService;
    }

    @Override
    public AuthorizationGrantType getGrantType() {
        return new AuthorizationGrantType(GRANT_TYPE);
    }

    @Override
    public UsernamePasswordAuthenticationToken login(HttpServletRequest request) {

        // 获取用户信息
        // return super.getUserInfo(username, password, request);
        return null;
    }

    @Override
    public String captcha(HttpServletRequest request) {
        // 判断唯一标识是否为空
        String uuid = request.getParameter(AuthConstant.UUID);
        log.info("唯一标识：{}",uuid);
        if (StringUtil.isEmpty(uuid)) {
            throw new CustomException(ErrorCode.IDENTIFIER_NOT_NULL);
        }
        // 三个参数分别为宽、高、位数
        Captcha captcha = new GifCaptcha(130, 48, 4);
        // 设置字体，有默认字体，可以不用设置
        captcha.setFont(new Font("Verdana", Font.PLAIN, 32));
        // 设置类型，纯数字、纯字母、字母数字混合
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        sysCaptchaService.setCode(uuid,captcha.text());
        return captcha.toBase64();
    }

}
