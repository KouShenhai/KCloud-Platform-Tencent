package org.laokou.api.server.service;

import reactor.core.publisher.Mono;
/**
 * @author Kou Shenhai
 */
public interface OpenApiService {

    /**
     * get请求
     * @return
     */
   Mono doGet();

    /**
     * post请求
     * @return
     */
    Mono doPost();

    /**
     * 转成post请求
     * @return
     */
    Mono toPost();

}
