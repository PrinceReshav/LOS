package com.los.administration.outbox.repository;

import com.los.administration.outbox.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop50ByPublishedFalseOrderByCreatedAtAsc();

}