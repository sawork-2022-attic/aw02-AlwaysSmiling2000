package com.example.poshell.db;

import com.example.poshell.model.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.sqlite.SQLiteException;

// 访问 sqlite 数据库的接口，此接口的方法实现由 mybatis 根据
// resources/mapper/SqlitePosDBMapper.xml 文件自动生成
@Mapper
public interface SqlitePosDB extends PosDB {

    // sqlite 需要使用指令 PRAGMA FOREIGN_KEYS = ON 来开启外键约束
    public void openForeignKeyConstraint();

}
