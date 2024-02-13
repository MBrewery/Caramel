package org.mbrew.caramel.comptime

class TypeSymbol(
    val name: String,
    val fields: Map<String, TypeSymbol>,
) {
    companion object {
        // special types
        val Unknown = TypeSymbol("<unknown>", emptyMap())
        val Any = TypeSymbol("<any>", emptyMap())
        val Null = TypeSymbol("<null>", emptyMap())

        // primitives
        val Number = TypeSymbol("Number", emptyMap())
        val String = TypeSymbol("String", emptyMap())
        val Boolean = TypeSymbol("Boolean", emptyMap())
        val Function = TypeSymbol("Function", emptyMap())
        val Type = TypeSymbol("Type", emptyMap())
    }
}

sealed interface SymbolTable {
    fun hasType(name: String): Boolean
    fun getType(name: String): TypeSymbol?
    fun addType(name: String, type: TypeSymbol)
}

class RootSymbolTable : SymbolTable {
    val types = mutableMapOf<String, TypeSymbol>()
    val vars = mutableMapOf<String, TypeSymbol>()

    override fun hasType(name: String) = types.containsKey(name)
    override fun getType(name: String) = types[name]
    override fun addType(name: String, type: TypeSymbol) {
        types[name] = type
    }
}

class ScopedSymbolTable(val parent: SymbolTable) : SymbolTable {
    val types = mutableMapOf<String, TypeSymbol>()
    val vars = mutableMapOf<String, TypeSymbol>()

    override fun hasType(name: String) = types.containsKey(name) || parent.hasType(name)
    override fun getType(name: String) = types[name] ?: parent.getType(name)
    override fun addType(name: String, type: TypeSymbol) {
        types[name] = type
    }
}
