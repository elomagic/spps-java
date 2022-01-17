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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "FormEncryptServlet", urlPatterns = "/encrypt")
public class FormEncryptServlet extends HttpServlet {

    private static final Logger LOGGER = LogManager.getLogger(FormEncryptServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String secret = request.getParameter("secret");

        try {
            String encryptedSecret = SimpleCryptFactory.getInstance().encrypt(secret.getBytes(StandardCharsets.UTF_8));

            request.setAttribute("encryptedSecret", encryptedSecret);

            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.sendRedirect("index.jsp");
        }
    }

}
