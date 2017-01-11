package org.dorkmaster.library.materializer;

import org.dorkmaster.library.event.Event;

public interface Materializer {

    boolean canProcess(Event event);
    void process(Event event);

}
