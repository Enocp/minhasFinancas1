package com.epierre.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epierre.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
