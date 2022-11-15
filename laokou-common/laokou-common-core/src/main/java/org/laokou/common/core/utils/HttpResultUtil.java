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
package org.laokou.common.core.utils;
import lombok.Data;
import org.laokou.common.core.exception.ErrorCode;

/**
 * 统一返回结果类
 * @author  Kou Shenhai
 */
@Data
public class HttpResultUtil<T> {
    /**
     * 编码：200标识成功，其他值表示失败
     */
    private int code = 200;

    /**
     * 消息内容
     */
    private String msg = "success";


    /**
     * 响应数据
     */
    private T data;

    public boolean success(){
        return code == 200;
    }

    public HttpResultUtil<T> error(){
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = MessageUtil.getMessage(this.code);
        return this;
    }

    public HttpResultUtil<T> error(int code){
        this.code = code;
        this.msg = MessageUtil.getMessage(this.code);
        return this;
    }

    public HttpResultUtil<T> ok(T data){
        this.setData(data);
        return this;
    }

    public HttpResultUtil<T> error(int code,String msg){
        this.code = code;
        this.msg = msg;
        return this;
    }

    public HttpResultUtil<T> error(String msg){
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
        return this;
    }

}
