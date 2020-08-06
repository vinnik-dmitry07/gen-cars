package edu.ttp.gengame

class Def {
    var index: Int = 0
    val id: Int
    var ancestry: List<Def> = emptyList()

    lateinit var wheelRadius: DoubleArray
    lateinit var wheelDensity: DoubleArray
    lateinit var wheelVertex: DoubleArray
    lateinit var chassisDensity: DoubleArray
    lateinit var vertexList: DoubleArray

    var isElite: Boolean = false

    constructor(id: Int) {
        this.id = id
    }

    constructor(id: Int, ancestry: List<Def>) {
        this.id = id
        //this.ancestry = ancestry
    }
}
