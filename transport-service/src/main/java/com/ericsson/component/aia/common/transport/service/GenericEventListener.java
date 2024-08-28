/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.common.transport.service;

import com.ericsson.aia.common.datarouting.api.EventListener;

/**
 * The <code>GenericEventListener</code> is a generic interface for the data collection. This interface's onEven method will be called by the internal
 * processor manager.
 *
 * @param <V>
 *            value type for the GenericEventListener.
 */
public interface GenericEventListener<V> extends EventListener<V> {

}
