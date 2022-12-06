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
package org.laokou.admin.server.application.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.laokou.admin.server.application.service.SysResourceApplicationService;
import org.laokou.admin.server.application.service.WorkflowProcessApplicationService;
import org.laokou.admin.server.domain.sys.entity.SysResourceDO;
import org.laokou.admin.server.domain.sys.repository.service.SysResourceAuditLogService;
import org.laokou.admin.server.domain.sys.repository.service.SysResourceService;
import org.laokou.rocketmq.client.enums.ChannelTypeEnum;
import org.laokou.admin.client.enums.MessageTypeEnum;
import org.laokou.admin.client.index.ResourceIndex;
import org.laokou.admin.server.infrastructure.feign.kafka.RocketmqApiFeignClient;
import org.laokou.admin.client.dto.SysResourceDTO;
import org.laokou.admin.server.interfaces.qo.SysResourceQo;
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
import org.laokou.rocketmq.client.constant.RocketmqConstant;
import org.laokou.rocketmq.client.dto.ResourceSyncDTO;
import org.laokou.rocketmq.client.dto.RocketmqDTO;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/8/19 0019 下午 3:43
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysResourceApplicationServiceImpl implements SysResourceApplicationService {
    private static final String RESOURCE_KEY = "laokou_resource";

    private static final String PROCESS_KEY = "Process_88888888";

    private static final Integer INIT_STATUS = 0;

    private final WorkflowProcessApplicationService workflowProcessApplicationService;

    private final SysResourceService sysResourceService;
    private final WorkFlowUtil workFlowUtil;

    private final SysResourceAuditLogService sysResourceAuditLogService;

    private final ThreadPoolTaskExecutor adminThreadPoolTaskExecutor;

    private final RocketmqApiFeignClient rocketmqApiFeignClient;

    @Override
    public IPage<SysResourceVO> queryResourcePage(SysResourceQo qo) {
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
        sysResourceDO.setStatus(INIT_STATUS);
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
        sysResourceDO.setStatus(INIT_STATUS);
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
    public Boolean syncResourceIndex(String code) {
            //总数
            final Long resourceTotal = sysResourceService.getResourceTotal(code);
            if (resourceTotal > 0) {
                beforeSyncAsync();
                //创建索引 - 时间分区
                final String resourceIndex = RESOURCE_KEY + "_" + code;
                //同步数据 - 异步
                final int chunkSize = 500;
                int pageIndex = 0;
                while (pageIndex < resourceTotal) {
                    final List<ResourceIndex> resourceIndexList = sysResourceService.getResourceIndexList(chunkSize, pageIndex,code);
                    final Map<String, List<ResourceIndex>> resourceDataMap = resourceIndexList.stream().collect(Collectors.groupingBy(ResourceIndex::getYm));
                    for (Map.Entry<String, List<ResourceIndex>> entry : resourceDataMap.entrySet()) {
                        final String ym = entry.getKey();
                        final List<ResourceIndex> resourceDataList = entry.getValue();
                        //同步数据
                        adminThreadPoolTaskExecutor.execute(() -> {
                            try {
                                RocketmqDTO dto = new RocketmqDTO();
                                final String indexName = resourceIndex + "_" + ym;
                                final String jsonDataList = JacksonUtil.toJsonStr(resourceDataList);
                                final ResourceSyncDTO model = new ResourceSyncDTO();
                                model.setIndexName(indexName);
                                model.setData(jsonDataList);
                                dto.setData(JacksonUtil.toJsonStr(model));
                                rocketmqApiFeignClient.sendAsyncMessage(RocketmqConstant.LAOKOU_SYNC_RESOURCE_TOPIC,dto);
                            } catch (final FeignException e) {
                                log.error("错误信息：{}",e.getMessage());
                            }
                        });
                    }
                    pageIndex += chunkSize;
                }
                afterSyncAsync();
            }
        return true;
    }

    @Override
    public List<SysResourceAuditLogVO> queryAuditLogList(Long resourceId) {
        return sysResourceAuditLogService.getAuditLogList(resourceId);
    }

    private void beforeCreateIndex() {
        log.info("开始索引创建...");
    }

    private void afterCreateIndex() {
        log.info("结束索引创建...");
    }

    @Override
    public Boolean createResourceIndex(String code) {
        // 总数
        final Long resourceTotal = sysResourceService.getResourceTotal(code);
        if (resourceTotal > 0) {
            beforeCreateIndex();
            //创建索引 - 时间分区
            final String resourceIndex = RESOURCE_KEY + "_" + code;
            final String resourceIndexAlias = RESOURCE_KEY;
            final List<String> resourceYmPartitionList = sysResourceService.getResourceYmPartitionList(code);
            for (String ym : resourceYmPartitionList) {
                adminThreadPoolTaskExecutor.execute(() -> {
                    try {
                        final CreateIndexModel model = new CreateIndexModel();
                        final String indexName = resourceIndex + "_" + ym;
                        model.setIndexName(indexName);
                        model.setIndexAlias(resourceIndexAlias);
                        // 用独立的类
                        // 写入rocketmq
                    } catch (final FeignException e) {
                        log.error("错误信息：{}", e.getMessage());
                    }
                });
            }
            afterCreateIndex();
        }
        return true;
    }

    private void beforeSyncAsync() {
        log.info("开始异步同步数据...");
    }

    private void afterSyncAsync() {
        log.info("结束异步同步数据...");
    }

}
