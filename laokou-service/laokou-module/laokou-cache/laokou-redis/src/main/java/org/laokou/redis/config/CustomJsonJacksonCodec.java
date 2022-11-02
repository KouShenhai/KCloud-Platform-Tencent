package org.laokou.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import io.netty.buffer.*;
import org.laokou.common.utils.JacksonUtil;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Kou Shenhai
 */
public class CustomJsonJacksonCodec extends BaseCodec {
    public static final CustomJsonJacksonCodec INSTANCE = new CustomJsonJacksonCodec();

    private ObjectMapper mapObjectMapper;
    
    public CustomJsonJacksonCodec(){
        this.mapObjectMapper = CustomJsonJacksonCodec.getObjectMapper();
    }
    
    private final Encoder encoder = in -> {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        try {
            ByteBufOutputStream os = new ByteBufOutputStream(out);
            mapObjectMapper.writeValue((OutputStream) os, in);
            return os.buffer();
        } catch (IOException e) {
            out.release();
            throw e;
        } catch (Exception e) {
            out.release();
            throw new IOException(e);
        }
    };

    private Decoder<Object> decoder = (buf, state) -> mapObjectMapper.readValue((InputStream) new ByteBufInputStream(buf), Object.class);


    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }

    public static ObjectMapper getObjectMapper() {
        //解决查询缓存转换异常的问题
        ObjectMapper objectMapper = JacksonUtil.MAPPER;
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance , ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return objectMapper;
    }

}
