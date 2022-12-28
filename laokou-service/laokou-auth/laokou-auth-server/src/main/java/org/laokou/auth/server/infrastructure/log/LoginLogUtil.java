/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.auth.server.infrastructure.log;

import eu.bitwalker.useragentutils.UserAgent;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.laokou.auth.server.infrastructure.feign.rocketmq.RocketmqApiFeignClient;
import org.laokou.common.core.utils.AddressUtil;
import org.laokou.common.core.utils.IpUtil;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.common.core.utils.SnowFlakeShortUtil;
import org.laokou.log.client.dto.LoginLogDTO;
import org.laokou.rocketmq.client.dto.RocketmqDTO;
import org.laokou.rocketmq.client.constant.RocketmqConstant;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * @author laokou
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LoginLogUtil {

    private final RocketmqApiFeignClient rocketmqApiFeignClient;

    public void recordLogin(String username,String loginType, Integer status, String msg, HttpServletRequest request) {
        try {
            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader(HttpHeaders.USER_AGENT));
            String ip = IpUtil.getIpAddr(request);
            //获取客户端操作系统
            String os = userAgent.getOperatingSystem().getName();
            //获取客户端浏览器
            String browser = userAgent.getBrowser().getName();
            LoginLogDTO dto = new LoginLogDTO();
            dto.setLoginName(username);
            dto.setRequestIp(ip);
            dto.setRequestAddress(AddressUtil.getRealAddress(ip));
            dto.setBrowser(browser);
            dto.setOs(os);
            dto.setMsg(msg);
            dto.setLoginType(loginType);
            dto.setRequestStatus(status);
            RocketmqDTO rocketmqDTO = new RocketmqDTO();
            rocketmqDTO.setData(JacksonUtil.toJsonStr(dto));
            rocketmqDTO.setMsgId(String.valueOf(SnowFlakeShortUtil.getInstance().nextId()));
            rocketmqApiFeignClient.sendOneMessage(RocketmqConstant.LAOKOU_LOGIN_LOG_TOPIC, rocketmqDTO);
        } catch (FeignException | IOException e) {
            log.error("异常信息：{}",e.getMessage());
        }
    }

}
