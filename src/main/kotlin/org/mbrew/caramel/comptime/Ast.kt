package org.mbrew.caramel.comptime

sealed interface Node

class ModuleRoot(
    val imports: List<String>,
    val topLevelVars: Map<String, VarDeclStmt>,
    val funcs: Map<String, FuncDecl>,
    val types: Map<String, TypeDecl>
) : Node

class FuncDecl(
    val mangledName: String,
    val params: List<Pair<String, TypeSymbol>>,
    val body: Stmt,
) : Node

class TypeDecl(
    val name: String,
    val fields: Map<String, VarDeclStmt>,
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
    val elseBlock: Stmt? = null,
) : Stmt

class WhileStmt(
    val cond: Expr,
    val block: Stmt,
) : Stmt

class ForRangeStmt(
    val iteratorName: String,
    val start: Expr,
    val end: Expr,
    val step: Expr,
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
    val op: TokenTypes,
    val lhs: Expr,
    val rhs: Expr,
): Expr()

class UnaryExpr(
    val op: TokenTypes,
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
