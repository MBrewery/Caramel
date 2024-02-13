package org.mbrew.caramel.comptime.sema

import org.mbrew.caramel.comptime.*

// type check and infer
class TypeChecker(val task: CompileTask) {
    val ast by lazy { task.ast }

    fun check() {
        for (v in ast.topLevelVars) checkLetStmt(v.value)
    }

    fun checkLetStmt(stmt: VarDeclStmt) {

    }

    fun infer(n: Expr): TypeSymbol {
        when(n) {
            else -> TODO()
        }
    }
}
