package edu.ttp.gengame

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.math.Vector2
import java.util.*

import kotlin.math.roundToInt
import kotlin.properties.Delegates


class Game(displayWidth: Int, displayHeight: Int) : ApplicationAdapter() {
    object Camera {
        const val speed = 0.05
        @JvmField var pos = Vector2(0f, 0f)
        const val distanceToLeftBound = 200f
        var distanceToTopBound : Int by Delegates.notNull()
        @JvmField var target: CarRunner? = null
        const val zoom = 70f
    }

    internal object LeaderPosition {
        var position = Vector2(0f, 0f)
        var leader: Int = 0
    }

    object WordDef {
        @JvmField val gravity = Vector2(0.0f, -9.81f)
        const val doSleep = true
        @JvmField var floorseed = random.nextLong()
        @JvmField val tileDimensions = Vector2(1.5f, 0.15f)
        const val maxFloorTiles = 200
        @JvmField var mutable_floor = false
        const val box2dfps = Game.box2dfps
        const val motorSpeed = 20
        @JvmField val max_car_health = Game.max_car_health
        @JvmField val schema = GenerationConfig.schema
    }

    private fun cw_generationZero() {
        generationState = MachineLearning.GeneticAlgorithm.ManageRound.generationZero()
    }

    object Listeners {
        @JvmStatic
        fun generationEnd(results: Array<CarRunner>) {
            cw_newRound(results.sortedBy { it.score.v }.toTypedArray())
        }

        @JvmStatic
        fun preCarStep() {
            //ghost_move_frame(ghost);
        }

        @JvmStatic
        fun carStep(car: CarRunner) {
            updateCarUI(car)
        }

        @JvmStatic
        fun carDeath(carInfo: CarRunner) {
            val k = carInfo.index

            //            CarSchema.Car car = carInfo.car;
            val score = carInfo.score
            carMap[carInfo]!!.kill(currentRunner)

            // refocus camera to leader on death
            if (Camera.target === carInfo) {
                cw_setCameraTarget(null)
            }
            // console.log(score);
            carMap.remove(carInfo)
            //            ghost_compare_to_replay(car.replay, ghost, score.v);
            score.i = generationState.counter

            cw_deadCars++
            //            int generationSize = GenerationConfig.generationSize;
            //            document.getElementById("population").innerHTML = (generationSize - cw_deadCars).toString();

            // console.log(LeaderPosition.leader, k)
            if (LeaderPosition.leader == k) {
                // leader is dead, find new leader
                cw_findLeader()
            }
        }
    }

    init {
        random = Random()
        Camera.distanceToTopBound = displayHeight / 2
        spaceRightToCam = displayWidth / Camera.zoom
        //        var mmm = document.getElementsByName('minimapmarker')[0];
        // TODO
        //        PolygonShape mmm = new PolygonShape();
        //        mmm.setAsBox(1, 200);
        //        var hbar = document.getElementsByName('healthbar')[0];
        //        PolygonShape hbar = new PolygonShape();
        //        hbar.setAsBox(94, 12);
        //        var generationSize = generationConfig.constants.generationSize;
        val generationSize = GenerationConfig.generationSize


        for (k in 0 until generationSize) {
            //            // minimap markers
            //            var newbar = mmm.cloneNode(true);
            //            newbar.id = "bar" + k;
            //            newbar.style.paddingTop = k * 9 + "px";
            //            minimapholder.appendChild(newbar);
            //
            //            // health bars
            //            var newhealth = hbar.cloneNode(true);
            //            newhealth.getElementsByTagName("DIV")[0].id = "health" + k;
            //            newhealth.car_index = k;
            //            document.getElementById("health").appendChild(newhealth);
        }
        cw_generationZero()
        // TODO: 2/15/2020
        //  ghost = ghost_create_ghost();
        resetCarUI()
        currentRunner = edu.ttp.gengame.Run.runDefs(generationState.generation)
        alivecars = currentRunner.cars
        setupCarUI()
        // TODO
        //  cw_drawMiniMap();
    }

