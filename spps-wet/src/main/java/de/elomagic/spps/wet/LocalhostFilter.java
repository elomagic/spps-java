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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

@WebFilter(displayName="Localhost Filter", urlPatterns = {
        "/index.jsp",
        "/encrypt",
        "/generate",
        "/import"
} )
public final class LocalhostFilter implements Filter {

    private static final Pattern LOCALHOST_PATTERN = Pattern.compile("127\\.\\d+\\.\\d+\\.\\d+|::1|0:0:0:0:0:0:0:1|localhost");
    private static final Logger LOGGER = LogManager.getLogger(LocalhostFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        // noop
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            String remoteAddress = request.getRemoteAddr();
            if (!LOCALHOST_PATTERN.matcher(remoteAddress).matches()) {
                request.getSession(true);
                response.sendRedirect(request.getContextPath() + "/onlylocalhost");
            }

            chain.doFilter(req, res);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void destroy() {
        // noop
    }
}
