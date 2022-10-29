package org.laokou.ump.server.auth.enums;

/**
 * @author Kou Shenhai
 */
public enum LoginTypeEnum {

    USER_PASSWORD,
    OAUTH2;

    public static LoginTypeEnum getType(Integer type) {
        LoginTypeEnum[] values = LoginTypeEnum.values();
        for (LoginTypeEnum value : values) {
            if (value.ordinal() == type) {
                return value;
            }
        }
        return null;
    }

}
