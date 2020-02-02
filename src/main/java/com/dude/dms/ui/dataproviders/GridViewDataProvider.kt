package com.dude.dms.ui.dataproviders

import com.dude.dms.backend.data.DataEntity
import com.vaadin.flow.data.provider.QuerySortOrder
import com.vaadin.flow.data.provider.QuerySortOrderBuilder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider
import java.io.Serializable
import java.util.function.Consumer

abstract class GridViewDataProvider<T : DataEntity, U : Serializable> : FilterablePageableDataProvider<T, U>() {

    private var defaultSortOrders: List<QuerySortOrder>? = null
    protected var pageObserver: Consumer<in Page<T>>? = null

    protected fun setSortOrders(direction: Sort.Direction, vararg properties: String) {
        val builder = QuerySortOrderBuilder()
        for (property in properties) {
            if (direction.isAscending) {
                builder.thenAsc(property)
            } else {
                builder.thenDesc(property)
            }
        }
        defaultSortOrders = builder.build()
    }

    override fun getDefaultSortOrders() = defaultSortOrders!!

    override fun getId(item: T) = item.hashCode()
}