package com.myproject.controllers;

import com.myproject.models.dtos.ColumnTaskCountResponse;
import com.myproject.services.interfaces.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/columns")
public class ColumnController {

    @Autowired
    private ColumnService columnService;

    @GetMapping("/{columnId}/count")
    public ResponseEntity<ColumnTaskCountResponse> getColumnTaskCount(@PathVariable String columnId) {
        ColumnTaskCountResponse response = columnService.getColumnTaskCount(columnId);
        return ResponseEntity.ok(response);
    }
}