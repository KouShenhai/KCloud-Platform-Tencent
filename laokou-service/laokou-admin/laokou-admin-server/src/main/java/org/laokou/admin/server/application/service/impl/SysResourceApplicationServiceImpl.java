/**
 * Copyright (c) 2022 KCloud-Platform Authors. All Rights Reserved.
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
import org.laokou.admin.client.feign.elasticsearch.ElasticsearchApiFeignClient;
import org.laokou.admin.client.index.ResourceIndex;
import org.laokou.admin.server.infrastructure.utils.WorkFlowUtil;
import org.laokou.admin.client.dto.SysResourceDTO;
import org.laokou.admin.server.interfaces.qo.SysResourceQO;
import org.laokou.admin.client.vo.StartProcessVO;
import org.laokou.admin.client.vo.SysResourceAuditLogVO;
import org.laokou.admin.client.vo.SysResourceVO;
import org.laokou.admin.client.vo.UploadVO;
import org.laokou.common.exception.CustomException;
import org.laokou.auth.client.user.SecurityUser;
import org.laokou.common.utils.ConvertUtil;
import org.laokou.common.utils.FileUtil;
import org.laokou.common.utils.JacksonUtil;
import org.laokou.datasource.annotation.DataSource;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.laokou.elasticsearch.client.model.CreateIndexModel;
import org.laokou.elasticsearch.client.model.ElasticsearchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Iterator;
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
@GlobalTransactional(rollbackFor = Exception.class)
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

    @Override
    @DataSource("master")
    public IPage<SysResourceVO> queryResourcePage(SysResourceQO qo) {
        IPage<SysResourceVO> page = new Page(qo.getPageNum(),qo.getPageSize());
        return sysResourceService.getResourceList(page,qo);
    }

    @Override
    @DataSource("master")
    public SysResourceVO getResourceById(Long id) {
        return sysResourceService.getResourceById(id);
    }

    @Override
    @DataSource("master")
    public Boolean insertResource(SysResourceDTO dto, HttpServletRequest request) {
        SysResourceDO sysResourceDO = ConvertUtil.sourceToTarget(dto, SysResourceDO.class);
        sysResourceDO.setCreator(SecurityUser.getUserId(request));
        sysResourceDO.setAuthor(SecurityUser.getUsername(request));
        sysResourceDO.setStatus(0);
        sysResourceService.save(sysResourceDO);
        String instanceId = startWork(sysResourceDO.getId(), sysResourceDO.getTitle(),request);
        sysResourceDO.setProcessInstanceId(instanceId);
        return sysResourceService.updateById(sysResourceDO);
    }

    private String startWork(Long id,String name,HttpServletRequest request) {
        StartProcessVO startProcessVO = workflowProcessApplicationService.startResourceProcess(PROCESS_KEY,id.toString(),name);
        String definitionId = startProcessVO.getDefinitionId();
        String instanceId = startProcessVO.getInstanceId();
        String auditUser = workFlowUtil.getAuditUser(definitionId, null, instanceId);
        workFlowUtil.sendAuditMsg(auditUser, MessageTypeEnum.REMIND.ordinal(), ChannelTypeEnum.PLATFORM.ordinal(),id,name,request);
        return instanceId;
    }

    @Override
    @DataSource("master")
    public Boolean updateResource(SysResourceDTO dto, HttpServletRequest request) {
        SysResourceDO sysResourceDO = ConvertUtil.sourceToTarget(dto, SysResourceDO.class);
        sysResourceDO.setEditor(SecurityUser.getUserId(request));
        sysResourceDO.setStatus(0);
        String instanceId = startWork(sysResourceDO.getId(), dto.getTitle(),request);
        sysResourceDO.setProcessInstanceId(instanceId);
        return sysResourceService.updateById(sysResourceDO);
    }

    @Override
    @DataSource("master")
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
    public Boolean syncAsyncBatchResource(String code) {
        //总数
        final Long resourceTotal = sysResourceService.getResourceTotal(code);
        if (resourceTotal > 0) {
            beforeSync();
            //创建索引 - 时间分区
            final String resourceIndex = "laokou_resource_" + code;
            final String resourceIndexAlias = "laokou_resource";
            final List<String> resourceYMPartitionList = sysResourceService.getResourceYMPartitionList(code);
            for (String ym: resourceYMPartitionList) {
                final CreateIndexModel model = new CreateIndexModel();
                final String indexName = resourceIndex + "_" + ym;
                final String indexAlias = resourceIndexAlias;
                model.setIndexName(indexName);
                model.setIndexAlias(indexAlias);
                elasticsearchApiFeignClient.create(model);
            }
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
                    final String indexName = resourceIndex + "_" + ym;
                    final String jsonDataList = JacksonUtil.toJsonStr(resourceDataList);
                    final ElasticsearchModel model = new ElasticsearchModel();
                    model.setIndexName(indexName);
                    model.setData(jsonDataList);
                    model.setIndexAlias(resourceIndexAlias);
                    //同步数据
                    elasticsearchApiFeignClient.syncAsyncBatch(model);
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
