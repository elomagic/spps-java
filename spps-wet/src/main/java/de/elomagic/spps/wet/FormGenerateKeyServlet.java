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

@WebServlet(name = "FormGenerateKeyServlet", urlPatterns = "/generate")
public class FormGenerateKeyServlet extends HttpServlet {

    private static final Logger LOGGER = LogManager.getLogger(FormGenerateKeyServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String privateKey = new String(SimpleCryptFactory.getInstance().createPrivateKey(), StandardCharsets.UTF_8);
            request.setAttribute("generateResultText", privateKey);
        } catch (Exception e) {
            request.setAttribute("generateErrorText", e.getMessage());
            LOGGER.error(e.getMessage(), e);
        } finally{
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
            response.sendRedirect("index.jsp");
        }
    }

}
