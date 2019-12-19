package ca.aequilibrium.base.di

val appComponent= listOf(applicationModules, createRemoteModule("https://transformers-api.firebaseapp.com"), localModule, repositoryModule, viewModelModule)