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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.laokou.admin.server.application.service.SysDeptApplicationService;
import org.laokou.admin.server.domain.sys.entity.SysDeptDO;
import org.laokou.admin.server.domain.sys.entity.SysUserDO;
import org.laokou.admin.server.domain.sys.repository.service.SysDeptService;
import org.laokou.admin.client.dto.SysDeptDTO;
import org.laokou.admin.server.domain.sys.repository.service.SysUserService;
import org.laokou.admin.server.interfaces.qo.SysDeptQO;
import org.laokou.common.constant.Constant;
import org.laokou.common.exception.CustomException;
import org.laokou.common.utils.ConvertUtil;
import org.laokou.common.utils.TreeUtil;
import org.laokou.admin.client.vo.SysDeptVO;
import org.laokou.security.client.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/7/26 0026 下午 4:30
 */
@Service
public class SysDeptApplicationServiceImpl implements SysDeptApplicationService {

    @Autowired
    private SysDeptService sysDeptService;

    @Autowired
    private SysUserService sysUserService;

    @Override
    public SysDeptVO getDeptList() {
        SysDeptQO qo = new SysDeptQO();
        qo.setStatus(Constant.NO);
        List<SysDeptVO> deptList = sysDeptService.getDeptList(qo);
        return buildDept(deptList);
    }

    @Override
    public List<SysDeptVO> queryDeptList(SysDeptQO qo) {
        return sysDeptService.getDeptList(qo);
    }

    @Override
    public Boolean insertDept(SysDeptDTO dto) {
        SysDeptDO sysDeptDO = ConvertUtil.sourceToTarget(dto, SysDeptDO.class);
        long count = sysDeptService.count(Wrappers.lambdaQuery(SysDeptDO.class).eq(SysDeptDO::getName, dto.getName()).eq(SysDeptDO::getDelFlag, Constant.NO));
        if (count > 0) {
            throw new CustomException("部门已存在，请重新填写");
        }
        sysDeptDO.setCreator(UserUtil.getUserId());
        sysDeptService.save(sysDeptDO);
        // 修改当前节点path
        sysDeptService.updateDeptPath1ById(sysDeptDO.getId(),sysDeptDO.getPid());
        return true;
    }

    @Override
    public Boolean updateDept(SysDeptDTO dto) {
        SysDeptDO sysDeptDO = ConvertUtil.sourceToTarget(dto, SysDeptDO.class);
        long count = sysDeptService.count(Wrappers.lambdaQuery(SysDeptDO.class).eq(SysDeptDO::getName, dto.getName()).eq(SysDeptDO::getDelFlag, Constant.NO).ne(SysDeptDO::getId,dto.getId()));
        if (count > 0) {
            throw new CustomException("部门已存在，请重新填写");
        }
        sysDeptDO.setEditor(UserUtil.getUserId());
        sysDeptService.updateById(sysDeptDO);
        // 修改当前节点及子节点path
        sysDeptService.updateDeptPath2ById(dto.getId(),dto.getPid());
        return true;
    }

    @Override
    public Boolean deleteDept(Long id) {
        long count = sysUserService.count(Wrappers.lambdaQuery(SysUserDO.class).eq(SysUserDO::getDeptId, id).eq(SysUserDO::getDelFlag, Constant.NO));
        if (count > 0) {
            throw new CustomException("不可删除，该部门下存在用户");
        }
        sysDeptService.deleteDept(id);
        return true;
    }

    @Override
    public SysDeptVO getDept(Long id) {
        return sysDeptService.getDept(id);
    }

    @Override
    public List<Long> getDeptIdsByRoleId(Long roleId) {
        return sysDeptService.getDeptIdsByRoleId(roleId);
    }

    /**
     * 组装树部门
     * @param deptList
     * @return
     */
    private SysDeptVO buildDept(List<SysDeptVO> deptList) {
        TreeUtil.TreeNo<TreeUtil.TreeNo> rootNode = TreeUtil.rootRootNode();
        SysDeptVO rootDeptNode = ConvertUtil.sourceToTarget(rootNode, SysDeptVO.class);
        return TreeUtil.buildTreeNode(deptList,rootDeptNode);
    }

}
