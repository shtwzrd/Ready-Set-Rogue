package com.warsheep.scamp;

import com.badlogic.gdx.utils.Pool;

public class StateSignalPool extends Pool<StateSignal> {

    @Override
    protected StateSignal newObject() {
        return new StateSignal();
    }
}
