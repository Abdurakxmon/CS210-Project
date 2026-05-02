package com.cs210.project.models;

import java.time.LocalDateTime;

public class AuditLogEntry {
    private final LocalDateTime eventTime;
    private final String category;
    private final String actor;
    private final String subject;
    private final String description;

    public AuditLogEntry(LocalDateTime eventTime, String category, String actor, String subject, String description) {
        this.eventTime = eventTime;
        this.category = category;
        this.actor = actor;
        this.subject = subject;
        this.description = description;
    }

    public LocalDateTime getEventTime() { return eventTime; }
    public String getCategory() { return category; }
    public String getActor() { return actor; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
}
