package com.dude.dms.ui.views

import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.ui.Const.PAGE_RULES
import com.dude.dms.ui.MainView
import com.dude.dms.ui.builder.BuilderFactory
import com.github.appreciated.card.Card
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(value = PAGE_RULES, layout = MainView::class)
@PageTitle("Rules")
class RulesView(
        private val builderFactory: BuilderFactory,
        private val plainTextRuleService: PlainTextRuleService,
        private val regexRuleService: RegexRuleService,
        private val mailFilterService: MailFilterService
) : FormLayout() {

    init {
        element.style["padding"] = "10px"
        fillContent()
    }

    private fun fillContent() {
        removeAll()
        addPlaintext()
        addRegex()
        addMailFilter()
    }

    private fun addPlaintext() {
        val create = Button("Create", VaadinIcon.PLUS.create()) { builderFactory.rules().plainCreateDialog { fillContent() }.build().open() }.apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
        val verticalLayout = VerticalLayout(create).apply { setSizeFull() }
        plainTextRuleService.findAll().map { builderFactory.rules().plainTextCard(it).build() }.forEach { verticalLayout.add(it) }
        val details = Details("Text Matchers", verticalLayout).apply {
            isOpened = true
            element.style["padding"] = "5px"
            element.style["width"] = "100%"
        }
        val card = Card(details).apply {
            setSizeFull()
            element.style["height"] = "100%"
        }
        add(card)
    }

    private fun addRegex() {
        val create = Button("Create", VaadinIcon.PLUS.create()) { builderFactory.rules().regexCreateDialog { fillContent() }.build().open() }.apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
        val verticalLayout = VerticalLayout(create).apply { setSizeFull() }
        regexRuleService.findAll().map { builderFactory.rules().regexCard(it, { fillContent() }) { fillContent() }.build() }.forEach { verticalLayout.add(it) }
        val details = Details("Regex Matchers", verticalLayout).apply {
            isOpened = true
            element.style["padding"] = "5px"
            element.style["width"] = "100%"
        }
        val card = Card(details).apply {
            setSizeFull()
            element.style["height"] = "100%"
        }
        add(card)
    }

    private fun addMailFilter() {
        val create = Button("Create", VaadinIcon.PLUS.create()) { builderFactory.rules().mailCreateDialog { fillContent() }.build().open() }.apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
        val verticalLayout = VerticalLayout(create).apply { setSizeFull() }
        mailFilterService.findAll().map { builderFactory.rules().mailCard(it, { fillContent() }) { fillContent() }.build() }.forEach { verticalLayout.add(it) }
        val details = Details("Mail Filters", verticalLayout).apply {
            isOpened = true
            element.style["padding"] = "5px"
            element.style["width"] = "100%"
        }
        val card = Card(details).apply {
            setSizeFull()
            element.style["height"] = "100%"
        }
        add(card)
    }
}