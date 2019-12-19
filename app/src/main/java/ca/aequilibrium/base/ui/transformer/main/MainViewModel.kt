package ca.aequilibrium.base.ui.transformer.main

import androidx.lifecycle.ViewModel
import ca.aequilibrium.base.data.TransformerRepository

/**
 * Created by Chris on 2018-09-30.
 */
class MainViewModel(repository: TransformerRepository) : ViewModel() {
    val transformers = repository.transformers
}