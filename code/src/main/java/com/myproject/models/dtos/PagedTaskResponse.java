package com.myproject.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedTaskResponse {
    private List<TaskResponse> content;
    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;
    private Integer pageSize;
    private Integer page;
    private Integer size;
    private Integer number;
    private Boolean last;
    
    public void setPage(Integer page) {
        this.page = page;
        this.currentPage = page;
        this.number = page;
    }
    
    public void setSize(Integer size) {
        this.size = size;
        this.pageSize = size;
    }
    
    public void setLast(Boolean last) {
        this.last = last;
    }
    
    public Integer getPage() {
        return this.page != null ? this.page : this.currentPage;
    }
    
    public Integer getSize() {
        return this.size != null ? this.size : this.pageSize;
    }
    
    public Integer getNumber() {
        return this.number != null ? this.number : this.currentPage;
    }
    
    public Boolean getLast() {
        return this.last;
    }
}