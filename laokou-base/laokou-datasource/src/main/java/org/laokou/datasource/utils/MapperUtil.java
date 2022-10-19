package org.laokou.datasource.utils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.laokou.common.exception.CustomException;
import org.laokou.datasource.mapper.BaseBatchDao;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * @author Kou Shenhai
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MapperUtil<T> {

    private final SqlSessionFactory sqlSessionFactory;

    public void insertBatch(List<T> dataList, int batchNum, BaseBatchDao<T> baseBatchDao){
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        List<List<T>> partition = Lists.partition(dataList, batchNum);
        for(int i = 0; i < partition.size(); i++) {
            try {
                baseBatchDao.insertBatch(partition.get(i));
            } catch (Exception e) {
                sqlSession.rollback();
                throw new CustomException(500,"批量插入数据失败");
            }
            if (i % batchNum == 0) {
                sqlSession.commit();
                sqlSession.clearCache();
            }
        }
        sqlSession.commit();
        sqlSession.clearCache();
        sqlSession.close();
    }

}
