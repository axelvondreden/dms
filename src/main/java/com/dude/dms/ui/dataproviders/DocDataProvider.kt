package com.dude.dms.ui.dataproviders

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.backend.service.DocService
import com.dude.dms.ui.dataproviders.DocDataProvider.Filter
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.spring.annotation.SpringComponent
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.io.Serializable

@SpringComponent
class DocDataProvider(private val docService: DocService) : GridViewDataProvider<Doc, Filter>() {

    data class Filter(
            var tag: Tag? = null,
            var mail: Mail? = null
    ) : Serializable

    init {
        setSortOrders(Sort.Direction.DESC, "id")
    }

    override fun fetchFromBackEnd(query: Query<Doc, Filter>, pageable: Pageable) = when {
        query.filter.isPresent -> {
            val page = docService.findByFilter(query.filter.get(), pageable)
            pageObserver?.accept(page)
            page
        }
        else -> docService.findAll(pageable)
    }

    override fun sizeInBackEnd(query: Query<Doc, Filter>) = when {
        query.filter.isPresent -> docService.countByFilter(query.filter.get())
        else -> docService.count()
    }.toInt()
}