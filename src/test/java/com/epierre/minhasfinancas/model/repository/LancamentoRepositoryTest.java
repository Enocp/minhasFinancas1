package com.epierre.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.epierre.minhasfinancas.model.entity.Lancamento;
import com.epierre.minhasfinancas.model.enums.StatusLancamento;
import com.epierre.minhasfinancas.model.enums.TipoLancamento;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
//@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveSalvarUnLancamento() {
		// cenario
		Lancamento lancamento = Lancamento.builder().id(1l).ano(2020).mes(1).descricao("lancamento qualquer")
				.valor(BigDecimal.valueOf(10)).tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now()).build();

		lancamento = repository.save(lancamento);

		Assertions.assertThat(lancamento.getId()).isNotNull();

	}

	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());

		repository.delete(lancamento);

		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		assertThat(lancamentoInexistente).isNull();
	}
	

	@Test
	public void deveAtualizarUmLancamento () {
		
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		lancamento.setAno(2018);
		lancamento.setDescricao("Teste Atualizar");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		
		repository.save(lancamento);
		
		Lancamento lancamentoAtualizado =  entityManager.find(Lancamento.class, lancamento.getId());
		
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste Atualizar");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);

	}
	

	@Test
	public void deveBuscarUmLancamentoPorId() {
		Lancamento lancamento = criarEPersistirUmLancamento();

		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}
	
	private Lancamento criarEPersistirUmLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}

	public static Lancamento criarLancamento() {
		return Lancamento.builder()
				.id(1l)
				.ano(2020)
				.mes(1).descricao("lancamento qualquer")
				.valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now()).build();

	}
	
}
