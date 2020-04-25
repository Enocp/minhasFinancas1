package com.epierre.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;

import com.epierre.minhasfinancas.exception.RegraNegocioException;
import com.epierre.minhasfinancas.model.entity.Lancamento;
import com.epierre.minhasfinancas.model.entity.Usuario;
import com.epierre.minhasfinancas.model.enums.StatusLancamento;
import com.epierre.minhasfinancas.model.repository.LancamentoRepository;
import com.epierre.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.epierre.minhasfinancas.service.impl.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl service;
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento () {
		//cenario
		Lancamento lancamentoAsalvar = LancamentoRepositoryTest.criarLancamento();
		
		Mockito.doNothing().when(service).validar(lancamentoAsalvar);
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoAsalvar)).thenReturn(lancamentoAsalvar);
		//execucao
		Lancamento lancamento = service.salvar(lancamentoAsalvar);
		
		//verificacao
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDevalidacao() {
		//cenario
		Lancamento lancamentoAsalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoAsalvar);
		//execucao
		Assertions.catchThrowableOfType( () -> service.salvar(lancamentoAsalvar), RegraNegocioException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamentoAsalvar);

	}
	
	@Test
	public void deveAtualizarUmLancamento () {
		//cenario
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);

		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		//execucao
		service.atualizar(lancamentoSalvo);
		
		//verificacao
		Mockito.verify(repository,Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancaErroAoTentarAtualizarLancamentoQueNaoAindaSalvo() {
		//cenario
		Lancamento lancamentoAsalvar = LancamentoRepositoryTest.criarLancamento();
		
		//execucao e verificaçao
		//Assertions.catchThrowableOfType(shouldRaiseThrowable, type)catchThrowableOfType( () -> service.atualizar(lancamentoAsalvar), NullPointerException.class);
		Assertions.catchThrowableOfType( () -> service.atualizar(lancamentoAsalvar), NullPointerException.class);

		Mockito.verify(repository, Mockito.never()).save(lancamentoAsalvar);

	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		//lancamento.setId(1l);
		//execucao
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);
		//verificacao
		 //Mockito.verify(repository, Mockito.never()).delete(lancamento);
		Mockito.verify( repository ).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when( repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
        
		//execucao
		List<Lancamento> resultado = service.buscar(lancamento);
		
		//verificacoes
			Assertions.assertThat(resultado)
					.isNotEmpty()
					.hasSize(1)
					.contains(lancamento);	

	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
       //execucao
		service.atualizarStatus(lancamento, novoStatus);
		
		//verificacoes
	    Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
	    Mockito.verify(service).atualizar(lancamento);
	    
	}
	
	@Test
	public void deveObterUmLancamentoPorID() {
		//cenário
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		//execucao
		Optional<Lancamento> resultado =  service.obterPorId(id);
		
		//verificacao
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveREtornarVazioQuandoOLancamentoNaoExiste() {
		//cenário
		Long id = 1l;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when( repository.findById(id) ).thenReturn( Optional.empty() );
		
		//execucao
		Optional<Lancamento> resultado =  service.obterPorId(id);
		
		//verificacao
		Assertions.assertThat(resultado.isPresent()).isFalse();
	
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();

		Throwable erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");

		lancamento.setDescricao("");

		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");

		lancamento.setDescricao("Salario");

		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

		lancamento.setAno(0);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

		lancamento.setAno(13);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

		lancamento.setMes(1);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");

		lancamento.setAno(202);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");

		lancamento.setAno(2020);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");

		lancamento.setUsuario(new Usuario());

		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");

		lancamento.getUsuario().setId(1l);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");

		lancamento.setValor(BigDecimal.ZERO);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");

		lancamento.setValor(BigDecimal.valueOf(1));

		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de Lançamento.");

		
	}
	

}
