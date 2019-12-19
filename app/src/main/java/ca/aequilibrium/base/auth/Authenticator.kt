/**
 * Created by Vincent Cho on 1/9/20.
 * Copyright (c) 2019 aequilibrium LLC. All rights reserved.
 */
package ca.aequilibrium.base.auth

import android.content.Intent
import ca.aequilibrium.base.R
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.IdentityProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.orhanobut.logger.Logger
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.Tasks
import io.reactivex.Single

class Authenticator constructor(val context: Context) {

    var googleSignInClient : GoogleSignInClient? = null

    fun getGoogleSignedInAccountFromIntent(data: Intent?) : Task<GoogleSignInAccount>{
        return GoogleSignIn.getSignedInAccountFromIntent(data)
    }

    fun getGoogleSignInIntent(): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(context.getString(R.string.server_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
        return googleSignInClient?.getSignInIntent()!!
    }

    fun signInWithPassword(email: String, password: String?): Single<Unit> {
        return Single.fromCallable {
            val task = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password!!)

            Tasks.await(task)
            if (task.isSuccessful) {
                handleAuthToken()
                Crashlytics.setUserEmail(email)
            } else {
                throw task.exception ?: Throwable(context.getString(R.string.err_fail_to_signin))
            }
        }
    }

    fun signInWithCredential(credential: Credential): Single<Unit> {
        val accountType = credential.getAccountType()
        if (accountType == null) {
            // Sign the user in with information from the Credential.
            return signInWithPassword(credential.getId(), credential.getPassword())
        } else if (accountType == IdentityProviders.GOOGLE) {
            return Single.fromCallable {
                // The user has previously signed in with Google Sign-In. Silently
                // sign in the user with the same ID.
                // See https://developers.google.com/identity/sign-in/android/
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(context.getString(R.string.server_client_id))
                    .setAccountName(credential.id)
                    .build()

                googleSignInClient = GoogleSignIn.getClient(context, gso)
                val task = googleSignInClient?.silentSignIn()

                task?.let{
                    Tasks.await(task)
                    if (task.isSuccessful) {
                        Logger.d(context.getString(R.string.notify_succeed_to_signin))
                        val acct = GoogleSignIn.getLastSignedInAccount(context)
                        if (acct != null) {
                            Crashlytics.setUserEmail(acct.email)
                        }
                        handleAuthToken()
                        return@fromCallable

                    } else {
                        Logger.d(context.getString(R.string.err_fail_to_signin), task.getException())
                        throw task.exception ?: Throwable(context.getString(R.string.err_fail_to_signin))
                    }
                }

                throw Throwable(context.getString(R.string.err_fail_to_signin))
            }
        } else {
            return Single.fromCallable {
                throw Throwable(context.getString(R.string.err_fail_to_signin))
            }
        }
    }

    fun handleAuthToken() {
        //handle auth token from server
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Logger.w(context.getString(R.string.msg_token_fail), task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                Crashlytics.setUserIdentifier(token)
                Logger.d(context.getString(R.string.msg_token_fmt, token))
            })
    }

    fun signOut(){
        FirebaseAuth.getInstance().signOut()
        googleSignInClient?.let {
            it.signOut()
            it.revokeAccess()
        }
    }
}