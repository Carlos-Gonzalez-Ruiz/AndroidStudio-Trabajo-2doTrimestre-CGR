package com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.detail

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carlosgonzalezruiz2dam.trabajo1trimestre.R
import com.carlosgonzalezruiz2dam.trabajo1trimestre.databinding.FragmentDetailBinding
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.CommentsData
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.New
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server.DbFirestore
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server.LoginData
import com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.comment.CommentsFragment
import com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.news.NewsFragment

class DetailFragment : Fragment(R.layout.fragment_detail) {
    private  val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(arguments?.getParcelable<New>(EXTRA_NEW)!!)
    }
    companion object{
        const val EXTRA_NEW = "DetailActivity:New"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDetailBinding.bind(view)

        viewModel.newArticle.observe(viewLifecycleOwner){ newArticle ->
            (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (requireActivity() as AppCompatActivity).supportActionBar?.title = newArticle.title

            binding.goBack.setOnClickListener {
                findNavController().popBackStack()
            }
            binding.goComments.setOnClickListener {
                val commentsData = CommentsData(newArticle.urlArticle, newArticle.title)
                findNavController().navigate(
                    R.id.action_detailFragment_to_commentsFragment,
                    bundleOf(CommentsFragment.EXTRA_COMMENTSDATA to commentsData)
                )
            }

            // Crear WebView
            binding.webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    // Mostrar cargando
                    binding.progress.visibility = View.VISIBLE;
                    // Esconder WebView mientras carga
                    binding.webView.visibility = View.GONE;
                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // Esconder cargando
                    binding.progress.visibility = View.GONE;
                    // Mostrar WebView tras cargar
                    binding.webView.visibility = View.VISIBLE;
                }
            }
            // URL de la noticia
            binding.webView.loadUrl(newArticle.urlArticle)
            // Permitir Zoom
            binding.webView.settings.setSupportZoom(true)
            // No permitir Javascript, ya que no resultan necesarios para visualizar articulos
            binding.webView.settings.javaScriptEnabled = false


            // Votos
            updateVotes(binding, newArticle.urlArticle)

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
                                    updateVotes(binding, newArticle.urlArticle)
                                }, {
                                    Toast.makeText(
                                        (requireActivity() as AppCompatActivity),
                                        R.string.vote_notsent.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                })

                                binding.upvote.setImageResource(R.drawable.ic_baseline_thumb_up_24)
                            } else {
                                removeDownvote(binding, newArticle.urlArticle)
                            }
                        }
                    } else {
                        removeUpvote(binding, newArticle.urlArticle)
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
                                    updateVotes(binding, newArticle.urlArticle)
                                }, {
                                    Toast.makeText(
                                        (requireActivity() as AppCompatActivity),
                                        R.string.vote_notsent.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                })

                                binding.downvote.setImageResource(R.drawable.ic_baseline_thumb_down_alt_24)
                            } else {
                                removeDownvote(binding, newArticle.urlArticle)
                            }
                        }
                    } else {
                        removeUpvote(binding, newArticle.urlArticle)
                    }
                }
            }
        }
    }

    private fun removeUpvote(binding: FragmentDetailBinding, urlArticle : String) {
        DbFirestore.removeUpvote(urlArticle, LoginData.id, {
            binding.upvote.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_24)
            updateVotes(binding, urlArticle)
        }, {
            Toast.makeText(
                (requireActivity() as AppCompatActivity),
                R.string.vote_notremoved.toString(),
                Toast.LENGTH_LONG
            ).show()
        })
    }

    private fun removeDownvote(binding: FragmentDetailBinding, urlArticle : String) {
        DbFirestore.removeDownvote(urlArticle, LoginData.id, {
            binding.downvote.setImageResource(R.drawable.ic_baseline_thumb_down_off_alt_24)
            updateVotes(binding, urlArticle)
        }, {
            Toast.makeText(
                (requireActivity() as AppCompatActivity),
                R.string.vote_notremoved.toString(),
                Toast.LENGTH_LONG
            ).show()
        })
    }

    private fun updateVotes(binding: FragmentDetailBinding, urlArticle: String) {
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

