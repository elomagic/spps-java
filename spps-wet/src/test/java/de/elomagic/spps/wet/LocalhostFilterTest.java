package de.elomagic.spps.wet;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocalhostFilterTest {

    private static final LocalhostFilter filter = new LocalhostFilter();
    private String remoteAddress;
    private String redirectPage;

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