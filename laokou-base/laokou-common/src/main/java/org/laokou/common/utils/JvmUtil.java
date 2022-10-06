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
        readAndCheckMagic(bytes);
        readAndCheckVersion(bytes);
    }
    private static void readAndCheckMagic(final byte[] bytes) {
        //验证魔数
        log.info("验证魔数...");
        //从class字节码中提取前四位
        byte[] buff = new byte[4];
        System.arraycopy(bytes,0,buff,0,4);
        //将4位byte 字节转为16进制字符串 -> cafebabe
        final String magic_hex_str = new BigInteger(1, buff).toString(16);
        log.info("截取前4位字节并转换成16进制字符串：{}",magic_hex_str);
        //magic_hex_str是16进制字符串 -> cafebabe，因为Java中没有无符号整型，所以如果想要无符号只能放到更高位中
        final long magic_unsigned_int32 = Long.parseLong(magic_hex_str, 16);
        log.info("无符号16进制：{}",magic_unsigned_int32);
        //魔数对比，一种通过字符串对比，另外使用假设无符号16进制对比，如果使用无符号比较需要将0xCAFEBABE & 0x0FFFFFFFFL 与运算
        final long i = 0xCAFEBABE & 0x0FFFFFFFFL;
        log.info("0xCAFEBABE & 0x0FFFFFFFFL -> {}",i);
        if (magic_unsigned_int32 == i) {
            log.info("class字节码魔数无符号16进制数值一致校验通过");
        } else {
            log.info("class字节码魔数无符号16进制数值一致校验拒绝");
        }
    }
    private static void readAndCheckVersion(final byte[] bytes) {
        //解析版本
        log.info("校验版本...");
        //从class字节码第4位开始读取，读取2位
        byte[] minor_byte = new byte[2];
        System.arraycopy(bytes,4,minor_byte,0,2);
        //将2位byte字节转成16进制字符串
        final String minor_hex_str = new BigInteger(1, minor_byte).toString(16);
        log.info("minor_hex_str:{}",minor_hex_str);
        //minor_unsigned_int32转成无符号16进制
        final int minor_unsigned_int32 = Integer.parseInt(minor_hex_str, 16);
        log.info("minor_unsigned_int32:{}",minor_unsigned_int32);
        //从字节码第6位开始读取，读取2位
        byte[] major_byte = new byte[2];
        System.arraycopy(bytes,6,major_byte,0,2);
        //将2位byte转换为16进制字符串
        final String major_hex_str = new BigInteger(1, major_byte).toString(16);
        log.info("major_hex_str:{}",major_byte);
        //major_unsigned_int32转成无符号16进制
        final int major_unsigned_int32 = Integer.parseInt(major_hex_str, 16);
        log.info("major_unsigned_int32:{}",major_unsigned_int32);
        log.info("版本号：{}.{}",major_unsigned_int32,minor_unsigned_int32);
    }
}
