package de.elomagic.spps.wet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.AnnotationSupport;
import org.mockito.Mockito;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class LocalhostFilterTest {

    private static final LocalhostFilter filter = new LocalhostFilter();
    private String remoteAddress;
    private String redirectPage;

    @Test
    void testAnnotations() {
        Optional<WebFilter> o = AnnotationSupport.findAnnotation(filter.getClass(), WebFilter.class);
        Assertions.assertArrayEquals(new String[] { "/index.jsp", "/encrypt", "/generate", "/import" }, o.map(WebFilter::urlPatterns).get());
    }

    @Test
    void testDoFilter() throws ServletException, IOException {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        doAnswer((args) -> remoteAddress).when(request).getRemoteAddr();
        doAnswer((args) -> remoteAddress).when(request).getContextPath();
        doAnswer(args -> redirectPage = args.getArgument(0)).when(response).sendRedirect(Mockito.anyString());

        // Test valid addresses

        remoteAddress = "127.0.0.1";
        filter.doFilter(request, response, chain);
        assertNull(redirectPage);

        redirectPage = null;
        remoteAddress = "localhost";
        filter.doFilter(request, response, chain);
        assertNull(redirectPage);

        redirectPage = null;
        remoteAddress = "::1";
        filter.doFilter(request, response, chain);
        assertNull(redirectPage);

        redirectPage = null;
        remoteAddress = "0:0:0:0:0:0:0:1";
        filter.doFilter(request, response, chain);
        assertNull(redirectPage);

        // Test invalid addresses

        redirectPage = null;
        remoteAddress = "192.168.150.1";
        filter.doFilter(request, response, chain);
        assertEquals(remoteAddress + "/onlylocalhost", redirectPage);

        redirectPage = null;
        remoteAddress = "somewhere.anywhere.local";
        filter.doFilter(request, response, chain);
        assertEquals(remoteAddress + "/onlylocalhost", redirectPage);

    }
}