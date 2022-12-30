package org.laokou.gateway.service;
import java.io.IOException;
/**
 * @author laokou
 */
public interface DynamicGatewayRoutesService {
    /**
     * 导入
     * @throws IOException
     */
    void batch() throws IOException;

}
