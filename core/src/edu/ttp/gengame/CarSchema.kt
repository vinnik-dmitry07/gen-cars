package edu.ttp.gengame

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef

class CarSchema {
    object CarConstants {
        const val wheelCount = 2
        const val wheelMinRadius = 0.2
        const val wheelRadiusRange = 0.5
        const val wheelMinDensity = 40.0
        const val wheelDensityRange = 100.0
        const val chassisDensityRange = 300.0
        const val chassisMinDensity = 30.0
        const val chassisMinAxis = 0.1
        const val chassisAxisRange = 1.1
    }

    object Schema {
        val wheel_radius = SchemaElement(type = "float", length = CarConstants.wheelCount, min = CarConstants.wheelMinRadius, range = CarConstants.wheelRadiusRange, factor = 1)
        val wheel_density = SchemaElement(type = "float", length = CarConstants.wheelCount, min = CarConstants.wheelMinDensity, range = CarConstants.wheelDensityRange, factor = 1)
        val chassis_density = SchemaElement(type = "float", length = 1,
                min = CarConstants.chassisDensityRange, // ???
                range = CarConstants.chassisMinDensity, // ???
                factor = 1)
        val vertex_list = SchemaElement(type = "float", length = 12, min = CarConstants.chassisMinAxis, range = CarConstants.chassisAxisRange, factor = 1)
        val wheel_vertex = SchemaElement(type = "shuffle", length = 8, limit = CarConstants.wheelCount, factor = 1)
    }

    class Car {
        lateinit var chassis: Chassis
        lateinit var wheels: Array<Body>
    }

    class Chassis internal constructor(@JvmField val body: Body) {
        internal var vertex_list: Array<Vector2>? = null
    }

    internal object DefToCar {
        fun defToCar(normal_def: Def, world: World): Car {
            val car_def = MachineLearning.CreateInstance.applyTypes(normal_def)
            val instance = Car()
            instance.chassis = createChassis(
                    world, car_def.vertex_list, car_def.chassis_density[0]
            )

            val wheelCount = car_def.wheel_radius.size

            instance.wheels = Array(wheelCount) { i ->
                createWheel(
                        world,
                        car_def.wheel_radius[i],
                        car_def.wheel_density[i]
                )
            }

            var carmass = instance.chassis.body.mass.toDouble()
            for (i in 0 until wheelCount) {
                carmass += instance.wheels[i].mass.toDouble()
            }

            val joint_def = RevoluteJointDef()

            for (i in 0 until wheelCount) {
                val torque = carmass * -Game.WordDef.gravity.y / car_def.wheel_radius[i]

                val randvertex = instance.chassis.vertex_list!![car_def.wheel_vertex[i].toInt()]
                joint_def.localAnchorA.set(randvertex.x, randvertex.y)
                joint_def.localAnchorB.set(0f, 0f)
                joint_def.maxMotorTorque = torque.toFloat()
                joint_def.motorSpeed = (-Game.WordDef.motorSpeed).toFloat()
                joint_def.enableMotor = true
                joint_def.bodyA = instance.chassis.body
                joint_def.bodyB = instance.wheels[i]
                world.createJoint(joint_def)
            }

            return instance
        }

        //function createChassis(world, vertexs, density) {
        //
        //  var vertex_list = new Array();
        //  vertex_list.push(new b2Vec2(vertexs[0], 0));
        //  vertex_list.push(new b2Vec2(vertexs[1], vertexs[2]));
        //  vertex_list.push(new b2Vec2(0, vertexs[3]));
        //  vertex_list.push(new b2Vec2(-vertexs[4], vertexs[5]));
        //  vertex_list.push(new b2Vec2(-vertexs[6], 0));
        //  vertex_list.push(new b2Vec2(-vertexs[7], -vertexs[8]));
        //  vertex_list.push(new b2Vec2(0, -vertexs[9]));
        //  vertex_list.push(new b2Vec2(vertexs[10], -vertexs[11]));
        //
        //  var body_def = new b2BodyDef();
        //  body_def.type = b2Body.b2_dynamicBody;
        //  body_def.position.Set(0.0, 4.0);
        //
        //  var body = world.CreateBody(body_def);
        //
        //  createChassisPart(body, vertex_list[0], vertex_list[1], density);
        //  createChassisPart(body, vertex_list[1], vertex_list[2], density);
        //  createChassisPart(body, vertex_list[2], vertex_list[3], density);
        //  createChassisPart(body, vertex_list[3], vertex_list[4], density);
        //  createChassisPart(body, vertex_list[4], vertex_list[5], density);
        //  createChassisPart(body, vertex_list[5], vertex_list[6], density);
        //  createChassisPart(body, vertex_list[6], vertex_list[7], density);
        //  createChassisPart(body, vertex_list[7], vertex_list[0], density);
        //
        //  body.vertex_list = vertex_list;
        //
        //  return body;
        //}
        fun createChassis(world: World, vertices: List<Double>, density: Double): Chassis {
            // @formatter:off
            val vertex_list = arrayOf(
                Vector2(vertices[0].toFloat(),      0f),
                Vector2(vertices[1].toFloat(),      vertices[2].toFloat()),
                Vector2(0f,                         vertices[3].toFloat()),
                Vector2((-vertices[4]).toFloat(),   vertices[5].toFloat()),
                Vector2((-vertices[6]).toFloat(),   0f),
                Vector2((-vertices[7]).toFloat(),   -vertices[8].toFloat()),
                Vector2(0f,                         -vertices[9].toFloat()),
                Vector2(vertices[10].toFloat(),     -vertices[11].toFloat())
            )
            // @formatter:on

            val body_def = BodyDef()
            body_def.type = BodyDef.BodyType.DynamicBody
            body_def.position.set(0.0f, 4.0f)

            val chassis = Chassis(world.createBody(body_def))

            createChassisPart(chassis.body, vertex_list[0], vertex_list[1], density)
            createChassisPart(chassis.body, vertex_list[1], vertex_list[2], density)
            createChassisPart(chassis.body, vertex_list[2], vertex_list[3], density)
            createChassisPart(chassis.body, vertex_list[3], vertex_list[4], density)
            createChassisPart(chassis.body, vertex_list[4], vertex_list[5], density)
            createChassisPart(chassis.body, vertex_list[5], vertex_list[6], density)
            createChassisPart(chassis.body, vertex_list[6], vertex_list[7], density)
            createChassisPart(chassis.body, vertex_list[7], vertex_list[0], density)

            chassis.vertex_list = vertex_list

            return chassis
        }

