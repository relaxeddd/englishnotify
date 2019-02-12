package com.example.vchechin.testapp.model.http

import androidx.annotation.NonNull
import com.example.vchechin.testapp.common.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*

object Firestore {

    private var isBlocked = false
    private val funList: ArrayList<() -> Unit> = ArrayList()

    fun addUnblockListener(onUnblock: () -> Unit) {
        funList.add { onUnblock() }
    }

    fun isBlocked() : Boolean {
        return isBlocked
    }

    fun getUserObservable(userId: String) : Single<MutableMap<String, Any>>? {
        return getDocumentObservable(userId, COLLECTION_USERS, ERROR_USER_NOT_FOUND)
    }

    fun getAppInfoObservable() : Single<MutableMap<String, Any>>? {
        return getDocumentObservable(APP, COLLECTION_INFO, ERROR_APP_INFO)
    }

    fun getAlertsObservable() : Single<List<DocumentSnapshot>>? {
        return getCollectionObservable(COLLECTION_ALERTS)
    }

    fun addWordToDictionary(name: String, item: Map<String, Any>, listener: ListenerOperation) {
        FirebaseFirestore.getInstance().collection(COLLECTION_DICTIONARY).document(name).set(item, SetOptions.merge()).addOnSuccessListener {
            listener.onSuccess()
        }.addOnFailureListener { e -> listener.onError() }
    }

    fun addWordsTags() {
        getCollectionObservable(COLLECTION_DICTIONARY)?.subscribe ({ answer ->
            for (word in answer) {
                val wordData = word.data

                wordData.set("tags", Arrays.asList("verb", "irregular"))
                FirebaseFirestore.getInstance().collection(COLLECTION_DICTIONARY).document(word.id).set(wordData,
                    SetOptions.merge()).addOnSuccessListener {

                }.addOnFailureListener {
                    System.out.print(it)
                }
            }
        }, {
        })
    }

    //------------------------------------------------------------------------------------------------------------------
    fun subscribeToUserChange(userId: String, @NonNull listener: EventListener<DocumentSnapshot>) : ListenerRegistration {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(userId).addSnapshotListener(listener)
    }

    fun subscribeToAppInfo(@NonNull listener: EventListener<DocumentSnapshot>) : ListenerRegistration {
        return FirebaseFirestore.getInstance().collection(COLLECTION_INFO).document(APP).addSnapshotListener(listener)
    }

    fun subscribeToAlertsChange(@NonNull listener: EventListener<QuerySnapshot>) : ListenerRegistration {
        return FirebaseFirestore.getInstance().collection(COLLECTION_ALERTS).addSnapshotListener(listener)
    }

    //------------------------------------------------------------------------------------------------------------------
    private fun blockFirestore() {
        isBlocked = true
    }

    private fun unblockFirestore() {
        isBlocked = false

        if (!funList.isEmpty()) {
            val onUnblockFun = funList.first()

            funList.removeAt(0)
            onUnblockFun()
        }
    }

    private fun getDocumentObservable(docId: String, collectionName: String, errorString: String) : Single<MutableMap<String, Any>>? {
        if (isBlocked) {
            return null
        } else {
            blockFirestore()
        }

        return Single
            .create<MutableMap<String, Any>> { e ->
                FirebaseFirestore.getInstance().collection(collectionName).document(docId).get()
                    .addOnCompleteListener {
                        unblockFirestore()
                        if (it.isSuccessful) {
                            val document = it.result

                            if (document.exists()) e.onSuccess(document.data)
                            else e.onError(Throwable(errorString))
                        } else {
                            e.onError(Throwable(errorString))
                        }
                    }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getCollectionObservable(collectionName: String) : Single<List<DocumentSnapshot>>? {
        if (isBlocked) {
            return null
        } else {
            blockFirestore()
        }

        return Single.create<List<DocumentSnapshot>>{ e ->
            FirebaseFirestore.getInstance().collection(collectionName).get()
                .addOnCompleteListener {
                    unblockFirestore()
                    if (it.isSuccessful && !it.result.isEmpty) {
                        e.onSuccess(it.result.documents)
                    } else {
                        e.onSuccess(ArrayList())
                    }
                }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}