/**
 * Created by Vincent Cho on 1/9/20.
 * Copyright (c) 2019 aequilibrium LLC. All rights reserved.
 */
package ca.aequilibrium.base.auth

import android.content.Context
import com.google.android.gms.auth.api.credentials.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Tasks
import io.reactivex.Single

class CredentialKeeper constructor(val context: Context) {
    private lateinit var credentialsClient : CredentialsClient
    private lateinit var credential: Credential

    fun saveCredential(id: String, password: String): Single<Unit> {
        credential = Credential.Builder(id)
            .setPassword(password)
            .build()

        return saveCredential(credential)
    }

    fun saveCredential(googleSignInAccount: GoogleSignInAccount): Single<Unit> {
        val credential = Credential.Builder(googleSignInAccount.getEmail())
            .setAccountType(IdentityProviders.GOOGLE)
            .setName(googleSignInAccount.getDisplayName())
            .setProfilePictureUri(googleSignInAccount.getPhotoUrl())
            .build();

        return saveCredential(credential)
    }

    fun saveCredential(credential: Credential): Single<Unit> {
        return Single.fromCallable {
            credentialsClient = Credentials.getClient(context)

            this.credential = credential

            val task = credentialsClient.save(credential)
            Tasks.await(task)
            if (task.isSuccessful) {
                return@fromCallable
            } else {
                throw task.exception ?: Throwable("Failed to save credential")
            }
        }
    }

    fun retrieveCredential(): Single<Credential> {
        return Single.fromCallable {
            val options = CredentialsOptions.Builder().forceEnableSaveDialog().build()
            credentialsClient = Credentials.getClient(context, options)

            val credentialRequest = CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .setAccountTypes(IdentityProviders.GOOGLE)
                .build()

            val task = credentialsClient.request(credentialRequest)
            val result = Tasks.await(task)
            if (task.isSuccessful) {
                credential = result.getCredential()
                return@fromCallable credential
            }
            else
                throw task.exception ?: Throwable("Failed to retrieve credential")
        }
    }

    fun revokeCurrentCredential(){
        credentialsClient.disableAutoSignIn()
        credentialsClient.delete(credential)
    }
}