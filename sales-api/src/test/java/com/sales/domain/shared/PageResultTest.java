package com.sales.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PageResult Tests")
class PageResultTest {

    @Test
    @DisplayName("Should create page result with all properties")
    void shouldCreatePageResultWithAllProperties() {
        List<String> content = Arrays.asList("Item1", "Item2", "Item3");
        long totalElements = 10L;
        int page = 0;
        int size = 3;

        PageResult<String> pageResult = new PageResult<>(content, totalElements, page, size);

        assertThat(pageResult.getContent()).isEqualTo(content);
        assertThat(pageResult.getTotalElements()).isEqualTo(totalElements);
        assertThat(pageResult.getPage()).isEqualTo(page);
        assertThat(pageResult.getSize()).isEqualTo(size);
    }

    @Test
    @DisplayName("Should calculate total pages correctly")
    void shouldCalculateTotalPagesCorrectly() {
        List<String> content = Arrays.asList("Item1", "Item2", "Item3");
        PageResult<String> pageResult = new PageResult<>(content, 10L, 0, 3);

        assertThat(pageResult.getTotalPages()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should calculate total pages with exact division")
    void shouldCalculateTotalPagesWithExactDivision() {
        List<String> content = Arrays.asList("Item1", "Item2", "Item3");
        PageResult<String> pageResult = new PageResult<>(content, 9L, 0, 3);

        assertThat(pageResult.getTotalPages()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should return 1 page when total elements less than size")
    void shouldReturnOnePageWhenTotalElementsLessThanSize() {
        List<String> content = Arrays.asList("Item1", "Item2");
        PageResult<String> pageResult = new PageResult<>(content, 2L, 0, 10);

        assertThat(pageResult.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return 0 pages when no elements")
    void shouldReturnZeroPagesWhenNoElements() {
        List<String> content = Collections.emptyList();
        PageResult<String> pageResult = new PageResult<>(content, 0L, 0, 10);

        assertThat(pageResult.getTotalPages()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle single element")
    void shouldHandleSingleElement() {
        List<String> content = Collections.singletonList("Item1");
        PageResult<String> pageResult = new PageResult<>(content, 1L, 0, 10);

        assertThat(pageResult.getTotalPages()).isEqualTo(1);
        assertThat(pageResult.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should handle large numbers correctly")
    void shouldHandleLargeNumbersCorrectly() {
        List<String> content = Arrays.asList("Item1", "Item2");
        PageResult<String> pageResult = new PageResult<>(content, 1000L, 0, 50);

        assertThat(pageResult.getTotalPages()).isEqualTo(20);
    }

    @Test
    @DisplayName("Should calculate total pages with remainder")
    void shouldCalculateTotalPagesWithRemainder() {
        List<String> content = Arrays.asList("Item1", "Item2", "Item3", "Item4", "Item5");
        PageResult<String> pageResult = new PageResult<>(content, 11L, 0, 5);

        assertThat(pageResult.getTotalPages()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should work with different page numbers")
    void shouldWorkWithDifferentPageNumbers() {
        List<String> content = Arrays.asList("Item1", "Item2");
        PageResult<String> pageResult1 = new PageResult<>(content, 10L, 0, 5);
        PageResult<String> pageResult2 = new PageResult<>(content, 10L, 1, 5);

        assertThat(pageResult1.getTotalPages()).isEqualTo(2);
        assertThat(pageResult2.getTotalPages()).isEqualTo(2);
        assertThat(pageResult1.getPage()).isEqualTo(0);
        assertThat(pageResult2.getPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle empty content with total elements")
    void shouldHandleEmptyContentWithTotalElements() {
        List<String> content = Collections.emptyList();
        PageResult<String> pageResult = new PageResult<>(content, 10L, 2, 5);

        assertThat(pageResult.getContent()).isEmpty();
        assertThat(pageResult.getTotalElements()).isEqualTo(10L);
        assertThat(pageResult.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should work with custom objects")
    void shouldWorkWithCustomObjects() {
        List<TestObject> content = Arrays.asList(
                new TestObject("Test1"),
                new TestObject("Test2")
        );
        PageResult<TestObject> pageResult = new PageResult<>(content, 20L, 0, 10);

        assertThat(pageResult.getContent()).hasSize(2);
        assertThat(pageResult.getContent().get(0).name).isEqualTo("Test1");
        assertThat(pageResult.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should calculate ceil correctly for fractional pages")
    void shouldCalculateCeilCorrectlyForFractionalPages() {
        // 7 elements / 3 size = 2.33... should be 3 pages
        PageResult<String> pageResult = new PageResult<>(Arrays.asList("1", "2", "3"), 7L, 0, 3);

        assertThat(pageResult.getTotalPages()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should handle size of 1")
    void shouldHandleSizeOfOne() {
        PageResult<String> pageResult = new PageResult<>(Collections.singletonList("Item1"), 5L, 0, 1);

        assertThat(pageResult.getTotalPages()).isEqualTo(5);
    }

    // Helper class for testing with custom objects
    private static class TestObject {
        private final String name;

        public TestObject(String name) {
            this.name = name;
        }
    }
}
