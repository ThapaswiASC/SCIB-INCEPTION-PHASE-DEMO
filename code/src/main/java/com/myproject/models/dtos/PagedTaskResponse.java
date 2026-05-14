package com.myproject.models.dtos;

import java.util.List;

public class PagedTaskResponse {
    private List<TaskResponse> content;
    private Long totalElements;
    private Integer totalPages;
    private Integer size;
    private Integer number;

    public PagedTaskResponse() {
    }

    public PagedTaskResponse(List<TaskResponse> content, Long totalElements, Integer totalPages, Integer size, Integer number) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.size = size;
        this.number = number;
    }

    public List<TaskResponse> getContent() {
        return content;
    }

    public void setContent(List<TaskResponse> content) {
        this.content = content;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
