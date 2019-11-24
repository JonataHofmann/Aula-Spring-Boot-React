package com.aula.minhasfinancas.service.imp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aula.minhasfinancas.exception.RegraNegocioException;
import com.aula.minhasfinancas.model.entity.Lancamento;
import com.aula.minhasfinancas.model.enums.StatusLancamento;
import com.aula.minhasfinancas.model.repository.LancamentoRepository;
import com.aula.minhasfinancas.service.LancamentoService;
import com.fasterxml.jackson.databind.BeanProperty.Bogus;

@Service
public class LancamentoServiceImpl implements LancamentoService{

	private LancamentoRepository repository;
	
	public LancamentoServiceImpl(LancamentoRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		return repository.save(lancamento);
	}

	@Override
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		
		repository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento filtro) {
		Example example = Example.of(filtro, 
				ExampleMatcher
				.matching()
				.withIgnoreCase()
				.withStringMatcher(StringMatcher.CONTAINING));
		
		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		validar(lancamento);
		atualizar(lancamento);
		
	}

	@Override
	public void validar(Lancamento lancamento) {
		
		if(lancamento.getDescricao()==null || lancamento.getDescricao().trim().isEmpty()) {
			throw new RegraNegocioException("Informe uma descrição válida.");
		}
		
		if(lancamento.getMes()==null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			throw new RegraNegocioException("Informe um mês válido.");
		}
		
		if(lancamento.getAno()==null || lancamento.getAno().toString().length() !=4 ) {
			throw new RegraNegocioException("Informe um ano válido.");
		}
		
		if(lancamento.getUsuario()==null || lancamento.getUsuario().getId()==null ) {
			throw new RegraNegocioException("Informe um usuário válido.");
		}
		
		if(lancamento.getValor()==null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1 ) {
			throw new RegraNegocioException("Informe um valor válido.");
		}
		
		if(lancamento.getStatus()==null ) {
			throw new RegraNegocioException("Informe um tipo de lançamento.");
		}
		
	}

}
