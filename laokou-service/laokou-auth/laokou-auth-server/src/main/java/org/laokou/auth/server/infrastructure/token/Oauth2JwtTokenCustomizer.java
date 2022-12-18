package org.laokou.auth.server.infrastructure.token;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

/**
 * JWT token 输出增强
 * @author OAuth2
 */
public class Oauth2JwtTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    /**
     * Customize the OAuth 2.0 Token attributes.
     * @param context the context containing the OAuth 2.0 Token attributes
     */
    @Override
    public void customize(JwtEncodingContext context) {
        JwtClaimsSet.Builder claims = context.getClaims();
        claims.claim(OAuth2ParameterNames.GRANT_TYPE, context.getAuthorizationGrantType().getValue());
        claims.claim(OAuth2ParameterNames.CLIENT_ID, context.getAuthorizationGrant().getName());
    }
}
