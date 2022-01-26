package de.elomagic.spps.wet;

import de.elomagic.spps.shared.SimpleCryptFactory;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.AnnotationSupport;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

class FormImportKeyServletTest {

    private final Map<String, Object> attributes = new HashMap<>();
    private final Map<String, String> parameters = new HashMap<>();

    private final FormImportKeyServlet servlet = new FormImportKeyServlet();

    private Path createEmptyTempFile() throws IOException {
        Path file = File.createTempFile("SimpleCryptTest-Temp-", ".txt").toPath();
        file.toFile().deleteOnExit();

        return file;
    }

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
        Assertions.assertEquals("/import", o.map(a -> a.urlPatterns()[0]).get());
    }

    @Test
    void testDoPost() throws Exception {
        // Preparing test
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        parameters.put("importKey", Base64.getEncoder().encodeToString(SimpleCryptFactory.getInstance().createPrivateKey()));

        when(request.getParameter(Mockito.anyString())).thenAnswer(args ->parameters.get(args.getArgument(0)));
        when(request.getRequestDispatcher("index.jsp")).thenReturn(dispatcher);
        doAnswer(i -> {
            attributes.put(i.getArgument(0).toString(), i.getArgument(1));
            return null;
        }).when(request).setAttribute(Mockito.anyString(), Mockito.any());

        // Start test w/o force
        Path file = createEmptyTempFile();
        SimpleCryptFactory.getInstance().setSettingsFile(file);
        String content = IOUtils.toString(Files.newBufferedReader(file));

        servlet.doPost(request, response);

        Assertions.assertFalse(attributes.containsKey("importResultText"));
        Assertions.assertTrue(attributes.containsKey("importErrorText"));

        // Start test with force
        attributes.clear();
        parameters.put("importForce", "on");

        servlet.doPost(request, response);

        String newContent = IOUtils.toString(Files.newBufferedReader(file));

        Assertions.assertNotEquals(content, newContent);
        Assertions.assertEquals("Successful imported", attributes.get("importResultText"));
        Assertions.assertFalse(attributes.containsKey("importErrorText"));
    }
}