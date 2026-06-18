package com.inventory.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Stable paginated API response shape for list endpoints.
 *
 * @param <T> item type in the current page
 * @param content items in the current page
 * @param totalElements total matching records across all pages
 * @param totalPages number of pages available
 * @param size requested page size
 * @param number zero-based page index
 */
public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int size,
        int number) {

    /**
     * Maps a Spring Data page to the API pagination envelope.
     *
     * @param page source page
     * @param mapper entity-to-DTO mapper
     * @param <E> entity type
     * @param <D> DTO type
     * @return paginated API response
     */
    public static <E, D> PageResponse<D> from(Page<E> page, Function<E, D> mapper) {
        return new PageResponse<>(
                page.map(mapper).getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber());
    }
}
