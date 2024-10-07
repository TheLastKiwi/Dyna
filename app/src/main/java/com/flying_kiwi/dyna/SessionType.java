package com.flying_kiwi.dyna;

import java.io.Serializable;

public enum SessionType implements Serializable {
    //TODO: I could over-engineer this and have subclasses of session for each type to hold
    // only their own data. Maybe in the future I'll do that
    LIVE_DATA,
    PEAK_LOAD,
    ENDURANCE,
    REPEATER,
    CRITICAL_FORCE
}
