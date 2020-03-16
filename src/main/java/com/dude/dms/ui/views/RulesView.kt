package com.dude.dms.ui.views

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.polling.MailPollingService
import com.dude.dms.brain.t
import com.dude.dms.ui.Const.PAGE_RULES
import com.dude.dms.ui.builder.BuilderFactory
import com.github.appreciated.card.Card
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(value = PAGE_RULES, layout = MainView::class)
@PageTitle("Rules")
class RulesView(
        private val builderFactory: BuilderFactory,
        private val plainTextRuleService: PlainTextRuleService,
        private val regexRuleService: RegexRuleService,
        private val mailFilterService: MailFilterService,
        private val mailPollingService: MailPollingService,
        eventManager: EventManager
) : VerticalLayout() {

    init {
        element.style["padding"] = "10px"
        fillContent()

        eventManager.register(this, PlainTextRule::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { fillContent() }
        eventManager.register(this, RegexRule::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { fillContent() }
        eventManager.register(this, MailFilter::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { fillContent() }
    }

    private fun fillContent() {
        removeAll()
        addPlaintext()
        addRegex()
        addMailFilter()
    }

    private fun addPlaintext() {
        val create = Button(t("create"), VaadinIcon.PLUS.create()) { builderFactory.rules().plainCreateDialog().build().open() }.apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
        val verticalLayout = VerticalLayout(create).apply { setSizeFull() }
        plainTextRuleService.findAll().map { builderFactory.rules().plainTextCard(it).build() }.forEach { verticalLayout.add(it) }
        val details = Details(t("rules.text"), verticalLayout).apply {
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
        val create = Button(t("create"), VaadinIcon.PLUS.create()) { builderFactory.rules().regexCreateDialog().build().open() }.apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
        val verticalLayout = VerticalLayout(create).apply { setSizeFull() }
        regexRuleService.findAll().map { builderFactory.rules().regexCard(it).build() }.forEach { verticalLayout.add(it) }
        val details = Details(t("rules.regex"), verticalLayout).apply {
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
        val header = HorizontalLayout(
                Button(t("create"), VaadinIcon.PLUS.create()) { builderFactory.rules().mailCreateDialog().build().open() }.apply {
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                },
                Button(t("check"), VaadinIcon.CLOUD_DOWNLOAD.create()) { mailPollingService.poll() }.apply {
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                }
        ).apply { setSizeFull() }
        val verticalLayout = VerticalLayout(header).apply { setSizeFull() }
        mailFilterService.findAll().map { builderFactory.rules().mailCard(it).build() }.forEach { verticalLayout.add(it) }
        val details = Details("Mail Filter", verticalLayout).apply {
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