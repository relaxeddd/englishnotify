package relaxeddd.englishnotify

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import relaxeddd.englishnotify.notifications.NotificationsWorkManagerHelper
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.models.NotificationRepeatTime
import relaxeddd.englishnotify.ui.time.ViewModelTime

class ViewModelTimeTest {

    @MockK
    lateinit var prefs: Preferences

    @MockK
    lateinit var notificationsWorkManagerHelper: NotificationsWorkManagerHelper

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun viewModelTimeOnClickAcceptTest() {
        every { prefs.isNotificationsEnabled() } returns true
        every { prefs.getNotificationsRepeatTime() } returns NotificationRepeatTime.MINUTES_60
        every { prefs.setNotificationsRepeatTime(any()) } just Runs
        every { notificationsWorkManagerHelper.launchWork(any(), any(), any()) } just Runs

        val viewModel = ViewModelTime(prefs, notificationsWorkManagerHelper)

        viewModel.onNotificationTimeChanged(notificationTime = 0)
        viewModel.onClickAccept()

        verify {
            prefs.setNotificationsRepeatTime(0)
            notificationsWorkManagerHelper.launchWork(
                repeatTimeInMinutes = 30,
                isForceUpdate = true,
                isNotificationsEnabled = true
            )
        }
    }
}
