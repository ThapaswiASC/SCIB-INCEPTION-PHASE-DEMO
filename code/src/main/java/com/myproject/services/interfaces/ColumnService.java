package com.myproject.services.interfaces;

import com.myproject.models.dtos.ColumnTaskCountResponse;

public interface ColumnService {

    ColumnTaskCountResponse getColumnTaskCount(String columnId);
}