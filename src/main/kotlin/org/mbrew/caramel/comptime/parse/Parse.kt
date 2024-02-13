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
        println("Parsing...")

        consume(TokenTypes.EDGE_EOS)
        val imports = parseImports()
        tlVarNames += imports
        val varDecls = mutableListOf<VarDeclStmt>()
        val funcDecls = mutableListOf<FuncDecl>()
        val typeDecls = mutableListOf<TypeDecl>()

        consume(TokenTypes.EDGE_EOS)
        while(src.peek().type != TokenTypes.EDGE_EOF) {
            when(src.peek().type) {
                TokenTypes.KW_LET -> parseVarDecl(tlVarNames)
                TokenTypes.KW_DEFINE -> parseFuncDecl(tlVarNames)
                TokenTypes.KW_TYPE -> parseTypeDecl(tlVarNames)
                else -> throw Exception("Unexpected token: ${src.peek()}")
            }
            consume(TokenTypes.EDGE_EOS)
        }

        ModuleRoot(imports, varDecls, funcDecls, typeDecls)
    }

    private fun parseImports(): List<String> = with(src) {
        val result = mutableListOf<String>()

        if(consume(TokenTypes.KW_IMPORT) != null) {
            expect(TokenTypes.EDGE_BRACE_L)
            consume(TokenTypes.EDGE_EOS)
            while(!test(TokenTypes.EDGE_BRACE_R)) {
                result.add(expect(TokenTypes.WORD).text)
                consume(TokenTypes.EDGE_EOS)
            }
            expect(TokenTypes.EDGE_BRACE_R)
        }

        result.also(::println)
    }

    private fun parseVarDecl(visibleVars: List<String>): VarDeclStmt = with(src) {
        expect(TokenTypes.KW_LET)
        val name = expect(TokenTypes.WORD).text
        val type = if(consume(TokenTypes.EDGE_COLON) != null) expect(TokenTypes.WORD).text else null
        val init = parseCall(tlVarNames)

        tlVarNames += name // referencing declaring var in init is invalid
        VarDeclStmt(name, type, init)
    }

    private fun parseFuncDecl(visibleVars: List<String>): FuncDecl = with(src) {
        expect(TokenTypes.KW_DEFINE)
        TODO()
    }

    private fun parseTypeDecl(visibleVars: List<String>): TypeDecl = with(src) {
        expect(TokenTypes.KW_TYPE)
        val name = expect(TokenTypes.WORD).text
        expect(TokenTypes.EDGE_BRACE_L)
        val fields = mutableListOf<VarDeclStmt>()
        while(!test(TokenTypes.EDGE_BRACE_R)) {
            val decl = parseVarDecl(visibleVars)
            fields += decl
        }
        expect(TokenTypes.EDGE_BRACE_R)

        TypeDecl(name, fields)
    }

    private fun parseBlock(visibleVars: List<String>): Block = with(src) {
        expect(TokenTypes.EDGE_BRACE_L)
        val stmts = mutableListOf<Stmt>()
        consume(TokenTypes.EDGE_EOS)
        while(!test(TokenTypes.EDGE_BRACE_R)) {
            stmts += parseStmt(visibleVars)
            consume(TokenTypes.EDGE_EOS)
        }
        expect(TokenTypes.EDGE_BRACE_R)

        Block(stmts)
    }

    private fun parseStmt(visibleVars: List<String>): Stmt = when(src.peek().type) {
        TokenTypes.KW_BREAK -> BreakStmt
        TokenTypes.KW_CONTINUE -> ContinueStmt
        TokenTypes.KW_RETURN -> parseReturnStmt(visibleVars)
        TokenTypes.KW_LET -> parseVarDecl(visibleVars)
        TokenTypes.KW_IF -> parseIfStmt(visibleVars)
        TokenTypes.KW_FOR -> parseForStmt(visibleVars)
        TokenTypes.KW_WHILE -> parseWhileStmt(visibleVars)
        TokenTypes.KW_THROW -> parseThrowStmt(visibleVars)
        TokenTypes.KW_TRY -> parseTryStmt(visibleVars)
        else -> ExprStmt(parseCall(visibleVars))
    }

    private fun parseIfStmt(visibleVars: List<String>): IfStmt = with(src) {
        expect(TokenTypes.KW_IF)
        val cond = parseExpr(visibleVars)
        val thenBlock = parseBlock(visibleVars)
        val elseBlock =
            if(consume(TokenTypes.KW_ELSE) != null)
                if(consume(TokenTypes.KW_IF) != null) parseIfStmt(visibleVars)
                else parseBlock(visibleVars)
            else null
        IfStmt(cond, thenBlock, elseBlock)
    }

    private fun parseWhileStmt(visibleVars: List<String>): WhileStmt = with(src) {
        expect(TokenTypes.KW_WHILE)
        val cond = parseExpr(visibleVars)
        val body = parseBlock(visibleVars)
        WhileStmt(cond, body)
    }

    private fun parseForStmt(visibleVars: List<String>): Stmt = with(src) {
        expect(TokenTypes.KW_FOR)
        val iterator = expect(TokenTypes.WORD).text
        if(consume(TokenTypes.OP_IN) != null) {
            val iterable = parseExpr(visibleVars)
            val body = parseBlock(visibleVars + iterator)
            ForeachStmt(iterator, iterable, body)
        } else {
            expect("from")
            val start = parseExpr(visibleVars)
            expect("to")
            val stop = parseExpr(visibleVars)
            val step = if(consume("by") != null) parseExpr(visibleVars) else null
            val body = parseBlock(visibleVars + iterator)
            ForRangeStmt(iterator, start, stop, step, body)
        }
    }

    private fun parseReturnStmt(visibleVars: List<String>) = with(src) {
        expect(TokenTypes.KW_RETURN)
        if(test(TokenTypes.EDGE_EOS) || test(TokenTypes.EDGE_BRACE_R)) ReturnStmt.NoReturn
        else {
            val expr = parseCall(visibleVars)
            ReturnStmt(expr)
        }
    }

    private fun parseTryStmt(visibleVars: List<String>): TryStmt = with(src) {
        expect(TokenTypes.KW_TRY)
        val tryBlock = parseBlock(visibleVars)
        val catchBlock = if(consume("catch") != null) parseBlock(visibleVars + "error") else null
        val finallyBlock = if(consume("finally") != null) parseBlock(visibleVars) else null
        TryStmt(tryBlock, catchBlock, finallyBlock)
    }

    private fun parseThrowStmt(visibleVars: List<String>): ThrowStmt = with(src) {
        expect(TokenTypes.KW_THROW)
        val expr = parseCall(visibleVars)
        ThrowStmt(expr)
    }

    private fun parseExpr(visibleVars: List<String>): Expr =  parseDisjunctionExpr(visibleVars)

    private fun parseCall(visibleVars: List<String>): Expr = with(src) {
        val words = mutableListOf<String>()
        val args = mutableListOf<Expr>()
        var hasWord = false

        while(!(
                test(TokenTypes.EDGE_EOS) ||
                test(TokenTypes.EDGE_EOF) ||
                test(TokenTypes.EDGE_PAREN_R) ||
                test(TokenTypes.EDGE_BRACE_R) ||
                test(TokenTypes.EDGE_BRACKET_R) ||
                test(TokenTypes.EDGE_COMMA)
                )) when {
            test(TokenTypes.WORD) -> {
                val word = expect(TokenTypes.WORD)
                if(word.text in visibleVars) {
                    args += parseExpr(visibleVars)
                    words += "$"
                } else {
                    words += word.text
                    hasWord = true
                }
            }
            else -> args += parseExpr(visibleVars)
        }

        return if (hasWord) CallExpr(words.joinToString(" "), args)
            else if (args.isNotEmpty()) args[0] else throw Exception("Invalid call")
    }

    private fun parseDisjunctionExpr(visibleVars: List<String>): Expr {
        TODO()
    }

    private fun parseConjunctionExpr(visibleVars: List<String>): Expr {
        TODO()
    }

    private fun parseEqualityExpr(visibleVars: List<String>): Expr {
        TODO()
    }

    private fun parseComparisonExpr(visibleVars: List<String>): Expr {
        TODO()
    }

    private fun parsePostfixExpr(visibleVars: List<String>): Expr = with(src) {
        val exp = parseExpr(visibleVars)
        return when(peek().type) {
            TokenTypes.EDGE_BRACKET_L -> parseIndexAccessExpr(visibleVars, exp)
            TokenTypes.OP_DOT -> parseMemberAccessExpr(visibleVars, exp)
            else -> exp
        }
    }

    private fun parseMemberAccessExpr(visibleVars: List<String>, exp: Expr): Expr = with(src) {
        var lhs = exp
        consume(TokenTypes.EDGE_EOS)
        while(consume(TokenTypes.OP_DOT) != null) {
            val rhs = expect(TokenTypes.WORD).text
            lhs = MemberAccessExpr(lhs, rhs)
            consume(TokenTypes.EDGE_EOS)
        }
        lhs
    }

    private fun parseIndexAccessExpr(visibleVars: List<String>, exp: Expr): Expr = with(src) {
        expect(TokenTypes.EDGE_BRACKET_L)
        consume(TokenTypes.EDGE_EOS)
        val rhs = parseCall(visibleVars)
        consume(TokenTypes.EDGE_EOS)
        expect(TokenTypes.EDGE_BRACKET_R)
        IndexAccessExpr(exp, rhs)
    }

    private fun parsePrefixExpr(visibleVars: List<String>): Expr = with(src) {
        val op = when(peek().type) {
            TokenTypes.OP_ADD -> TokenTypes.OP_ADD
            TokenTypes.OP_SUB -> TokenTypes.OP_SUB
            TokenTypes.OP_NOT -> TokenTypes.OP_NOT
            else -> return parsePostfixExpr(visibleVars)
        }
        consume(op)
        UnaryExpr(op, parsePostfixExpr(visibleVars))
    }

}
