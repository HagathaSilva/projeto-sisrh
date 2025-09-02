package sisrh.dto;

import java.util.Date;
import javax.xml.bind.annotation.*;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Empregado {
	
	@XmlElement(name = "matricula")
	private String matricula;
	@XmlElement(name = "nome")
	private String nome;
	@XmlElement(name = "admissao")
	private Date admissao;
	@XmlElement(name = "desligamento")
	private Date desligamento;
	@XmlElement(name = "salario")
	private Double salario;

	public Empregado() {

	}

	public Empregado(String matricula, String nome, Date admissao, Date desligamento, Double salario) {
		super();
		this.matricula = matricula;
		this.nome = nome;
		this.admissao = admissao;
		this.desligamento = desligamento;
		this.salario = salario;
	}

	public String getMatricula() {
		return matricula;
	}

	public String getNome() {
		return nome;
	}

	public Date getAdmissao() {
		return admissao;
	}

	public Date getDesligamento() {
		return desligamento;
	}

	public Double getSalario() {
		return salario;
	}
}
