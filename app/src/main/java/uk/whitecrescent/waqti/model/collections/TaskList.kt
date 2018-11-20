package uk.whitecrescent.waqti.model.collections

import android.annotation.SuppressLint
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.reactivex.Observable
import uk.whitecrescent.waqti.model.Cacheable
import uk.whitecrescent.waqti.model.persistence.Database
import uk.whitecrescent.waqti.model.task.ID
import uk.whitecrescent.waqti.model.task.ObserverException
import uk.whitecrescent.waqti.model.task.TIME_CHECKING_PERIOD
import uk.whitecrescent.waqti.model.task.TIME_CHECKING_UNIT
import uk.whitecrescent.waqti.model.task.Task
import uk.whitecrescent.waqti.model.task.TaskState
import java.util.concurrent.ConcurrentHashMap

@Entity
open class TaskList(tasks: Collection<Task> = emptyList()) : AbstractWaqtiList<Task>(), Cacheable {

    @Convert(converter = IDArrayListConverter::class, dbType = String::class)
    override var idList = ArrayList<ID>()

    @Id
    override var id: Long = 0L

    var isAutoRemovingKilled = false
        set(value) {
            field = value
            autoRemoveKilled()
        }

    init {
        this.growTo(tasks.size)
        this.addAll(tasks)
    }

    override fun getAll(): ConcurrentHashMap<ID, Task> {
        return ConcurrentHashMap(
                Database.tasks.all
                        .filter { it.id in idList }
                        .map { Pair(it.id, it) }
                        .toMap()
        )
    }

    override fun update() {

    }

    override fun notDefault(): Boolean {
        return this.idList == emptyList<Task>()
    }

    fun add(collection: Collection<Tuple>): TaskList {
        collection.forEach { this.addAll(it.toList()) }
        return this
    }

    fun add(vararg tuples: Tuple) = add(tuples.toList())

    fun sortByTime(): TaskList {
        this.sort(Comparator { t1, t2 -> t1.time.value.compareTo(t2.time.value) })
        return this
    }

    fun sortByDuration(): TaskList {
        this.sort(Comparator { t1, t2 -> t1.duration.value.compareTo(t2.duration.value) })
        return this
    }

    fun sortByPriority(): TaskList {
        this.sort(Comparator { t1, t2 ->
            t1.priority.value.importanceLevel.compareTo(t2.priority.value.importanceLevel)
        })
        return this
    }

    fun sortByDeadline(): TaskList {
        this.sort(Comparator { t1, t2 -> t1.deadline.value.compareTo(t2.deadline.value) })
        return this
    }

    @Throws(IndexOutOfBoundsException::class)
    fun killAndRemove(index: Int): TaskList {
        if (!inRange(index)) {
            throw IndexOutOfBoundsException("Cannot kill at $index, limits are 0 to $nextIndex")
        } else {
            this[index].kill()
            this.removeAt(index)
            return this
        }
    }

    fun hasKilledTasks() = this.any { it.state == TaskState.KILLED }

    fun removeKilledTasks() = this.removeIf { it.state == TaskState.KILLED }

    // We can make the killed Tasks go to another List as well
    @SuppressLint("CheckResult")
    fun autoRemoveKilled(): TaskList {
        Observable.interval(TIME_CHECKING_PERIOD, TIME_CHECKING_UNIT)
                .takeWhile { isAutoRemovingKilled }
                .subscribeOn(LIST_OBSERVER_THREAD)
                .subscribe(
                        {
                            if (this.hasKilledTasks()) {
                                this.removeKilledTasks()
                            }
                        },
                        {
                            throw ObserverException("Concurrent List Observing failed!")
                        }
                )
        return this
    }
}