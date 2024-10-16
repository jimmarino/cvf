/*
 *  Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 *
 */

package org.eclipse.dataspacetck.dsp.system.api.message;

/**
 * DSP Message constants.
 */
public interface DspConstants {

    String TCK_PARTICIPANT_ID = "TCK_PARTICIPANT";

    String DSPACE_NAMESPACE = "https://w3id.org/dspace/v0.8/";

    String CONTEXT = "@context";

    String ID = "@id";

    String VALUE = "@value";

    String TYPE = "@type";

    String DSPACE_NAMESPACE_KEY = "dspace";

    String DSPACE_NAMESPACE_PREFIX = DSPACE_NAMESPACE_KEY + ":";

    String DSPACE_PROPERTY_TIMESTAMP = DSPACE_NAMESPACE_PREFIX + "timestamp";

    String DSPACE_PROPERTY_CONSUMER_PID = DSPACE_NAMESPACE_PREFIX + "consumerPid";

    String DSPACE_PROPERTY_CONSUMER_PID_EXPANDED = DSPACE_NAMESPACE + "consumerPid";

    String DSPACE_PROPERTY_PROVIDER_PID = DSPACE_NAMESPACE_PREFIX + "providerPid";

    String DSPACE_PROPERTY_PROVIDER_PID_EXPANDED = DSPACE_NAMESPACE + "providerPid";

    String DSPACE_PROPERTY_CODE = DSPACE_NAMESPACE_PREFIX + "code";

    String DSPACE_PROPERTY_REASON = DSPACE_NAMESPACE_PREFIX + "reason";

    String DSPACE_PROPERTY_STATE = DSPACE_NAMESPACE_PREFIX + "state";

    String DSPACE_PROPERTY_STATE_EXPANDED = DSPACE_NAMESPACE + "state";

    String DSPACE_PROPERTY_EVENT_TYPE = DSPACE_NAMESPACE_PREFIX + "eventType";

    String DSPACE_PROPERTY_EVENT_TYPE_EXPANDED = DSPACE_NAMESPACE + "eventType";

    String DSPACE_PROPERTY_CALLBACK_ADDRESS = DSPACE_NAMESPACE_PREFIX + "callbackAddress";

    String DSPACE_PROPERTY_CALLBACK_ADDRESS_EXPANDED = DSPACE_NAMESPACE + "callbackAddress";

    String DSPACE_PROPERTY_OFFER = DSPACE_NAMESPACE_PREFIX + "offer";

    String DSPACE_PROPERTY_OFFER_EXPANDED = DSPACE_NAMESPACE + "offer";

}
