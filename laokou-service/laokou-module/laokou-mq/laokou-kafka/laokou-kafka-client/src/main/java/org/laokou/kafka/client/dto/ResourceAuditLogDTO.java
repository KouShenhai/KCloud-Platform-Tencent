package org.laokou.kafka.client.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Kou Shenhai
 */
@Data
public class ResourceAuditLogDTO implements Serializable {
    private Long resourceId;

    private String auditName;

    private Date auditDate;

    private Integer auditStatus;

    private String comment;

    private Integer status;

    private Long creator;
}
