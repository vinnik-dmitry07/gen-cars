package edu.ttp.gengame

import java.util.HashMap
import kotlin.math.floor

object GenerationConfig {
    val schema = CarSchema.Schema
    internal val generationSize = 20
    internal val championLength = 1
    internal val mutation_range = 1
    internal val gen_mutation = 0.05
    private val currentChoices = HashMap<Int, Pick>()

    private val nAttributes = 15

    //function simpleSelect(parents){
    //  var totalParents = parents.length
    //  var r = Math.random();
    //  if (r == 0)
    //    return 0;
    //  return Math.floor(-Math.log(r) * totalParents) % totalParents;
    //}
    //
    //function selectFromAllParents(parents, parentList, previousParentIndex) {
    //  var previousParent = parents[previousParentIndex];
    //  var validParents = parents.filter(function(parent, i){
    //    if(previousParentIndex === i){
    //      return false;
    //    }
    //    if(!previousParent){
    //      return true;
    //    }
    //    var child = {
    //      id: Math.random().toString(32),
    //      ancestry: [previousParent, parent].map(function(p){
    //        return {
    //          id: p.def.id,
    //          ancestry: p.def.ancestry
    //        }
    //      })
    //    }
    //    var iCo = getInbreedingCoefficient(child);
    //    console.log("inbreeding coefficient", iCo)
    //    if(iCo > 0.25){
    //      return false;
    //    }
    //    return true;
    //  })
    //  if(validParents.length === 0){
    //    return Math.floor(Math.random() * parents.length)
    //  }
    //  var totalScore = validParents.reduce(function(sum, parent){
    //    return sum + parent.score.v;
    //  }, 0);
    //  var r = totalScore * Math.random();
    //  for(var i = 0; i < validParents.length; i++){
    //    var score = validParents[i].score.v;
    //    if(r > score){
    //      r = r - score;
    //    } else {
    //      break;
    //    }
    //  }
    //  return i;
    //}
    internal fun selectFromAllParents(parents: Array<CarRunner>, parentList: List<IntArray>, previousParentIndex: Int?): Int {
        val totalParents = parents.size
        val r = Game.random.nextDouble()
        return if (r == 0.0) 0 else Math.floor(-Math.log(r) * totalParents).toInt() % totalParents
    }

    internal fun pickParent(chooseId: Int, key: String, parents: Array<Def>): Int {
        if (!currentChoices.containsKey(chooseId)) {
            currentChoices[chooseId] = initializePick()
        }
        // console.log(chooseId);
        val state = currentChoices[chooseId]
        // console.log(state.curparent);
        state!!.i++

        if (key == "wheel_radius" || key == "wheel_vertex" || key == "wheel_density") {
            state.curparent = cw_chooseParent(state)
            return state.curparent
        }
        state.curparent = cw_chooseParent(state)
        return state.curparent
    }

    private fun cw_chooseParent(state: Pick): Int {
        val curparent = state.curparent
        val attributeIndex = state.i
        val swapPoint1 = state.swapPoint1
        val swapPoint2 = state.swapPoint2
        // console.log(swapPoint1, swapPoint2, attributeIndex)
        return if (swapPoint1 == attributeIndex || swapPoint2 == attributeIndex) {
            if (curparent == 1) 0 else 1
        } else curparent
    }

    internal class Pick(var curparent: Int, var i: Int, val swapPoint1: Int, val swapPoint2: Int)

    private fun initializePick(): Pick {
        val curparent = 0

        val swapPoint1 = floor(Game.random.nextDouble() * nAttributes).toInt()
        var swapPoint2 = swapPoint1
        while (swapPoint2 == swapPoint1) {
            swapPoint2 = floor(Game.random.nextDouble() * nAttributes).toInt()
        }
        val i = 0
        return Pick(curparent, i, swapPoint1, swapPoint2)
    }

    internal fun generateRandom(): Double {
        return Game.random.nextDouble()
    }

}

