package org.laokou.common.utils;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author Kou Shenhai
 */
@Slf4j
public class JvmUtil {

    public static void getJvmInfo(String path) throws IOException {
        final byte[] bytes = Files.readAllBytes(Paths.get(path));
        log.info("读取的字节数组：{}",Arrays.toString(bytes));
        //验证魔数
        //从class字节码中提取前四位
        byte[] buff = new byte[4];
        System.arraycopy(bytes,0,buff,0,4);
        //将4位byte 字节转为16进制字符串
        final String magic_hex_str = new BigInteger(1, buff).toString(16);
        log.info("截取前4位字节并转换成16进制字符串：{}",magic_hex_str);
    }

}
