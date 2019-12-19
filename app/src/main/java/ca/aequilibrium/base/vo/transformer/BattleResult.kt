package ca.aequilibrium.base.vo.transformer

/**
 * Created by Chris on 2018-10-01.
 */
data class BattleResult(
    val lhs: Transformer,
    val rhs: Transformer,
    var result: String
)