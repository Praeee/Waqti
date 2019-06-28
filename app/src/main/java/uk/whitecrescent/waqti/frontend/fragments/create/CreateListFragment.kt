@file:Suppress("NOTHING_TO_INLINE")

package uk.whitecrescent.waqti.frontend.fragments.create

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_create_list.*
import org.jetbrains.anko.textColor
import uk.whitecrescent.waqti.R
import uk.whitecrescent.waqti.backend.collections.TaskList
import uk.whitecrescent.waqti.backend.persistence.Caches
import uk.whitecrescent.waqti.backend.task.ID
import uk.whitecrescent.waqti.frontend.GoToFragment
import uk.whitecrescent.waqti.frontend.appearance.WaqtiColor
import uk.whitecrescent.waqti.frontend.fragments.parents.WaqtiCreateFragment
import uk.whitecrescent.waqti.requestFocusAndShowSoftKeyboard
import uk.whitecrescent.waqti.scrollToEnd

class CreateListFragment : WaqtiCreateFragment<TaskList>() {

    var boardID: ID = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        boardID = mainActivityVM.boardID

        setUpViews()

    }

    override fun setUpViews() {

        setUpAppBar()

        setUpButton()

    }

    private inline fun setUpAppBar() {
        mainActivity.appBar {
            color = WaqtiColor.WHITE
            elevation = 0F
            leftImageView.isVisible = false
            editTextView {
                textColor = WaqtiColor.BLACK.toAndroidColor
                removeAllTextChangedListeners()
                isEditable = true
                hint = getString(R.string.listNameHint)
                text = SpannableStringBuilder("")
                requestFocusAndShowSoftKeyboard()
                addAfterTextChangedListener {
                    if (it != null) {
                        addList_button.isVisible = !(it.isEmpty() || it.isBlank())
                    }
                }
            }
            rightImageView.isVisible = false
        }
    }

    private inline fun setUpButton() {
        addList_button.apply {
            isVisible = false
            setOnClickListener {
                Caches.boards[boardID].add(createElement()).update()
                finish()
            }
        }
    }

    override fun createElement(): TaskList {
        return TaskList(mainActivity.appBar.editTextView.text.toString())
    }

    override fun finish() {
        mainActivityVM.boardAdapter?.onInflate = {
            scrollToEnd()
        }

        mainActivityVM.boardPosition
                .changeTo(true to mainActivityVM.boardPosition.position + 1)
        @GoToFragment
        mainActivity.supportFragmentManager.popBackStack()
    }
}
