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
package org.laokou.common.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.laokou.common.core.utils.MessageUtil;

/**
 * 自定义异常
 * @author laokou
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class CustomException extends RuntimeException{

    private int code;
    private String msg;

    public CustomException(int code) {
        this.code = code;
        this.msg = MessageUtil.getMessage(code);
    }

    public CustomException(int code, String... params) {
        this.code = code;
        this.msg = MessageUtil.getMessage(code, params);
    }

    public CustomException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CustomException(int code, Throwable e) {
        super(e);
        this.code = code;
        this.msg = MessageUtil.getMessage(code);
    }

    public CustomException(int code, Throwable e, String... params) {
        super(e);
        this.code = code;
        this.msg = MessageUtil.getMessage(code, params);
    }

    public CustomException(String msg) {
        super(msg);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
    }

    public CustomException(String msg, Throwable e) {
        super(msg, e);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
    }

}
