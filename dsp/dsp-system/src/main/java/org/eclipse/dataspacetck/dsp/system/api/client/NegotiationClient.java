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

package org.eclipse.dataspacetck.dsp.system.api.client;

import java.util.Map;

/**
 * Proxy to the connector being verified for contract negotiation.
 */
public interface NegotiationClient {

    /**
     * Creates a contract request. Used for initial requests and client counter-offers.
     */
    Map<String, Object> contractRequest(Map<String, Object> message);

    /**
     * Accepts the most recent offer.
     */
    void consumerAccept(Map<String, Object> offer);

    /**
     * Verifies the contract agreement with the provider.
     */
    void consumerVerify(Map<String, Object> verification);

    /**
     * Terminates a negotiation.
     */
    void terminate(Map<String, Object> termination);

    /**
     * Returns a negotiation.
     */
    Map<String, Object> getNegotiation(String processId);

}
