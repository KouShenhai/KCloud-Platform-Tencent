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
package org.laokou.auth.server.infrastructure.exception;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
/**
 * 自定义异常
 * 官方不再维护，过期类无法替换
 * @author Kou Shenhai
 */
@Getter
@Setter
public class CustomOauth2Exception extends OAuth2Exception {
	private String msg;
	private String code;

	public CustomOauth2Exception(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
		this.code = "500";
	}

	public CustomOauth2Exception(String code, String msg) {
		super(msg);
		this.msg = msg;
		this.code = code;
	}
	/**
	 * 官方方法，不能驼峰命名
	 * @return
	 */
	@Override
	public String getOAuth2ErrorCode() {
		if(null == this.code){
			return "invalid_request";
		}
		return this.code;
	}
}
