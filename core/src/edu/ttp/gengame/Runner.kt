package edu.ttp.gengame

class Runner {
    companion object {
        lateinit var scene: Scene
        lateinit var cars: List<CarRunner>

        fun updateDefs(defs: Array<Def>) {
            if (Game.WordDef.mutable_floor) {
                // GHOST DISABLED
                Game.WordDef.floorseed = Game.random.nextLong() // ???
            }

            scene = SetupScene.setupScene()
            scene.world.step(1.0.toFloat() / Game.WordDef.box2dfps, 20, 20)
            println("about to build cars")
            cars = defs.indices.map { i -> CarRunner(i, defs[i], CarSchema.DefToCar.defToCar(defs[i], scene.world), CarSchema.Run.initialState) }
            Game.alivecars = cars;
        }

        fun step() {
            if (Game.alivecars.isEmpty()) {
                throw Error("no more cars")
            }
            scene.world.step(1.0.toFloat() / Game.WordDef.box2dfps, 20, 20)
            Game.Listeners.preCarStep()
            Game.alivecars = Game.alivecars.filter { car ->
                car.state = CarSchema.Run.updateState(car.car, car.state)
                val status = CarSchema.Run.getStatus(car.state)
                Game.Listeners.carStep(car)
                if (status == 0) {
                    return@filter true
                }
                car.score = CarSchema.Run.calculateScore(car.state)
                Game.Listeners.carDeath(car)

                val world = scene.world
                val worldCar = car.car
                world.destroyBody(worldCar.chassis.body)

                for (element in worldCar.wheels) {
                    world.destroyBody(element)
                }

                false
            }
            // TODO: 2/18/2020
            if (Game.alivecars.isEmpty()) {
                Game.Listeners.generationEnd(cars)
            }
        }
    }
}
