package org.laokou.gateway.route;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author laokou
 */
@Component
@ConfigurationProperties(prefix = "dynamic.router")
@Data
public class RouterProperties {
    private String rule;
}