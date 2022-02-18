package de.elomagic.spps.wet;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.AnnotationSupport;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocalhostErrorServletTest {

    private final LocalhostErrorServlet servlet = new LocalhostErrorServlet();
    private String errorText;
    private String redirectPage;

    @Test
    void testAnnotations() {
        Optional<WebServlet> o = AnnotationSupport.findAnnotation(servlet.getClass(), WebServlet.class);
        assertEquals(servlet.getClass().getSimpleName(), o.map(WebServlet::name).get());
        assertEquals(1, o.map(a -> a.urlPatterns().length).get());
        assertEquals("/onlylocalhost", o.map(a -> a.urlPatterns()[0]).get());
    }

    @Test
    void testDoGet() throws Exception {

        // Preparing test
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher("error.jsp")).thenReturn(dispatcher);
        doAnswer((args) -> {
            errorText = args.getArgument(0); return null;
        }).when(request).setAttribute(Mockito.anyString(), Mockito.any());
        doAnswer((args) -> {
            redirectPage = args.getArgument(0); return null;
        }).when(response).sendRedirect(Mockito.anyString());

        servlet.doGet(request, response);

        assertNotNull("errorText");
    }

}