/**
 * Created by Vincent Cho on 12/19/19.
 * Copyright (c) 2019 aequilibrium LLC. All rights reserved.
 */
package ca.aequilibrium.base.ui.login

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ca.aequilibrium.base.App
import ca.aequilibrium.base.R
import ca.aequilibrium.base.auth.Authenticator
import ca.aequilibrium.base.auth.CredentialKeeper
import ca.aequilibrium.base.databinding.FragmentEmailLoginBinding
import ca.aequilibrium.base.ui.SignInActivity
import ca.aequilibrium.base.util.ActivityUtils
import com.google.android.gms.common.api.ResolvableApiException
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_email_login.*
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import org.koin.core.parameter.parametersOf

class EmailLoginFragment : Fragment(), KoinComponent {
    val authenticator : Authenticator by inject { parametersOf(activity) }
    val credentialKeeper : CredentialKeeper by inject { parametersOf(activity) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = FragmentEmailLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar.visibility = View.GONE
        backButton.setOnClickListener{
            (activity as SignInActivity).onBackPressed()
        }

        forget_password_button.setOnClickListener{
            showForgetPasswordWebpage()
        }

        signup_button.setOnClickListener{
            showSignUpWebpage()
        }

        email_sign_in_button.setOnClickListener{
            onSigninButtonPressed()
        }
    }

    private fun onSigninButtonPressed(){
        progressBar.visibility = View.VISIBLE
        ActivityUtils.hideSoftInput(view)
        setUISigninButtonsOnOff(false)
        loginUserAccount()
    }

    @SuppressLint("CheckResult")
    private fun loginUserAccount() {
        val email = editLoginEmail.text.toString()
        val password = editLoginPassword.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(activity, getString(R.string.notify_enter_email), Toast.LENGTH_LONG).show();
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(activity, getString(R.string.notify_enter_password), Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.visibility = View.VISIBLE

        authenticator.signInWithPassword(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(activity, getString(R.string.notify_succeed_to_signin), Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                saveCredential()
                showMainActivity()
            },
            {
                Toast.makeText(activity, getString(R.string.err_fail_to_signin), Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                setUISigninButtonsOnOff(true)
            })

    }

    @SuppressLint("CheckResult")
    fun saveCredential() {
        val id = editLoginEmail.text.toString()
        val password = editLoginPassword.text.toString()
        credentialKeeper.saveCredential(id, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Logger.d(getString(R.string.notify_succeed_to_save))
                },
                {
                    if (it is ResolvableApiException) {
                        try {
                            it.startResolutionForResult(activity, App.RC_SAVE)
                        } catch (e: IntentSender.SendIntentException) {
                            Logger.e(e, getString(R.string.err_fail_to_send_resolution))
                            Toast.makeText(activity, getString(R.string.err_fail_to_send_resolution), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(activity, getString(R.string.err_fail_to_save), Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun setUISigninButtonsOnOff(onoff: Boolean){
        editLoginPassword.isEnabled = onoff
        editLoginEmail.isEnabled = onoff
        email_sign_in_button.isEnabled = onoff
    }

    private fun showSignUpWebpage(){
        try {
            val webpage = Uri.parse(getString(R.string.url_signup))
            val myIntent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, getString(R.string.err_no_browser_app), Toast.LENGTH_LONG).show()
        }
    }

    private fun showForgetPasswordWebpage(){
        try {
            val webpage = Uri.parse(getString(R.string.url_resetpassword))
            val myIntent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, getString(R.string.err_no_browser_app), Toast.LENGTH_LONG).show()
        }
    }

    private fun showMainActivity(){
        (activity as SignInActivity).showMainActivity()
    }
}