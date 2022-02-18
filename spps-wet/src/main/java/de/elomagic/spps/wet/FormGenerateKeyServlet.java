/*
 * Simple Password Protection Solution WebTool
 *
 * Copyright © 2021-present Carsten Rambow (spps.dev@elomagic.de)
 *
 * This file is part of Simple Password Protection Solution WebTool.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.elomagic.spps.wet;

import de.elomagic.spps.shared.SimpleCryptFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@WebServlet(name = "FormGenerateKeyServlet", urlPatterns = "/generate")
public class FormGenerateKeyServlet extends HttpServlet {


    private static final String NAME_GEN_ERROR_TEXT = "generateErrorText";
    private static final String NAME_GEN_RESULT_TEXT = "generateResultText";
    private static final String NAME_GEN_RESULT_KEY = "generateResultKey";

    private static final Logger LOGGER = LogManager.getLogger(FormGenerateKeyServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            Optional<GenerateMode> generateModeOptional = GenerateMode.parseString(request.getParameter("generateMode"));
            boolean force = "on".equalsIgnoreCase(request.getParameter("importForce"));

            if (generateModeOptional.isPresent()) {
                GenerateMode mode = generateModeOptional.get();
                switch (mode) {
                    case GENERATE_AND_IMPORT:
                        Arrays.fill(SimpleCryptFactory.getInstance().createPrivateKeyFile(null, null, force), (byte)0);
                        request.setAttribute(NAME_GEN_RESULT_TEXT, "Private key successful generated and imported");
                        break;
                    case GENERATE_IMPORT_AND_PRINT:
                        String privateKey = new String(SimpleCryptFactory.getInstance().createPrivateKeyFile(null, null, force), StandardCharsets.UTF_8);
                        request.setAttribute(NAME_GEN_RESULT_TEXT, "Private key successful generated and imported");
                        request.setAttribute(NAME_GEN_RESULT_KEY, privateKey);
                        break;
                    case GENERATE_AND_PRINT:
                        privateKey = new String(SimpleCryptFactory.getInstance().createPrivateKey(), StandardCharsets.UTF_8);
                        request.setAttribute(NAME_GEN_RESULT_KEY, privateKey);
                        break;
                }

                request.setAttribute("generateMode", mode.getValue());
            } else {
                request.setAttribute(NAME_GEN_ERROR_TEXT, "Invalid HTTP post call.");
            }
        } catch (Exception e) {
            request.removeAttribute(NAME_GEN_RESULT_KEY);
            request.removeAttribute(NAME_GEN_RESULT_TEXT);
            request.setAttribute(NAME_GEN_ERROR_TEXT, e.getMessage());
            LOGGER.error(e.getMessage(), e);
        } finally{
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
        }
    }

}
