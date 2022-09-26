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
import org.laokou.auth.client.vo.SysDeptVO;
import org.laokou.datasource.annotation.DataSource;
import org.laokou.log.publish.PublishFactory;
import org.laokou.redis.RedisUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * auth实现类
 * @author Kou Shenhai
 */
@Service
@Slf4j
@GlobalTransactional(rollbackFor = Exception.class)
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
    private SysRoleService sysRoleService;
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    @DataSource("master")
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
        UserDetail userDetail = getUserDetail(username);
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
        List<SysMenuVO> resourceList = sysMenuService.getMenuList(userDetail,true,1);
        if (CollectionUtils.isEmpty(resourceList) && SuperAdminEnum.NO.ordinal() == userDetail.getSuperAdmin()) {
            PublishFactory.recordLogin(username, ResultStatusEnum.FAIL.ordinal(),MessageUtil.getMessage(ErrorCode.NOT_PERMISSIONS));
            throw new CustomException(ErrorCode.NOT_PERMISSIONS);
        }
        PublishFactory.recordLogin(username, ResultStatusEnum.SUCCESS.ordinal(),"登录成功");
        //获取token
        return getToken(userDetail,resourceList);
    }

    private String getToken(UserDetail userDetail,List<SysMenuVO> resourceList) {
        //region Description
        //编号
        final Long userId = userDetail.getId();
        final String username = userDetail.getUsername();
        //登录成功 > 生成token
        String token = TokenUtil.getToken(TokenUtil.getClaims(userId,username));
        log.info("Token is：{}", token);
        //用户信息
        String userInfoKey = RedisKeyUtil.getUserInfoKey(userId);
        //资源列表放到redis中
        String userResourceKey = RedisKeyUtil.getUserResourceKey(userId);
        //原子操作 -> 防止数据被修改，更新到redis的数据不是最新数据
        List<String> permissionList = getPermissionList(userDetail);
        userDetail.setPermissionsList(permissionList);
        userDetail.setRoles(sysRoleService.getRoleListByUserId(userDetail.getId()));
        userDetail.setDepts(getDeptList(userDetail));
        redisUtil.delete(userInfoKey);
        redisUtil.delete(userResourceKey);
        redisUtil.set(userInfoKey,userDetail,RedisUtil.HOUR_ONE_EXPIRE);
        redisUtil.set(userResourceKey,resourceList,RedisUtil.HOUR_ONE_EXPIRE);
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        request.setAttribute(Constant.AUTHORIZATION_HEAD, token);
        return token;
        //endregion
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

    private List<SysDeptVO> getDeptList(UserDetail userDetail) {
        Integer superAdmin = userDetail.getSuperAdmin();
        Long userId = userDetail.getId();
        if (SuperAdminEnum.YES.ordinal() == superAdmin) {
            return sysDeptService.getDeptList();
        } else {
            return sysDeptService.getDeptListByUserId(userId);
        }
    }

    @Override
    public Boolean logout(Long userId) {
        //region Description
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        //删除相关信息
        removeInfo(request,userId);
        return true;
        //endregion
    }

    private void removeInfo(HttpServletRequest request,Long userId) {
        //region Description
        //删除缓存
        String userResourceKey = RedisKeyUtil.getUserResourceKey(userId);
        String userInfoKey = RedisKeyUtil.getUserInfoKey(userId);
        redisUtil.delete(userResourceKey);
        redisUtil.delete(userInfoKey);
        //退出
        request.removeAttribute(Constant.AUTHORIZATION_HEAD);
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
    @DataSource("master")
    public Mono<HttpResultUtil<UserDetail>> resource(String Authorization, String uri, String method) {
        //region Description
        //1.获取用户信息
        return Mono.just(getUserId(Authorization)).flatMap(userId -> Mono.just(getUserDetail(userId))).flatMap(userDetail -> {
            CompletableFuture<Boolean> booleanCompletableFuture1 = CompletableFuture.supplyAsync(() ->
                        //2.获取所有资源列表
                        sysMenuService.getMenuList(null,null),executorService)
                        //异步回调
                .thenApplyAsync((resourceList) ->
                        //3.判断资源是否在资源列表列表里
                        pathMatcher(uri, method, resourceList),executorService)
                .thenApplyAsync((resource) -> {
                    //4.无需认证
                    if (resource != null && AuthTypeEnum.NO_AUTH.ordinal() == resource.getAuthLevel()) {
                        return true;
                    }
                    //5.登录认证
                    if (resource != null && AuthTypeEnum.LOGIN_AUTH.ordinal() == resource.getAuthLevel()) {
                        return true;
                    }
                    //6.不在资源列表，只要登录了，就能访问
                    if (resource == null) {
                        return true;
                    }
                    //7.当前登录用户为超级管理员
                    if (SuperAdminEnum.YES.ordinal() == userDetail.getSuperAdmin()) {
                        return true;
                    }
                    return false;
                },executorService);
            //8. 需要鉴权，获取用户资源列表
            CompletableFuture<Boolean> booleanCompletableFuture2 = CompletableFuture.supplyAsync(() -> sysMenuService.getMenuList(userDetail, false, 1), executorService)
                    .thenApplyAsync((resourceList) ->
                            //9.如果不在用户资源列表里，则无权访问
                            pathMatcher(uri, method, resourceList), executorService)
                    .thenApplyAsync((resource) -> {
                        if (resource != null) {
                            return true;
                        }
                        return false;
                    }, executorService);
            Boolean flag = false;
            try {
                flag = booleanCompletableFuture1.thenCombineAsync(booleanCompletableFuture2, (a, b) -> {
                    if (a || b) {
                        return true;
                    }
                    return false;
                },executorService).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (flag) {
                return Mono.just(new HttpResultUtil<UserDetail>().ok(userDetail));
            } else {
                return Mono.error(new CustomException(ErrorCode.FORBIDDEN));
            }
        });
        //endregion
    }

    @Override
    @DataSource("master")
    public UserInfoVO userInfo(Long userId) {
        UserDetail userDetail = getUserDetail(userId);
        return UserInfoVO.builder().imgUrl(userDetail.getImgUrl())
                        .username(userDetail.getUsername())
                        .userId(userDetail.getId())
                        .mobile(userDetail.getMobile())
                        .email(userDetail.getEmail())
                        .roles(userDetail.getRoles())
                        .permissionList(userDetail.getPermissionsList()).build();
    }

    @Override
    @DataSource("master")
    public BaseUserVO openUserInfo(Long userId) {
        UserDetail userDetail = getUserDetail(userId);
        return BaseUserVO.builder().imgUrl(userDetail.getImgUrl())
                .username(userDetail.getUsername())
                .userId(userDetail.getId())
                .mobile(userDetail.getMobile())
                .email(userDetail.getEmail()).build();
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

    private Long getUserId(String Authorization) {
        //region Description
        if (TokenUtil.isExpiration(Authorization)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_INVALID);
        }
        return TokenUtil.getUserId(Authorization);
        //endregion
    }

    @Override
    @DataSource("master")
    public UserDetail getUserDetail(Long userId) {
        //region Description
        String userInfoKey = RedisKeyUtil.getUserInfoKey(userId);
        final Object obj = redisUtil.get(userInfoKey);
        UserDetail userDetail;
        if (obj != null) {
            userDetail = (UserDetail) obj;
        } else {
            userDetail = sysUserService.getUserDetail(userId,null);
            if (Objects.isNull(userDetail)) {
                throw new CustomException(ErrorCode.ACCOUNT_NOT_EXIST);
            }
            CompletableFuture<UserDetail> c1 = CompletableFuture.supplyAsync(() -> getPermissionList(userDetail),executorService).thenApplyAsync(permissionList -> {
                userDetail.setPermissionsList(permissionList);
                return userDetail;
            },executorService);
            CompletableFuture<UserDetail> c2 = CompletableFuture.supplyAsync(() -> sysRoleService.getRoleListByUserId(userId),executorService).thenApplyAsync(roles -> {
                userDetail.setRoles(roles);
                return userDetail;
            },executorService);
            CompletableFuture<UserDetail> c3 = CompletableFuture.supplyAsync(() -> getDeptList(userDetail), executorService).thenApplyAsync(depts -> {
                userDetail.setDepts(depts);
                return userDetail;
            }, executorService);
            //等待所有任务都完成
            CompletableFuture.allOf(c1,c2,c3).join();
            redisUtil.set(userInfoKey,userDetail,RedisUtil.HOUR_ONE_EXPIRE);
        }
        return userDetail;
        //endregion
    }

    @Override
    public void zfbLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        zfbOauth.sendRedirectLogin(request,response);
    }

    @Override
    public void zfbBind(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String Authorization = SecurityUser.getAuthorization(request);
        final Long userId = SecurityUser.getUserId(request);
        final String zfbOpenid = request.getParameter("zfb_openid");
        final UserDetail userDetail = sysUserService.getUserDetail(userId,null);
        if (StringUtils.isBlank(userDetail.getZfbOpenid())) {
            sysUserService.updateZfbOpenid(userId,zfbOpenid);
            response.sendRedirect(String.format(INDEX_URL,Authorization));
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

    private UserDetail getUserDetail(String username) {
        return sysUserService.getUserDetail(null, username);
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
