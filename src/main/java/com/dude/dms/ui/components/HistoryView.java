package com.dude.dms.ui.components;

import com.dude.dms.backend.data.entity.DataEntity;
import com.dude.dms.backend.data.entity.Historical;
import com.dude.dms.backend.data.entity.History;
import com.dude.dms.backend.service.HistoryCrudService;
import com.github.appreciated.Swiper;
import com.github.appreciated.SwiperConfigBuilder;
import com.github.appreciated.config.Direction;
import com.github.appreciated.config.builder.MousewheelControlBuilder;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class HistoryView<T extends DataEntity & Historical<U>, U extends History> extends VerticalLayout {

    private final HistoryCrudService<T, U> service;

    private T entity;

    public HistoryView(HistoryCrudService<T, U> service) {
        this.service = service;
        setSizeFull();
        setPadding(true);
    }

    public void load(T entity) {
        clear();

        this.entity = entity;
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
        if (entity != null) {
            load(entity);
        }
    }

    public void clear() {
        entity = null;
        removeAll();
    }
}
