package org.laokou.mybatis.plus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author Kou Shenhai
 */
public interface BaseBatchDao<T> extends BaseMapper<T> {

    /**
     * 批量插入
     * @param list
     * @throws Exception
     */
    void insertBatch(List<T> list)throws Exception;

}
