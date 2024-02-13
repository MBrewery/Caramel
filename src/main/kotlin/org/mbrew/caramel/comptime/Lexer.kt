package org.mbrew.caramel.comptime

class Lexer(task: CompileTask) {
    val src: CharArray = task.src.toCharArray()
    var offset: Int = 0

    private inline fun hasMore(): Boolean = offset < src.size
    private inline fun hasMore(n: Int): Boolean = offset + n < src.size

    private inline fun skip() {
        offset++
    }

    private inline fun skip(n: Int) {
        offset += n
    }

    fun getTokens(): List<Token> {
        val result = mutableListOf<Token>()

        while (hasMore()) {
            val start = offset
            val ch = src[offset]
            when {
                ch.isWordStart() -> {
                    while (hasMore() && src[offset].isWordPart()) skip()
                    when (val word = String(src, start, offset - start)) {
                        "true" -> result += CommonTokens.LIT_TRUE
                        "false" -> result += CommonTokens.LIT_FALSE
                        "null" -> result += CommonTokens.LIT_NULL

                        "if" -> result += CommonTokens.KW_IF
                        "else" -> result += CommonTokens.KW_ELSE
                        "while" -> result += CommonTokens.KW_WHILE
                        "for" -> result += CommonTokens.KW_FOR
                        "break" -> result += CommonTokens.KW_BREAK
                        "continue" -> result += CommonTokens.KW_CONTINUE
                        "return" -> result += CommonTokens.KW_RETURN
                        "let" -> result += CommonTokens.KW_LET
                        "def", "define" -> result += CommonTokens.KW_DEFINE
                        "type" -> result += CommonTokens.KW_TYPE
                        "import" -> result += CommonTokens.KW_IMPORT
                        "throw" -> result += CommonTokens.KW_THROW
                        "try" -> result += CommonTokens.KW_TRY

                        "as" -> result += CommonTokens.OP_AS
                        "is" -> result += CommonTokens.OP_IS
                        "in" -> result += CommonTokens.OP_IN

                        "and" -> result += CommonTokens.OP_AND
                        "or" -> result += CommonTokens.OP_OR
                        "not" -> result += CommonTokens.OP_NOT

                        else -> result += Token(TokenTypes.WORD, word)
                    }
                }

                ch.isDigit() -> {
                    while (hasMore() && src[offset].isDigit()) skip()
                    if (hasMore() && src[offset] == '.') {
                        skip()
                        while (hasMore() && src[offset].isDigit()) skip()
                    }
                    result += Token(TokenTypes.LIT_NUMBER, String(src, start, offset - start))
                }

                ch == '"' -> {
                    skip()
                    while (hasMore() && src[offset] != '"') skip()
                    skip()
                    result += Token(TokenTypes.LIT_STRING, String(src, start + 1, offset - start - 2))
                }

                else -> {
                    when (ch) {
                        '(' -> {
                            skip()
                            result += CommonTokens.PAREN_L
                        }

                        ')' -> {
                            skip()
                            result += CommonTokens.PAREN_R
                        }

                        '{' -> {
                            skip()
                            result += CommonTokens.BRACE_L
                        }

                        '}' -> {
                            skip()
                            result += CommonTokens.BRACE_R
                        }

                        '[' -> {
                            skip()
                            result += CommonTokens.BRACKET_L
                        }

                        ']' -> {
                            skip()
                            result += CommonTokens.BRACKET_R
                        }

                        ',' -> {
                            skip()
                            result += CommonTokens.COMMA
                        }

                        '.' -> {
                            skip()
                            result += CommonTokens.OP_DOT
                        }

                        '+' -> {
                            skip()
                            result += CommonTokens.OP_ADD
                        }

                        '-' -> {
                            skip()
                            if(hasMore() && src[offset] == '>') {
                                skip()
                                result += CommonTokens.ARROW
                            } else result += CommonTokens.OP_SUB
                        }

                        '*' -> {
                            skip()
                            result += CommonTokens.OP_MUL
                        }

                        '/' -> {
                            skip()
                            if (hasMore() && src[offset] == '/') {
                                skip()
                                while (hasMore() && !(src[offset] == '\n' || src[offset] == '\r')) skip()
                            } else {
                                result += CommonTokens.OP_DIV
                            }
                        }

                        '%' -> {
                            skip()
                            result += CommonTokens.OP_MOD
                        }

                        '=' -> {
                            skip()
                            if (hasMore() && src[offset] == '=') skip()
                            result += CommonTokens.OP_EQ
                        }

                        '!' -> {
                            skip()
                            if (hasMore() && src[offset] == '=') {
                                skip()
                                result += CommonTokens.OP_NEQ
                            } else {
                                result += CommonTokens.OP_NOT
                            }
                        }

                        '>' -> {
                            skip()
                            if (hasMore() && src[offset] == '=') {
                                skip()
                                result += CommonTokens.OP_GEQ
                            } else {
                                result += CommonTokens.OP_GT
                            }
                        }

                        '<' -> {
                            skip()
                            if (hasMore() && src[offset] == '=') {
                                skip()
                                result += CommonTokens.OP_LEQ
                            } else {
                                result += CommonTokens.OP_LT
                            }
                        }

                        '&' -> {
                            skip()
                            if (hasMore() && src[offset] == '&') skip()
                            result += CommonTokens.OP_AND
                        }

                        '|' -> {
                            skip()
                            if (hasMore() && src[offset] == '|') skip()
                            result += CommonTokens.OP_OR
                        }

                        ':' -> {
                            skip()
                            result += CommonTokens.COLON
                        }

                        '\n', '\r', ';' -> {
                            while(hasMore() && (src[offset] == '\n' || src[offset] == '\r' || src[offset] == ';')) skip()
                            result += CommonTokens.EOS
                        }
                        ' ', '\t' -> skip()
                        '\\' -> {
                            skip()
                            if(hasMore() && src[offset] == '\n') {
                                skip()
                            } else throw RuntimeException("Unexpected character: $ch")
                        }
                        else -> throw RuntimeException("Unexpected character: $ch")
                    }
                }
            }
        }

        result += CommonTokens.EOF
        return result
    }

