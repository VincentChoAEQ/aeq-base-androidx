package ca.aequilibrium.base.ui.transformer.battle

import androidx.lifecycle.ViewModel
import ca.aequilibrium.base.vo.transformer.Battle
import ca.aequilibrium.base.vo.transformer.Transformers

/**
 * Created by Chris on 2018-10-01.
 */
class BattleViewModel() : ViewModel() {

    lateinit var battle: Battle

    fun start(transformers : Transformers){
        battle = Battle(transformers.transformers)
    }



}