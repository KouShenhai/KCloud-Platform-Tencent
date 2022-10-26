/**
 * Copyright (c) 2022 KCloud-Platform Authors. All Rights Reserved.
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
package org.laokou.auth.server.application.service.impl;
import cn.hutool.core.thread.ThreadUtil;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.laokou.auth.server.application.service.SysAuthApplicationService;
import org.laokou.auth.client.utils.TokenUtil;
import org.laokou.auth.server.domain.sys.repository.service.*;
import org.laokou.auth.server.domain.zfb.entity.ZfbUserDO;
import org.laokou.auth.server.domain.zfb.repository.service.ZfbUserService;
import org.laokou.auth.client.enums.AuthTypeEnum;
import org.laokou.auth.client.dto.LoginDTO;
import org.laokou.auth.client.enums.UserStatusEnum;
import org.laokou.auth.client.password.PasswordUtil;
import org.laokou.auth.client.password.RsaCoder;
import org.laokou.auth.client.user.BaseUserVO;
import org.laokou.auth.client.vo.LoginVO;
import org.laokou.auth.client.vo.SysMenuVO;
import org.laokou.auth.client.vo.UserInfoVO;
import org.laokou.auth.client.user.SecurityUser;
import org.laokou.common.constant.Constant;
import org.laokou.common.enums.ResultStatusEnum;
import org.laokou.common.enums.SuperAdminEnum;
import org.laokou.common.exception.CustomException;
import org.laokou.common.exception.ErrorCode;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.common.utils.*;
import org.laokou.log.publish.PublishFactory;
import org.laokou.redis.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import javax.crypto.BadPaddingException;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * auth实现类
 * @author Kou Shenhai
 */
@Service
@Slf4j
public class SysAuthApplicationServiceImpl implements SysAuthApplicationService {

    public static final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
            8,
            16,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(512),
            ThreadUtil.newNamedThreadFactory("laokou-auth-service",true),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 高性能缓存
     */
    private static final Cache<String,UserDetail> caffeineCache = Caffeine.newBuilder().initialCapacity(128).expireAfterAccess(10,TimeUnit.MINUTES).maximumSize(1024).build();;

    private static AntPathMatcher antPathMatcher = new AntPathMatcher();

    private static final String CALLBACK_LOGIN_URL = "http://192.168.62.1:5555/auth/sys/login.html?redirect_url=%s&error_info=%s";

    private static final String CALLBACK_FAIL_URL = "http://192.168.62.1:5555/auth/sys/zfb_bind_fail.html";

