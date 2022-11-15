/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.admin.server.application.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.laokou.admin.server.application.service.SysResourceApplicationService;
import org.laokou.admin.server.application.service.WorkflowProcessApplicationService;
import org.laokou.admin.server.domain.sys.entity.SysResourceDO;
import org.laokou.admin.server.domain.sys.repository.service.SysResourceAuditLogService;
import org.laokou.admin.server.domain.sys.repository.service.SysResourceService;
import org.laokou.admin.client.enums.ChannelTypeEnum;
import org.laokou.admin.client.enums.MessageTypeEnum;
import org.laokou.admin.server.infrastructure.component.feign.elasticsearch.ElasticsearchApiFeignClient;
import org.laokou.admin.client.index.ResourceIndex;
import org.laokou.admin.server.infrastructure.utils.WorkFlowUtil;
import org.laokou.admin.client.dto.SysResourceDTO;
import org.laokou.admin.server.interfaces.qo.SysResourceQO;
import org.laokou.admin.client.vo.StartProcessVO;
import org.laokou.admin.client.vo.SysResourceAuditLogVO;
import org.laokou.admin.client.vo.SysResourceVO;
import org.laokou.auth.client.utils.UserUtil;
import org.laokou.common.core.exception.CustomException;
import org.laokou.common.core.utils.ConvertUtil;
import org.laokou.common.core.utils.FileUtil;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.oss.client.vo.UploadVO;
import lombok.extern.slf4j.Slf4j;
import org.laokou.elasticsearch.client.model.CreateIndexModel;
import org.laokou.elasticsearch.client.model.ElasticsearchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/8/19 0019 下午 3:43
 */
@Service
@Slf4j
public class SysResourceApplicationServiceImpl implements SysResourceApplicationService {

    @Autowired
    private SysResourceService sysResourceService;

    private static final String PROCESS_KEY = "Process_88888888";

    @Autowired
    private WorkflowProcessApplicationService workflowProcessApplicationService;

    @Autowired
    private WorkFlowUtil workFlowUtil;

    @Autowired
    private SysResourceAuditLogService sysResourceAuditLogService;

    @Autowired
    private ElasticsearchApiFeignClient elasticsearchApiFeignClient;

    @Autowired
    private AsyncTaskExecutor asyncTaskExecutor;

    @Override
    public IPage<SysResourceVO> queryResourcePage(SysResourceQO qo) {
        IPage<SysResourceVO> page = new Page(qo.getPageNum(),qo.getPageSize());
        return sysResourceService.getResourceList(page,qo);
    }

    @Override
    public SysResourceVO getResourceById(Long id) {
        return sysResourceService.getResourceById(id);
    }

    @Override
    public Boolean insertResource(SysResourceDTO dto) {
        SysResourceDO sysResourceDO = ConvertUtil.sourceToTarget(dto, SysResourceDO.class);
        sysResourceDO.setCreator(UserUtil.getUserId());
        sysResourceDO.setAuthor(UserUtil.getUsername());
        sysResourceDO.setStatus(0);
        sysResourceService.save(sysResourceDO);
        String instanceId = startWork(sysResourceDO.getId(), sysResourceDO.getTitle());
        sysResourceDO.setProcessInstanceId(instanceId);
        return sysResourceService.updateById(sysResourceDO);
    }

    private String startWork(Long id,String name) {
        StartProcessVO startProcessVO = workflowProcessApplicationService.startResourceProcess(PROCESS_KEY,id.toString(),name);
        String definitionId = startProcessVO.getDefinitionId();
        String instanceId = startProcessVO.getInstanceId();
        String auditUser = workFlowUtil.getAuditUser(definitionId, instanceId);
        workFlowUtil.sendAuditMsg(auditUser, MessageTypeEnum.REMIND.ordinal(), ChannelTypeEnum.PLATFORM.ordinal(),id,name);
        return instanceId;
    }

