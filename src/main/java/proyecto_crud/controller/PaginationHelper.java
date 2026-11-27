package proyecto_crud.controller;

import java.util.ArrayList;
import java.util.List;

public final class PaginationHelper {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 5;
    public static final int MIN_PAGE_SIZE = 1;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int MAX_VISIBLE_PAGES = 5;

    private PaginationHelper() {
    }

    public static int parsePage(String rawPage) {
        if (rawPage == null || rawPage.isBlank()) {
            return DEFAULT_PAGE;
        }
        try {
            int parsed = Integer.parseInt(rawPage.trim());
            return Math.max(DEFAULT_PAGE, parsed);
        } catch (NumberFormatException ex) {
            return DEFAULT_PAGE;
        }
    }

    public static int parseSize(String rawSize) {
        if (rawSize == null || rawSize.isBlank()) {
            return DEFAULT_PAGE_SIZE;
        }
        try {
            int parsed = Integer.parseInt(rawSize.trim());
            return Math.clamp(parsed, MIN_PAGE_SIZE, MAX_PAGE_SIZE);
        } catch (NumberFormatException ex) {
            return DEFAULT_PAGE_SIZE;
        }
    }

    public static List<PageItem> buildEllipsisPages(int currentPage, int totalPages) {
        if (totalPages <= 0) {
            return List.of();
        }

        int safeCurrent = Math.max(1, Math.min(currentPage, totalPages));
        List<PageItem> items = new ArrayList<>();

        if (totalPages <= MAX_VISIBLE_PAGES) {
            for (int i = 1; i <= totalPages; i++) {
                items.add(PageItem.page(i, i == safeCurrent));
            }
            return items;
        }

        if (safeCurrent <= 3) {
            for (int i = 1; i <= 4; i++) {
                items.add(PageItem.page(i, i == safeCurrent));
            }
            items.add(PageItem.ellipsisItem());
            items.add(PageItem.page(totalPages, false));
        } else if (safeCurrent >= totalPages - 2) {
            items.add(PageItem.page(1, false));
            items.add(PageItem.ellipsisItem());
            for (int i = totalPages - 3; i <= totalPages; i++) {
                items.add(PageItem.page(i, i == safeCurrent));
            }
        } else {
            items.add(PageItem.page(1, false));
            items.add(PageItem.ellipsisItem());
            for (int i = safeCurrent - 1; i <= safeCurrent + 1; i++) {
                items.add(PageItem.page(i, i == safeCurrent));
            }
            items.add(PageItem.ellipsisItem());
            items.add(PageItem.page(totalPages, false));
        }

        return items;
    }
}
