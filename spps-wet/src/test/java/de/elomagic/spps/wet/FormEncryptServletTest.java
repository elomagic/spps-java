package de.elomagic.spps.wet;

import de.elomagic.spps.bc.SimpleCryptBC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.AnnotationUtils;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

class FormEncryptServletTest {

    private final Map<String, Object> attributes = new HashMap<>();
    private final Map<String, String> parameters = new HashMap<>();

    private final FormEncryptServlet servlet = new FormEncryptServlet();

    @BeforeEach
    void beforeEach() {
        attributes.clear();
        parameters.clear();
    }

    @Test
    void testAnnotations() {
        Optional<WebServlet> o = AnnotationSupport.findAnnotation(servlet.getClass(), WebServlet.class);
        Assertions.assertEquals(servlet.getClass().getSimpleName(), o.map(WebServlet::name).get());
        Assertions.assertEquals(1, o.map(a -> a.urlPatterns().length).get());
        Assertions.assertEquals("/encrypt", o.map(a -> a.urlPatterns()[0]).get());
    }

    @Test
    void testDoPost() throws IOException, ServletException {
        // Preparing test
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        String secret = "MySecret";

        parameters.put("encryptSecret", secret);

        when(request.getParameter(Mockito.anyString())).thenAnswer(args ->parameters.get(args.getArgument(0)));
        when(request.getRequestDispatcher("index.jsp")).thenReturn(dispatcher);
        doAnswer(i -> {
            attributes.put(i.getArgument(0).toString(), i.getArgument(1));
            return null;
        }).when(request).setAttribute(Mockito.anyString(), Mockito.any());

        // Start test
        servlet.doPost(request, response);

        Assertions.assertTrue(attributes.containsKey("encryptedSecret"));

        String encryptedSecret = attributes.get("encryptedSecret").toString();

        Assertions.assertNotEquals(secret, encryptedSecret);
        Assertions.assertEquals(secret, new String(new SimpleCryptBC().decrypt(encryptedSecret), StandardCharsets.UTF_8));

        Assertions.assertFalse(attributes.containsKey("encryptErrorText"));
    }
}