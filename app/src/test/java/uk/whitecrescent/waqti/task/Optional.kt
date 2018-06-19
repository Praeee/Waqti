package uk.whitecrescent.waqti.task

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.whitecrescent.waqti.model.task.Constraint
import uk.whitecrescent.waqti.model.task.DEFAULT_OPTIONAL
import uk.whitecrescent.waqti.model.task.HIDDEN
import uk.whitecrescent.waqti.model.task.OPTIONAL
import uk.whitecrescent.waqti.model.task.Property
import uk.whitecrescent.waqti.model.task.SHOWING
import uk.whitecrescent.waqti.model.task.UNMET
import uk.whitecrescent.waqti.testTask

@DisplayName("Optional Tests")
class Optional : BaseTaskTest() {

    @DisplayName("Optional Default Values")
    @Test
    fun testTaskOptionalDefaultValues() {
        val task = testTask()
        assertFalse(task.optional is Constraint)
        assertEquals(DEFAULT_OPTIONAL, task.optional.value)
        assertFalse(task.optional.isVisible)
    }

    @DisplayName("Set Optional Property using setOptionalProperty")
    @Test
    fun testTaskSetOptionalProperty() {
        val task = testTask()
                .setOptionalProperty(Property(SHOWING, OPTIONAL))

        assertFalse(task.optional is Constraint)
        assertEquals(OPTIONAL, task.optional.value)
        assertTrue(task.optional.isVisible)


        task.hideOptional()
        assertEquals(Property(HIDDEN, DEFAULT_OPTIONAL), task.optional)
    }

    @DisplayName("Set Optional Property using setOptionalValue")
    @Test
    fun testTaskSetOptionalValue() {
        val task = testTask()
                .setOptionalValue(OPTIONAL)

        assertFalse(task.optional is Constraint)
        assertEquals(OPTIONAL, task.optional.value)
        assertTrue(task.optional.isVisible)

        task.hideOptional()
        assertEquals(Property(HIDDEN, DEFAULT_OPTIONAL), task.optional)
    }

    @DisplayName("Set Optional Constraint")
    @Test
    fun testTaskSetOptionalConstraint() {
        val task = testTask()
                .setOptionalProperty(Constraint(SHOWING, OPTIONAL, UNMET))

        assertFalse(task.optional is Constraint)
        assertTrue(task.getAllUnmetAndShowingConstraints().isEmpty())
        assertEquals(OPTIONAL, task.optional.value)
        assertTrue(task.optional.isVisible)
        assertThrows(ClassCastException::class.java,
                { assertTrue((task.optional as Constraint).isMet == true) })

    }

}