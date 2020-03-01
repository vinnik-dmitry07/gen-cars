package edu.ttp.gengame

import java.util.ArrayList

internal class Ghost {

    private val enable_ghost = true

    internal inner class Replay(val num_frames: Int, val frames: List<Int>)

    fun ghost_create_replay(): Replay? {
        return if (!enable_ghost) null else Replay(0, ArrayList())

    }

    //    function ghost_add_replay_frame(replay, car) {
    //        if (!enable_ghost)
    //            return;
    //        if (replay == null)
    //            return;
    //
    //        var frame = ghost_get_frame(car);
    //        replay.frames.push(frame);
    //        replay.num_frames++;
    //    }
}
