package ca.aequilibrium.base.di

import ca.aequilibrium.base.data.AppDatabase
import ca.aequilibrium.base.data.TransformerRepository
import ca.aequilibrium.base.ui.AppViewModel
import ca.aequilibrium.base.ui.transformer.battle.BattleViewModel
import ca.aequilibrium.base.ui.transformer.details.DetailsViewModel
import ca.aequilibrium.base.ui.transformer.list.ListViewModel
import ca.aequilibrium.base.ui.transformer.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DATABASE = "DATABASE"

val localModule = module {
    single(named(DATABASE)) { AppDatabase.buildDatabase(androidContext()) }
    factory { (get(named(DATABASE)) as AppDatabase).transformerDao() }
}

val repositoryModule = module {
    single {
        TransformerRepository(get(), get())
    }
}

val viewModelModule = module {
    viewModel { DetailsViewModel(get()) }
    viewModel { ListViewModel(get()) }
    viewModel { BattleViewModel() }
    viewModel { MainViewModel(get()) }
    viewModel { AppViewModel(get()) }
}