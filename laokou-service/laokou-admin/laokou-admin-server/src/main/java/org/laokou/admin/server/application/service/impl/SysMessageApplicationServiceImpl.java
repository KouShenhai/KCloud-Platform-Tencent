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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.laokou.admin.server.application.service.SysMessageApplicationService;
import org.laokou.admin.server.domain.sys.entity.SysMessageDO;
import org.laokou.admin.server.domain.sys.entity.SysMessageDetailDO;
import org.laokou.admin.server.domain.sys.repository.service.SysMessageDetailService;
import org.laokou.admin.server.domain.sys.repository.service.SysMessageService;
import org.laokou.admin.server.infrastructure.annotation.DataFilter;
import org.laokou.admin.client.dto.MessageDTO;
import org.laokou.admin.server.infrastructure.feign.rocketmq.RocketmqApiFeignClient;
import org.laokou.admin.server.interfaces.qo.SysMessageQo;
import org.laokou.admin.client.vo.MessageDetailVO;
import org.laokou.admin.client.vo.SysMessageVO;
import org.apache.commons.collections.CollectionUtils;
import org.laokou.auth.client.utils.UserUtil;
import org.laokou.common.core.constant.Constant;
import org.laokou.common.core.utils.ConvertUtil;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.common.core.utils.SnowFlakeShortUtil;
import org.laokou.rocketmq.client.dto.RocketmqDTO;
import org.laokou.rocketmq.client.constant.RocketmqConstant;
import org.laokou.rocketmq.client.dto.MsgDTO;
import org.laokou.rocketmq.client.enums.ChannelTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
/**
 * @author Kou Shenhai
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SysMessageApplicationServiceImpl implements SysMessageApplicationService {

    private final SysMessageService sysMessageService;

    private final SysMessageDetailService sysMessageDetailService;

    private final RocketmqApiFeignClient rocketmqApiFeignClient;

    @Override
    public Boolean insertMessage(MessageDTO dto) {
        SysMessageDO messageDO = ConvertUtil.sourceToTarget(dto, SysMessageDO.class);
        messageDO.setCreateDate(new Date());
        messageDO.setCreator(UserUtil.getUserId());
        messageDO.setUsername(UserUtil.getUsername());
        messageDO.setDeptId(UserUtil.getDeptId());
        messageDO.setSendChannel(getSendChannel(dto));
        sysMessageService.save(messageDO);
        Set<String> receiver = dto.getPlatformReceiver();
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
        String content = dto.getContent();
        String title = dto.getTitle();
        Set<String> emailReceiver = dto.getEmailReceiver();
        Set<String> platformReceiver = dto.getPlatformReceiver();
        // 平台-发送消息
        if (CollectionUtils.isNotEmpty(platformReceiver)) {
            String newTitle = String.format("%s发来一条消息", UserUtil.getUsername());
            pushMessage(newTitle,"",platformReceiver,ChannelTypeEnum.PLATFORM.ordinal());
        }
        // 微信公众号-发送消息
        if (false) {

        }
        // 邮件-发送消息
        if (CollectionUtils.isNotEmpty(emailReceiver)) {
            pushMessage(title,content,emailReceiver, ChannelTypeEnum.EMAIL.ordinal());
        }
        return true;
    }

    private String getSendChannel(MessageDTO dto) {
        StringBuffer stringBuffer = new StringBuffer();
        if (CollectionUtils.isNotEmpty(dto.getPlatformReceiver())) {
            stringBuffer.append(ChannelTypeEnum.PLATFORM.ordinal()).append(",");
        }
        if (CollectionUtils.isNotEmpty(dto.getEmailReceiver())) {
            stringBuffer.append(ChannelTypeEnum.EMAIL.ordinal()).append(",");
        }
        String str = stringBuffer.toString();
        return str.isEmpty() ? "" : str.substring(0,str.length() - 1);
    }

    private void pushMessage(String title,String content,Set<String> receiver,Integer sendChannel) {
        try {
            MsgDTO msgDTO = new MsgDTO();
            msgDTO.setTitle(title);
            msgDTO.setReceiver(receiver);
            msgDTO.setContent(content);
            msgDTO.setSendChannel(sendChannel);
            RocketmqDTO dto = new RocketmqDTO();
            dto.setData(JacksonUtil.toJsonStr(msgDTO));
            dto.setMsgId(String.valueOf(SnowFlakeShortUtil.getInstance().nextId()));
            rocketmqApiFeignClient.sendMessage(RocketmqConstant.LAOKOU_MESSAGE_NOTICE_TOPIC,dto);
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
        return sysMessageService.getUnReadList(page,qo.getType(),userId);
    }

    @Override
    public Long unReadCount() {
        final Long userId = UserUtil.getUserId();
        return sysMessageDetailService.count(Wrappers.lambdaQuery(SysMessageDetailDO.class).eq(SysMessageDetailDO::getUserId,userId)
                .eq(SysMessageDetailDO::getDelFlag, Constant.NO)
                .eq(SysMessageDetailDO::getReadFlag, Constant.NO));
    }

}
