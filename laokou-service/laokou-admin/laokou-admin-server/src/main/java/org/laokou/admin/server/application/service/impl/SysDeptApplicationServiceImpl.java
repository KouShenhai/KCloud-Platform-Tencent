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
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
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
import org.laokou.ump.client.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

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
        if (0 == dto.getPid()) {
            sysDeptDO.setPath("0/" + sysDeptDO.getId());
        } else {
            SysDeptDO deptDO = sysDeptService.getById(dto.getPid());
            if (deptDO != null) {
                sysDeptDO.setPath(deptDO.getPath() + "/" + sysDeptDO.getId());
            }
        }
        return sysDeptService.updateById(sysDeptDO);
    }

    @Override
    public Boolean updateDept(SysDeptDTO dto) {
        SysDeptDO sysDeptDO = ConvertUtil.sourceToTarget(dto, SysDeptDO.class);
        long count = sysDeptService.count(Wrappers.lambdaQuery(SysDeptDO.class).eq(SysDeptDO::getName, dto.getName()).eq(SysDeptDO::getDelFlag, Constant.NO).ne(SysDeptDO::getId,dto.getId()));
        if (count > 0) {
            throw new CustomException("部门已存在，请重新填写");
        }
        // 替换所有相关的子节点
        List<SysDeptDO> list = sysDeptService.list(Wrappers.lambdaQuery(SysDeptDO.class)
                .eq(SysDeptDO::getDelFlag, Constant.NO).and(r ->
                r.eq(SysDeptDO::getId, dto.getPid()).or()
                        .like(SysDeptDO::getPath, dto.getId()))
                .select(SysDeptDO::getId,SysDeptDO::getPid,SysDeptDO::getPath));
        String path;
        // 非顶级节点
        if (dto.getPid() != 0) {
            //父节点
            SysDeptDO parentDeptDO = list.stream().filter(i -> i.getId().equals(dto.getPid())).findFirst().get();
            //path
            path = parentDeptDO.getPath() + "/" + dto.getId();
        } else {
            path = "0/" + dto.getId();
        }
        // 顶级节点下的子节点 或 二级节点及以下的子节点
        boolean flag = (CollectionUtils.isNotEmpty(list) && dto.getPid() == 0) || (list.size() > 1 && dto.getPid() != 0);
        if (flag) {
            List<SysDeptDO> deptDOList = list.stream().filter(i -> !i.getId().equals(dto.getId()) && i.getPath().contains(dto.getId().toString())).collect(Collectors.toList());
            for (SysDeptDO dept : deptDOList) {
                //替换掉子节点path
                dept.setPath(path + dept.getPath().split(dto.getId().toString())[1]);
            }
            sysDeptService.updateBatchById(deptDOList);
        }
        sysDeptDO.setPath(path);
        sysDeptDO.setEditor(UserUtil.getUserId());
        return sysDeptService.updateById(sysDeptDO);
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
