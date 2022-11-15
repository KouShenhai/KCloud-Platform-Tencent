/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.auth.server.domain.sys.repository.service.impl;
import com.google.code.kaptcha.Producer;
import org.laokou.auth.server.domain.sys.repository.service.SysCaptchaService;
import org.laokou.common.core.utils.RedisKeyUtil;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;

/**
 * 验证码实现类
 * @author Kou Shenhai
 * @version 1.0
 * @date 2020/12/19 0019 下午 7:23
 */
@Service
public class SysCaptchaServiceImpl implements SysCaptchaService {

    @Autowired
    private Producer producer;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public BufferedImage createImage(String uuid) {
        //生成文字验证码
        String code = producer.createText();

        //保存到缓存
        setCache(uuid,code);
        return producer.createImage(code);
    }

    @Override
    public boolean validate(String uuid, String code) {
        //获取验证码
        String captcha = getCache(uuid);
        //效验成功
        if (code.equalsIgnoreCase(captcha)) {
            return true;
        }
        return false;
    }

    private void setCache(String key,String value) {
        key = RedisKeyUtil.getUserCaptchaKey(key);
        //保存五分钟
        redisUtil.set(key, value,60 * 5);
    }

    private String getCache(String uuid) {
        String key = RedisKeyUtil.getUserCaptchaKey(uuid);
        Object captcha = redisUtil.get(key);
        if (captcha != null) {
            redisUtil.delete(key);
        }
        return captcha != null ? captcha.toString() : "";
    }

}
