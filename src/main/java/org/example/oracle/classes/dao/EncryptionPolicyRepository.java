package org.example.oracle.classes.dao;


import org.example.oracle.classes.model.EncryptionPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EncryptionPolicyRepository extends JpaRepository<EncryptionPolicy, Long> {
    List<EncryptionPolicy> findByEnabled(boolean enabled);
}
