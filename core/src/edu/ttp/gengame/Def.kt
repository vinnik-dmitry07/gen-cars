package edu.ttp.gengame

class Def {
    internal var index: Int = 0
    internal val id: Int
    internal lateinit var ancestry: List<Def>

    lateinit var wheel_radius: List<Double>
    lateinit var wheel_density: List<Double>
    lateinit var wheel_vertex: List<Double>
    lateinit var chassis_density: List<Double>
    lateinit var vertex_list: List<Double>

    internal var is_elite: Boolean = false

    internal constructor(id: Int) {
        this.id = id
    }

    internal constructor(id: Int, ancestry: List<Def>) {
        this.id = id
        this.ancestry = ancestry
    }
}
