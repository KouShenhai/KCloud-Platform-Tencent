# 创建存储过程
DROP PROCEDURE
    IF EXISTS updatePath1;

CREATE PROCEDURE updatePath1 (
    IN did BIGINT (20),
    IN pid BIGINT (20)
        )
BEGIN
	# 定义变量
DECLARE s INT DEFAULT 0;


DECLARE nid BIGINT (20);


DECLARE npath VARCHAR (2000);

# 接收数据集
DECLARE consume CURSOR FOR SELECT
id,
path
FROM
boot_sys_dept
WHERE
pid = pid;

# 打开游标
OPEN consume;

# 赋值
FETCH consume INTO nid,
 npath;


IF pid = '0' THEN
UPDATE boot_sys_dept
SET path = CONCAT('0/', did)
WHERE
        id = did;


ELSE
UPDATE boot_sys_dept
SET path = CONCAT(npath, '/', did)
WHERE
        id = did;


END
IF;

# 关闭游标
CLOSE consume;


END;

# 调用
CALL updatePath1 ('1587317699557044226', '0');