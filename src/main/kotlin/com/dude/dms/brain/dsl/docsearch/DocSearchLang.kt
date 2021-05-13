package com.dude.dms.brain.dsl.docsearch

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.brain.dsl.Translatable
import com.dude.dms.brain.dsl.hint.Hint
import com.dude.dms.brain.dsl.hint.Hints
import com.dude.dms.brain.t
import com.dude.dms.utils.attributeService
import com.dude.dms.utils.convert
import com.dude.dms.utils.tagService
import com.vaadin.flow.component.icon.VaadinIcon
import parser4k.*
import parser4k.commonparsers.Tokens
import parser4k.commonparsers.joinedWith
import parser4k.commonparsers.token
import java.time.LocalDate

object DocSearchLang {

    private val tagList = tagService.findAll()
    private val attributeList = attributeService.findAll()

    private val boolLiteral = oneOf(str("true"), str("false")).map { if (it == "true") Value.True else Value.False }
    private val intLiteral = Tokens.integer.map { Value.Int(it.toInt()) }
    private val floatLiteral = Tokens.number.map { Value.Float(it.toDouble()) }
    private val stringLiteral = Tokens.string.map { Value.String(it) }
    private val stringLikeLiteral = Tokens.string.map { Value.StringLike(it) }
    private val dateLiteral = inOrder(intLiteral, token("."), intLiteral, token("."), intLiteral).map { Value.Date(it.val1.value, it.val3.value, it.val5.value) }
    private val tagLiteral = oneOf(tagList.map { token(it.name) }).map { Value.Tag(it) }
    private val stringArrayLiteral = inOrder(token("["), stringLiteral.joinedWith(token(",")), token("]")).skipWrapper().map { Value.StringArray(it) }
    private val tagArrayLiteral = inOrder(token("["), tagLiteral.joinedWith(token(",")), token("]")).skipWrapper().map { Value.TagArray(it) }
    private val unaryMinus = inOrder(token("-"), intLiteral).map { (_, it) -> Value.Int(-it.value) }

    private val textKey = token(t("text")).map { Key.Text }
    private val dateKey = token(t("date")).map { Key.Order.Date }
    private val createdKey = token(t("created")).map { Key.Order.Created }
    private val tagKey = token(t("tag")).map { Key.Tag }
    private val stringAttributeKey = oneOf(attributeList.filter { it.type == Attribute.Type.STRING }
            .map { token(it.name) }).map { Key.Attribute.String(it) }
    private val intAttributeKey = oneOf(attributeList.filter { it.type == Attribute.Type.INT }
            .map { token(it.name) }).map { Key.Attribute.Int(it) }
    private val floatAttributeKey = oneOf(attributeList.filter { it.type == Attribute.Type.FLOAT }
            .map { token(it.name) }).map { Key.Attribute.Float(it) }
    private val dateAttributeKey = oneOf(attributeList.filter { it.type == Attribute.Type.DATE }
            .map { token(it.name) }).map { Key.Attribute.Date(it) }
    private val attributeKey = oneOf(stringAttributeKey, intAttributeKey, floatAttributeKey, dateAttributeKey)

    private val orderAsc = token(t("search.order.asc")).map { OrderDirection.Asc }
    private val orderDesc = token(t("search.order.desc")).map { OrderDirection.Desc }
    private val orderBy = inOrder(
            token(t("search.order")),
            token(t("search.order.by")),
            oneOf(dateKey, createdKey),
            oneOf(orderAsc, orderDesc)
    ).map { OrderBy(it.val3, it.val4) }

    private val equal = token("=").map { Operator.Equal }
    private val notEqual = token("!=").map { Operator.NotEqual }
    private val less = token("<").map { Operator.Less }
    private val greater = token(">").map { Operator.Greater }
    private val inArray = token("in").map { Operator.InArray }
    private val notInArray = token("!in").map { Operator.NotInArray }
    private val like = token("~=").map { Operator.Like }
    private val notLike = token("!~=").map { Operator.NotLike }

    private val textFilter = inOrder(textKey, oneOf(like, notLike), stringLikeLiteral).map { Filter.Text(it.second, it.third) }
    private val dateFilter = inOrder(dateKey, oneOf(equal, notEqual, less, greater), dateLiteral).map { Filter.Date(it.second, it.third) }
    private val createdFilter = inOrder(createdKey, oneOf(equal, notEqual, less, greater), dateLiteral).map { Filter.Created(it.second, it.third) }
    private val tagFilter = inOrder(
            tagKey,
            oneOf(
                    inOrder(oneOf(equal, notEqual), tagLiteral),
                    inOrder(oneOf(inArray, notInArray), tagArrayLiteral),
            )
    ).map { Filter.Tag(it.second.first, it.second.second) }

