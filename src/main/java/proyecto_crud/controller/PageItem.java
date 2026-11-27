package proyecto_crud.controller;

public record PageItem(Integer pageNumber, boolean ellipsis, boolean active) {

    public static PageItem page(int pageNumber, boolean active) {
        return new PageItem(pageNumber, false, active);
    }

    public static PageItem ellipsisItem() {
        return new PageItem(null, true, false);
    }
}
