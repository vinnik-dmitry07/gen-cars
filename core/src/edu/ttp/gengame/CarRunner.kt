package edu.ttp.gengame

class CarRunner constructor(@JvmField val index: Int,
                            @JvmField val def: Def,
                            @JvmField val car: CarSchema.Car,
                            @JvmField var state: CarSchema.Run.State) {
    lateinit var score: CarSchema.Run.Score
}
