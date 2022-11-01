# 删除存储过程
DROP PROCEDURE
IF EXISTS deptIds;

# 创建存储过程
CREATE PROCEDURE deptIds (IN userId BIGINT(20))
BEGIN
# 定义变量值
DECLARE dept_id BIGINT (20);

# 定义变量值
DECLARE s INT DEFAULT 0;

# 接收数据集
DECLARE consume CURSOR FOR SELECT
boot_sys_dept.id
FROM
boot_sys_dept,
boot_sys_role,
boot_sys_role_dept,
boot_sys_user,
boot_sys_user_role
WHERE
boot_sys_dept.id = boot_sys_role_dept.dept_id
AND boot_sys_role_dept.role_id = boot_sys_role.id
AND boot_sys_user.id = boot_sys_user_role.user_id
AND boot_sys_role.id = boot_sys_user_role.role_id
AND boot_sys_user.id = userId
AND boot_sys_dept.del_flag = '0'
GROUP BY
boot_sys_dept.id;

# 没有数据返回，将变量设置为1
DECLARE CONTINUE HANDLER FOR NOT FOUND
SET s = 1;

# 创建表
CREATE TABLE
IF NOT EXISTS `temp_boot_sys_dept` (
`id` BIGINT (20) DEFAULT NULL,
`uid` BIGINT (20) DEFAULT NULL,
KEY `idx_id_uid` (`id`, `uid`)
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4;

# 删除数据
DELETE
FROM
 temp_boot_sys_dept
WHERE
 uid = userId;

# 打开cosume游标进行程序调用
OPEN consume;

# 将consume赋值给dept_id
FETCH consume INTO dept_id;

WHILE s <> 1 DO
INSERT temp_boot_sys_dept SELECT
id,
userId
FROM
boot_sys_dept
WHERE
del_flag = '0'
AND path LIKE concat('%', dept_id, '%');

# 将consume赋值给dept_id
FETCH consume INTO dept_id;


END
WHILE;


# 关闭游标
CLOSE consume;

# 查询
SELECT
    id
FROM
    temp_boot_sys_dept
WHERE
        uid = userId
GROUP BY
    id;

END;

# 调用
CALL deptIds ('');