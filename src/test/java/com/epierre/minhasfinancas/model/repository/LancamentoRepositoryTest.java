package com.epierre.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

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
		Assertions.assertThat(lancamentoInexistente).isNull();
	}

	private Lancamento criarLancamento() {
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
