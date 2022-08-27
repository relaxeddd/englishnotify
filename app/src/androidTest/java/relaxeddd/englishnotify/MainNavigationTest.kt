package relaxeddd.englishnotify

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import relaxeddd.englishnotify.infrastructure.FakePreferences
import relaxeddd.englishnotify.infrastructure.TestApp
import relaxeddd.englishnotify.ui.main.MainActivity

class MainNavigationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun drawerNavigationTest() {
        var needRecreate = false
        activityRule.scenario.onActivity {
            val prefs = (it.application as TestApp).applicationComponent.prefs as FakePreferences

            if (prefs.isBottomNavigationValue) {
                prefs.isBottomNavigationValue = false
                needRecreate = true
            }
        }
        if (needRecreate) activityRule.scenario.recreate()

        onView(withId(R.id.drawer)).perform(DrawerActions.open())
        onView(allOf(isDescendantOfA(withId(R.id.drawer_navigation)), withText("Dictionary")))
            .perform(click())
        onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withText("Dictionary"))).check(matches(isDisplayed()))

        onView(withId(R.id.drawer)).perform(DrawerActions.open())
        onView(allOf(isDescendantOfA(withId(R.id.drawer_navigation)), withId(R.id.fragmentTrainingSetting)))
            .perform(click())
        onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withText("Training setting"))).check(matches(isDisplayed()))

        onView(withId(R.id.drawer)).perform(DrawerActions.open())
        onView(allOf(isDescendantOfA(withId(R.id.drawer_navigation)), withId(R.id.fragmentNotifications)))
            .perform(click())
        onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withText("Notifications"))).check(matches(isDisplayed()))

        onView(withId(R.id.drawer)).perform(DrawerActions.open())
        onView(allOf(isDescendantOfA(withId(R.id.drawer_navigation)), withId(R.id.fragmentSettings)))
            .perform(click())
        onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withText("Common"))).check(matches(isDisplayed()))

        onView(withId(R.id.drawer)).perform(DrawerActions.open())
        onView(allOf(isDescendantOfA(withId(R.id.drawer_navigation)), withId(R.id.fragmentDictionaryContainer)))
            .perform(click())
        onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withText("Dictionary"))).check(matches(isDisplayed()))
    }

    @Test
    fun bottomNavigationTest() {
        var needRecreate = false
        activityRule.scenario.onActivity {
            val prefs = (it.application as TestApp).applicationComponent.prefs as FakePreferences

            if (!prefs.isBottomNavigationValue) {
                prefs.isBottomNavigationValue = true
                needRecreate = true
            }
        }
        if (needRecreate) activityRule.scenario.recreate()

        onView(allOf(isDescendantOfA(withId(R.id.bottom_navigation_view_main)), withId(R.id.fragmentDictionaryContainer))).perform(click())
        onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withText("Dictionary"))).check(matches(isDisplayed()))

        onView(withId(R.id.drawer)).perform(DrawerActions.open())
        onView(allOf(isDescendantOfA(withId(R.id.bottom_navigation_view_main)), withId(R.id.fragmentTrainingSetting))).perform(click())
        onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withText("Training setting"))).check(matches(isDisplayed()))

        onView(withId(R.id.drawer)).perform(DrawerActions.open())
        onView(allOf(isDescendantOfA(withId(R.id.bottom_navigation_view_main)), withId(R.id.fragmentNotifications))).perform(click())
        onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withText("Notifications"))).check(matches(isDisplayed()))

        onView(withId(R.id.drawer)).perform(DrawerActions.open())
        onView(allOf(isDescendantOfA(withId(R.id.bottom_navigation_view_main)), withId(R.id.fragmentSettings))).perform(click())
        onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withText("Common"))).check(matches(isDisplayed()))

        onView(withId(R.id.drawer)).perform(DrawerActions.open())
        onView(allOf(isDescendantOfA(withId(R.id.bottom_navigation_view_main)), withId(R.id.fragmentDictionaryContainer))).perform(click())
        onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withText("Dictionary"))).check(matches(isDisplayed()))
    }
}
