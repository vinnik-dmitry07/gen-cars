package edu.ttp.gengame

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.bitfire.postprocessing.PostProcessor
import com.bitfire.postprocessing.effects.Fxaa
import com.bitfire.utils.ShaderLoader
import java.util.*
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class Game(callback_: ICallback, displayWidth: Int, displayHeight: Int) : ApplicationAdapter() {
    interface ICallback {
        fun call(result: Double)
    }

//    fun showDistance(distance: Double, height: Double) { // TODO: 2/18/2020
//        distanceMeter.innerHTML = distance + " meters<br />";
//        heightMeter.innerHTML = height + " meters";
//        if (distance > minimapfogdistance) {
//            fogdistance.width = 800 - Math.round(distance + 15) * minimapscale + "px";
//            minimapfogdistance = distance;
//        }
//    }

    private fun drawScreen() {
        postProcessor.capture()
        val floorTiles = Runner.scene.floorTiles
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        setCameraPosition()
        Camera.camera.position.set(LeaderPosition.position.x + ((Camera.camera.viewportWidth - 200) / Camera.zoom) / 2f, LeaderPosition.position.y, 0f)
//        Camera.camera.translate(Camera.distanceToLeftBound - Camera.pos.x * Camera.zoom,
//        Camera.distanceToTopBound + Camera.pos.y * Camera.zoom)
        Camera.camera.zoom = 1f / Camera.zoom

        Camera.camera.update()

        @Suppress("UsePropertyAccessSyntax")
        Draw.renderer.setProjectionMatrix(Camera.camera.combined)

        Draw.drawFloor(floorTiles)
//        ghost_draw_frame(ctx, ghost, camera);
        drawCars()
        postProcessor.render()
    }

    private fun drawCars() {
        for (myCar in carMap.values) {
            Draw.drawCar(myCar)
        }
    }

    object Camera {
        val camera = OrthographicCamera(800f, 600f)
        const val speed = 0.05
        @JvmField
        var pos = Vector2(0f, 0f)
        const val distanceToLeftBound = 200f
        var distanceToTopBound: Int by Delegates.notNull()
        @JvmField
        var target: CarRunner? = null
        const val zoom = 60f
    }

    object LeaderPosition {
        var position = Vector2(0f, 0f)
        var leader: Int = 0
    }

    object WordDef {
        @JvmField
        val gravity = Vector2(0.0f, -9.81f)
        const val doSleep = true
        @JvmField
        var floorseed = random.nextLong()
        @JvmField
        val tileDimensions = Vector2(1.5f, 0.15f)
        const val maxFloorTiles = 200
        @JvmField
        var mutable_floor = false
        const val box2dfps = Game.box2dfps
        const val motorSpeed = 20
        const val max_car_health = box2dfps * 10
        @JvmField
        val schema = CarSchema.Schema
    }

    object Listeners {
        @JvmStatic
        fun generationEnd(results: List<CarRunner>) {
            val resultsSorted = results.sortedByDescending { it.score.v }
            callback.call(resultsSorted[0].score.s)
            newRound(resultsSorted)
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
            carMap[carInfo]!!.kill()

            if (Camera.target === carInfo) {
                setCameraTarget(null)
            }

            carMap.remove(carInfo)
//            ghost_compare_to_replay(car.replay, ghost, score.v);
            score.i = generationState.counter

            deadCars++
//            int generationSize = GenerationConfig.generationSize;
//            document.getElementById("population").innerHTML = (generationSize - cw_deadCars).toString();

            if (LeaderPosition.leader == k) {
                findLeader()
            }
        }
    }

    lateinit var shape: ShapeRenderer
    private lateinit var stage: Stage
    lateinit var skin: Skin
    lateinit var postProcessor: PostProcessor
    override fun create() {
        shape = ShapeRenderer()
        stage = Stage()
//        Gdx.input.inputProcessor = stage
//        // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
//        // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
//        skin = Skin()
//        // Generate a 1x1 white texture and store it in the skin named "white".
//        val pixmap = Pixmap(1, 1, Format.RGBA8888)
//        pixmap.setColor(Color.WHITE)
//        pixmap.fill()
//        skin.add("white", Texture(pixmap))
//        // Store the default libgdx font under the name "default".
//        skin.add("default", BitmapFont())
//        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
//        val textButtonStyle = TextButtonStyle()
//        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY)
//        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY)
//        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE)
//        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY)
//        textButtonStyle.font = skin.getFont("default")
//        skin.add("default", textButtonStyle)
//        // Create a table that fills the screen. Everything else will go inside this table.
//        val table = Table()
//        table.setFillParent(true)
//        stage.addActor(table)
//        // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
//        val button = TextButton("Click me!", skin)
//        table.add(button)
//        // Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when clicked,
//        // Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
//        // ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
//        // revert the checked state.
//        button.addListener(object : ChangeListener() {
//            override fun changed(event: ChangeEvent, actor: Actor) {
//                println("Clicked! Is checked: " + button.isChecked)
//                button.setText("Good job!")
//            }
//        })
//        // Add an image actor. Have to set the size, else it would be the size of the drawable (which is the 1x1 texture).
//        table.add(Image(skin.newDrawable("white", Color.RED))).size(64f)

        ShaderLoader.BasePath = "shaders/"
        postProcessor = PostProcessor(false, false, false)
        val fxaa = Fxaa((Gdx.graphics.getWidth() * 0.9).toInt(), (Gdx.graphics.getHeight() * 0.9).toInt())
        postProcessor.addEffect(fxaa)
    }


    override fun render() {
//        stage.act(min(Gdx.graphics.deltaTime, 1 / 30f))
//        stage.draw()
//        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
//
////        cwSetCameraPosition()
////        Camera.camera.translate(Camera.distanceToLeftBound - Camera.pos.x * Camera.zoom,
////                Camera.distanceToTopBound + Camera.pos.y * Camera.zoom)
//        Camera.camera.zoom = Camera.zoom
//
//        Camera.camera.update()
//        @Suppress("UsePropertyAccessSyntax")
//        shape.setProjectionMatrix(Camera.camera.combined)
//
//        shape.begin(ShapeRenderer.ShapeType.Filled)
//        shape.circle(50f, 50f, 50f)
//        shape.end()
        if (!paused) {
            if (doDraw) {
                drawScreen()
            }
            for (i in 1..speed) simulationStep()
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
        postProcessor.dispose()
    }

    init {
        callback = callback_
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

        for (k in 0 until GenerationConfig.generationSize) {
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
        generationZero()
        // TODO: 2/15/2020
        //  ghost = ghost_create_ghost();
        resetCarUI()
        Runner()
        Runner.updateDefs(generationState.generation)
        alivecars = Runner.cars
        setupCarUI()
        // TODO
        //  cw_drawMiniMap();
    }

    companion object {
        lateinit var callback: ICallback
        var spaceRightToCam: Float by Delegates.notNull()
        lateinit var alivecars: List<CarRunner>
        lateinit var random: Random
        const val spaceLeftToCam = Camera.distanceToLeftBound / Camera.zoom

//        var ghost_fns = Ghost()

        @JvmField
        val carMap = HashMap<CarRunner, CwCar>()

        @JvmField
        var paused = false
        @JvmField
        var doDraw = true

        private const val box2dfps = 60
        @JvmField
        val skipTicks = (1000.0 / box2dfps).roundToInt()
        @JvmField
        val maxFrameSkip = skipTicks * 2

        private var deadCars: Int = 0
        lateinit var generationState: MachineLearning.GeneticAlgorithm.ManageRound.GenerationState
        lateinit var runner: Runner

        @JvmField
        var speed = 1

        @JvmStatic
        fun simulationStep() {
            Runner.step()
//      TODO
//        showDistance(
//                Math.round(LeaderPosition.x * 100) / 100,
//                Math.round(LeaderPosition.y * 100) / 100
//        );
        }

        @JvmStatic
        fun generationZero() {
            generationState = MachineLearning.GeneticAlgorithm.ManageRound.generationZero()
        }

        @JvmStatic
        fun resetCarUI() {
            deadCars = 0
            LeaderPosition.position = Vector2(0f, 0f)
//            document.getElementById("generation").innerHTML = generationState.counter.toString();
//            document.getElementById("cars").innerHTML = "";
//            document.getElementById("population").innerHTML = generationConfig.constants.generationSize.toString();
        }

        private fun setCameraTarget(k: CarRunner?) {
            Camera.target = k
        }

        @JvmStatic
        fun setCameraPosition() {
            var cameraTargetPosition: Vector2

            if (Camera.target != null) {
                // cameraTargetPosition = carMap.get(camera.target).getPosition(); ???
            } else {
                cameraTargetPosition = LeaderPosition.position
            }
            cameraTargetPosition = LeaderPosition.position
            val diffY = Camera.pos.y - cameraTargetPosition.y
            val diffX = Camera.pos.x - cameraTargetPosition.x
            Camera.pos.y -= (Camera.speed * diffY).toFloat()
            Camera.pos.x -= (Camera.speed * diffX).toFloat()
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

        private fun newRound(results: List<CarRunner>) {
            Camera.pos.y = 0f
            Camera.pos.x = Camera.pos.y
            setCameraTarget(null)

            generationState = MachineLearning.GeneticAlgorithm.ManageRound.nextGeneration(
                    generationState, results
            )

            if (WordDef.mutable_floor) {
                // GHOST DISABLED
                //            ghost = null;
                WordDef.floorseed = random.nextLong()
            } else {
                // RE-ENABLE GHOST
                //            ghost_reset_ghost(ghost);
            }
            Runner.updateDefs(generationState.generation)
            setupCarUI()
            //        cw_drawMiniMap();
            resetCarUI()
        }

        @JvmStatic
        fun clearPopulationWorld() {
            for (car in carMap.values) {
                car.kill()
            }
        }

        @JvmStatic
        fun setupCarUI() {
            Runner.cars.forEach { carInfo: CarRunner ->
                val car = CwCar(carInfo)
                carMap[carInfo] = car
                // TODO: 2/18/2020
                //            car.replay = ghost_fns.ghost_create_replay();
                //            ghost_fns.ghost_add_replay_frame(car.replay, car.car.car);
            }
        }

        private fun findLeader() {
            val lead = 0
            val cwCarArray = carMap.values.toTypedArray()
            for (k in cwCarArray.indices) {
                if (!cwCarArray[k].alive) {
                    continue
                }
                val position = cwCarArray[k].position
                if (position.x > lead) {
                    LeaderPosition.position = position
                    LeaderPosition.leader = k
                }
            }
        }
    }
}
