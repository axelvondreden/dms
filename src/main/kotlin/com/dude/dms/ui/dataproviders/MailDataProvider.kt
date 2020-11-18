package com.dude.dms.ui.dataproviders

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.backend.service.MailService
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.spring.annotation.SpringComponent
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.io.Serializable

@SpringComponent
class MailDataProvider(private val mailService: MailService) : GridViewDataProvider<Mail, MailDataProvider.Filter>() {

    data class Filter(
            var tag: Tag? = null,
            var doc: Doc? = null
    ) : Serializable

    init {
        setSortOrders(Sort.Direction.DESC, "received")
    }

    override fun fetchFromBackEnd(query: Query<Mail, Filter>, pageable: Pageable) = when {
        query.filter.isPresent -> {
            val page = mailService.findByFilter(query.filter.get(), pageable)
            pageObserver?.accept(page)
            page
        }
        else -> mailService.findAll(pageable)
    }

    override fun sizeInBackEnd(query: Query<Mail, Filter>) = when {
        query.filter.isPresent -> mailService.countByFilter(query.filter.get())
        else -> mailService.count()
    }.toInt()
}