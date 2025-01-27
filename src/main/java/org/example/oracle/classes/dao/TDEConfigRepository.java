package org.example.oracle.classes.dao;


import org.example.oracle.classes.model.TDEConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TDEConfigRepository extends JpaRepository<TDEConfig, Long> {
    TDEConfig findByTableNameAndColumnName(String tableName, String columnName);
    boolean existsByTableNameAndColumnName(String tableName, String columnName);
    List<TDEConfig> findByActiveTrue(); // Fetch only active TDE configurations

}
