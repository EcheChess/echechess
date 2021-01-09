/*
 *    Copyright 2014 - 2021 Yannick Watier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ca.watier.echechess.components;

import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.*;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;

// Thanks to @siphiuel on SO https://stackoverflow.com/a/28020519
public class UndertowHttpToHttpsCustomizerImpl implements UndertowDeploymentInfoCustomizer {

    private final int securePort;

    public UndertowHttpToHttpsCustomizerImpl(int securePort) {
        this.securePort = securePort;
    }

    @Override
    public void customize(DeploymentInfo deploymentInfo) {

        SecurityConstraint securityConstraint = new SecurityConstraint()
                .addWebResourceCollection(new WebResourceCollection()
                        .addUrlPattern("/*"))
                .setTransportGuaranteeType(TransportGuaranteeType.CONFIDENTIAL)
                .setEmptyRoleSemantic(SecurityInfo.EmptyRoleSemantic.PERMIT);

        ConfidentialPortManager confidentialPortManager = new ConfidentialPortManager() {
            @Override
            public int getConfidentialPort(HttpServerExchange exchange) {
                return securePort;
            }
        };

        deploymentInfo.addSecurityConstraint(securityConstraint)
                .setConfidentialPortManager(confidentialPortManager);
    }
}
