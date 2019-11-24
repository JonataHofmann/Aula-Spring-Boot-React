package com.aula.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aula.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
