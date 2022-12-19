package org.laokou.gateway.constant;

/**
 * @author Kou Shenhai
 */
public interface GatewayConstant {

    /**
     * 异常处理
     */
    String SERVICE_MAINTENANCE_MSG = "服务正在维护，请联系管理员";
    /**
     * token鉴定
     */
    String UNAUTHORIZED_MSG = "未授权";
    /**
     * 未知异常
     */
    String OTHER_MSG = "其他的异常";
    /**
     * 服务熔断
     */
    String FALLBACK_MSG = "服务已被降级熔断";

    /**
     * 请求链-用户id
     */
    String REQUEST_USER_ID = "userId";
    /**
     * 请求链-用户名
     */
    String REQUEST_USERNAME = "username";
    /**
     * 密码模式-请求地址
     */
    String OAUTH_URI = "/oauth2/login";

    /**
     * 密码模式-用户名
     */
    String USERNAME = "username";

    /**
     * 密码模式-密码
     */
    String PASSWORD = "password";

}
