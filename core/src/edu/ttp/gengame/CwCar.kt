package edu.ttp.gengame

import com.badlogic.gdx.math.Vector2

class CwCar  constructor(val car: CarRunner) {
    //private int frames;
    var alive: Boolean
    val isElite: Boolean

    //cw_Car.prototype.getPosition = function () {
    //  return this.car.car.chassis.GetPosition();
    //}
    val position: Vector2 = this.car.car.chassis.body.position

    init {
        //this.frames = 0;
        this.alive = true
        this.isElite = car.def.isElite
        //  this.healthBar = document.getElementById("health" + car_def.index).style;
        //  this.healthBarText = document.getElementById("health" + car_def.index).nextSibling.nextSibling;
        //  this.healthBarText.innerHTML = car_def.index;
        //  this.minimapmarker = document.getElementById("bar" + car_def.index);
        //
        //  if (this.isElite) {
        //    this.healthBar.backgroundColor = "#3F72AF";
        //    this.minimapmarker.style.borderLeft = "1px solid #3F72AF";
        //    this.minimapmarker.innerHTML = car_def.index;
        //  } else {
        //    this.healthBar.backgroundColor = "#F7C873";
        //    this.minimapmarker.style.borderLeft = "1px solid #F7C873";
        //    this.minimapmarker.innerHTML = car_def.index;
        //  }
    }

     fun kill() {
        //this.minimapmarker.style.borderLeft = "1px solid #3F72AF";
        //        var finishLine = currentRunner.scene.finishLine
        //        var max_car_health = constants.max_car_health;

        // Always -1 ???
        //        switch(status){
        //            case 1: {
        //                this.healthBar.width = "0";
        //                break
        //            }
        //            case -1: {
        //                this.healthBarText.innerHTML = "&dagger;";
        //                this.healthBar.width = "0";
        //                break
        //            }
        //        }
        this.alive = false
    }
}
