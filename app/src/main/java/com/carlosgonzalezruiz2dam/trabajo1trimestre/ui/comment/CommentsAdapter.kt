package com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.comment

import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.carlosgonzalezruiz2dam.trabajo1trimestre.R
import com.carlosgonzalezruiz2dam.trabajo1trimestre.databinding.ViewCommentBinding
import com.carlosgonzalezruiz2dam.trabajo1trimestre.inflate
import com.carlosgonzalezruiz2dam.trabajo1trimestre.loadUrl
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.Comment
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server.DbFirestore
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server.LoginData

class CommentsAdapter(val listener : (Comment) -> Unit, val onDeleteComment : (Comment) -> Unit):
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    var comments = mutableListOf<Comment>()

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int): ViewHolder {
        val view = parent.inflate(R.layout.view_comment, false)
        return ViewHolder(view, onDeleteComment)
    }

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        val comment = comments[position]
        holder.bind(comment)

        holder.itemView.setOnClickListener {
            listener(comment)
        }
    }

    override fun getItemCount() : Int = comments.size

    class ViewHolder(view : View, val onDeleteComment : (Comment) -> Unit) : RecyclerView.ViewHolder(view){
        private val binding = ViewCommentBinding.bind(view)
        fun bind(comment : Comment){
            // Datos del comentario.
            if (comment.photoUrl == "") {
                binding.cardView.visibility = View.GONE
            } else {
                comment.photoUrl?.let { binding.photo.loadUrl(it) }
            }

            binding.displayName.text = comment.displayName
            binding.content.text = comment.content
            binding.postedAt.text = comment.postedAt

            // Eliminar comentario.
            if (comment.userId == LoginData.id) {
                binding.deleteComment.visibility = VISIBLE;
                binding.deleteComment.setOnClickListener {
                    onDeleteComment(comment)
                }
            }
        }
    }
}