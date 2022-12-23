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
package org.laokou.admin.client.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.util.Date;

/**
 * @author laokou
 */
@Data
public class SysLoginLogExcel {

    /**
     * 登录用户
     */
    @ExcelProperty(index = 0,value = "登录用户")
    @ColumnWidth(value = 20)
    private String loginName;

    /**
     * IP地址
     */
    @ExcelProperty(index = 1,value = "IP地址")
    @ColumnWidth(value = 20)
    private String requestIp;
    /**
     * 归属地
     */
    @ExcelProperty(index = 2,value = "归属地")
    @ColumnWidth(value = 20)
    private String requestAddress;

    /**
     * 浏览器版本
     */
    @ExcelProperty(index = 3,value = "浏览器版本")
    @ColumnWidth(value = 40)
    private String browser;

    /**
     * 操作系统
     */
    @ExcelProperty(index = 4,value = "操作系统")
    @ColumnWidth(value = 40)
    private String os;

    /**
     * 状态  0：成功   1：失败
     */
    @ExcelProperty(index = 5,value = "状态")
    @ColumnWidth(value = 20)
    private String requestStatusMsg;

    /**
     * 提示信息
     */
    @ExcelProperty(index = 6,value = "提示信息")
    @ColumnWidth(value = 40)
    private String msg;

    /**
     * 登录时间
     */
    @ExcelProperty(index = 7,value = "登录时间")
    @ColumnWidth(value = 20)
    private Date createDate;

    @ExcelIgnore
    private Integer requestStatus;

    public void setRequestStatusMsg(Integer requestStatus) {
        requestStatusMsg = requestStatus == 0 ? "成功" : "失败";
        this.requestStatusMsg = requestStatusMsg;
    }

    public String getRequestStatusMsg() {
        return requestStatusMsg;
    }

    public void setRequestStatus(Integer requestStatus) {
        setRequestStatusMsg(requestStatus);
        this.requestStatus = requestStatus;
    }
}
