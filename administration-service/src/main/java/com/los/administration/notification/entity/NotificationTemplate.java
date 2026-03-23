package com.los.administration.notification.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notification_template")
@Data
public class NotificationTemplate {

    @Id
    private String id;

    private String code;

    private String type;

    private String subject;

    private String body;

    private Boolean active;
}