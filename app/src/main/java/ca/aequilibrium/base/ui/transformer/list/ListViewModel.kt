package ca.aequilibrium.base.ui.transformer.list

import androidx.lifecycle.ViewModel
import ca.aequilibrium.base.data.TransformerRepository

class ListViewModel(repository: TransformerRepository) : ViewModel() {
    val transformers = repository.transformers
}
