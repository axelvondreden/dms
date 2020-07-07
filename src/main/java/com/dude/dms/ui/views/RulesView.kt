package com.dude.dms.ui.views

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.*
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.brain.parsing.PlainTextRuleValidator
import com.dude.dms.brain.parsing.RegexRuleValidator
import com.dude.dms.brain.polling.MailPollingService
import com.dude.dms.brain.t
import com.dude.dms.extensions.*
import com.dude.dms.ui.Const.PAGE_RULES
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(value = PAGE_RULES, layout = MainView::class)
@PageTitle("Rules")
class RulesView(
        private val docService: DocService,
        private val plainTextRuleService: PlainTextRuleService,
        private val regexRuleService: RegexRuleService,
        private val mailFilterService: MailFilterService,
        private val mailPollingService: MailPollingService,
        private val tagService: TagService,
        private val plainTextRuleValidator: PlainTextRuleValidator,
        private val regexRuleValidator: RegexRuleValidator,
        private val mailManager: MailManager,
        private val eventManager: EventManager
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
        card {
            setSizeFull()
            element.style["height"] = "100%"

            details(t("rules.plain")) {
                isOpened = true
                element.style["padding"] = "5px"
                element.style["width"] = "100%"

                content {
                    verticalLayout {
                        setSizeFull()

                        button(t("create"), VaadinIcon.PLUS.create()) {
                            onLeftClick { this@verticalLayout.plaintextRuleCreateDialog(plainTextRuleService, tagService).open() }
                            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                        }
                        plainTextRuleService.findAll().forEach {
                            plaintextRuleCard(docService, tagService, plainTextRuleService, it, plainTextRuleValidator, eventManager)
                        }
                    }
                }
            }
        }
    }

    private fun addRegex() {
        card {
            setSizeFull()
            element.style["height"] = "100%"

            details(t("rules.regex")) {
                isOpened = true
                element.style["padding"] = "5px"
                element.style["width"] = "100%"

                content {
                    verticalLayout {
                        setSizeFull()

                        button(t("create"), VaadinIcon.PLUS.create()) {
                            onLeftClick { this@verticalLayout.regexRuleCreateDialog(regexRuleService, tagService).open() }
                            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                        }
                        regexRuleService.findAll().forEach {
                            regexRuleCard(docService, tagService, regexRuleService, it, regexRuleValidator, eventManager)
                        }
                    }
                }
            }
        }
    }

    private fun addMailFilter() {
        card {
            setSizeFull()
            element.style["height"] = "100%"

            details("Mail Filter") {
                isOpened = true
                element.style["padding"] = "5px"
                element.style["width"] = "100%"

                content {
                    verticalLayout {
                        setSizeFull()

                        horizontalLayout {
                            setSizeFull()

                            button(t("create"), VaadinIcon.PLUS.create()) {
                                onLeftClick { this@verticalLayout.mailFilterCreateDialog(mailFilterService, mailManager).open() }
                                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                            }
                            button(t("check"), VaadinIcon.CLOUD_DOWNLOAD.create()) {
                                onLeftClick { mailPollingService.poll() }
                                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                            }
                        }
                        mailFilterService.findAll().forEach {
                            mailFilterCard(mailFilterService, it, mailManager)
                        }
                    }
                }
            }
        }
    }
}