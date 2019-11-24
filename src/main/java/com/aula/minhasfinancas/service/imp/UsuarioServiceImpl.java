package com.aula.minhasfinancas.service.imp;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.aula.minhasfinancas.exception.ErroAutenticacao;
import com.aula.minhasfinancas.exception.RegraNegocioException;
import com.aula.minhasfinancas.model.entity.Usuario;
import com.aula.minhasfinancas.model.repository.UsuarioRepository;
import com.aula.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{
	
	
	private UsuarioRepository repository;
	

	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		
		Optional<Usuario> usuario = repository.findByEmail(email);	
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não encontrado.");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida.");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean hasUser = repository.existsByEmail(email);
		if(hasUser) {
			throw new RegraNegocioException("Já existe um usuário com este email.");
		}
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		// TODO Auto-generated method stub
		return repository.findById(id);
	}

}