    companion object LexerHelper {
        /**
         * 判断一个字符是否是合法的单词开头
         * 合法的单词开头可以是 英文大小写字母, 下划线, 美元符号, 中文字符
         * 中文的范围是 4e00 - 9fff
         */
        @JvmStatic
        inline fun Char.isWordStart(): Boolean =
            this in 'a'..'z' || this in 'A'..'Z' || this == '_' || this == '$'
                    || (this in '\u4e00'..'\u9fff'
                    && this != '：' && this != '；' && this != '，' && this != '“' && this != '”'
                    && this != '【' && this != '】' && this != '（' && this != '）')

        @JvmStatic
        inline fun Char.isWordPart(): Boolean = isDigit() || isWordStart()

        @JvmStatic
        inline fun Char.isDigit(): Boolean = this in '0'..'9'
    }

}

class TokenStream(val tokens: List<Token>) {
    var offset: Int = 0

    fun peek(): Token = tokens[offset]
    fun next(): Token = tokens[offset++]

    fun skip() {
        offset++
    }

    fun skip(n: Int) {
        offset += n
    }
}

object TokenStreamUtil {
    @JvmStatic
    fun TokenStream.expect(type: TokenType): Token {
        val token = next()
        if (token.type != type) throw RuntimeException("Expected $type, but got $token")
        return token
    }

    @JvmStatic
    fun TokenStream.expect(text: String): Token {
        val token = next()
        if (token.text != text) throw RuntimeException("Expected $text, but got $token")
        return token
    }

    @JvmStatic
    fun TokenStream.expect(tk: Token): Token {
        val token = next()
        if (token != tk) throw RuntimeException("Expected $tk, but got $token")
        return token
    }

    @JvmStatic
    fun TokenStream.test(type: TokenType): Boolean = type == peek().type

    @JvmStatic
    fun TokenStream.test(text: String): Boolean = text == peek().text

    @JvmStatic
    fun TokenStream.test(tk: Token): Boolean = tk == peek()

    @JvmStatic
    fun TokenStream.consume(type: TokenType): Token? = if (test(type)) next() else null

    @JvmStatic
    fun TokenStream.consume(text: String): Token? = if (test(text)) next() else null

    @JvmStatic
    fun TokenStream.consume(tk: Token): Token? = if (test(tk)) next() else null

}
