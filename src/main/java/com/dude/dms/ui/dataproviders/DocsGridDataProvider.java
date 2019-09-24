package com.dude.dms.ui.dataproviders;

import com.dude.dms.backend.data.entity.Doc;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.ui.dataproviders.DocsGridDataProvider.DocFilter;
import com.dude.dms.ui.utils.Const;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.QuerySortOrderBuilder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A pageable order data provider.
 */
@SpringComponent
@UIScope
public class DocsGridDataProvider extends FilterablePageableDataProvider<Doc, DocFilter> {

    public static class DocFilter implements Serializable {
        private final String filter;
        private final boolean showPrevious;

        public String getFilter() {
            return filter;
        }

        public boolean isShowPrevious() {
            return showPrevious;
        }

        public DocFilter(String filter, boolean showPrevious) {
            this.filter = filter;
            this.showPrevious = showPrevious;
        }

        public static DocFilter getEmptyFilter() {
            return new DocFilter("", false);
        }
    }

    private final DocService docService;
    private List<QuerySortOrder> defaultSortOrders;
    private Consumer<Page<Doc>> pageObserver;

    @Autowired
    public DocsGridDataProvider(DocService docService) {
        this.docService = docService;
        setSortOrders(Const.DEFAULT_SORT_DIRECTION, Const.DOC_SORT_FIELDS);
    }

    private void setSortOrders(Direction direction, String[] properties) {
        QuerySortOrderBuilder builder = new QuerySortOrderBuilder();
        for (String property : properties) {
            if (direction.isAscending()) {
                builder.thenAsc(property);
            } else {
                builder.thenDesc(property);
            }
        }
        defaultSortOrders = builder.build();
    }

    @Override
    protected Page<Doc> fetchFromBackEnd(Query<Doc, DocFilter> query, Pageable pageable) {
        DocFilter filter = query.getFilter().orElse(DocFilter.getEmptyFilter());
        Page<Doc> page = docService.findAnyMatchingAfterUploadDate(Optional.ofNullable(filter.getFilter()), getFilterDate(filter.isShowPrevious()), pageable);
        if (pageObserver != null) {
            pageObserver.accept(page);
        }
        return page;
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return defaultSortOrders;
    }

    @Override
    protected int sizeInBackEnd(Query<Doc, DocFilter> query) {
        DocFilter filter = query.getFilter().orElse(DocFilter.getEmptyFilter());
        return (int) docService.countAnyMatchingAfterUploadDate(Optional.ofNullable(filter.getFilter()), getFilterDate(filter.isShowPrevious()));
    }

    private static Optional<LocalDate> getFilterDate(boolean showPrevious) {
        return showPrevious ? Optional.empty() : Optional.of(LocalDate.now().minusDays(1));
    }

    public void setPageObserver(Consumer<Page<Doc>> pageObserver) {
        this.pageObserver = pageObserver;
    }

    @Override
    public Object getId(Doc item) {
        return item.getId();
    }
}
