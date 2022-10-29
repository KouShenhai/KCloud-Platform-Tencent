package org.laokou.ump.server.controller;

import lombok.RequiredArgsConstructor;
import org.laokou.common.constant.Constant;
import org.laokou.ump.server.feign.auth.AuthApiFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

/**
     * @author Kou Shenhai
 */
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final AuthApiFeignClient authApiFeignClient;

    @GetMapping()
    public void captcha(HttpServletRequest request) {
        authApiFeignClient.captcha(request.getParameter(Constant.UUID));
    }

}
