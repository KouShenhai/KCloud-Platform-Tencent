/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
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
import org.laokou.admin.server.infrastructure.annotation.DataFilter;
import org.laokou.auth.client.utils.UserUtil;
import org.laokou.common.core.enums.SuperAdminEnum;
import org.laokou.common.core.utils.StringUtil;
import org.laokou.common.mybatisplus.entity.BasePage;
import org.laokou.auth.client.user.UserDetail;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.List;
/**
 * @author laokou
 */
@Component
@Aspect
public class DataFilterAspect {

    @Pointcut("@annotation(org.laokou.admin.server.infrastructure.annotation.DataFilter)")
    public void dataFilterPointCut() {}

    @Before("dataFilterPointCut()")
    public void dataFilterPoint(JoinPoint point) {
        Object params = point.getArgs()[0];
        if (params instanceof BasePage) {
            UserDetail userDetail = UserUtil.userDetail();
            //如果是超级管理员，不进行数据过滤
            if (userDetail.getSuperAdmin() == SuperAdminEnum.YES.ordinal()) {
                return;
            }
            try {
                //否则进行数据过滤
                BasePage page = (BasePage)params;
                String sqlFilter = getSqlFilter(userDetail, point);
                page.setSqlFilter(sqlFilter);
            }catch (Exception ignored){}
        }
    }

    /**
     * 获取数据过滤的SQL
     */
    private String getSqlFilter(UserDetail userDetail, JoinPoint point) throws Exception {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = point.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
        DataFilter dataFilter = method.getAnnotation(DataFilter.class);
        if (dataFilter == null) {
            dataFilter = AnnotationUtils.findAnnotation(method,DataFilter.class);
        }
        //获取表的别名
        assert dataFilter != null;
        String tableAlias = dataFilter.tableAlias();
        if(StringUtil.isNotEmpty(tableAlias)){
            tableAlias +=  ".";
        }
        StringBuilder sqlFilter = new StringBuilder();
        //用户列表
        List<Long> deptIds = userDetail.getDeptIds();
        if (CollectionUtils.isNotEmpty(deptIds)) {
            sqlFilter.append(" find_in_set(").append(tableAlias).append(dataFilter.deptId()).append(" , ").append("'").append(StringUtil.join(deptIds,",")).append("'").append(") or ");
        }
        sqlFilter.append(tableAlias).append(dataFilter.userId()).append(" = ").append("'").append(userDetail.getUserId()).append("' ");
        return sqlFilter.toString();
    }

}
