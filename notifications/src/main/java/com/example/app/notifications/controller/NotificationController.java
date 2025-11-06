package com.example.app.notifications.controller;

import com.example.app.notifications.domain.NotificationRequest;
import com.example.app.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for notification operations.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Notification API")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    @Operation(summary = "Send a notification")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<Map<String, String>> sendNotification(@Valid @RequestBody NotificationRequest request) {
        notificationService.sendNotification(request);
        Map<String, String> response = new HashMap<>();
        response.put("status", "sent");
        return ResponseEntity.ok(response);
    }
}

