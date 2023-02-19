package com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server

import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.Comment
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.Vote
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.AggregateQuerySnapshot
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object DbFirestore {
    const val COLLECTION_UPVOTES = "upvotes";
    const val COLLECTION_DOWNVOTES = "downvotes";
    const val COLLECTION_COMMENTS = "comments"

    /* VOTOS A NOTICIAS */
    fun getUpvoteCount(urlArticle : String, onCompleteListener : (task : Task<AggregateQuerySnapshot>) -> Unit) {
        FirebaseFirestore.getInstance().collection(COLLECTION_UPVOTES)
            .whereEqualTo("urlArticle", urlArticle)
            .count()
            .get(AggregateSource.SERVER)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getDownvoteCount(urlArticle : String, onCompleteListener : (task : Task<AggregateQuerySnapshot>) -> Unit) {
        FirebaseFirestore.getInstance().collection(COLLECTION_DOWNVOTES)
            .whereEqualTo("urlArticle", urlArticle)
            .count()
            .get(AggregateSource.SERVER)
            .addOnCompleteListener(onCompleteListener)
    }

    fun upvote(urlArticle : String, userId : String, onSuccess : () -> Unit, onFailure : () -> Unit) {
        val voteId = FirebaseFirestore.getInstance().collection(COLLECTION_UPVOTES).document().id;

        FirebaseFirestore.getInstance().collection(COLLECTION_UPVOTES)
            .add(Vote(voteId, urlArticle, userId))
            .addOnCompleteListener {
                if (it.isSuccessful){
                    onSuccess()
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun downvote(urlArticle : String, userId : String, onSuccess : () -> Unit, onFailure : () -> Unit) {
        val voteId = FirebaseFirestore.getInstance().collection(COLLECTION_DOWNVOTES).document().id;

        FirebaseFirestore.getInstance().collection(COLLECTION_DOWNVOTES)
            .add(Vote(voteId, urlArticle, userId))
            .addOnCompleteListener {
                if (it.isSuccessful){
                    onSuccess()
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun upvotesByUserCount(urlArticle : String, userId : String, onCompleteListener : (task : Task<AggregateQuerySnapshot>) -> Unit) {
        FirebaseFirestore.getInstance().collection(COLLECTION_UPVOTES)
            .whereEqualTo("urlArticle", urlArticle)
            .whereEqualTo("userId", userId)
            .count()
            .get(AggregateSource.SERVER)
            .addOnCompleteListener(onCompleteListener)
    }

    fun downvotesByUserCount(urlArticle : String, userId : String, onCompleteListener : (task : Task<AggregateQuerySnapshot>) -> Unit) {
        FirebaseFirestore.getInstance().collection(COLLECTION_DOWNVOTES)
            .whereEqualTo("urlArticle", urlArticle)
            .whereEqualTo("userId", userId)
            .count()
            .get(AggregateSource.SERVER)
            .addOnCompleteListener(onCompleteListener)
    }

    fun removeUpvote(urlArticle : String, userId : String, onSuccess : () -> Unit, onFailure: () -> Unit) {
        FirebaseFirestore.getInstance().collection(COLLECTION_UPVOTES)
            .whereEqualTo("urlArticle", urlArticle)
            .whereEqualTo("userId", userId)
            .get()
            .addOnCompleteListener { it ->
                if (it.isSuccessful){
                    val id = it.result.first().id
                    FirebaseFirestore.getInstance().collection(COLLECTION_UPVOTES)
                        .document(id)
                        .delete()
                        .addOnCompleteListener { ut ->
                            if (ut.isSuccessful){
                                onSuccess()
                            }
                        }
                        .addOnFailureListener {
                            onFailure()
                        }
                }
            }
    }

    fun removeDownvote(urlArticle : String, userId : String, onSuccess : () -> Unit, onFailure: () -> Unit) {
        FirebaseFirestore.getInstance().collection(COLLECTION_DOWNVOTES)
            .whereEqualTo("urlArticle", urlArticle)
            .whereEqualTo("userId", userId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val id = it.result.first().id
                    FirebaseFirestore.getInstance().collection(COLLECTION_DOWNVOTES)
                        .document(id)
                        .delete()
                        .addOnCompleteListener { ut ->
                            if (ut.isSuccessful){
                                onSuccess()
                            }
                        }
                        .addOnFailureListener {
                            onFailure()
                        }
                }
            }
    }

    /* COMENTARIOS */
    fun getCommentCount(urlArticle : String, onCompleteListener : (task : Task<AggregateQuerySnapshot>) -> Unit) {
        FirebaseFirestore.getInstance().collection(COLLECTION_COMMENTS)
            .whereEqualTo("urlArticle", urlArticle)
            .count()
            .get(AggregateSource.SERVER)
            .addOnCompleteListener(onCompleteListener)
    }

    suspend fun getComments(urlArticle : String) : List<Comment> {
        val comments = mutableListOf<Comment>()

        val snapshot = FirebaseFirestore.getInstance().collection(COLLECTION_COMMENTS)
            .whereEqualTo("urlArticle", urlArticle)
            .get()
            .await()

        for (documentSnapshot in snapshot){
            val comment = documentSnapshot.toObject(Comment::class.java)
            comments.add(comment)
        }

        return comments
    }

    fun writeComment(comment : Comment, onSuccess: () -> Unit, onFailure: () -> Unit) {
        comment.id = FirebaseFirestore.getInstance().collection(COLLECTION_COMMENTS).document().id;

        FirebaseFirestore.getInstance().collection(COLLECTION_COMMENTS)
            .add(comment)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    onSuccess()
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun removeComment(comment : Comment, onSuccess: () -> Unit, onFailure: () -> Unit) {
        FirebaseFirestore.getInstance().collection(COLLECTION_COMMENTS)
            .whereEqualTo("id", comment.id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val id = it.result.first().id
                    FirebaseFirestore.getInstance().collection(COLLECTION_COMMENTS)
                        .document(id)
                        .delete()
                        .addOnCompleteListener { ut ->
                            if (ut.isSuccessful){
                                onSuccess()
                            }
                        }
                        .addOnFailureListener {
                            onFailure()
                        }
                }
            }
    }

}

/*
*
*
"delete" comment styles
"delete" comment dialog
log out slider menu
log out dialog
about slider menu
about fragment
vote comments
vote comments style
report comment
report comment style
detail fragment upvote/downvote buttons
detail fragment save button (room)
news fragment save (room)
keep custom toolbar on top (sticky toolbar)
sort comments by date
sort comments by votes
sort comments button
see saved articles slider menu
add saved articles fragment
clean code
*
* */