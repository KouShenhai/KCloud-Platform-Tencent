package org.laokou.cas.provider;
import org.laokou.cas.exception.RenOAuth2Exception;
import org.laokou.cas.service.SysMenuService;
import org.laokou.cas.service.SysUserService;
import org.laokou.cas.user.BaseUserVO;
import org.laokou.cas.user.UserDetail;
import org.laokou.cas.utils.AuthUtil;
import io.laokou.common.exception.ErrorCode;
import io.laokou.common.utils.MessageUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/4/16 0016 上午 9:45
 */
@Component
@Slf4j
public class AuthAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysUserService sysUserService;

    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String code = authentication.getName();
        String password = (String)authentication.getCredentials();
        log.info("code：{}",code);
        String accessToken = authUtil.getAccessToken(code);
        BaseUserVO vo = authUtil.getUerInfo(accessToken);
        if (null == vo) {
            throw new RenOAuth2Exception(ErrorCode.ACCOUNT_NOT_EXIST,MessageUtil.getMessage(ErrorCode.ACCOUNT_NOT_EXIST));
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        UserDetail userDetail = sysUserService.getUserDetail(vo.getUserId(), null);
        List<String> permissionList;
        if(1 == userDetail.getSuperAdmin()) {
            permissionList = sysMenuService.getPermissionsList();
        } else {
            permissionList = sysMenuService.getPermissionsListByUserId(vo.getUserId());
        }
        authorities.addAll(permissionList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(vo,password,authorities);
        authenticationToken.setDetails(authentication.getDetails());
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
