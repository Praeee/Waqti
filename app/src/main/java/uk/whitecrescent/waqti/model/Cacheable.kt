package uk.whitecrescent.waqti.model

import uk.whitecrescent.waqti.model.task.ID

interface Cacheable {

    fun id(): ID
}