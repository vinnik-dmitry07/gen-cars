package edu.ttp.gengame

import com.badlogic.gdx.physics.box2d.World

import java.util.Arrays
import java.util.stream.IntStream

class Run private constructor(internal val scene: Scene, internal val cars: List<CarRunner>, internal val step: Runnable) {
    companion object {
        internal fun runDefs(defs: Array<Def>): Run {
            if (Game.WordDef.mutable_floor) {
                // GHOST DISABLED
                Game.WordDef.floorseed = Game.random.nextLong() // ???
            }

            val scene = SetupScene.setupScene()
            scene.world.step(1.0.toFloat() / Game.WordDef.box2dfps, 20, 20)
            println("about to build cars")
            val cars = (0..defs.size).map { i -> CarRunner(i, defs[i], CarSchema.DefToCar.defToCar(defs[i], scene.world), CarSchema.Run.initialState) }
            Game.alivecars = cars
            return Run(scene, cars, Runnable {
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

                    for (w in 0 until worldCar.wheels.size) {
                        world.destroyBody(worldCar.wheels[w])
                    }

                    false
                }
                // TODO: 2/18/2020
                if (Game.alivecars.isEmpty()) {
                    Game.Listeners.generationEnd(cars)
                }
            })
        }
    }
}
