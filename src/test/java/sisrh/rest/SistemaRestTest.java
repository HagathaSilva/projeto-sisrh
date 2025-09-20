package sisrh.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

class SistemaRestTest {

    @Test
    void ping_teste() {
        SistemaRest resource = new SistemaRest();

        Response resp = resource.ping();

        assertEquals(200, resp.getStatus());
        Object entity = resp.getEntity();
        assertNotNull(entity);
        String body = entity.toString();
        assertTrue(body.startsWith("pong: "),
                "Body deveria começar com 'pong: ', recebido: " + body);
        assertTrue(body.length() > "pong: ".length());
    }

    @Test
    void datahora_teste() {
        SistemaRest resource = new SistemaRest();

        Response resp = resource.datahora();

        assertEquals(200, resp.getStatus());
        String body = resp.getEntity().toString();

        Pattern p = Pattern.compile("\\d{2}/\\d{2}/\\d{4} - \\d{2}:\\d{2}:\\d{2}");
        assertTrue(p.matcher(body).matches(),
                "Formato de data/hora inválido: " + body);
    }
}
