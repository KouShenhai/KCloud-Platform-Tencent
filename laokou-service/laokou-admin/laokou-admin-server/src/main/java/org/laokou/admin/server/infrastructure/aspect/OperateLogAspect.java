/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.admin.server.infrastructure.aspect;
import feign.FeignException;
import org.laokou.admin.server.infrastructure.annotation.OperateLog;
import org.laokou.admin.server.infrastructure.feign.rocketmq.RocketmqApiFeignClient;
import org.laokou.auth.client.utils.UserUtil;
import org.laokou.common.core.enums.DataTypeEnum;
import org.laokou.common.core.enums.ResultStatusEnum;
import org.laokou.common.core.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.laokou.log.client.dto.OperateLogDTO;
import org.laokou.rocketmq.client.constant.RocketmqConstant;
import org.laokou.rocketmq.client.dto.RocketmqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @author Kou Shenhai
 */
@Component
@Aspect
@Slf4j
public class OperateLogAspect {

    @Autowired
    private RocketmqApiFeignClient rocketmqApiFeignClient;

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(org.laokou.admin.server.infrastructure.annotation.OperateLog)")
    public void logPointCut() {}

    /**
     * 处理完请求后执行
     */
    @AfterReturning(pointcut = "logPointCut()")
    public void doAfterReturning(JoinPoint joinPoint) throws IOException {
        handleLog(joinPoint,null);

    }

    @AfterThrowing(pointcut = "logPointCut()",throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint,Exception e) throws IOException {
        handleLog(joinPoint,e);
    }

    protected void handleLog(final JoinPoint joinPoint,final Exception e) throws IOException {
        try {
            HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
            //获取注解
            Signature signature = joinPoint.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            if (null == method) {
                return;
            }
            OperateLog operateLog = method.getAnnotation(OperateLog.class);
            if (operateLog == null) {
                operateLog = AnnotationUtils.findAnnotation(method, OperateLog.class);
            }
            String ip = IpUtil.getIpAddr(request);
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            List<?> params = new ArrayList<>(Arrays.asList(args)).stream().filter(arg -> (!(arg instanceof HttpServletRequest)
                    && !(arg instanceof HttpServletResponse))).collect(Collectors.toList());
            OperateLogDTO dto = new OperateLogDTO();
            assert operateLog != null;
            dto.setModule(operateLog.module());
            dto.setOperation(operateLog.name());
            dto.setRequestUri(request.getRequestURI());
            dto.setRequestIp(ip);
            dto.setRequestAddress(AddressUtil.getRealAddress(ip));
            dto.setOperator(UserUtil.getUsername());
            dto.setCreator(UserUtil.getUserId());
            dto.setDeptId(UserUtil.getDeptId());
            if (null != e) {
                dto.setRequestStatus(ResultStatusEnum.FAIL.ordinal());
                dto.setErrorMsg(e.getMessage());
            } else {
                dto.setRequestStatus(ResultStatusEnum.SUCCESS.ordinal());
            }
            dto.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            dto.setMethodName(className + "." + methodName + "()");
            dto.setRequestMethod(request.getMethod());
            if (DataTypeEnum.TEXT.equals(operateLog.type())) {
                dto.setRequestParams(JacksonUtil.toJsonStr(params, true));
            }
            RocketmqDTO rocketmqDTO = new RocketmqDTO();
            rocketmqDTO.setData(JacksonUtil.toJsonStr(dto));
            rocketmqApiFeignClient.sendOneMessage(RocketmqConstant.LAOKOU_OPERATE_LOG_TOPIC, rocketmqDTO);
        } catch (FeignException ex) {
            log.error("错误信息：{}", ex.getMessage());
        }
    }

}
