package proyecto_crud.controller;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaginationHelperTest {

    @Test
    void parseSizeUsesDefaultFiveWhenMissingOrInvalid() {
        assertThat(PaginationHelper.parseSize(null)).isEqualTo(5);
        assertThat(PaginationHelper.parseSize("")).isEqualTo(5);
        assertThat(PaginationHelper.parseSize("abc")).isEqualTo(5);
    }

    @Test
    void parseSizeClampsBetweenOneAndHundred() {
        assertThat(PaginationHelper.parseSize("0")).isEqualTo(1);
        assertThat(PaginationHelper.parseSize("-15")).isEqualTo(1);
        assertThat(PaginationHelper.parseSize("1")).isEqualTo(1);
        assertThat(PaginationHelper.parseSize("25")).isEqualTo(25);
        assertThat(PaginationHelper.parseSize("100")).isEqualTo(100);
        assertThat(PaginationHelper.parseSize("101")).isEqualTo(100);
        assertThat(PaginationHelper.parseSize("999")).isEqualTo(100);
    }

    @Test
    void parsePageDefaultsToOneAndRejectsNonPositive() {
        assertThat(PaginationHelper.parsePage(null)).isEqualTo(1);
        assertThat(PaginationHelper.parsePage("  ")).isEqualTo(1);
        assertThat(PaginationHelper.parsePage("xyz")).isEqualTo(1);
        assertThat(PaginationHelper.parsePage("0")).isEqualTo(1);
        assertThat(PaginationHelper.parsePage("-4")).isEqualTo(1);
        assertThat(PaginationHelper.parsePage("7")).isEqualTo(7);
    }

    @Test
    void buildEllipsisPagesReturnsEmptyWhenZeroPages() {
        assertThat(PaginationHelper.buildEllipsisPages(1, 0)).isEmpty();
    }

    @Test
    void buildEllipsisPagesShowsAllPagesWhenFiveOrFewer() {
        List<PageItem> items = PaginationHelper.buildEllipsisPages(3, 5);
        assertThat(items).containsExactly(
                PageItem.page(1, false),
                PageItem.page(2, false),
                PageItem.page(3, true),
                PageItem.page(4, false),
                PageItem.page(5, false)
        );
    }

    @Test
    void buildEllipsisPagesShowsStartWindowWithMaxFiveVisiblePages() {
        List<PageItem> items = PaginationHelper.buildEllipsisPages(2, 10);
        assertThat(items).containsExactly(
                PageItem.page(1, false),
                PageItem.page(2, true),
                PageItem.page(3, false),
                PageItem.page(4, false),
                PageItem.ellipsisItem(),
                PageItem.page(10, false)
        );
    }

    @Test
    void buildEllipsisPagesShowsMiddleWindowWithMaxFiveVisiblePages() {
        List<PageItem> items = PaginationHelper.buildEllipsisPages(5, 10);
        assertThat(items).containsExactly(
                PageItem.page(1, false),
                PageItem.ellipsisItem(),
                PageItem.page(4, false),
                PageItem.page(5, true),
                PageItem.page(6, false),
                PageItem.ellipsisItem(),
                PageItem.page(10, false)
        );
    }

    @Test
    void buildEllipsisPagesShowsEndWindowWithMaxFiveVisiblePages() {
        List<PageItem> items = PaginationHelper.buildEllipsisPages(9, 10);
        assertThat(items).containsExactly(
                PageItem.page(1, false),
                PageItem.ellipsisItem(),
                PageItem.page(7, false),
                PageItem.page(8, false),
                PageItem.page(9, true),
                PageItem.page(10, false)
        );
    }

    @Test
    void buildEllipsisPagesNeverExceedsFiveNumericPages() {
        for (int total = 1; total <= 20; total++) {
            for (int current = 1; current <= total; current++) {
                List<PageItem> items = PaginationHelper.buildEllipsisPages(current, total);
                List<PageItem> numeric = items.stream().filter(i -> !i.ellipsis()).toList();
                assertThat(numeric.size()).isLessThanOrEqualTo(5);
                assertThat(numeric.stream().filter(PageItem::active).count()).isEqualTo(1);
            }
        }
    }
}
