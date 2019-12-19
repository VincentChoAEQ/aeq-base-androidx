/**
 * Created by Vincent Cho on 12/19/19.
 * Copyright (c) 2019 aequilibrium LLC. All rights reserved.
 */
package ca.aequilibrium.base.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.aequilibrium.base.App
import ca.aequilibrium.base.R
import ca.aequilibrium.base.auth.Authenticator
import ca.aequilibrium.base.auth.CredentialKeeper
import ca.aequilibrium.base.util.ActivityUtils
import ca.aequilibrium.base.util.Preferences
import ca.aequilibrium.base.util.Version
import com.google.android.gms.common.api.ResolvableApiException
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SplashActivity : AppCompatActivity() {

    val authenticator : Authenticator by inject { parametersOf(this) }
    val credentialKeeper : CredentialKeeper by inject { parametersOf(this) }
    private val viewModel : AppViewModel by viewModel()
    val uiScope = CoroutineScope(Dispatchers.Main)

    fun getActivity() : Activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Preferences.theme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkVersionAndSignIn()
    }

    @SuppressLint("CheckResult")
    private fun checkVersionAndSignIn() =
        uiScope.launch{
            val version = viewModel.getVersion()
            val currentVersion = "2.0.0"//BuildConfig.VERSION_NAME
            if (Version(currentVersion) < Version(version)) {
                ActivityUtils.showForcedUpgradeDialog(getActivity() as Context)
            } else {
                //credentialKeeper.retrieveCredential(getCredentialCallback)
                credentialKeeper.retrieveCredential()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            authenticator.signInWithCredential(it)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({showMainActivity()},{ showLoginSelectionActivity()})
                        },
                        {
                            if (it is ResolvableApiException && it.statusCode != 4) {
                                try {
                                    it.startResolutionForResult(getActivity(), App.RC_READ)
                                } catch (e: IntentSender.SendIntentException) {
                                    Logger.e(e, getString(R.string.err_fail_to_send_resolution))
                                    showLoginSelectionActivity()
                                }
                            } else {
                                // The user must create an account or sign in manually.
                                Logger.e(it, getString(R.string.err_fail_to_get_credential))
                                Toast.makeText(getActivity(), getString(R.string.err_fail_to_get_credential), Toast.LENGTH_SHORT).show()
                                showLoginSelectionActivity()
                            }
                        })
            }
        }

    private fun showLoginSelectionActivity(){
        SignInActivity.startActivity(this)
        finish()
    }

    private fun showMainActivity(){
        MainActivity.startActivity(this)
        finish()
    }
}