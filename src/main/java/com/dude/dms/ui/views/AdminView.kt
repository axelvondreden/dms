package com.dude.dms.ui.views

import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.github.appreciated.apexcharts.ApexChartsBuilder
import com.github.appreciated.apexcharts.config.builder.ChartBuilder
import com.github.appreciated.apexcharts.config.builder.LegendBuilder
import com.github.appreciated.apexcharts.config.builder.ResponsiveBuilder
import com.github.appreciated.apexcharts.config.chart.Type
import com.github.appreciated.apexcharts.config.legend.Position
import com.github.appreciated.apexcharts.config.responsive.builder.OptionsBuilder
import com.github.appreciated.card.Card
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import java.io.File
import kotlin.math.round


@Route(value = Const.PAGE_ADMIN, layout = MainView::class)
@PageTitle("Administration")
class AdminView : VerticalLayout() {

    private val options = Options.get()

    init {
        createStorageSection()
    }

    private fun createStorageSection() {
        val pdfSize = File(options.doc.savePath, "pdf").listFiles()?.map { it.length() }?.sum()?.toDouble()?.div(1024.0 * 1024.0)?.round(2) ?: 0.0
        val imgSize = File(options.doc.savePath, "img").listFiles()?.map { it.length() }?.sum()?.toDouble()?.div(1024.0 * 1024.0)?.round(2) ?: 0.0

        val pieChart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.pie).build())
                .withLabels("PDFs: $pdfSize MB", "${t("images")}: $imgSize MB")
                .withLegend(LegendBuilder.get()
                        .withPosition(Position.right)
                        .build())
                .withSeries(pdfSize, imgSize)
                .withResponsive(ResponsiveBuilder.get()
                        .withOptions(OptionsBuilder.get()
                                .withLegend(LegendBuilder.get()
                                        .withPosition(Position.bottom)
                                        .build())
                                .build())
                        .build())
                .build()
        add(pieChart).apply {
            width = "50%"
        }

        add(createSection(t("storage"), pieChart))
    }

    private fun createSection(title: String, vararg components: Component): Card {
        val details = Details(title, FormLayout(*components)).apply {
            isOpened = true
            element.style.set("padding", "5px")["width"] = "100%"
        }
        return Card(details).apply { setWidthFull() }
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }
}