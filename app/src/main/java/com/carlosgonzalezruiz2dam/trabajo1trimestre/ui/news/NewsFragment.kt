package com.carlosgonzalezruiz2dam.trabajo1trimestre.ui.news

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.carlosgonzalezruiz2dam.trabajo1trimestre.R
import com.carlosgonzalezruiz2dam.trabajo1trimestre.databinding.FragmentNewsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import java.util.*

/**
 * Función para obtener la fecha actual en el formate AÑO-MES-DÍA.
 *
 * @return String
 */
fun getCurrentDate() : String {
    val c = Calendar.getInstance()

    return  c.get(Calendar.YEAR).toString() + "-" +
            c.get(Calendar.MONTH).toString() + "-" +
            c.get(Calendar.DAY_OF_MONTH).toString()
}

class NewsFragment : Fragment(R.layout.fragment_news) {

    private lateinit var binding: FragmentNewsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var viewPager2Adapter : ViewPager2Adapter

        binding = FragmentNewsBinding.bind(view).apply {
            viewPager2Adapter = ViewPager2Adapter(this@NewsFragment)
        }

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.latest_news)

        binding.viewPager2.adapter = viewPager2Adapter
        binding.viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.getTabAt(position)?.select()
            }
        })

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let {
                    binding.viewPager2.currentItem = it
                }
            }

            override fun onTabUnselected(tab: Tab?) {

            }

            override fun onTabReselected(tab: Tab?) {

            }
        })

        binding.signout.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient((requireActivity() as AppCompatActivity), googleConf)
            googleClient.signOut()

            findNavController().navigate(
                R.id.action_newsFragment_to_mainFragment
            )
        }
    }

}
