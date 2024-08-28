/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.common.transport.service.kafka.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.ericsson.aia.common.datarouting.api.EventListener;

/**
 * A simple implementation of an EventListener, for generic test purposes.
 *
 * @param <V>
 *            the type of the event.
 */
class SimpleEventListener<V> implements EventListener<V> {

    final List<V> events = new ArrayList<>();

    public List<V> getEvents() {
        return Collections.unmodifiableList(events);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.aia.common.datarouting.api.EventListener#onEvent(java.util.Collection)
     */
    @Override
    public void onEvent(final Collection<V> microBatch) {
        events.addAll(microBatch);
    }
}
