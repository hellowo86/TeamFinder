package com.hellowo.teamfinder.ui.activity

import android.support.v7.app.AppCompatActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.View

import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        initLayout()
        initObserve()
    }

    private fun initLayout() {
        loginBtn.setOnClickListener { viewModel.signIn(emailEdit.text.toString(), passwordEdit.text.toString()) }
    }

    private fun initObserve() {
        MeLiveData.observe(this, Observer { user ->
            if (user != null) {
                MeLiveData.removeObservers(this)
                /*
                val mainIntent = Intent(this, MainActivity::class.java)
                intent.extras?.let { mainIntent.putExtras(it) }
                startActivity(mainIntent)
                finish()*/
            } else {
                //startActivity(Intent(this, SignInActivity::class.java))
                //finish()
            }
        })

        viewModel.signInStatus.observe(this, Observer{ status ->
            when (status) {
                SplashViewModel.SignInStatus.InvalidEmail -> emailEdit.error = getString(R.string.invalid_email)
                SplashViewModel.SignInStatus.InvalidPassword -> passwordEdit.error = getString(R.string.invalid_password)
                else -> {}
            }
        })

        viewModel.loading.observe(this, Observer{ status ->
            progressBar.visibility = if (status as Boolean) View.VISIBLE else View.INVISIBLE
            loginBtn.visibility = if (status) View.INVISIBLE else View.VISIBLE
        })
    }
}
