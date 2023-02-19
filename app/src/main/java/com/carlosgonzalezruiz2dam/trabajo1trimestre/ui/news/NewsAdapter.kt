package com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.news

import android.graphics.Color
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.carlosgonzalezruiz2dam.trabajo1trimestre.R
import com.carlosgonzalezruiz2dam.trabajo1trimestre.databinding.ViewNewBinding
import com.carlosgonzalezruiz2dam.trabajo1trimestre.inflate
import com.carlosgonzalezruiz2dam.trabajo1trimestre.loadUrl
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.New
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server.DbFirestore
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server.LoginData

class NewsAdapter(val listener: (New) -> Unit):

    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    var news = emptyList<New>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.view_new, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newArticle = news[position]
        holder.bind(newArticle)

        holder.itemView.setOnClickListener {
            listener(newArticle)
        }
    }

    override fun getItemCount(): Int = news.size

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val binding = ViewNewBinding.bind(view)
         fun bind(newArticle: New){
             // En función de la URL de la imagen: mostrar o no la imagen.
             if (newArticle.urlImage == "") {
                binding.cardView.visibility = View.GONE
             } else {
                 binding.image.loadUrl(newArticle.urlImage)
             }

             binding.title.text = newArticle.title

             // En función de si hay descripción: mostrar o no la descripción.
             if (newArticle.description == "") {
                 binding.description.visibility = View.GONE
             } else {
                 binding.description.text = newArticle.description
             }

             // En función de si hay fecha: mostrar o no la fecha.
             if (newArticle.date == "") {
                 binding.date.visibility = View.GONE
             } else {
                 binding.date.text = newArticle.date
             }

             // Votos.
             updateVotes(newArticle.urlArticle)

             // Inicializacion voto positivo.
             DbFirestore.upvotesByUserCount(newArticle.urlArticle, LoginData.id) {
                 if (it.result.count != 0L) {
                     binding.upvote.setImageResource(R.drawable.ic_baseline_thumb_up_24)
                 }
             }
             // Dar voto positivo.
             binding.upvote.setOnClickListener {
                 DbFirestore.upvotesByUserCount(newArticle.urlArticle, LoginData.id) { it ->
                     if (it.result.count == 0L) {
                         DbFirestore.downvotesByUserCount(newArticle.urlArticle, LoginData.id) { ut ->
                             if (ut.result.count == 0L) {
                                 DbFirestore.upvote(newArticle.urlArticle, LoginData.id, {
                                     updateVotes(newArticle.urlArticle)
                                 }, {
                                     Toast.makeText(
                                         itemView.context,
                                         R.string.vote_notsent.toString(),
                                         Toast.LENGTH_LONG
                                     ).show()
                                 })

                                 binding.upvote.setImageResource(R.drawable.ic_baseline_thumb_up_24)
                             } else {
                                 removeDownvote(newArticle.urlArticle)
                             }
                         }
                     } else {
                         removeUpvote(newArticle.urlArticle)
                     }
                 }
             }

             // Inicializacion voto positivo.
             DbFirestore.downvotesByUserCount(newArticle.urlArticle, LoginData.id) {
                 if (it.result.count != 0L) {
                     binding.downvote.setImageResource(R.drawable.ic_baseline_thumb_down_alt_24)
                 }
             }
             // Dar voto negativo.
             binding.downvote.setOnClickListener {
                 DbFirestore.upvotesByUserCount(newArticle.urlArticle, LoginData.id) { it ->
                     if (it.result.count == 0L) {
                         DbFirestore.downvotesByUserCount(newArticle.urlArticle, LoginData.id) { ut ->
                             if (ut.result.count == 0L) {
                                 DbFirestore.downvote(newArticle.urlArticle, LoginData.id, {
                                     updateVotes(newArticle.urlArticle)
                                 }, {
                                     Toast.makeText(
                                         itemView.context,
                                         R.string.vote_notsent.toString(),
                                         Toast.LENGTH_LONG
                                     ).show()
                                 })

                                 binding.downvote.setImageResource(R.drawable.ic_baseline_thumb_down_alt_24)
                             } else {
                                 removeDownvote(newArticle.urlArticle)
                             }
                         }
                     } else {
                         removeUpvote(newArticle.urlArticle)
                     }
                 }
             }

             // Comentarios.
             DbFirestore.getCommentCount(newArticle.urlArticle) {
                 val commentCount = it.result.count
                 if (commentCount != 0L) {
                     binding.commentLayout.visibility = VISIBLE
                     binding.totalComments.text = commentCount.toString()
                 }
             }
         }

        private fun removeUpvote(urlArticle : String) {
            DbFirestore.removeUpvote(urlArticle, LoginData.id, {
                binding.upvote.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_24)
                updateVotes(urlArticle)
            }, {
                Toast.makeText(
                    itemView.context,
                    R.string.vote_notremoved.toString(),
                    Toast.LENGTH_LONG
                ).show()
            })
        }

        private fun removeDownvote(urlArticle : String) {
            DbFirestore.removeDownvote(urlArticle, LoginData.id, {
                binding.downvote.setImageResource(R.drawable.ic_baseline_thumb_down_off_alt_24)
                updateVotes(urlArticle)
            }, {
                Toast.makeText(
                    itemView.context,
                    R.string.vote_notremoved.toString(),
                    Toast.LENGTH_LONG
                ).show()
            })
        }

        private fun updateVotes(urlArticle: String) {
            DbFirestore.getUpvoteCount(urlArticle) { it ->
                val upvoteCount = it.result.count;
                DbFirestore.getDownvoteCount(urlArticle) { ut ->
                    val totalVotes = upvoteCount - ut.result.count

                    if (totalVotes < 0) {
                        binding.totalVotes.setTextColor(Color.RED)
                    } else {
                        binding.totalVotes.setTextColor(Color.GRAY)
                    }

                    binding.totalVotes.text = totalVotes.toString()
                }
            }
        }
    }
}