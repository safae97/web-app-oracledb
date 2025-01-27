package org.example.oracle.classes.dao;


import org.example.oracle.classes.model.TableInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableInfoRepository extends JpaRepository<TableInfo, Long> {
    TableInfo findByTableName(String tableName);
}
