package com.example.gaim.ui.utility

import android.content.Intent
import android.widget.Button
import com.example.gaim.search.*

//creates a switch button that tracks the button state via an opposable class (enum)
abstract class SwitchButtonTracker<K : Opposable<K>>(button: Button){
    protected abstract var state: K;

    init{
        button.setOnClickListener {
            state = state.opposite()
        }
    }

    fun state(): Boolean{ //makes state read only (val vs var)
        return state.value
    }
}

//Creates a tracker which updates the intent based on a name
abstract class NamedStateTracker<T : Named, K : Opposable<K>>(private val nameTracker: T, private val intent: Intent, button: Button) : SwitchButtonTracker<K>(button) {
    abstract override var state: K

    fun searchName(): String{
        return nameTracker.name
    }

    init{
        button.setOnClickListener {
            state = state.opposite()
            intent.putExtra(nameTracker.name, state.value)
        }
    }
}