    private val stringAttributeFilter: Parser<Filter.StringAttribute> = inOrder(
        stringAttributeKey, oneOf(
            inOrder(oneOf(equal, notEqual), stringLiteral),
            inOrder(oneOf(like, notLike), stringLikeLiteral),
            inOrder(oneOf(inArray, notInArray), stringArrayLiteral)
    )).map { Filter.StringAttribute(it.first.name, it.second.first, it.second.second) }

    private val intAttributeFilter: Parser<Filter.IntAttribute> = inOrder(
            intAttributeKey, inOrder(oneOf(equal, notEqual, less, greater), intLiteral)
    ).map { Filter.IntAttribute(it.first.name, it.second.first, it.second.second) }

    private val floatAttributeFilter: Parser<Filter.FloatAttribute> = inOrder(
            floatAttributeKey, inOrder(oneOf(equal, notEqual, less, greater), floatLiteral)
    ).map { Filter.FloatAttribute(it.first.name, it.second.first, it.second.second) }

    private val dateAttributeFilter: Parser<Filter.DateAttribute> = inOrder(
            dateAttributeKey, inOrder(oneOf(equal, notEqual, less, greater), dateLiteral)
    ).map { Filter.DateAttribute(it.first.name, it.second.first, it.second.second) }

    private val attributeFilter = oneOf(stringAttributeFilter, intAttributeFilter, floatAttributeFilter, dateAttributeFilter)

    private val filter = oneOf(textFilter, tagFilter, dateFilter, createdFilter, attributeFilter)

    private val and = inOrder(ref { query }, token(t("and")), ref { query }).map { Query.And(it.first, it.third) }
    private val or = inOrder(ref { query }, token(t("or")), ref { query }).map { Query.Or(it.first, it.third) }

    private val paren = inOrder(token("("), ref { query }, token(")")).skipWrapper()

    private val query: Parser<Query> = oneOfWithPrecedence(or, and, paren.nestedPrecedence(), filter)

    private val search: Parser<Search> = inOrder(optional(query), optional(orderBy)).map { Search(it.first, it.second) }

    private val key: Parser<Key> = oneOf(textKey, tagKey, dateKey, createdKey, attributeKey)
    private val orderKey: Parser<Key.Order> = oneOf(dateKey, createdKey)
    private val OP: Parser<Operator> = oneOf(equal, notEqual, less, greater, inArray, notInArray, like, notLike)

    fun parse(s: String): Search = s.parseWith(search)

    /**
     * Tries to parse a valid query while dropping the last character continuously
     * @return [Pair] query and number of dropped characters until a query could be parsed
     */
    fun testForQueryDroppingLast(s: String): Pair<Query?, Int> {
        var count = 0
        var copy = s
        var testQuery: Query? = null
        while (testQuery == null && copy.isNotEmpty() && copy.last() != ' ') {
            try {
                testQuery = copy.parseWith(query)
            } catch (e: NoMatchingParsers) {
                count++
                copy = copy.dropLast(1)
            } catch (e: InputIsNotConsumed) {
                count++
                copy = copy.dropLast(1)
            }
        }
        return testQuery to count
    }

    /**
     * Tries to parse a valid key while increasing the amount if searched characters from the end of the string
     * @return [Pair] key and number of characters until a key could be parsed
     */
    fun testForKeyFromEnd(s: String): Pair<Key?, Int> {
        var count = 1
        var testKey: Key? = null
        while (count < 10 && testKey == null) {
            try {
                testKey = s.takeLast(count).parseWith(key)
            } catch (e: NoMatchingParsers) {
                count++
            } catch (e: InputIsNotConsumed) {
                count++
            }
        }
        return testKey to count
    }

    /**
     * Tries to parse a valid order-key while increasing the amount if searched characters from the end of the string
     * @return [Pair] order-key and number of characters until a key could be parsed
     */
    fun testForOrderKeyFromEnd(s: String): Pair<Key.Order?, Int> {
        var count = 1
        var testKey: Key.Order? = null
        while (count < 10 && testKey == null) {
            try {
                testKey = s.takeLast(count).parseWith(orderKey)
            } catch (e: NoMatchingParsers) {
                count++
            } catch (e: InputIsNotConsumed) {
                count++
            }
        }
        return testKey to count
    }

    /**
     * Tries to parse a valid operator while increasing the amount if searched characters from the end of the string
     * @return [Pair] operator and number of characters until an operator could be parsed
     */
    fun testForOpFromEnd(s: String): Pair<Operator?, Int> {
        var count = 1
        var testOp: Operator? = null
        var resultCount = 0
        while (count < 5) {
            try {
                testOp = s.takeLast(count).parseWith(OP)
                resultCount = count
            } catch (e: NoMatchingParsers) {
            } catch (e: InputIsNotConsumed) {
            }
            count++
        }
        return testOp to resultCount
    }


