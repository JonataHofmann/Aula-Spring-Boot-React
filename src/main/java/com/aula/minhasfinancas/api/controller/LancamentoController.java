package com.aula.minhasfinancas.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aula.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.aula.minhasfinancas.api.dto.LancamentoDTO;
import com.aula.minhasfinancas.exception.RegraNegocioException;
import com.aula.minhasfinancas.model.entity.Lancamento;
import com.aula.minhasfinancas.model.entity.Usuario;
import com.aula.minhasfinancas.model.enums.StatusLancamento;
import com.aula.minhasfinancas.model.enums.TipoLancamento;
import com.aula.minhasfinancas.service.LancamentoService;
import com.aula.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

	private final LancamentoService service;
	private final UsuarioService usuarioService;

	@GetMapping
	public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano, @RequestParam("usuario") Long usuarioId) {
		Lancamento filtro = new Lancamento();
		filtro.setDescricao(descricao);
		filtro.setMes(mes);
		filtro.setAno(ano);

		Optional<Usuario> usuario = usuarioService.obterPorId(usuarioId);
		if (!usuario.isPresent()) {
			return ResponseEntity.badRequest()
					.body("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado.");
		} else {
			filtro.setUsuario(usuario.get());
		}

		List<Lancamento> lancamentos = service.buscar(filtro);
		return ResponseEntity.ok(lancamentos);
	}

	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		//return ResponseEntity.ok(dto);
	
	
		try {
			Lancamento entidade = converter(dto);
			entidade = service.salvar(entidade);
			return ResponseEntity.ok(entidade);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		/**/
	}

	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {

		return service.obterPorId(id).map(entity -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));

	

	}

	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {

		return service.obterPorId(id).map(entity -> {
			service.deletar(entity);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}

	@PutMapping("{id}/atualizar-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		
		return service.obterPorId(id).map(entity -> {
			
				StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			
				if(statusSelecionado==null) {
					return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, Status enviado é inválido.");
				}
				try {
					entity.setStatus(statusSelecionado);
					service.atualizar(entity);
					return ResponseEntity.ok(entity);
				} catch (RegraNegocioException e) {
					return ResponseEntity.badRequest().body(e.getMessage());
				}
		
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));

	
		
	}
	
	
	public Lancamento converter(LancamentoDTO dto) {
		
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());

		Usuario usuario = usuarioService.obterPorId(dto.getUsuarioId())
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado parao Id informado. "));

		lancamento.setUsuario(usuario);
		
		if (dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		
		if (dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}

		return lancamento;

	}
}
