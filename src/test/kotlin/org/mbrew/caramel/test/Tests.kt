package org.mbrew.caramel.test

import org.junit.jupiter.api.Test
import org.mbrew.caramel.comptime.*

object Tests {
    val src = """
        import {
        
        ;
            formula; math
                prelude;
        }
        
        let x = 1
        
        def main {
            let y = 2
            let z = sin x + y
            print z
            if z > 1 {
                throw "z is too big"
            }
            let f = compile formula "3 ^ 2 + 2"
            print (execute f)
            return execute f
        }
        
    """.trimIndent()

    @Test
    fun test() {
        val task = CompileTask(src, CompileEnv())
        task.ast
    }
}
