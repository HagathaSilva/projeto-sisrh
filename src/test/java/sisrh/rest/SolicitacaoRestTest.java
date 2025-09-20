package sisrh.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import sisrh.banco.Banco;
import sisrh.dto.Solicitacao;

class SolicitacaoRestTest {

	@Test
	void listarSolicitacoes_deveRetornarOkComLista() throws Exception {
	    Solicitacao s1 = novaSolicitacao();
	    Solicitacao s2 = novaSolicitacao();

	    List<Solicitacao> lista = Arrays.asList(s1, s2);

	    try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
	        mocked.when(Banco::listarSolicitacoes).thenReturn(lista);

	        SolicitacaoRest resource = new SolicitacaoRest();
	        Response resp = resource.listarSolicitacoes();

	        assertEquals(200, resp.getStatus());
	        Object entity = resp.getEntity();
	        assertNotNull(entity);

	        assertTrue(entity instanceof List<?>);
	        @SuppressWarnings("unchecked")
	        List<Solicitacao> result = (List<Solicitacao>) entity;

	        assertEquals(2, result.size());
	        assertSame(lista.get(0), result.get(0));
	        assertSame(lista.get(1), result.get(1));
	    }
	}


    @Test
    void obterSolicitacao_quandoExiste_deveRetornarOk() throws Exception {
        Solicitacao s = novaSolicitacao();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarSolicitacaoPorId(10))
                  .thenReturn(s);

            SolicitacaoRest resource = new SolicitacaoRest();
            Response resp = resource.obterSolicitacao(10);

            assertEquals(200, resp.getStatus());
            assertSame(s, resp.getEntity());
        }
    }

    @Test
    void obterSolicitacao_quandoNaoExiste_deveRetornar404() throws Exception {
        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarSolicitacaoPorId(999))
                  .thenReturn(null);

            SolicitacaoRest resource = new SolicitacaoRest();
            Response resp = resource.obterSolicitacao(999);

            assertEquals(404, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Solicitacao nao encontrada"));
        }
    }

    @Test
    void obterSolicitacao_quandoErro_deveRetornar500() throws Exception {
        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarSolicitacaoPorId(13))
                  .thenThrow(new RuntimeException("boom"));

            SolicitacaoRest resource = new SolicitacaoRest();
            Response resp = resource.obterSolicitacao(13);

            assertEquals(500, resp.getStatus());
            String body = resp.getEntity().toString();
            assertTrue(body.contains("Falha para obter solicitacao"));
            assertTrue(body.contains("boom"));
        }
    }

    @Test
    void incluirSolicitacao_ok_deveRetornar200() {
        Solicitacao input = novaSolicitacao();
        Solicitacao salvo = novaSolicitacao();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.incluirSolicitacao(input)).thenReturn(salvo);

            SolicitacaoRest resource = new SolicitacaoRest();
            Response resp = resource.incluirSolicitacao(input);

            assertEquals(200, resp.getStatus());
            assertSame(salvo, resp.getEntity());
        }
    }

    @Test
    void incluirSolicitacao_quandoErro_deveRetornar500() {
        Solicitacao input = novaSolicitacao();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.incluirSolicitacao(input))
                  .thenThrow(new RuntimeException("erro incluir"));

            SolicitacaoRest resource = new SolicitacaoRest();
            Response resp = resource.incluirSolicitacao(input);

            assertEquals(500, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Falha na inclusao"));
        }
    }

    @Test
    void alterarSolicitacao_quandoNaoExiste_deveRetornar404() throws Exception {
        Solicitacao nova = novaSolicitacao();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarSolicitacaoPorId(50))
                  .thenReturn(null);

            SolicitacaoRest resource = new SolicitacaoRest();
            Response resp = resource.alterarSolicitacao(50, nova);

            assertEquals(404, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Solicitacao nao encontrada"));
        }
    }

    @Test
    void alterarSolicitacao_quandoExiste_deveRetornar200() throws Exception {
        Solicitacao existente = novaSolicitacao();
        Solicitacao alterada = novaSolicitacao();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarSolicitacaoPorId(50))
                  .thenReturn(existente);
            mocked.when(() -> Banco.alterarSolicitacao(50, existente))
                  .thenReturn(alterada);

            SolicitacaoRest resource = new SolicitacaoRest();
            Response resp = resource.alterarSolicitacao(50, existente);

            assertEquals(200, resp.getStatus());
            assertSame(alterada, resp.getEntity());
        }
    }

    @Test
    void alterarSolicitacao_quandoErro_deveRetornar500() throws Exception {
        Solicitacao existente = novaSolicitacao();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarSolicitacaoPorId(50))
                  .thenReturn(existente);
            mocked.when(() -> Banco.alterarSolicitacao(50, existente))
                  .thenThrow(new RuntimeException("erro alterar"));

            SolicitacaoRest resource = new SolicitacaoRest();
            Response resp = resource.alterarSolicitacao(50, existente);

            assertEquals(500, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Falha na alteracao"));
        }
    }

    @Test
    void excluirSolicitacao_quandoNaoExiste_deveRetornar404() throws Exception {
        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarSolicitacaoPorId(80))
                  .thenReturn(null);

            SolicitacaoRest resource = new SolicitacaoRest();
            Response resp = resource.excluirSolicitacao(80);

            assertEquals(404, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Solicitacao nao encontrada"));
        }
    }

    @Test
    void excluirSolicitacao_quandoExiste_deveRetornar200() throws Exception {
        Solicitacao existente = novaSolicitacao();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarSolicitacaoPorId(80))
                  .thenReturn(existente);

            mocked.when(() -> Banco.excluirSolicitacao(80))
                  .thenAnswer(inv -> null);

            SolicitacaoRest resource = new SolicitacaoRest();
            Response resp = resource.excluirSolicitacao(80);

            assertEquals(200, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("excluida"));

            mocked.verify(() -> Banco.excluirSolicitacao(80));
        }
    }

    @Test
    void excluirSolicitacao_quandoErro_deveRetornar500() throws Exception {
        Solicitacao existente = novaSolicitacao();

        try (MockedStatic<Banco> mocked = mockStatic(Banco.class)) {
            mocked.when(() -> Banco.buscarSolicitacaoPorId(80))
                  .thenReturn(existente);
            mocked.when(() -> Banco.excluirSolicitacao(80))
                  .thenThrow(new RuntimeException("erro excluir"));

            SolicitacaoRest resource = new SolicitacaoRest();
            Response resp = resource.excluirSolicitacao(80);

            assertEquals(500, resp.getStatus());
            assertTrue(resp.getEntity().toString().contains("Falha na exclusao"));
        }
    }

    private Solicitacao novaSolicitacao() {
        return mock(Solicitacao.class);
    }
}
