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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.laokou.admin.server.application.service.SysMessageApplicationService;
import org.laokou.admin.server.domain.sys.entity.SysMessageDO;
import org.laokou.admin.server.domain.sys.entity.SysMessageDetailDO;
import org.laokou.admin.server.domain.sys.repository.service.SysMessageDetailService;
import org.laokou.admin.server.domain.sys.repository.service.SysMessageService;
import org.laokou.admin.server.infrastructure.annotation.DataFilter;
import org.laokou.admin.client.dto.MessageDTO;
import org.laokou.admin.server.infrastructure.feign.im.ImApiFeignClient;
import org.laokou.admin.server.interfaces.qo.SysMessageQo;
import org.laokou.admin.client.vo.MessageDetailVO;
import org.laokou.admin.client.vo.SysMessageVO;
import org.apache.commons.collections.CollectionUtils;
import org.laokou.auth.client.utils.UserUtil;
import org.laokou.common.core.constant.Constant;
import org.laokou.common.core.utils.ConvertUtil;
import org.laokou.im.client.PushMsgDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
/**
 * @author Kou Shenhai
 */
@Service
@Slf4j
public class SysMessageApplicationServiceImpl implements SysMessageApplicationService {

    @Autowired
    private SysMessageService sysMessageService;

    @Autowired
    private SysMessageDetailService sysMessageDetailService;

    @Autowired
    private ImApiFeignClient apiFeignClient;

    @Override
    public Boolean insertMessage(MessageDTO dto) {
        SysMessageDO messageDO = ConvertUtil.sourceToTarget(dto, SysMessageDO.class);
        messageDO.setCreateDate(new Date());
        messageDO.setCreator(UserUtil.getUserId());
        messageDO.setUsername(UserUtil.getUsername());
        messageDO.setDeptId(UserUtil.getDeptId());
        sysMessageService.save(messageDO);
        Set<String> receiver = dto.getReceiver();
        Iterator<String> iterator = receiver.iterator();
        List<SysMessageDetailDO> detailDOList = new ArrayList<>(receiver.size());
        while (iterator.hasNext()) {
            String next = iterator.next();
            SysMessageDetailDO detailDO = new SysMessageDetailDO();
            detailDO.setMessageId(messageDO.getId());
            detailDO.setUserId(Long.valueOf(next));
            detailDO.setCreateDate(new Date());
            detailDO.setCreator(UserUtil.getUserId());
            detailDOList.add(detailDO);
        }
        if (CollectionUtils.isNotEmpty(detailDOList)) {
            sysMessageDetailService.saveBatch(detailDOList);
        }
        // 发送消息
        sendMessage(dto);
        return true;
    }

    private void sendMessage(MessageDTO dto) {
        try {
            PushMsgDTO pushMsgDTO = new PushMsgDTO();
            pushMsgDTO.setSender(UserUtil.getUsername());
            pushMsgDTO.setMsg(String.format("%s发来一条消息", UserUtil.getUsername()));
            pushMsgDTO.setReceiver(dto.getReceiver());
            apiFeignClient.push(pushMsgDTO);
        } catch (FeignException e) {
            log.error("错误消息：{}",e.getMessage());
        }
    }

    @Override
    @DataFilter(tableAlias = "boot_sys_message")
    public IPage<SysMessageVO> queryMessagePage(SysMessageQo qo) {
        IPage<SysMessageVO> page = new Page<>(qo.getPageNum(),qo.getPageSize());
        return sysMessageService.getMessageList(page,qo);
    }

    @Override
    public MessageDetailVO getMessageByDetailId(Long id) {
        sysMessageService.readMessage(id);
        return sysMessageService.getMessageByDetailId(id);
    }

    @Override
    public MessageDetailVO getMessageById(Long id) {
        return sysMessageService.getMessageById(id);
    }

    @Override
    public IPage<SysMessageVO> getUnReadList(SysMessageQo qo) {
        IPage<SysMessageVO> page = new Page<>(qo.getPageNum(),qo.getPageSize());
        final Long userId = UserUtil.getUserId();
        return sysMessageService.getUnReadList(page,userId);
    }

    @Override
    public Long unReadCount() {
        final Long userId = UserUtil.getUserId();
        return sysMessageDetailService.count(Wrappers.lambdaQuery(SysMessageDetailDO.class).eq(SysMessageDetailDO::getUserId,userId)
                .eq(SysMessageDetailDO::getDelFlag, Constant.NO)
                .eq(SysMessageDetailDO::getReadFlag, Constant.NO));
    }

}