    @Override
    public Boolean updateResource(SysResourceDTO dto) {
        SysResourceDO sysResourceDO = ConvertUtil.sourceToTarget(dto, SysResourceDO.class);
        sysResourceDO.setEditor(UserUtil.getUserId());
        sysResourceDO.setStatus(0);
        String instanceId = startWork(sysResourceDO.getId(), dto.getTitle());
        sysResourceDO.setProcessInstanceId(instanceId);
        return sysResourceService.updateById(sysResourceDO);
    }

    @Override
    public Boolean deleteResource(Long id) {
        sysResourceService.deleteResource(id);
        return true;
    }

    @Override
    public UploadVO uploadResource(String code, String fileName, InputStream inputStream, Long fileSize) throws Exception {
        //判断类型
        String fileSuffix = FileUtil.getFileSuffix(fileName);
        if (!FileUtil.checkFileExt(code,fileSuffix)) {
            throw new CustomException("格式不正确，请重新上传资源");
        }
        UploadVO vo = new UploadVO();
        String md5 = DigestUtils.md5DigestAsHex(inputStream);
        //判断是否有md5
        LambdaQueryWrapper<SysResourceDO> wrapper = Wrappers.lambdaQuery(SysResourceDO.class);
        wrapper.eq(SysResourceDO::getMd5,md5);
        List<SysResourceDO> list = sysResourceService.list(wrapper);
        if (list.size() > 0) {
            vo.setUrl(list.get(0).getUri());
        }
        vo.setMd5(md5);
        return vo;
    }

    @Override
    public Boolean syncAsyncBatchResource(String code) throws InterruptedException {
            //总数
            final Long resourceTotal = sysResourceService.getResourceTotal(code);
            if (resourceTotal > 0) {
                beforeSync();
                //创建索引 - 时间分区
                final String resourceIndex = "laokou_resource_" + code;
                final String resourceIndexAlias = "laokou_resource";
                final List<String> resourceYMPartitionList = sysResourceService.getResourceYMPartitionList(code);
                CountDownLatch countDownLatch = new CountDownLatch(resourceYMPartitionList.size());
                for (String ym : resourceYMPartitionList) {
                    asyncTaskExecutor.execute(() -> {
                        final CreateIndexModel model = new CreateIndexModel();
                        final String indexName = resourceIndex + "_" + ym;
                        final String indexAlias = resourceIndexAlias;
                        model.setIndexName(indexName);
                        model.setIndexAlias(indexAlias);
                        elasticsearchApiFeignClient.create(model);
                        countDownLatch.countDown();
                    });
                }
                countDownLatch.await();
                //同步数据 - 异步
                final int chunkSize = 500;
                int pageIndex = 0;
                while (pageIndex < resourceTotal) {
                    final List<ResourceIndex> resourceIndexList = sysResourceService.getResourceIndexList(chunkSize, pageIndex,code);
                    final Map<String, List<ResourceIndex>> resourceDataMap = resourceIndexList.stream().collect(Collectors.groupingBy(ResourceIndex::getYm));
                    final Iterator<Map.Entry<String, List<ResourceIndex>>> iterator = resourceDataMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        final Map.Entry<String, List<ResourceIndex>> entry = iterator.next();
                        final String ym = entry.getKey();
                        final List<ResourceIndex> resourceDataList = entry.getValue();
                        //同步数据
                        asyncTaskExecutor.execute(() -> {
                            final String indexName = resourceIndex + "_" + ym;
                            final String jsonDataList = JacksonUtil.toJsonStr(resourceDataList);
                            final ElasticsearchModel model = new ElasticsearchModel();
                            model.setIndexName(indexName);
                            model.setData(jsonDataList);
                            //同步数据
                            elasticsearchApiFeignClient.syncAsyncBatch(model);
                        });
                    }
                    pageIndex += chunkSize;
                }
                afterSync();
            }
        return true;
    }

    @Override
    public List<SysResourceAuditLogVO> queryAuditLogList(Long resourceId) {
        return sysResourceAuditLogService.getAuditLogList(resourceId);
    }

    private void beforeSync() {
        log.info("开始同步数据...");
    }

    private void afterSync() {
        log.info("结束同步数据...");
    }

}
