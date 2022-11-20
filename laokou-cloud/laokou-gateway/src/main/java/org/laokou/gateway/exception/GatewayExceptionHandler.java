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
package org.laokou.gateway.exception;
import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.core.exception.ErrorCode;
import org.laokou.common.core.utils.HttpResultUtil;
import org.laokou.gateway.constant.GatewayConstant;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;
/**
 * 异常处理器
 * @author Kou Shenhai
 * @since 1.0.0
 */
@Slf4j
@Getter
@Setter
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

	private List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();
	private List<HttpMessageWriter<?>> messageWriters = HandlerStrategies.withDefaults().messageWriters();
	private List<ViewResolver> viewResolvers = HandlerStrategies.withDefaults().viewResolvers();
	private static final TransmittableThreadLocal<HttpResultUtil<?>> TL = new TransmittableThreadLocal<>();

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable e) {
		log.error("网关全局处理异常，异常信息:{}",e.getMessage());
		HttpResultUtil<Boolean> result = new HttpResultUtil<>();
		if (e instanceof RuntimeException){
			log.error("服务正在维护，请联系管理员");
			result = result.error(ErrorCode.SERVICE_MAINTENANCE, GatewayConstant.SERVICE_MAINTENANCE_MSG);
		}
		TL.set(result);
		ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);
		return RouterFunctions.route(RequestPredicates.all(),this::renderErrorResponse)
				.route(serverRequest)
				.switchIfEmpty(Mono.error(e))
				.flatMap((handler) -> handler.handle(serverRequest))
				.flatMap((response) -> write(exchange,response));
	}

	protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
		return ServerResponse.status(TL.get().getCode())
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(TL.get()));
	}

	private Mono<? extends Void> write(ServerWebExchange exchange,ServerResponse response) {
		final HttpHeaders exchangeHeaders = exchange.getResponse().getHeaders();
		final MediaType responseContentType = response.headers().getContentType();
		log.info("exchangeHeaders:{}",exchangeHeaders);
		log.info("responseContentType:{}",responseContentType);
		exchangeHeaders.setContentType(responseContentType);
		TL.remove();
		return response.writeTo(exchange,new ResponseContext());
	}

	private class ResponseContext implements ServerResponse.Context {

		@Override
		public List<HttpMessageWriter<?>> messageWriters() {
			return GatewayExceptionHandler.this.messageWriters;
		}

		@Override
		public List<ViewResolver> viewResolvers() {
			return GatewayExceptionHandler.this.viewResolvers;
		}
	}

}
