package sisrh.soap;

import java.util.List;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.ws.WebServiceContext;

import sisrh.banco.Banco;
import sisrh.dto.Solicitacao;
import sisrh.dto.Solicitacoes;
import sisrh.seguranca.Autenticador;

@WebService
@SOAPBinding(style = Style.RPC)
public class ServicoSolicitacao {
	
    @Resource
    WebServiceContext context;

    @WebMethod(action = "listar")
    public Solicitacoes listar() throws Exception {
        Autenticador.autenticarUsuarioSenha(context);

        // Pegue o usuário exatamente como foi enviado no header
        String usuario = Autenticador.getUsuario(context);
        System.out.println("[ServicoSolicitacao] usuarioHeader=" + usuario);

        List<Solicitacao> lista = Banco.listarSolicitacoes(usuario);

        Solicitacoes solicitacoes = new Solicitacoes();
        for (Solicitacao s : lista) {
            solicitacoes.getSolicitacoes().add(s);
        }
        return solicitacoes;
    }
}
