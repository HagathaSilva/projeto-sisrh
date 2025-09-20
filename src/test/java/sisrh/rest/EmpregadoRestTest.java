package sisrh.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import sisrh.banco.Banco;
import sisrh.dto.Empregado;

class EmpregadoRestTest {

	@Test
	void listarEmpregadosTeste() throws Exception {
	    Empregado e1 = novoEmpregado();
	    Empregado e2 = novoEmpregado();
	    List<Empregado> lista = Arrays.asList(e1, e2);

	    try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
	        mocked.when(Banco::listarEmpregados).thenReturn(lista);

	        EmpregadoRest resource = new EmpregadoRest();
	        Response resp = resource.listarEmpregados();

	        assertEquals(200, resp.getStatus());
	        Object entity = resp.getEntity();
	        assertNotNull(entity);

	        assertTrue(entity instanceof List<?>);
	        @SuppressWarnings("unchecked")
	        List<Empregado> result = (List<Empregado>) entity;

	        assertEquals(2, result.size());
	        assertSame(lista.get(0), result.get(0));
	        assertSame(lista.get(1), result.get(1));
	    }
	}


    @Test
    void obterEmpregado_quandoExiste_Teste() throws Exception {
        Empregado e = novoEmpregado();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarEmpregadoPorMatricula("123"))
                  .thenReturn(e);

            EmpregadoRest resource = new EmpregadoRest();
            Response resp = resource.obterEmpregado("123");

            assertEquals(200, resp.getStatus());
            assertSame(e, resp.getEntity());
        }
    }

    @Test
    void obterEmpregado_quandoNaoExiste_Teste() throws Exception {
        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarEmpregadoPorMatricula("999"))
                  .thenReturn(null);

            EmpregadoRest resource = new EmpregadoRest();
            Response resp = resource.obterEmpregado("999");

            assertEquals(404, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Empregado nao encontrado"));
        }
    }

    @Test
    void obterEmpregado_quandoLancaExcecao_Teste() throws Exception {
        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarEmpregadoPorMatricula("err"))
                  .thenThrow(new RuntimeException("boom"));

            EmpregadoRest resource = new EmpregadoRest();
            Response resp = resource.obterEmpregado("err");

            assertEquals(500, resp.getStatus());
            String body = resp.getEntity().toString();
            assertTrue(body.contains("Falha para obter empregado"));
            assertTrue(body.contains("boom"));
        }
    }

    @Test
    void incluirEmpregado_quandoOk_Teste() {
        Empregado input = novoEmpregado();
        Empregado salvo = novoEmpregado();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.incluirEmpregado(input)).thenReturn(salvo);

            EmpregadoRest resource = new EmpregadoRest();
            Response resp = resource.incluirEmpregado(input);

            assertEquals(200, resp.getStatus());
            assertSame(salvo, resp.getEntity());
        }
    }

    @Test
    void incluirEmpregado_quandoErro_Teste() {
        Empregado input = novoEmpregado();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.incluirEmpregado(input))
                  .thenThrow(new RuntimeException("falha ao incluir"));

            EmpregadoRest resource = new EmpregadoRest();
            Response resp = resource.incluirEmpregado(input);

            assertEquals(500, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Falha na inclusao"));
        }
    }

    @Test
    void alterarEmpregado_quandoNaoExiste_Teste() throws Exception {
        Empregado novo = novoEmpregado();
        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarEmpregadoPorMatricula("456"))
                  .thenReturn(null);

            EmpregadoRest resource = new EmpregadoRest();
            Response resp = resource.alterarEmpregado("456", novo);

            assertEquals(404, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Empregado nao encontrado"));
        }
    }

    @Test
    void alterarEmpregado_quandoExiste_Teste() throws Exception {
        Empregado novo = novoEmpregado();
        Empregado alterado = novoEmpregado();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarEmpregadoPorMatricula("456"))
                  .thenReturn(novo);
            mocked.when(() -> Banco.alterarEmpregado("456", novo))
                  .thenReturn(alterado);

            EmpregadoRest resource = new EmpregadoRest();
            Response resp = resource.alterarEmpregado("456", novo);

            assertEquals(200, resp.getStatus());
            assertSame(alterado, resp.getEntity());
        }
    }

    @Test
    void alterarEmpregado_quandoErro_Teste() throws Exception {
        Empregado novo = novoEmpregado();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarEmpregadoPorMatricula("456"))
                  .thenReturn(novo);
            mocked.when(() -> Banco.alterarEmpregado("456", novo))
                  .thenThrow(new RuntimeException("falha ao alterar"));

            EmpregadoRest resource = new EmpregadoRest();
            Response resp = resource.alterarEmpregado("456", novo);

            assertEquals(500, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Falha na alteracao"));
        }
    }

    @Test
    void excluirEmpregado_quandoNaoExiste_Teste() throws Exception {
        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarEmpregadoPorMatricula("777"))
                  .thenReturn(null);

            EmpregadoRest resource = new EmpregadoRest();
            Response resp = resource.excluirEmpregado("777");

            assertEquals(404, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Empregado nao encontrado"));
        }
    }

    @Test
    void excluirEmpregado_quandoExiste_Teste() throws Exception {
        Empregado existente = novoEmpregado();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarEmpregadoPorMatricula("777"))
                  .thenReturn(existente);

            mocked.when(() -> Banco.excluirEmpregado("777"))
                  .thenAnswer(inv -> null);

            EmpregadoRest resource = new EmpregadoRest();
            Response resp = resource.excluirEmpregado("777");

            assertEquals(200, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("excluido"));

            mocked.verify(() -> Banco.excluirEmpregado("777"));
        }
    }


    @Test
    void excluirEmpregado_quandoErro_Teste() throws Exception {
        Empregado existente = novoEmpregado();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarEmpregadoPorMatricula("777"))
                  .thenReturn(existente);
            mocked.when(() -> Banco.excluirEmpregado("777"))
                  .thenThrow(new RuntimeException("falha ao excluir"));

            EmpregadoRest resource = new EmpregadoRest();
            Response resp = resource.excluirEmpregado("777");

            assertEquals(500, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Falha na exclusao"));
        }
    }

    private Empregado novoEmpregado() {
        return mock(Empregado.class);
    }
}
