package com.ys.infrastructure.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageRequest {
    Integer currentPage;
    Integer pageSize;

    public static PageRequest of(Integer currentPage, Integer pageSize) {
        return new PageRequest(currentPage, pageSize);
    }

    public boolean hasPagination() {
        return (currentPage != null && pageSize != null);
    }

    public void removePageRequest() {
        this.currentPage = null;
        this.pageSize = null;
    }
}
