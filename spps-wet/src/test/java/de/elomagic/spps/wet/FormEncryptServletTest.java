package de.elomagic.spps.wet;

import de.elomagic.spps.bc.SimpleCrypt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormEncryptServletTest {

    private final AtomicReference<String> key = new AtomicReference<>();
    private final AtomicReference<String> value = new AtomicReference<>();


    @Test
    void testDoPost() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        String secret = "MySecret";

        when(request.getParameter("secret")).thenReturn(secret);
        when(request.getRequestDispatcher("index.jsp")).thenReturn(dispatcher);
        doAnswer(i -> {
            key.set(i.getArgument(0).toString());
            value.set(i.getArgument(1).toString());
            return null;
        }).when(request).setAttribute(Mockito.anyString(), Mockito.anyString());

        FormEncryptServlet servlet = new FormEncryptServlet();

        servlet.doPost(request, response);

        Assertions.assertEquals("encryptedSecret", key.get());
        Assertions.assertEquals(secret, SimpleCrypt.decryptToString(value.get()));
    }
}