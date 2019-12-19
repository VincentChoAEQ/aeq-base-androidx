/**
 * Created by Vincent Cho on 12/19/19.
 * Copyright (c) 2019 aequilibrium LLC. All rights reserved.
 */
package ca.aequilibrium.base.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ca.aequilibrium.base.App
import ca.aequilibrium.base.R
import ca.aequilibrium.base.auth.Authenticator
import ca.aequilibrium.base.auth.CredentialKeeper
import ca.aequilibrium.base.databinding.FragmentLoginSelectionBinding
import ca.aequilibrium.base.ui.SignInActivity
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login_selection.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class LoginSelectionFragment : Fragment() {
    val authenticator : Authenticator by inject { parametersOf(activity) }
    val credentialKeeper : CredentialKeeper by inject { parametersOf(activity) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = FragmentLoginSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonEmailSignIn.setOnClickListener {
            showEmailLoginFragment()
        }

        buttonGoogleSignIn.setOnClickListener {
            googleSignIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == App.RC_GOOGLE_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val task = authenticator.getGoogleSignedInAccountFromIntent(data)
                task.result?.let {
                    credentialKeeper.saveCredential(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                Logger.d(getString(R.string.notify_succeed_to_save))
                            },
                            {
                                Toast.makeText(activity, getString(R.string.err_fail_to_save), Toast.LENGTH_SHORT).show()
                            })
                }
                showMainActivity()
            }
            else {
                //should not reach here
                assert(false)
            }
        }
    }

    private fun googleSignIn(){
        val signInIntent = authenticator.getGoogleSignInIntent()
        activity?.startActivityForResult(signInIntent, App.RC_GOOGLE_SIGN_IN)
    }

    private fun showMainActivity(){
        (activity as SignInActivity).showMainActivity()
    }

    private fun showEmailLoginFragment() {
        (activity as SignInActivity).showEmailLoginFragment()
    }

}