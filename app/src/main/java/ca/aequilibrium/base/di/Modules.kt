package ca.aequilibrium.base.di

import android.content.Context
import ca.aequilibrium.base.auth.Authenticator
import ca.aequilibrium.base.auth.CredentialKeeper
import org.koin.dsl.module

val applicationModules = module(override = true) {
    single<Authenticator> {(context: Context) -> Authenticator(context) }
    single<CredentialKeeper> {(context: Context) -> CredentialKeeper(context)}
}