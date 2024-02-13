package org.mbrew.caramel.comptime.parse

import org.mbrew.caramel.comptime.*
import org.mbrew.caramel.comptime.TokenStreamUtil.consume
import org.mbrew.caramel.comptime.TokenStreamUtil.expect
import org.mbrew.caramel.comptime.TokenStreamUtil.test

class Parser(task: CompileTask) {
    private val src by lazy { TokenStream(task.tokens) }
    private val tlVarNames = mutableListOf<String>()

    private var tempVarId = 0
    private fun newTempVar() = "%" + tempVarId++.toString()

    fun parse(): ModuleRoot = with(src) {
        consume(TokenTypes.EDGE_EOS)
        val imports = parseImports()
        tlVarNames += imports

        consume(TokenTypes.EDGE_EOS)
        while(src.peek().type != TokenTypes.EDGE_EOF) {
            when(src.peek().type) {
                TokenTypes.KW_LET -> parseTopLevelVarDecl()
                TokenTypes.KW_DEFINE -> parseFuncDecl()
                TokenTypes.KW_TYPE -> parseTypeDecl()
                else -> throw Exception("Unexpected token: ${src.peek()}")
            }
        }

        TODO()
        //ModuleRoot(imports, )
    }

    private fun parseImports(): List<String> = with(src) {
        val result = mutableListOf<String>()

        if(consume(TokenTypes.KW_IMPORT) != null) {
            expect(TokenTypes.EDGE_BRACE_L)
            consume(TokenTypes.EDGE_EOS)
            while(!test(TokenTypes.EDGE_BRACE_R)) {
                result.add(expect(TokenTypes.LIT_STRING).text)
                consume(TokenTypes.EDGE_EOS)
            }
            expect(TokenTypes.EDGE_BRACE_R)
        }

        result
    }

    private fun parseTopLevelVarDecl(): VarDeclStmt = with(src) {
        TODO()
    }

    private fun parseFuncDecl() {
        TODO("Not yet implemented")
    }

    private fun parseTypeDecl() {
        TODO("Not yet implemented")
    }


}
