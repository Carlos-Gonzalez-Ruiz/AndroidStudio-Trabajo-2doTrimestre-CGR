package com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.carlosgonzalezruiz2dam.trabajo1trimestre.R
import com.carlosgonzalezruiz2dam.trabajo1trimestre.databinding.FragmentMainBinding
import com.carlosgonzalezruiz2dam.trabajo1trimestre.model.server.LoginData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainFragment : Fragment(R.layout.fragment_main) {

    private val GOOGLE_SIGN_IN = 100

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMainBinding.bind(view)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.log_in)

        // Si el usuario ya ha iniciado sesión antes, llevar directamente a las noticias.
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if (account != null) {
            goNews(account.id, account.email, account.displayName, account.photoUrl)
        }

        binding.signInButton.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient((requireActivity() as AppCompatActivity), googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)

            // goNews("", "", "", null)
            // Añadir a rules "request.auth != null;" en podrucción.
            // https://stackoverflow.com/questions/46590155/firestore-permission-denied-missing-or-insufficient-permissions
        }
    }

    private fun showError() {
        // Indicar que ha fallado.
        Toast.makeText(this.context, R.string.could_not_login, Toast.LENGTH_SHORT).show()
    }

    private fun goNews(id : String?, email : String?, displayName : String?, photoUrl : Uri?) {
        if (id != null) {
            LoginData.id = id
        }
        if (email != null) {
            LoginData.email = email
        }
        if (displayName != null) {
            LoginData.displayName = displayName
        }
        if (photoUrl != null) {
            LoginData.photoUrl = photoUrl.toString()
        } else {
            LoginData.photoUrl = Uri.encode("urlimagen.com/test.cpp")
        }

        findNavController().navigate(
            R.id.action_mainFragment_to_newsFragment
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            goNews( account.id,
                                    account.email,
                                    account.displayName,
                                    account.photoUrl)
                        } else {
                            showError()
                        }
                    }
                }
            } catch (e: ApiException) {
                showError()
            }
        }
    }
}