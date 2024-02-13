package org.mbrew.caramel.comptime

import org.mbrew.caramel.comptime.parse.Parser

class CompileEnv() {
    val symbolTable = RootSymbolTable()
}

class CompileTask(val src: String, val env: CompileEnv) {
    val tokens: List<Token> by lazy { lexer.getTokens() }
    val ast: ModuleRoot by lazy { parser.parse() }

    val lexer: Lexer by lazy { Lexer(this) }
    val parser: Parser by lazy { Parser(this) }
}
