package uk.whitecrescent.waqti.backend.task

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import uk.whitecrescent.waqti.backend.Cacheable
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.extensions.hash

// we've removed the nested labels feature for now as it may prove to be problematic
@Entity
class Label(name: String = "") : Cacheable {

    @Id
    override var id: ID = 0L

    override var name = name
        set(value) {
            field = value
            update()
        }

    //where init is placed matters! Always place it after all fields
    init {
        if (notDefault()) {
            update()
        }
    }

    override fun initialize() {

    }

    override fun notDefault(): Boolean {
        // if all these are false then this has been constructed using default constructor
        return this.name != "" || this.id != 0L
    }

    override fun update() = Caches.labels.put(this)

    override fun hashCode() = hash(name)

    override fun equals(other: Any?) =
            other is Label &&
                    other.name == this.name &&
                    other.id == this.id

    override fun toString(): String {
        return "Name: $name Id: $id"
    }
}