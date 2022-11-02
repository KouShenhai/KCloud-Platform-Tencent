package org.laokou.oss.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.laokou.oss.server.mapper.SysOssMapper;
import org.laokou.oss.server.service.SysOssService;
import org.springframework.stereotype.Service;

/**
 * @author Kou Shenhai
 */
@Service
@RequiredArgsConstructor
public class SysOssServiceImpl implements SysOssService {

    private final SysOssMapper sysOssMapper;

    @Override
    public String queryOssConfig() {
        return sysOssMapper.queryOssConfig();
    }

}
