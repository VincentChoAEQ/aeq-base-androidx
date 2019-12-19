package ca.aequilibrium.base.ui.transformer.details

import androidx.lifecycle.*
import ca.aequilibrium.base.data.RepositoryOperationError
import ca.aequilibrium.base.data.TransformerRepository
import ca.aequilibrium.base.vo.transformer.Transformer
import kotlinx.coroutines.launch

class DetailsViewModel constructor(val repository: TransformerRepository) : ViewModel() {

    lateinit var transformer : Transformer

    private val _spinner = MutableLiveData<Boolean>(false)
    val spinner: LiveData<Boolean>
        get() = _spinner

    private val _snackBar = MutableLiveData<String?>()
    val snackbar: LiveData<String?>
        get() = _snackBar

    fun onSnackbarShown() {
        _snackBar.value = null
    }

    companion object {
        const val STATE_CREATE = 0
        const val STATE_VIEW = 1
        const val STATE_EDIT = 2
    }

    fun createTransformer(t: Transformer){
        launchCoroutine("Saved"){
            repository.createTransformer(t)
        }
    }

    fun updateTransformer(t: Transformer){
        launchCoroutine("Saved"){
            repository.updateTransformer(t)
        }
    }

    fun deleteTransformer(t: Transformer){
        launchCoroutine("Deleted"){
            repository.deleteTransformer(t)
        }
    }

    private fun launchCoroutine(mesg: String, block: suspend () -> Unit): Unit {
        viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: RepositoryOperationError) {
                _snackBar.value = error.message
            } finally {
                _snackBar.value = mesg
                _spinner.value = false
            }
        }
    }

    val state = MediatorLiveData<Int>()

    fun start() {
        state.value = if (transformer.id.isNullOrEmpty()) STATE_CREATE else STATE_VIEW
    }

}
