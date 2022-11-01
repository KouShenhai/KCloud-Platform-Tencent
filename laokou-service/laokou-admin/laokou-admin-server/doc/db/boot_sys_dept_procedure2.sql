# 删除存储过程
DROP PROCEDURE
    IF EXISTS updatePath2;

# 创建存储过程
CREATE PROCEDURE updatePath2 (
    IN xid BIGINT (20),
    IN pid BIGINT (20)
        )
BEGIN
	# 定义变量
DECLARE s INT DEFAULT 0;

# 定义变量
DECLARE parpath VARCHAR (2000);

# 定义变量
DECLARE nid BIGINT (20);

# 定义变量
DECLARE npath VARCHAR (2000);

# 接收结果集
DECLARE con1 CURSOR FOR SELECT

IF (count(path) > 0, path, '0') AS path
FROM
boot_sys_dept
WHERE
  id = pid;

# 接收结果集
DECLARE con2 CURSOR FOR SELECT
id,
path
FROM
boot_sys_dept
WHERE
  path LIKE concat('%', xid, '%')
AND del_flag = '0'
AND id <> xid;

# 没有数据后返回，将s=1
DECLARE CONTINUE HANDLER FOR NOT FOUND
SET s = 1;

# 开启游标
OPEN con1;

# 开启游标
OPEN con2;

# 赋值
FETCH con1 INTO parpath;

# 赋值
FETCH con2 INTO nid,
 npath;


WHILE s <> 1 DO
UPDATE boot_sys_dept
SET path = concat(parpath, '/', nid)
WHERE
        id = nid;

# 赋值
FETCH con2 INTO nid,
 npath;


END
WHILE;

# 关闭游标
CLOSE con1;

# 关闭游标
CLOSE con2;

# 修改
UPDATE boot_sys_dept
SET path = concat(parpath, '/', xid)
WHERE
        id = xid;


END;

# 调用
CALL updatePath2 ('1535887940687765505', '0');