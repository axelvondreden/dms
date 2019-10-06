package com.dude.dms.ui.components.history;

import com.dude.dms.backend.data.DataEntity;
import com.dude.dms.backend.data.Historical;
import com.dude.dms.backend.data.history.History;
import com.dude.dms.backend.service.HistoryCrudService;
import com.github.appreciated.Swiper;
import com.github.appreciated.SwiperConfigBuilder;
import com.github.appreciated.config.Direction;
import com.github.appreciated.config.builder.MousewheelControlBuilder;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class HistoryView<T extends DataEntity & Historical<U>, U extends History> extends VerticalLayout {

    private final HistoryCrudService<T, U> service;

    private T loadedEntity;

    public HistoryView(HistoryCrudService<T, U> service) {
        this.service = service;
        setSizeFull();
        setPadding(true);
    }

    public void load(T entity) {
        clear();

        loadedEntity = entity;
        Swiper swiper = new Swiper(SwiperConfigBuilder.get()
                .withDirection(Direction.VERTICAL)
                .withAlignment(FlexComponent.Alignment.CENTER)
                .withJustifyContentMode(FlexComponent.JustifyContentMode.CENTER)
                .withSlidesPerView("auto")
                .withMousewheelControl(MousewheelControlBuilder.get().build())
                .withSpaceBetween(15.0)
                .build()
        );

        for (U history : service.getHistory(entity)) {
            swiper.add(new HistoryCard<>(history));
        }
        add(swiper);
    }

    public void reload() {
        if (loadedEntity != null) {
            load(loadedEntity);
        }
    }

    public void clear() {
        loadedEntity = null;
        removeAll();
    }
}