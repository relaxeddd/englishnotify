package relaxeddd.englishnotify

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnHolderItem
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary.ViewHolder
import relaxeddd.englishnotify.ui.main.MainActivity

class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun addAndDeleteWordTest() {
        onView(withId(R.id.fragmentDictionaryContainer)).perform(click())
        onView(withId(R.id.button_main_fab)).perform(click())
        onView(withId(R.id.text_input_word)).perform(typeText("espresso_test_eng"))
        onView(withId(R.id.text_input_transcription)).perform(typeText("espresso_test_transcription"))
        onView(withId(R.id.text_input_translation)).perform(typeText("espresso_test_translation"))
        onView(withId(R.id.item_menu_accept)).perform(click())
        onView(withId(R.id.recycler_view_dictionary)).perform(
            actionOnHolderItem(
                dictionaryItemWithTextWord("espresso_test_eng"),
                longClick()
            )
        )
        onView(withText("Delete")).perform(click())

        onView(withId(R.id.recycler_view_dictionary)).check(matches(dictionaryItemAtPosition(0, not(withText("espresso_test_eng")))))
    }

    private fun dictionaryItemWithTextWord(textWord: String): Matcher<ViewHolder> {
        return object: BoundedMatcher<ViewHolder, ViewHolder>(ViewHolder::class.java) {

            override fun matchesSafely(item: ViewHolder?): Boolean {
                return item?.textWord?.text == textWord
            }

            override fun describeTo(description: Description?) {
                description?.appendText("dictionary item with word text: $textWord")
            }
        }
    }

    private fun dictionaryItemAtPosition(position: Int, matcher: Matcher<View>): Matcher<View> {
        return object: BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

            override fun matchesSafely(recyclerView: RecyclerView?): Boolean {
                val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position)

                return if (viewHolder == null) {
                    false
                } else {
                    matcher.matches(viewHolder.itemView)
                }
            }

            override fun describeTo(description: Description?) {
                description?.appendText("has item at position: $position")
                matcher.describeTo(description)
            }
        }
    }
}
