package com.aula.minhasfinancas.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario", schema = "financas")
@Builder
/*
 * Cria um builder para a classe, e não eh necessario as anotations
 * @Setter
 * @Getter
 * @EqualsAndHashCode
 * @ToString
 * @NoArgsConstructor
 * @AllArgsConstructor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "usuario_id")
	
	private Long id;	
	private String nome;
	private String email;	
	private String senha;
	
}
