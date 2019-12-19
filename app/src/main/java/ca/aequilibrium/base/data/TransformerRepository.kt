package ca.aequilibrium.base.data

import ca.aequilibrium.base.vo.transformer.Transformer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransformerRepository constructor(private val dao: TransformerDao,
                                        private val transformerRemoteDataSource: TransformerRemoteDataSource) {

    var transformers = resultLiveData(
        databaseQuery = { dao.getAll() },
        networkCall = { transformerRemoteDataSource.fetchTransfomrers() },
        saveCallResult = { dao.insertTransformers(it.transformers) })

    suspend fun getTransformer(id: String): Transformer? {
        return dao.getTransformerById(id).value
    }

    suspend fun updateTransformer(t: Transformer){
        operate {
            val result = transformerRemoteDataSource.updateTransformer(t)
            if (result.isSuccessful){
                dao.updateTransformer(t)
            }
        }
    }

    suspend fun createTransformer(t: Transformer){
        operate{
            val result = transformerRemoteDataSource.addTransformer(t)
            if(result.isSuccessful) {
                dao.insertTransformer(result.body() as Transformer)
            }
        }
    }

    suspend fun deleteTransformer(t: Transformer){
        operate{
            val result = transformerRemoteDataSource.deleteTransformer(t.id)
            if(result.isSuccessful) {
                dao.delete(t)
            }
        }
    }

    suspend private fun operate(block: suspend() -> Unit): Unit {
        withContext(Dispatchers.IO) {
            try {
                block()
            } catch (error: Throwable) {
                throw RepositoryOperationError("Unable to refresh title", error)
            }
        }
    }
}

/**
 * Thrown when there was a error fetching a new title
 *
 * @property message user ready error message
 * @property cause the original cause of this exception
 */
class RepositoryOperationError(message: String, cause: Throwable) : Throwable(message, cause)