    data class Search(val query: Query?, val docSearchOrder: OrderBy?) : Translatable {
        override fun translate() = "WHERE doc.deleted = false${query?.let { " AND (${it.translate()})" } ?: ""} ${docSearchOrder?.translate() ?: ""}"
    }


    sealed class Filter : Query() {
        data class Text(val op: Operator, val value: Value.StringLike) : Filter() {
            override fun translate() = "doc.docText.text ${op.translate()} lower(concat('%', ${value.translate()},'%'))"
        }
        data class Date(val op: Operator, val value: Value.Date) : Filter() {
            override fun translate() = "doc.documentDate ${op.translate()} ${value.translate()}"
        }
        data class Created(val op: Operator, val value: Value.Date) : Filter() {
            override fun translate() = "cast(doc.insertTime as LocalDate) ${op.translate()} ${value.translate()}"
        }
        data class Tag(val op: Operator, val value: Value) : Filter() {
            override fun translate() = "tag.name ${op.translate()} ${value.translate()}"
        }
        data class StringAttribute(val name: String, val op: Operator, val value: Value) : Filter() {
            override fun translate() = "(av.attribute.name = '$name' and av.stringValue ${op.translate()} ${value.translate()})"
        }
        data class IntAttribute(val name: String, val op: Operator, val value: Value.Int) : Filter() {
            override fun translate() = "(av.attribute.name = '$name' and av.intValue ${op.translate()} ${value.translate()})"
        }
        data class FloatAttribute(val name: String, val op: Operator, val value: Value.Float) : Filter() {
            override fun translate() = "(av.attribute.name = '$name' and av.floatValue ${op.translate()} ${value.translate()})"
        }
        data class DateAttribute(val name: String, val op: Operator, val value: Value.Date) : Filter() {
            override fun translate() = "(av.attribute.name = '$name' and av.dateValue ${op.translate()} ${value.translate()})"
        }
    }

    sealed class Key : Hints {
        abstract fun getValueHints(op: Operator): List<Hint>

        object Text : Key() {
            override val hints get() = listOf(Hint("~=", t("search.like")), Hint("!~=", t("search.notlike")))
            override fun getValueHints(op: Operator) = when (op) {
                is Operator.Like, is Operator.NotLike -> listOf(Hint("\"\"", t("text"), 1))
                else -> emptyList()
            }
        }
        object Tag: Key() {
            override val hints get() = listOf(
                Hint("=", t("search.equal")),
                Hint("!=", t("search.notequal")),
                Hint("in", t("search.inarray")),
                Hint("!in", t("search.notinarray"))
            )
            override fun getValueHints(op: Operator) = when (op) {
                is Operator.Equal, is Operator.NotEqual -> tagService.findAll().map { Hint(it.name, t("tag"), icon = VaadinIcon.TAG) }
                is Operator.InArray, Operator.NotInArray -> listOf(Hint("[, ]", t("list"), 3, VaadinIcon.TAG))
                else -> emptyList()
            }
        }
        sealed class Order : Key(), Translatable {
            abstract val orderHints: List<Hint>
            object Date : Order() {
                override val hints get() = listOf(
                    Hint("=", t("search.equal")),
                    Hint("!=", t("search.notequal")),
                    Hint("<", t("search.less")),
                    Hint(">", t("search.greater"))
                )
                override val orderHints get() = listOf(
                    Hint(t("search.order.asc"), t("ascending")),
                    Hint(t("search.order.desc"), t("descending"))
                )
                override fun translate() = "doc.documentDate"
                override fun getValueHints(op: Operator) = when (op) {
                    is Operator.Equal, is Operator.NotEqual, is Operator.Less, is Operator.Greater -> listOf(Hint(LocalDate.now().convert(), t("date")))
                    else -> emptyList()
                }
            }
            object Created : Order() {
                override val hints get() = listOf(
                    Hint("=", t("search.equal")),
                    Hint("!=", t("search.notequal")),
                    Hint("<", t("search.less")),
                    Hint(">", t("search.greater"))
                )
                override val orderHints get() = listOf(
                    Hint(t("search.order.asc"), t("ascending")),
                    Hint(t("search.order.desc"), t("descending"))
                )
                override fun translate() = "doc.insertTime"
                override fun getValueHints(op: Operator) = when (op) {
                    is Operator.Equal, is Operator.NotEqual, is Operator.Less, is Operator.Greater -> listOf(Hint(LocalDate.now().convert(), t("date")))
                    else -> emptyList()
                }
            }
        }
        sealed class Attribute(open val name: kotlin.String) : Key() {
            override val hints get() = listOf(
                Hint("=", t("search.equal")),
                Hint("!=", t("search.notequal")),
                Hint("<", t("search.less")),
                Hint(">", t("search.greater"))
            )

