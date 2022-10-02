package org.laokou.gateway.constant;

import org.laokou.common.utils.FieldUtil;
import org.laokou.gateway.factory.GrayGatewayFilterFactory;

/**
 * @author Kou Shenhai
 */
public interface GrayConstant {
    String VERSION = FieldUtil.getFieldName(GrayGatewayFilterFactory.Config::getVersion);
    String IPS = FieldUtil.getFieldName(GrayGatewayFilterFactory.Config::getIps);
}