    companion object {
        var spaceRightToCam: Float by Delegates.notNull()
        lateinit var alivecars: List<CarRunner>
        lateinit var random: Random
        const val spaceLeftToCam = Camera.distanceToLeftBound / Camera.zoom

        internal var ghost_fns = Ghost()

        @JvmField val carMap = HashMap<CarRunner, cw_Car>()

        private const val box2dfps = 60
        @JvmField val skipTicks = (1000.0 / box2dfps).roundToInt()
        @JvmField val maxFrameSkip = skipTicks * 2

        private const val max_car_health = box2dfps * 10

        private var cw_deadCars: Int = 0
        private lateinit var generationState: MachineLearning.GeneticAlgorithm.ManageRound.GenerationState
        lateinit var currentRunner: Run

        private fun resetCarUI() {
            cw_deadCars = 0
            LeaderPosition.position = Vector2(0f, 0f)
            //        document.getElementById("generation").innerHTML = generationState.counter.toString();
            //        document.getElementById("cars").innerHTML = "";
            //        document.getElementById("population").innerHTML = generationConfig.constants.generationSize.toString();
        }

        private fun cw_setCameraTarget(k: CarRunner?) {
            Camera.target = k
        }

        @JvmStatic
        fun cw_setCameraPosition() {
            var cameraTargetPosition: Vector2

            if (Camera.target != null) {
                // cameraTargetPosition = carMap.get(camera.target).getPosition(); ???
            } else {

                cameraTargetPosition = LeaderPosition.position
            }
            cameraTargetPosition = LeaderPosition.position
            val diff_y = Camera.pos.y - cameraTargetPosition.y
            val diff_x = Camera.pos.x - cameraTargetPosition.x
            Camera.pos.y -= (Camera.speed * diff_y).toFloat()
            Camera.pos.x -= (Camera.speed * diff_x).toFloat()
            //        cw_minimapCamera(Camera.pos.x, camera.pos.y);
        }

        private fun updateCarUI(carInfo: CarRunner) {
            val k = carInfo.index
            val car = carMap[carInfo]
            val position = car!!.position

            //ghost_add_replay_frame(car.replay, car.car.car);
            //car.minimapmarker.style.left = Math.round((position.x + 5) * minimapscale) + "px";
            //car.healthBar.width = Math.round((car.car.state.health / max_car_health) * 100) + "%";
            if (position.x > LeaderPosition.position.x) {
                LeaderPosition.position = position
                LeaderPosition.leader = k
                // console.log("new leader: ", k);
            }
        }

        private fun cw_newRound(results: Array<CarRunner>) {
            Camera.pos.y = 0f
            Camera.pos.x = Camera.pos.y
            cw_setCameraTarget(null)

            generationState = MachineLearning.GeneticAlgorithm.ManageRound.nextGeneration(
                    generationState, results
            )

            if (WordDef.mutable_floor) {
                // GHOST DISABLED
                //            ghost = null;
                WordDef.floorseed = Game.random.nextLong()
            } else {
                // RE-ENABLE GHOST
                //            ghost_reset_ghost(ghost);
            }
            currentRunner = edu.ttp.gengame.Run.runDefs(generationState.generation)
            setupCarUI()
            //        cw_drawMiniMap();
            resetCarUI()
        }

        private fun setupCarUI() {
            currentRunner.cars.forEach { carInfo:CarRunner ->
                val car = cw_Car(carInfo)
                carMap[carInfo] = car
                // TODO: 2/18/2020
                //            car.replay = ghost_fns.ghost_create_replay();
                //            ghost_fns.ghost_add_replay_frame(car.replay, car.car.car);
            }
        }

        private fun cw_findLeader() {
            val lead = 0
            val cw_carArray = carMap.values.toTypedArray()
            for (k in cw_carArray.indices) {
                if (!cw_carArray[k].alive) {
                    continue
                }
                val position = cw_carArray[k].position
                if (position.x > lead) {
                    LeaderPosition.position = position
                    LeaderPosition.leader = k
                }
            }
        }
    }
}
