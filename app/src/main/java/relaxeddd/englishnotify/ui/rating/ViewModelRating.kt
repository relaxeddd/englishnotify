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
        var rating = ArrayList(repositoryUser.rating)
        val yourName = user.value?.name ?: ""
        var yourItem: RatingItem? = null
        var yourPosition = 0

        rating = ArrayList(rating.sortedByDescending { it.value })

        for (item in rating) {
            if (item.name == user.value?.name) {
                yourPosition = rating.indexOf(item) + 1
                yourItem = item
            }
        }
        for (ratingIx in 0 until rating.size) {
            val item = rating[ratingIx]
            if (item.value == yourItem?.value) {
                if (item.name != yourName) {
                    rating.remove(yourItem)
                    rating.add(ratingIx, yourItem)
                    yourPosition = ratingIx + 1
                }
                break
            }
        }

        if (yourItem != null) {
            yourPositionStr = "$yourPosition."
            yourValueStr = yourItem.value.toString()
        }
        this.rating = rating
    }
}