package com.los.administration.visibility.repository;

import com.los.administration.visibility.model.ManualShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManualShareRepository
        extends JpaRepository<ManualShare, Long> {

    List<ManualShare> findByOwnerUserId(String ownerUserId);
}