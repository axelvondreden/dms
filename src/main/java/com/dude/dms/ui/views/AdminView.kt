package com.dude.dms.ui.views

import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.extensions.round
import com.dude.dms.ui.Const
import com.github.appreciated.apexcharts.ApexChartsBuilder
import com.github.appreciated.apexcharts.config.builder.ChartBuilder
import com.github.appreciated.apexcharts.config.builder.LegendBuilder
import com.github.appreciated.apexcharts.config.builder.PlotOptionsBuilder
import com.github.appreciated.apexcharts.config.chart.Type
import com.github.appreciated.apexcharts.config.legend.Position
import com.github.appreciated.apexcharts.config.plotoptions.builder.BarBuilder
import com.github.appreciated.apexcharts.helper.Series
import com.github.appreciated.card.Card
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.springframework.stereotype.Service
import java.io.File
import java.math.BigInteger
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


@Route(value = Const.PAGE_ADMIN, layout = MainView::class)
@PageTitle("Administration")
class AdminView(private val docService: DocService, private val tagService: TagService, private val dbService: DbService) : VerticalLayout() {

    private val options = Options.get()

    init {
        createTagSection()
        createStorageSection()
    }

    private fun createStorageSection() {
        val pdfSize = File(options.doc.savePath, "pdf").listFiles()?.map { it.length() }?.sum()?.toDouble()?.div(1024.0 * 1024.0)?.round(2) ?: 0.0
        val imgSize = File(options.doc.savePath, "img").listFiles()?.map { it.length() }?.sum()?.toDouble()?.div(1024.0 * 1024.0)?.round(2) ?: 0.0

        val pieChart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.pie).build())
                .withLabels("PDFs: $pdfSize MB", "${t("images")}: $imgSize MB")
                .withLegend(LegendBuilder.get().withPosition(Position.right).build())
                .withSeries(pdfSize, imgSize)
                .build()

        val data = listOf("DOC", "TAG", "ATTRIBUTE_VALUE", "LOG_ENTRY", "WORD").map { it to (dbService.getTableSize(it) / (1024.0 * 1024.0)).round(2) }.toMap()
        val barChart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.bar).build())
                .withPlotOptions(PlotOptionsBuilder.get().withBar(BarBuilder.get().build()).build())
                .withLabels(*data.map { "${it.key}: ${it.value} MB" }.toTypedArray())
                .withSeries(Series(*data.values.toTypedArray()))
                .build()

        add(createSection(t("storage"), pieChart, barChart))
    }

    private fun createTagSection() {
        val data = tagService.findAll().map { it.name to docService.countByTag(it).toDouble() }.toMap()
        val barChart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.bar).build())
                .withPlotOptions(PlotOptionsBuilder.get().withBar(BarBuilder.get().build()).build())
                .withLabels(*data.keys.toTypedArray())
                .withSeries(Series(*data.values.toTypedArray()))
                .build()

        add(createSection(t("tags"), barChart))
    }

    private fun createSection(title: String, vararg components: Component): Card {
        val details = Details(title, FormLayout(*components)).apply {
            isOpened = true
            element.style.set("padding", "5px")["width"] = "100%"
        }
        return Card(details).apply { setWidthFull() }
    }

    @Service
    class DbService {
        @PersistenceContext
        private lateinit var entityManager: EntityManager

        fun getTableSize(table: String) = (entityManager.createNativeQuery("CALL DISK_SPACE_USED('$table')").singleResult as BigInteger).toInt()
    }
}