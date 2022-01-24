package de.elomagic.spps.wet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class FormGenerateKeyServletTest {

    private final Map<String, Object> attributes = new HashMap<>();
    private final Map<String, String> parameters = new HashMap<>();

    private final FormGenerateKeyServlet servlet = new FormGenerateKeyServlet();

    @BeforeEach
    void beforeEach() {
        attributes.clear();
        parameters.clear();
    }

    @Test
    void testDoPost() throws IOException, ServletException {
        // Preparing test
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter(Mockito.anyString())).thenAnswer(args ->parameters.get(args.getArgument(0)));
        when(request.getRequestDispatcher("index.jsp")).thenReturn(dispatcher);
        doAnswer(i -> {
            attributes.put(i.getArgument(0).toString(), i.getArgument(1));
            return null;
        }).when(request).setAttribute(Mockito.anyString(), Mockito.any());

        // Start test
        servlet.doPost(request, response);

        Assertions.assertTrue(attributes.get("generateResultText").toString().length() > 10);
        Assertions.assertFalse(attributes.containsKey("generateHideError") && attributes.get("generateErrorText").toString().isEmpty());
    }
}