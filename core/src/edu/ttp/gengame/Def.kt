package edu.ttp.gengame

class Def {
     var index: Int = 0
     val id: Int
     var ancestry: List<Def> = listOfNotNull()

    lateinit var wheelRadius: List<Double>
    lateinit var wheelDensity: List<Double>
    lateinit var wheelVertex: List<Double>
    lateinit var chassisDensity: List<Double>
    lateinit var vertexList: List<Double>

     var isElite: Boolean = false

     constructor(id: Int) {
        this.id = id
    }

    constructor(id: Int, ancestry: List<Def>) {
        this.id = id
        this.ancestry = ancestry
    }
}
