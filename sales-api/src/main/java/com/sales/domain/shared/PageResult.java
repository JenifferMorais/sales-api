package com.sales.domain.shared;

import java.util.List;

public class PageResult<T> {
    private final List<T> content;
    private final long totalElements;
    private final int page;
    private final int size;

    public PageResult(List<T> content, long totalElements, int page, int size) {
        this.content = content;
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
    }

    public List<T> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalElements / size);
    }
}