        fun createChassisPart(body: Body, vertex1: Vector2, vertex2: Vector2, density: Double) {
            val vertex_list = arrayOfNulls<Vector2>(3)
            vertex_list[0] = vertex1
            vertex_list[1] = vertex2
            vertex_list[2] = Vector2(0f, 0f)
            val fix_def = FixtureDef()
            fix_def.shape = PolygonShape()
            fix_def.density = density.toFloat()
            fix_def.friction = 10f
            fix_def.restitution = 0.2f
            fix_def.filter.groupIndex = -1
            (fix_def.shape as PolygonShape).set(vertex_list)

            body.createFixture(fix_def)
        }

        fun createWheel(world: World, radius: Double, density: Double): Body {
            val body_def = BodyDef()
            body_def.type = BodyDef.BodyType.DynamicBody
            body_def.position.set(0f, 0f)

            val body = world.createBody(body_def)

            val fix_def = FixtureDef()
            fix_def.shape = CircleShape()
            fix_def.shape.radius = radius.toFloat()
            fix_def.density = density.toFloat()
            fix_def.friction = 1f
            fix_def.restitution = 0.2f
            fix_def.filter.groupIndex = -1

            body.createFixture(fix_def)
            return body
        }
    }

    object Run {

        //function getInitialState(world_def){
        //  return {
        //    frames: 0,
        //    health: world_def.max_car_health,
        //    maxPositiony: 0,
        //    minPositiony: 0,
        //    maxPositionx: 0,
        //  };
        //}
        @JvmField
        val initialState: State = State()

        class State {
            var frames = 0
            var health: Int = 0
            var maxPositiony = 0.0
            var minPositiony = 0.0
            var maxPositionx = 0.0

            constructor() {
                this.health = Game.WordDef.max_car_health
            }

            constructor(frames: Int, maxPositionx: Double, maxPositiony: Double, minPositiony: Double) {
                this.frames = frames
                this.maxPositionx = maxPositionx
                this.maxPositiony = maxPositiony
                this.minPositiony = minPositiony
            }
        }

        @JvmStatic
        fun updateState(worldConstruct: Car, state: State): State {
            if (state.health <= 0) {
                throw Error("Already Dead")
            }
            // TODO: 2/18/2020
            //        if (state.maxPositionx > constants.finishLine){
            //            throw new Error("already Finished");
            //        }

            // console.log(state);
            // check health
            val position = worldConstruct.chassis.body.position
            // check if car reached end of the path
            val nextState = State(
                    state.frames + 1,
                    if (position.x > state.maxPositionx) position.x.toDouble() else state.maxPositionx,
                    if (position.y > state.maxPositiony) position.y.toDouble() else state.maxPositiony,
                    if (position.y < state.minPositiony) position.y.toDouble() else state.minPositiony
            )
            // TODO: 2/18/2020
            //        if (position.x > constants.finishLine) {
            //            return nextState;
            //        }

            if (position.x > state.maxPositionx + 0.02) {
                nextState.health = Game.WordDef.max_car_health
                return nextState
            }
            nextState.health = state.health - 1
            if (Math.abs(worldConstruct.chassis.body.linearVelocity.x) < 0.001) {
                nextState.health -= 5
            }
            return nextState
        }

        @JvmStatic
        fun getStatus(state: State): Int {
            if (hasFailed(state)) return -1
            return if (hasSuccess(state)) 1 else 0
        }

        @JvmStatic
        fun hasFailed(state: State): Boolean {
            return state.health <= 0
        }

        @JvmStatic
        fun hasSuccess(state: State): Boolean {
            return false // TODO state.maxPositionx > constants.finishLine; // ??? finishLine
        }

        class Score internal constructor(internal val v: Double, internal val s: Double, internal val x: Double, internal val y: Double, internal val y2: Double) {
            internal var i: Int = 0
        }

        @JvmStatic
        fun calculateScore(state: State): Score {
            val avgspeed = state.maxPositionx / state.frames * Game.WordDef.box2dfps
            val position = state.maxPositionx
            val score = position + avgspeed
            return Score(
                    score,
                    avgspeed,
                    position,
                    state.maxPositiony,
                    state.minPositiony
            )
        }
    }
}

