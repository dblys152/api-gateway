package com.ys.infrastructure.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {
    int currentPage;
    int pageSize;
    int totalItems;
    int totalPages;
    int previousPage;
    Integer nextPage;

    public static Pagination create(int currentPage, int pageSize, int totalItems) {
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        int previousPage = currentPage > 1 ? currentPage - 1 : 0;
        Integer nextPage = currentPage < totalPages ? currentPage + 1 : null;

        return new Pagination(currentPage, pageSize, totalItems, totalPages, previousPage, nextPage);
    }
}
