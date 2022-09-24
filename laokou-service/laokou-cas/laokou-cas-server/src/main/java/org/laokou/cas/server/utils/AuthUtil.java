package org.laokou.cas.server.utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.laokou.cas.server.exception.RenOAuth2Exception;
import org.laokou.cas.server.user.BaseUserVO;
import io.laokou.common.constant.Constant;
import io.laokou.common.exception.ErrorCode;
import io.laokou.common.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * 认证
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/4/11 0011 下午 5:16
 */
@Component
@Slf4j
public class AuthUtil {

    @Value("${auth.client_id}")
    private String CLIENT_ID;

    @Value("${auth.client_secret}")
    private String CLIENT_SECRET;

    @Value("${auth.redirect_uri}")
    private String REDIRECT_URI;

    @Value("${auth.grant_type}")
    private String GRANT_TYPE;

    private static final String POST_AUTHORIZE_URL = "http://39.108.96.111:9001/oauth/token";

    private static final String GET_USER_KEY_URL = "http://39.108.96.111:9001/oauth2/userInfo";

    public String getAccessToken(String code) throws IOException {
        //将code放入
        Map<String,String> tokenMap = new HashMap<>(5);
        tokenMap.put("code",code);
        tokenMap.put("client_id",CLIENT_ID);
        tokenMap.put("client_secret",CLIENT_SECRET);
        tokenMap.put("redirect_uri",REDIRECT_URI);
        tokenMap.put("grant_type",GRANT_TYPE);
        String resultJson = HttpUtil.doPost(POST_AUTHORIZE_URL,tokenMap,new HashMap<>(0));
        JSONObject jsonObject = JSON.parseObject(resultJson);
        String accessToken = jsonObject.getString(Constant.ACCESS_TOKEN);
        if (StringUtils.isEmpty(accessToken)){
            throw new RenOAuth2Exception(ErrorCode.UNAUTHORIZED,"授权码已过期，请重新获取");
        }
        return accessToken;
    }

    public BaseUserVO getUerInfo(String accessToken) throws IOException {
        Map<String,String> userInfoMap = new HashMap<>(1);
        userInfoMap.put(Constant.ACCESS_TOKEN,accessToken);
        String json = HttpUtil.doGet(GET_USER_KEY_URL, userInfoMap,new HashMap<>(0));
        String data = JSONObject.parseObject(json).getString("data");
        return JSONObject.parseObject(data).toJavaObject(BaseUserVO.class);
    }

}
