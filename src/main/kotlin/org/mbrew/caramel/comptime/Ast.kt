package org.mbrew.caramel.comptime

sealed interface Node

class ModuleRoot(
    val imports: List<String>,
    val topLevelVars: List<VarDeclStmt>,
    val funcs: List<FuncDecl>,
    val types: List<TypeDecl>
) : Node

class FuncDecl(
    val mangledName: String,
    val params: List<Pair<String, TypeSymbol>>,
    val body: Stmt,
) : Node

class TypeDecl(
    val name: String,
    val fields: List<VarDeclStmt>,
) : Node

// ---------------- Statements ---------------- //

sealed interface Stmt : Node
class Block(val stmts: List<Stmt>) : Stmt

class VarDeclStmt(
    val name: String,
    val type: String? = null, // null = use type infer
    val init: Expr,
) : Stmt

class VarAssignStmt(
    val name: String,
    val expr: Expr,
) : Stmt

class MemberAssignStmt(
    val obj: Expr,
    val member: String,
    val expr: Expr,
) : Stmt

class IndexAssignStmt(
    val obj: Expr,
    val index: Expr,
    val expr: Expr,
) : Stmt

class ExprStmt(
    val expr: Expr,
) : Stmt

class IfStmt(
    val cond: Expr,
    val thenBlock: Stmt,
    val elseBlock: Stmt?,
) : Stmt

class WhileStmt(
    val cond: Expr,
    val block: Stmt,
) : Stmt

class ForRangeStmt(
    val iteratorName: String,
    val start: Expr,
    val end: Expr,
    val step: Expr?,
    val block: Stmt,
) : Stmt

class ForeachStmt(
    val iteratorName: String,
    val iterable: Expr,
    val block: Stmt,
) : Stmt

data object BreakStmt : Stmt
data object ContinueStmt : Stmt

class ReturnStmt(
    val expr: Expr? = null,
) : Stmt {
    companion object {
        @JvmStatic
        val NoReturn = ReturnStmt(null)
    }
}

class ThrowStmt(
    val expr: Expr,
) : Stmt

class TryStmt(
    val tryBlock: Stmt,
    val catchBlock: Stmt?,
    val finallyBlock: Stmt?,
) : Stmt

// ---------------- Expressions ---------------- //

sealed class Expr: Node {
    var type: TypeSymbol = TypeSymbol.Unknown // annotate at sema
}

class VarExpr(
    val name: String,
): Expr()

class MemberAccessExpr(
    val obj: Expr,
    val member: String,
): Expr()

class IndexAccessExpr(
    val obj: Expr,
    val index: Expr,
): Expr()

class CallExpr(
    val mangledName: String,
    val args: List<Expr>
): Expr()

class BinaryExpr(
    val op: TokenType,
    val lhs: Expr,
    val rhs: Expr,
): Expr()

class UnaryExpr(
    val op: TokenType,
    val expr: Expr,
): Expr()

data object NullExpr: Expr() {
    init {
        type = TypeSymbol.Null
    }
}

class NumberLitExpr(
    val value: Double,
): Expr() {
    init {
        type = TypeSymbol.Number
    }
}

class StringLitExpr(
    val value: String,
): Expr() {
    init {
        type = TypeSymbol.String
    }
}

class BooleanLitExpr private constructor(
    val value: Boolean,
): Expr() {
    init {
        type = TypeSymbol.Boolean
    }

    companion object {
        val True = BooleanLitExpr(true)
        val False = BooleanLitExpr(false)
    }
}

class LambdaExpr(
    val params: List<Pair<String, TypeSymbol>>,
    val body: Stmt,
): Expr()
