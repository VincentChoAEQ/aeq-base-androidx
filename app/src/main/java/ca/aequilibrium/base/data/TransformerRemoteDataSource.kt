package ca.aequilibrium.base.data

import ca.aequilibrium.base.networking.TransformerService
import ca.aequilibrium.base.vo.transformer.Transformer


class TransformerRemoteDataSource constructor(private val service: TransformerService) : BaseDataSource() {

    suspend fun fetchTransfomrers()
            = getResult { service.getAll() }

    suspend fun updateTransformer(t: Transformer) = service.update(t)
    suspend fun addTransformer(t: Transformer) = service.add(t)
    suspend fun deleteTransformer(id: String) = service.delete(id)
}