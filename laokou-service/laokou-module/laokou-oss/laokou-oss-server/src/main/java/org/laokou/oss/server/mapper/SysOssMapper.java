package org.laokou.oss.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Kou Shenhai
 */
@Repository
@Mapper
public interface SysOssMapper {

    /**
     * 查询OSS配置
     * @return
     */
    String queryOssConfig();

}