    private static final String INDEX_URL = "http://192.168.62.1:8000/user/login?redirect=/index&access_token=%s";

    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysCaptchaService sysCaptchaService;
    @Autowired
    @Lazy
    private ZfbOauth zfbOauth;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public LoginVO login(LoginDTO loginDTO) throws Exception {
        //region Description
        //效验数据
        ValidatorUtil.validateEntity(loginDTO);
        String uuid = loginDTO.getUuid();
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        String captcha = loginDTO.getCaptcha();
        //SRA私钥解密
        try {
            username = RsaCoder.decryptByPrivateKey(username);
            password = RsaCoder.decryptByPrivateKey(password);
        } catch (BadPaddingException e) {
            PublishFactory.recordLogin(Constant.UNKNOWN, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.DECRYPT_FAIL));
            throw new CustomException(ErrorCode.DECRYPT_FAIL);
        }
        log.info("解密前，用户名：{}", username);
        log.info("解密前，密码：{}", password);
        log.info("解密后，用户名：{}", username);
        log.info("解密后，密码：{}", password);
        //验证码是否正确
        boolean validate = sysCaptchaService.validate(uuid, captcha);
        if (!validate) {
            PublishFactory.recordLogin(username, ResultStatusEnum.FAIL.ordinal(),MessageUtil.getMessage(ErrorCode.CAPTCHA_ERROR));
            throw new CustomException(ErrorCode.CAPTCHA_ERROR);
        }
        String token = getToken(username, password, true);
        return LoginVO.builder().token(token).build();
        //endregion
    }

    private String getToken(String username,String password,boolean isUserPasswordFlag) throws Exception {
        //查询数据库
        UserDetail userDetail = getUserDetailByName(username);
        log.info("查询的数据：{}",userDetail);
        if (!isUserPasswordFlag && null == userDetail) {
            PublishFactory.recordLogin(username, ResultStatusEnum.FAIL.ordinal(),MessageUtil.getMessage(ErrorCode.ACCOUNT_NOT_EXIST));
            throw new CustomException(ErrorCode.ACCOUNT_NOT_EXIST);
        }
        if (isUserPasswordFlag && null == userDetail){
            PublishFactory.recordLogin(username, ResultStatusEnum.FAIL.ordinal(),MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR));
            throw new CustomException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }
        if(isUserPasswordFlag && !PasswordUtil.matches(password, userDetail.getPassword())){
            PublishFactory.recordLogin(username, ResultStatusEnum.FAIL.ordinal(),MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR));
            throw new CustomException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }
        if (UserStatusEnum.DISABLE.ordinal() == userDetail.getStatus()) {
            PublishFactory.recordLogin(username, ResultStatusEnum.FAIL.ordinal(),MessageUtil.getMessage(ErrorCode.ACCOUNT_DISABLE));
            throw new CustomException(ErrorCode.ACCOUNT_DISABLE);
        }
        List<SysMenuVO> resourceList = sysMenuService.getMenuList(null,userDetail,true,1);
        if (CollectionUtils.isEmpty(resourceList) && SuperAdminEnum.NO.ordinal() == userDetail.getSuperAdmin()) {
            PublishFactory.recordLogin(username, ResultStatusEnum.FAIL.ordinal(),MessageUtil.getMessage(ErrorCode.NOT_PERMISSIONS));
            throw new CustomException(ErrorCode.NOT_PERMISSIONS);
        }
        //PublishFactory.recordLogin(username, ResultStatusEnum.SUCCESS.ordinal(),"登录成功");
        //获取token
        return getToken(userDetail,resourceList);
    }

    private String getToken(UserDetail userDetail,List<SysMenuVO> resourceList) {
        //region Description
        //编号
        final Long userId = userDetail.getId();
        final String username = userDetail.getUsername();
        //登录成功 > 生成token
        Map<String, Object> claims = TokenUtil.getClaims(userId, username);
        String token = TokenUtil.getToken(claims);
        log.info("Token is：{}", token);
        setToken(userDetail,resourceList,token);
        return token;
        //endregion
    }

    @Async
    public void setToken(UserDetail userDetail,List<SysMenuVO> resourceList,String token) {
        //用户信息
        String userInfoKey = RedisKeyUtil.getUserInfoKey(token);
        //资源列表放到redis中
        String userResourceKey = RedisKeyUtil.getUserResourceKey(token);
        userDetail.setPermissionsList(getPermissionList(userDetail));
        userDetail.setDepIds(getDeptIds(userDetail));
        redisUtil.set(userInfoKey,userDetail,RedisUtil.HOUR_ONE_EXPIRE);
        redisUtil.set(userResourceKey,resourceList,RedisUtil.HOUR_ONE_EXPIRE);
        caffeineCache.put(userInfoKey,userDetail);
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        request.setAttribute(Constant.AUTHORIZATION_HEAD, token);
    }

    private List<String> getPermissionList(UserDetail userDetail) {
        //region Description
        if (SuperAdminEnum.YES.ordinal() == userDetail.getSuperAdmin()){
            return sysMenuService.getPermissionsList();
        }else{
            return sysMenuService.getPermissionsListByUserId(userDetail.getId());
        }
        //endregion
    }

    private List<Long> getDeptIds(UserDetail userDetail) {
        Integer superAdmin = userDetail.getSuperAdmin();
        Long userId = userDetail.getId();
        if (SuperAdminEnum.YES.ordinal() == superAdmin) {
            return sysDeptService.getDeptIds();
        } else {
            return sysDeptService.getDeptIdsByUserId(userId);
        }
    }

    @Override
    public void logout(HttpServletRequest request) {
        //region Description
        String token = SecurityUser.getToken(request);
        if (StringUtils.isBlank(token)) {
            return;
        }
        //删除相关信息
        removeInfo(token);
        //退出
        request.removeAttribute(Constant.AUTHORIZATION_HEAD);
        //endregion
    }

    private void removeInfo(String token) {
        //region Description
        //删除缓存
        String userResourceKey = RedisKeyUtil.getUserResourceKey(token);
        String userInfoKey = RedisKeyUtil.getUserInfoKey(token);
        redisUtil.delete(userResourceKey);
        redisUtil.delete(userInfoKey);
        caffeineCache.invalidate(userInfoKey);
        //endregion
    }

    @Override
    public void captcha(String uuid, HttpServletResponse response) throws IOException {
        //region Description
        //生成图片验证码
        if (StringUtils.isBlank(uuid)) {
            throw new CustomException(ErrorCode.IDENTIFIER_NOT_NULL);
        }
        BufferedImage image = sysCaptchaService.createImage(uuid);
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image,"jpg",out);
        out.close();
        //endregion
    }

    @Override
    public BaseUserVO resource(String token, String uri, String method) {
        //region Description
        //1.获取用户信息
        UserDetail userDetail = getUserDetail(token);
        BaseUserVO userVO = BaseUserVO.builder().userId(userDetail.getId()).username(userDetail.getUsername()).build();
        //2.获取所有按钮资源列表
        List<SysMenuVO> resourceList = sysMenuService.getMenuList(null,1);
        //3.判断资源是否在资源列表列表里
        SysMenuVO resource = pathMatcher(uri, method, resourceList);
        //4.无需认证
        if (resource != null && AuthTypeEnum.NO_AUTH.ordinal() == resource.getAuthLevel()) {
            return userVO;
        }
        //5.登录认证
        if (resource != null && AuthTypeEnum.LOGIN_AUTH.ordinal() == resource.getAuthLevel()) {
            return userVO;
        }
        //6.不在资源列表，只要登录了，就能访问
        if (resource == null) {
            return userVO;
        }
        //7.当前登录用户为超级管理员
        if (SuperAdminEnum.YES.ordinal() == userDetail.getSuperAdmin()) {
            return userVO;
        }
        //8. 需要鉴权，获取用户资源列表
        resourceList = sysMenuService.getMenuList(token,userDetail, false, 1);
        //9.如果不在用户资源列表里，则无权访问
        if(pathMatcher(uri, method, resourceList) != null) {
            return userVO;
        }
        throw new CustomException(ErrorCode.FORBIDDEN);
        //endregion
    }

    @Override
    public UserDetail userDetail(HttpServletRequest request) {
        String token = SecurityUser.getToken(request);
        return getUserDetail(token);
    }

    @Override
    public UserInfoVO userInfo(HttpServletRequest request) {
        String token = SecurityUser.getToken(request);
        UserDetail userDetail = getUserDetail(token);
        return UserInfoVO.builder().imgUrl(userDetail.getImgUrl())
                        .username(userDetail.getUsername())
                        .userId(userDetail.getId())
                        .mobile(userDetail.getMobile())
                        .email(userDetail.getEmail())
                        .depId(userDetail.getDeptId())
                        .permissionList(userDetail.getPermissionsList()).build();
    }

    @Override
    public BaseUserVO openUserInfo(HttpServletRequest request) {
        String token = SecurityUser.getToken(request);
        UserDetail userDetail = getUserDetail(token);
        return BaseUserVO.builder()
                .username(userDetail.getUsername())
                .userId(userDetail.getId())
                .build();
    }

    private SysMenuVO pathMatcher(String url, String method, List<SysMenuVO> resourceList) {
        //region Description
        for (SysMenuVO resource : resourceList) {
            if (StringUtils.isNotEmpty(url) && antPathMatcher.match(resource.getUrl(), url)
                    && method.equalsIgnoreCase(resource.getMethod())) {
                log.info("匹配成功");
                return resource;
            }
        }
        return null;
        //endregion
    }

    private Long getUserId(String token) {
        //region Description
        if (TokenUtil.isExpiration(token)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_INVALID);
        }
        return TokenUtil.getUserId(token);
        //endregion
    }

    private UserDetail getUserDetail(String token) {
        //region Description
        String userInfoKey = RedisKeyUtil.getUserInfoKey(token);
        UserDetail userInfo = caffeineCache.getIfPresent(userInfoKey);
        if (null != userInfo) {
            return userInfo;
        }
        final Object obj = redisUtil.get(userInfoKey);
        UserDetail userDetail;
        if (obj != null) {
            userDetail = (UserDetail) obj;
        } else {
            final Long userId = getUserId(token);
            userDetail = getUserDetail(userId,null);
            if (Objects.isNull(userDetail)) {
                throw new CustomException(ErrorCode.ACCOUNT_NOT_EXIST);
            }
            CompletableFuture<UserDetail> c1 = CompletableFuture.supplyAsync(() -> getPermissionList(userDetail),executorService).thenApplyAsync(permissionList -> {
                userDetail.setPermissionsList(permissionList);
                return userDetail;
            },executorService);
            CompletableFuture<UserDetail> c2 = CompletableFuture.supplyAsync(() -> getDeptIds(userDetail), executorService).thenApplyAsync(deptIds -> {
                userDetail.setDepIds(deptIds);
                return userDetail;
            }, executorService);
            //等待所有任务都完成
            CompletableFuture.allOf(c1,c2).join();
            redisUtil.set(userInfoKey,userDetail,RedisUtil.HOUR_ONE_EXPIRE);
        }
        caffeineCache.put(userInfoKey,userDetail);
        return userDetail;
        //endregion
    }

    @Override
    public void zfbLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        zfbOauth.sendRedirectLogin(request,response);
    }

    @Override
    public void zfbBind(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = SecurityUser.getToken(request);
        final Long userId = SecurityUser.getUserId(request);
        final String zfbOpenid = request.getParameter("zfb_openid");
        final UserDetail userDetail = getUserDetailById(userId);
        if (StringUtils.isBlank(userDetail.getZfbOpenid())) {
            sysUserService.updateZfbOpenid(userId,zfbOpenid);
            response.sendRedirect(String.format(INDEX_URL,token));
        } else {
            response.sendRedirect(CALLBACK_FAIL_URL);
        }
    }

    @Override
    public void openLogin(HttpServletResponse response, HttpServletRequest request) throws IOException {
        final String username = request.getParameter(Constant.USERNAME_HEAD);
        final String password = request.getParameter(Constant.PASSWORD_HEAD);
        final String redirectUrl = request.getParameter(Constant.REDIRECT_URL_HEAD);
        String token;
        try {
            token = getToken(username,password,true);
        } catch (CustomException e) {
            response.sendRedirect(String.format(CALLBACK_LOGIN_URL, redirectUrl, URLEncoder.encode(e.getMsg(),"UTF-8")));
            return;
        } catch (Exception e) {
            response.sendRedirect(String.format(CALLBACK_LOGIN_URL, redirectUrl, URLEncoder.encode(MessageUtil.getMessage(ErrorCode.SERVICE_MAINTENANCE),"UTF-8") ));
            return;
        }
        String params = "?" + Constant.ACCESS_TOKEN + "=" + token;
        response.sendRedirect(redirectUrl + params);
    }

    private UserDetail getUserDetail(Long userId,String username) {
        return sysUserService.getUserDetail(userId, username);
    }

    private UserDetail getUserDetailByName(String username) {
        return getUserDetail(null,username);
    }

    private UserDetail getUserDetailById(Long userId) {
        return getUserDetail(userId,null);
    }

    @Component
    public class ZfbOauth{
        @Autowired
        private ZfbUserService zfbUserService;
        @Value("${oauth.zfb.app_id}")
        private String APP_ID;
        @Value("${oauth.zfb.merchant_private_key}")
        private String MERCHANT_PRIVATE_KEY;
        @Value("${oauth.zfb.public_key}")
        private String PUBLIC_KEY;
        @Value("${oauth.zfb.sign_type}")
        private String SIGN_TYPE;
        @Value("${oauth.zfb.charset}")
        private String CHARSET;
        @Value("${oauth.zfb.gateway_url}")
        private String GATEWAY_URL;
        @Value("${oauth.zfb.encrypt_type}")
        private String ENCRYPT_TYPE;
        @Value("${oauth.zfb.redirect_url}")
        private String REDIRECT_URL;
        @Value("${oauth.zfb.loading_url}")
        private String LOADING_URL;

        public void sendRedirectLogin(HttpServletRequest request,HttpServletResponse response) throws Exception {
            final String authCode = request.getParameter("auth_code");
            log.info("appId:{}",APP_ID);
            log.info("authCode:{}",authCode);
            //初始化AliPayClient
            AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL,APP_ID,MERCHANT_PRIVATE_KEY,SIGN_TYPE,CHARSET,PUBLIC_KEY,ENCRYPT_TYPE);
            // 通过authCode获取accessToken
            AlipaySystemOauthTokenRequest oauthTokenRequest = new AlipaySystemOauthTokenRequest();
            oauthTokenRequest.setCode(authCode);
            oauthTokenRequest.setGrantType("authorization_code");
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(oauthTokenRequest);
            String accessToken = oauthTokenResponse.getAccessToken();
            log.info("accessToken:{}",accessToken);
            if (StringUtils.isNotBlank(accessToken)) {
                //根据accessToken获取用户信息
                final AlipayUserInfoShareResponse userInfoResponse = alipayClient.execute(new AlipayUserInfoShareRequest(), accessToken);
                log.info("userInfo:{}", JacksonUtil.toJsonStr(userInfoResponse));
                if (userInfoResponse.isSuccess()) {
                    final String openid = userInfoResponse.getUserId();
                    final String city = userInfoResponse.getCity();
                    final String province = userInfoResponse.getProvince();
                    final String gender = userInfoResponse.getGender();
                    final String avatar = userInfoResponse.getAvatar();
                    zfbUserService.remove(Wrappers.lambdaQuery(ZfbUserDO.class).eq(ZfbUserDO::getOpenid,openid));
                    ZfbUserDO entity = new ZfbUserDO();
                    entity.setOpenid(openid);
                    entity.setAvatar(avatar);
                    entity.setProvince(province);
                    entity.setCity(city);
                    entity.setGender(gender);
                    zfbUserService.save(entity);
                    sendRedirectPage(openid,response,REDIRECT_URL,LOADING_URL);
                }
            }
        }
        private void sendRedirectPage(String openid,HttpServletResponse response,String redirectUrl,String loadingUrl) throws Exception {
            String username = sysUserService.getUsernameByOpenid(openid);
            String params = "";
            if (StringUtils.isNotBlank(username)) {
                final String token = getToken(username, null, false);
                if (StringUtils.isNotBlank(token)) {
                    params += "&" + Constant.ACCESS_TOKEN + "=" + token;
                }
                response.sendRedirect(redirectUrl + params);
            } else {
                response.sendRedirect(String.format(loadingUrl,openid));
            }
        }
    }
}
