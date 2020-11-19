package com.dude.dms.brain.parsing.search

import com.dude.dms.brain.parsing.search.ExpressionLang.Expr.*
import parser4k.*
import parser4k.commonparsers.Tokens
import parser4k.commonparsers.joinedWith
import parser4k.commonparsers.token

object ExpressionLang {
    private val cache = OutputCache<Expr>()

    private fun binaryExpr(tokenString: String, f: (Expr, Expr) -> Expr) =
        inOrder(ref { expr }, token(tokenString), ref { expr }).mapLeftAssoc(f.asBinary()).with(cache)

    private fun unaryExpr(tokenString: String, f: (Expr) -> Expr) =
        inOrder(token(tokenString), ref { expr }).map { (_, it) -> f(it) }.with(cache)

    private val boolLiteral = oneOf(str("true"), str("false")).map { if (it == "true") True else False }
    private val intLiteral = Tokens.integer.map { IntLiteral(it.toInt()) }
    private val stringLiteral = Tokens.string.map { StringLiteral(it) }
    private val arrayLiteral = inOrder(token("["), ref { expr }.joinedWith(token(",")), token("]"))
        .skipWrapper().map(::ArrayLiteral)
        .with(cache)

    private val equal = binaryExpr("==", ::Equal)
    private val notEqual = binaryExpr("!=", ::NotEqual)
    private val less = binaryExpr("<", ::Less)
    private val greater = binaryExpr(">", ::Greater)

    private val unaryMinus = unaryExpr("-", ::UnaryMinus)

    private val inArray = binaryExpr("in", ::InArray)
    private val notInArray = binaryExpr("not in", ::NotInArray)

    private val and = binaryExpr("and", ::And)
    private val or = binaryExpr("or", ::Or)
    private val not = unaryExpr("not", ::Not)

    private val paren = inOrder(token("("), ref { expr }, token(")")).skipWrapper().with(cache)

    private val expr: Parser<Expr> = oneOfWithPrecedence(
        or,
        and,
        oneOf(inArray, notInArray),
        oneOf(equal, notEqual, less, greater),
        oneOf(unaryMinus, not),
        paren.nestedPrecedence(),
        arrayLiteral.nestedPrecedence(),
        oneOf(stringLiteral, intLiteral, boolLiteral)
    ).reset(cache)

    fun parse(s: String): Expr = s.parseWith(expr)

    fun evaluate(s: String): Any = s.parseWith(expr).eval()

    sealed class Expr {
        object True : Expr()
        object False : Expr()
        data class IntLiteral(val value: Int) : Expr()
        data class StringLiteral(val value: String) : Expr()
        data class ArrayLiteral(val value: List<Expr>) : Expr()

        data class Equal(val left: Expr, val right: Expr) : Expr()
        data class NotEqual(val left: Expr, val right: Expr) : Expr()
        data class Less(val left: Expr, val right: Expr) : Expr()
        data class Greater(val left: Expr, val right: Expr) : Expr()

        data class UnaryMinus(val value: Expr) : Expr()

        data class And(val left: Expr, val right: Expr) : Expr()
        data class Or(val left: Expr, val right: Expr) : Expr()
        data class Not(val value: Expr) : Expr()

        data class InArray(val elementExpr: Expr, val arrayExpr: Expr) : Expr()
        data class NotInArray(val elementExpr: Expr, val arrayExpr: Expr) : Expr()
    }

    private fun Expr.eval(): Any =
        when (this) {
            True -> true
            False -> false
            is IntLiteral -> value
            is StringLiteral -> value
            is ArrayLiteral -> value.map { it.eval() }

            is Equal -> left.eval() == right.eval()
            is NotEqual -> left.eval() != right.eval()
            is Less -> (left.eval() as Int) < (right.eval() as Int)
            is Greater -> (left.eval() as Int) > (right.eval() as Int)

            is UnaryMinus -> -(value.eval() as Int)

            is InArray -> elementExpr.eval() in (arrayExpr.eval() as List<*>)
            is NotInArray -> elementExpr.eval() !in (arrayExpr.eval() as List<*>)

            is And -> (left.eval() as Boolean) && (right.eval() as Boolean)
            is Or -> (left.eval() as Boolean) || (right.eval() as Boolean)
            is Not -> !(value.eval() as Boolean)
        }
}