            data class String(override val name: kotlin.String): Attribute(name) {
                override val hints get() = listOf(
                    Hint("=", t("search.equal")),
                    Hint("!=", t("search.notequal")),
                    Hint("~=", t("search.like")),
                    Hint("!~=", t("search.notlike")),
                    Hint("in", t("search.inarray")),
                    Hint("!in", t("search.notinarray"))
                )

                override fun getValueHints(op: Operator) = when (op) {
                    is Operator.Equal, is Operator.NotEqual, is Operator.Like, is Operator.NotLike -> listOf(Hint("\"\"", t("text"), 1))
                    is Operator.InArray, Operator.NotInArray -> listOf(Hint("[\"\", \"\"]", t("list"), 6))
                    else -> emptyList()
                }
            }

            data class Int(override val name: kotlin.String): Attribute(name) {
                override fun getValueHints(op: Operator) = listOf(Hint("0", "Integer"))
            }

            data class Float(override val name: kotlin.String): Attribute(name) {
                override fun getValueHints(op: Operator) = listOf(Hint("0.0", t("number")))
            }

            data class Date(override val name: kotlin.String): Attribute(name) {
                override fun getValueHints(op: Operator) = listOf(Hint(LocalDate.now().convert(), t("date")))
            }
        }
    }

    sealed class Operator : Translatable, Hints {
        object Equal : Operator() {
            override fun translate() = " = "
            override val hints get() = listOf(
                Hint("\"\"", t("text"), 1),
                Hint("0.0", t("number"))
            )
        }
        object NotEqual : Operator() {
            override fun translate() = " != "
            override val hints get() = listOf(
                Hint("\"\"", t("text"), 1),
                Hint("0.0", t("number"))
            )
        }
        object Less : Operator() {
            override fun translate() = " <"
            override val hints get() = listOf(Hint("0.0", t("number")))
        }
        object Greater : Operator() {
            override fun translate() = " >"
            override val hints get() = listOf(Hint("0.0", t("number")))
        }
        object InArray : Operator() {
            override fun translate() = " IN "
            override val hints get() = listOf(Hint("[\"\", \"\"]", t("list"), 6))
        }
        object NotInArray : Operator() {
            override fun translate() = " NOT IN "
            override val hints get() = listOf(Hint("[\"\", \"\"]", t("list"), 6))
        }
        object Like : Operator() {
            override fun translate() = " LIKE "
            override val hints get() = listOf(Hint("\"\"", t("text"), 1))
        }
        object NotLike : Operator() {
            override fun translate() = " NOT LIKE "
            override val hints get() = listOf(Hint("\"\"", t("text"), 1))
        }
    }


    data class OrderBy(val key: Key.Order, val direction: OrderDirection) : Translatable {
        override fun translate() = " ORDER BY ${key.translate()} ${direction.translate()}"
    }

    sealed class OrderDirection : Translatable {
        object Asc : OrderDirection() {
            override fun translate() = "ASC"
        }
        object Desc : OrderDirection() {
            override fun translate() = "DESC"
        }
    }


    sealed class Query : Translatable, Hints {
        override val hints get() = listOf(
            Hint(t("and")),
            Hint(t("or")),
            Hint("${t("search.order")} ${t("search.order.by")}")
        )

        data class And(val left: Query, val right: Query) : Query() {
            override fun translate() = "(" + left.translate() + " and " + right.translate() + ")"
        }
        data class Or(val left: Query, val right: Query) : Query() {
            override fun translate() = "(" + left.translate() + " or " + right.translate() + ")"
        }
    }


    sealed class Value: Translatable {
        object True : Value() {
            override fun translate() = "true"
        }
        object False : Value() {
            override fun translate() = "true"
        }
        data class Int(val value: kotlin.Int) : Value() {
            override fun translate() = value.toString()
        }
        data class Float(val value: Double) : Value() {
            override fun translate() = value.toString()
        }
        data class String(val value: kotlin.String) : Value() {
            override fun translate() = "'$value'"
        }
        data class StringLike(val value: kotlin.String) : Value() {
            override fun translate() = "'$value'"
        }
        data class Tag(val value: kotlin.String) : Value() {
            override fun translate() = "'${value}'"
        }
        data class Date(val day: kotlin.Int, val month: kotlin.Int, val year: kotlin.Int) : Value() {
            override fun translate() = "'$year-$month-$day'"
        }
        data class StringArray(val value: List<String>) : Value() {
            override fun translate() = "(${value.joinToString(", ") { it.translate() }})"
        }
        data class TagArray(val value: List<Tag>) : Value() {
            override fun translate() = "(${value.joinToString(", ") { it.translate() }})"
        }
    }
}
