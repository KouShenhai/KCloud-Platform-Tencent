package org.laokou.cas.server.service.impl;
import org.laokou.cas.server.mapper.SysMenuMapper;
import org.laokou.cas.server.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @author Kou Shenhai
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<String> getPermissionsList() {
        return sysMenuMapper.getPermissionsList();
    }

    @Override
    public List<String> getPermissionsListByUserId(Long userId) {
        return sysMenuMapper.getPermissionsListByUserId(userId);
    }
}
