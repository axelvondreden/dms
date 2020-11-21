package com.dude.dms.ui.views

import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.PlainTextRuleService
import com.dude.dms.backend.service.RegexRuleService
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.events.EventType
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
        private val plainTextRuleService: PlainTextRuleService,
        private val regexRuleService: RegexRuleService,
        eventManager: EventManager
) : VerticalLayout() {

    init {
        element.style["padding"] = "10px"
        fillContent()

        eventManager.register(this, PlainTextRule::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { fillContent() }
        eventManager.register(this, RegexRule::class, EventType.CREATE, EventType.UPDATE, EventType.DELETE) { fillContent() }
    }

    private fun fillContent() {
        removeAll()
        addPlaintext()
        addRegex()
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
                            onLeftClick { this@verticalLayout.plaintextRuleCreateDialog().open() }
                            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                        }
                        plainTextRuleService.findAll().forEach {
                            plaintextRuleCard(it)
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
                            onLeftClick { this@verticalLayout.regexRuleCreateDialog().open() }
                            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                        }
                        regexRuleService.findAll().forEach {
                            regexRuleCard(it)
                        }
                    }
                }
            }
        }
    }
}