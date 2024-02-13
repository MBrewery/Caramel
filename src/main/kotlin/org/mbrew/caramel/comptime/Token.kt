package org.mbrew.caramel.comptime

data class Token(
    val type: TokenType,
    val text: String,

    // additional data for debugging
    val line: Int = 0,
    val posInLine: Int = 0,
) {
    override fun toString(): String = "Token($type, $text)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Token) return false

        if (type != other.type) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int = 31 * type + text.hashCode()
}

typealias TokenType = Int
object TokenTypes {
    const val WORD = 0

    const val LIT_STRING = 10
    const val LIT_BOOLEAN = 11
    const val LIT_NUMBER = 12
    const val LIT_NULL = 13

    const val EDGE_EOF = 200
    const val EDGE_EOS = 201
    const val EDGE_COMMA = 202
    const val EDGE_COLON = 203
    const val EDGE_PAREN_L = 204
    const val EDGE_PAREN_R = 205
    const val EDGE_BRACE_L = 206
    const val EDGE_BRACE_R = 207
    const val EDGE_BRACKET_L = 208
    const val EDGE_BRACKET_R = 209
    const val EDGE_ARROW = 210

    const val OP_ADD = 30
    const val OP_SUB = 31
    const val OP_MUL = 32
    const val OP_DIV = 33
    const val OP_MOD = 34

    const val OP_EQ = 37
    const val OP_NEQ = 38
    const val OP_GEQ = 39
    const val OP_LEQ = 40
    const val OP_GT = 41
    const val OP_LT = 42

    const val OP_DOT = 43

    const val OP_AND = 44
    const val OP_OR = 45
    const val OP_NOT = 46

    const val OP_AS = 47
    const val OP_IS = 48
    const val OP_IN = 49

    const val LIST_BEGIN = 70
    const val MAP_BEGIN = 71

    const val KW_IF = 100
    const val KW_ELSE = 101
    const val KW_WHILE = 102
    const val KW_FOR = 103
    const val KW_BREAK = 104
    const val KW_CONTINUE = 105
    const val KW_RETURN = 106
    const val KW_DEFINE = 107
    const val KW_LET = 108
    const val KW_TYPE = 109
    const val KW_IMPORT = 110
    const val KW_THROW = 111
    const val KW_TRY = 112

}

object CommonTokens {
    @JvmStatic val EOF = Token(TokenTypes.EDGE_EOF, "<EOF>")
    @JvmStatic val EOS = Token(TokenTypes.EDGE_EOS, "<EOS>")

    @JvmStatic val PAREN_L = Token(TokenTypes.EDGE_PAREN_L, "(")
    @JvmStatic val PAREN_R = Token(TokenTypes.EDGE_PAREN_R, ")")
    @JvmStatic val BRACE_L = Token(TokenTypes.EDGE_BRACE_L, "{")
    @JvmStatic val BRACE_R = Token(TokenTypes.EDGE_BRACE_R, "}")
    @JvmStatic val BRACKET_L = Token(TokenTypes.EDGE_BRACKET_L, "[")
    @JvmStatic val BRACKET_R = Token(TokenTypes.EDGE_BRACKET_R, "]")
    @JvmStatic val ARROW = Token(TokenTypes.EDGE_ARROW, "->")

//    @JvmStatic val LIST_BEGIN = Token(TokenTypes.LIST_BEGIN, "@[")
//    @JvmStatic val MAP_BEGIN = Token(TokenTypes.MAP_BEGIN, "@{")

    @JvmStatic val COMMA = Token(TokenTypes.EDGE_COMMA, ",")
    @JvmStatic val COLON = Token(TokenTypes.EDGE_COLON, ":")

    @JvmStatic val LIT_TRUE = Token(TokenTypes.LIT_BOOLEAN, "true")
    @JvmStatic val LIT_FALSE = Token(TokenTypes.LIT_BOOLEAN, "false")
    @JvmStatic val LIT_NULL = Token(TokenTypes.LIT_NULL, "null")

    @JvmStatic val OP_ADD = Token(TokenTypes.OP_ADD, "+")
    @JvmStatic val OP_SUB = Token(TokenTypes.OP_SUB, "-")
    @JvmStatic val OP_MUL = Token(TokenTypes.OP_MUL, "*")
    @JvmStatic val OP_DIV = Token(TokenTypes.OP_DIV, "/")
    @JvmStatic val OP_MOD = Token(TokenTypes.OP_MOD, "%")

    @JvmStatic val OP_EQ = Token(TokenTypes.OP_EQ, "==")
    @JvmStatic val OP_NEQ = Token(TokenTypes.OP_NEQ, "!=")
    @JvmStatic val OP_GEQ = Token(TokenTypes.OP_GEQ, ">=")
    @JvmStatic val OP_LEQ = Token(TokenTypes.OP_LEQ, "<=")
    @JvmStatic val OP_GT = Token(TokenTypes.OP_GT, ">")
    @JvmStatic val OP_LT = Token(TokenTypes.OP_LT, "<")

    @JvmStatic val OP_DOT = Token(TokenTypes.OP_DOT, ".")

    @JvmStatic val OP_AND = Token(TokenTypes.OP_AND, "and")
    @JvmStatic val OP_OR = Token(TokenTypes.OP_OR, "or")
    @JvmStatic val OP_NOT = Token(TokenTypes.OP_NOT, "not")

    @JvmStatic val OP_AS = Token(TokenTypes.OP_AS, "as")
    @JvmStatic val OP_IS = Token(TokenTypes.OP_IS, "is")
    @JvmStatic val OP_IN = Token(TokenTypes.OP_IN, "in")

    @JvmStatic val KW_IF = Token(TokenTypes.KW_IF, "if")
    @JvmStatic val KW_ELSE = Token(TokenTypes.KW_ELSE, "else")
    @JvmStatic val KW_WHILE = Token(TokenTypes.KW_WHILE, "while")
    @JvmStatic val KW_FOR = Token(TokenTypes.KW_FOR, "for")
    @JvmStatic val KW_BREAK = Token(TokenTypes.KW_BREAK, "break")
    @JvmStatic val KW_CONTINUE = Token(TokenTypes.KW_CONTINUE, "continue")
    @JvmStatic val KW_RETURN = Token(TokenTypes.KW_RETURN, "return")
    @JvmStatic val KW_DEFINE = Token(TokenTypes.KW_DEFINE, "define")
    @JvmStatic val KW_LET = Token(TokenTypes.KW_LET, "let")
    @JvmStatic val KW_TYPE = Token(TokenTypes.KW_TYPE, "type")
    @JvmStatic val KW_IMPORT = Token(TokenTypes.KW_IMPORT, "import")
    @JvmStatic val KW_THROW = Token(TokenTypes.KW_THROW, "throw")
    @JvmStatic val KW_TRY = Token(TokenTypes.KW_TRY, "try")

}
