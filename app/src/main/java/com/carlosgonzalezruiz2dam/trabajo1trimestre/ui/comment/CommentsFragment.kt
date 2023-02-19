package com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.comment

import android.content.Context
import com.carlosgonzalezruiz2dam.trabajo1trimestre.databinding.FragmentCommentsBinding
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.CommentsData

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carlosgonzalezruiz2dam.trabajo1trimestre.R
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.Comment
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server.DbFirestore
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server.LoginData
import com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.detail.DetailFragment
import java.text.SimpleDateFormat
import java.util.*

class CommentsFragment : Fragment(R.layout.fragment_comments) {
    private val viewModel: CommentsViewModel by viewModels {
        CommentsViewModelFactory(arguments?.getParcelable<CommentsData>(EXTRA_COMMENTSDATA)!!)
    }
    companion object{
        const val EXTRA_COMMENTSDATA = "CommentsActivity:CommentsData"
    }
    private lateinit var binding : FragmentCommentsBinding
    private val adapter = CommentsAdapter(
        { comment ->
            viewModel.navigateTo(comment)
        },
        { comment ->
            removeComment(comment)
        }
    )

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCommentsBinding.bind(view).apply {
            recycler.adapter = adapter
        }

        binding.goBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendComment.setOnClickListener {
            // Crear comentario.
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val current = formatter.format(time)
            val comment = Comment(
                "",
                LoginData.id,
                LoginData.email,
                LoginData.displayName,
                binding.editTextComment.text.toString(),
                LoginData.photoUrl,
                current,
                arguments?.getParcelable<CommentsData>(EXTRA_COMMENTSDATA)!!.urlArticle
            )

            // Enviar comentario.
            DbFirestore.writeComment(
                comment,
                {
                    Toast.makeText(requireActivity() as AppCompatActivity,
                        R.string.comment_sent,
                        Toast.LENGTH_LONG).show()

                    // Actualizar recycle view.
                    adapter.comments.add(comment)
                    adapter.notifyDataSetChanged()

                    // Econder el mensaje de no comentarios.
                    binding.noComments.visibility = GONE;
                },
                {
                    Toast.makeText(requireActivity() as AppCompatActivity,
                        R.string.comment_notsent,
                        Toast.LENGTH_LONG).show()
                }
            )

            // Salir de la caja de texto.
            binding.editTextComment.text.clear()
            binding.editTextComment.clearFocus()
            // Dejar de mostrar teclado.
            val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(binding.editTextComment.windowToken, 0)
        }

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = arguments?.getParcelable<CommentsData>(EXTRA_COMMENTSDATA)!!.title

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.visibility = if (state.loading) VISIBLE else GONE
            state.comments?.let {
                adapter.comments = state.comments
                adapter.notifyDataSetChanged()
            }

            state.navigateTo?.let {
                findNavController().navigate(
                    R.id.action_newsFragment_to_detailFragment,
                    bundleOf(DetailFragment.EXTRA_NEW to it)
                )
                viewModel.onNavigateDone()
            }

            // Comentarios.
            DbFirestore.getCommentCount(arguments?.getParcelable<CommentsData>(EXTRA_COMMENTSDATA)!!.urlArticle) {
                val commentCount = it.result.count
                if (commentCount == 0L) {
                    binding.noComments.text = getString(R.string.no_comments)
                    binding.noComments.visibility = VISIBLE;
                }
            }
        }
    }

    private fun removeComment(comment : Comment) {
        DbFirestore.removeComment(comment, {
            adapter.comments.remove(comment)
            adapter.notifyDataSetChanged()
            Toast.makeText( requireActivity() as AppCompatActivity,
                R.string.comment_removed,
                Toast.LENGTH_LONG).show()
        }, {
            Toast.makeText( requireActivity() as AppCompatActivity,
                R.string.comment_notremoved,
                Toast.LENGTH_LONG).show()
        })

    }
}
