package relaxeddd.englishnotify.ui.rating

import androidx.lifecycle.LiveData
import relaxeddd.englishnotify.common.RatingItem
import relaxeddd.englishnotify.common.User
import relaxeddd.englishnotify.common.ViewModelBase
import relaxeddd.englishnotify.model.repository.RepositoryUser

class ViewModelRating(val repositoryUser: RepositoryUser) : ViewModelBase() {

    val user: LiveData<User?> = repositoryUser.liveDataUser
    val rating: List<RatingItem>
    var yourPositionStr: String = "?"
    var yourValueStr: String = "?"

    init {
        var rating: List<RatingItem> = ArrayList(repositoryUser.rating)

        rating = rating.sortedByDescending { it.value }
        for (item in rating) {
            if (item.name == user.value?.name) {
                yourPositionStr = (rating.indexOf(item) + 1).toString() + "."
                yourValueStr = item.value.toString()
            }
        }

        this.rating = rating
    }
}