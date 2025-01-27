package org.example.oracle.classes.dao;


import org.example.oracle.classes.model.VPDPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VPDPolicyRepository extends JpaRepository<VPDPolicy, Long> {
    VPDPolicy findByPolicyName(String policyName);
    boolean existsByPolicyName(String policyName);
    VPDPolicy findByTableNameAndActive(String tableName, boolean active);
    List<VPDPolicy> findByActiveTrue();

}
