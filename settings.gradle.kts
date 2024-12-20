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

rootProject.name = "dataspace-tck"
include("tools")
include("boot")
include("core")
include("runtimes:tck-runtime")
include("dsp:dsp-api")
include("dsp:dsp-system")
include("dsp:dsp-contract-negotiation")
include("dsp:dsp-tck")
include("api:core-api")
