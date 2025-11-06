package com.example.app.common.dto;

import java.util.List;

/**
 * Generic pagination response wrapper for API endpoints that return paginated data.
 * 
 * <p>This class provides a standard structure for paginated responses, including
 * the data content, pagination metadata (page number, size, total elements, total pages),
 * and navigation information.
 * 
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * // In a service method:
 * Page<User> page = userRepository.findAll(pageable);
 * return new PagedResponse<>(
 *     page.getContent().stream().map(mapper::toResponse).collect(toList()),
 *     page.getNumber(),
 *     page.getSize(),
 *     page.getTotalElements(),
 *     page.getTotalPages()
 * );
 * }</pre>
 * 
 * @param <T> the type of elements in the content list
 * @author Generated
 * @since 1.0.0
 */
public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    /**
     * Default constructor for JSON deserialization.
     */
    public PagedResponse() {
    }

    /**
     * Constructs a paginated response with content and metadata.
     * 
     * @param content the list of items for the current page
     * @param page the zero-based page number
     * @param size the number of items per page
     * @param totalElements the total number of elements across all pages
     * @param totalPages the total number of pages
     */
    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}

