package org.laokou.gateway.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactivefeign.ReactiveOptions;
import reactivefeign.retry.BasicReactiveRetryPolicy;
import reactivefeign.spring.config.ReactiveRetryPolicies;
import reactivefeign.webclient.WebReactiveOptions;
/**
 * @author Kou Shenhai
 */
@Configuration
public class ReactiveFeignConfig {

    /**
     * 设置一些超时时间
      * @return
     */
    @Bean
    public ReactiveOptions reactiveOptions() {
        return new WebReactiveOptions.Builder()
                .setWriteTimeoutMillis(30000)
                .setReadTimeoutMillis(30000)
                .setConnectTimeoutMillis(30000)
                .build();
    }

    /**
     * 重试机制
     * @return
     */
    @Bean
    public ReactiveRetryPolicies retryOnNext() {
        //不进行重试，retryOnSame是控制对同一个实例的重试策略，retryOnNext是控制对不同实例的重试策略。
        return new ReactiveRetryPolicies.Builder()
                .retryOnSame(BasicReactiveRetryPolicy.retryWithBackoff(0, 10))
                .retryOnNext(BasicReactiveRetryPolicy.retryWithBackoff(0, 10))
                .build();
    }

}
