package com.los.loanoriginatingsystem.outbox.repository;

import com.los.loanoriginatingsystem.outbox.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, String> {

    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(String status);
}