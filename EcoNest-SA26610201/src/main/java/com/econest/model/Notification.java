package com.econest.model;

public record Notification(int id, int assignmentId, String recipientEmail, String subject,
                           String message, String status, String createdAt, String sentAt,
                           String errorMessage) { }
