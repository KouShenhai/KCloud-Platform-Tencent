package org.laokou.oss.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.laokou.oss.server.entity.SysOssLogDO;
import org.springframework.stereotype.Repository;

/**
 * @author laokou
 */
@Repository
@Mapper
public interface SysOssLogMapper extends BaseMapper<SysOssLogDO> {
}
