package com.los.administration.visibility.repository;

import com.los.administration.visibility.model.RecordShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordShareRepository extends JpaRepository<RecordShare, Long> {

    List<RecordShare> findByRecordTypeAndRecordIdAndActiveTrue(String recordType, String recordId);

    List<RecordShare> findByRecordTypeAndActiveTrue(String recordType);
}
