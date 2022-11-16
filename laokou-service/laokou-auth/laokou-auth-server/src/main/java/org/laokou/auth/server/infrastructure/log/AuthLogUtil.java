/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
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
import lombok.extern.slf4j.Slf4j;
import org.laokou.auth.server.infrastructure.feign.kafka.KafkaApiFeignClient;
import org.laokou.common.core.utils.AddressUtil;
import org.laokou.common.core.utils.HttpContextUtil;
import org.laokou.common.core.utils.IpUtil;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.kafka.client.constant.KafkaConstant;
import org.laokou.kafka.client.dto.KafkaDTO;
import org.laokou.kafka.client.dto.LoginLogDTO;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Kou Shenhai
 */
@Component
@Slf4j
public class AuthLogUtil {

    @Autowired
    private KafkaApiFeignClient kafkaApiFeignClient;

    public void recordLogin(String username,Integer status,String msg) {
        try {
            HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
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
            dto.setRequestStatus(status);
            KafkaDTO kafkaDTO = new KafkaDTO();
            kafkaDTO.setData(JacksonUtil.toJsonStr(dto));
            kafkaApiFeignClient.sendAsyncMessage(KafkaConstant.LAOKOU_LOGIN_LOG_TOPIC, kafkaDTO);
        } catch (Exception e) {
            log.info("异常信息：{}",e.getMessage());
        }
    }

}
