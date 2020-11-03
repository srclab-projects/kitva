package test.xyz.srclab.common.lang

import org.testng.annotations.Test
import xyz.srclab.common.base.Ref
import xyz.srclab.test.doAssertEquals

/**
 * @author sunqian
 */
object RefTest {

    @Test
    fun testRef() {
        val ref = Ref.empty<Int>()
        doAssertEquals(ref.get(), null)
        ref.set(1)
        doAssertEquals(ref.get(), 1)

        val ref2 = Ref.of("1")
        doAssertEquals(ref.get(), 1)
    }
}