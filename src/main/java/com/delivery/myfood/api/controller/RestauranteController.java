package com.delivery.myfood.api.controller;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delivery.myfood.domain.exception.EntidadeNaoEncontradaException;
import com.delivery.myfood.domain.model.Restaurante;
import com.delivery.myfood.domain.repository.RestauranteRepository;
import com.delivery.myfood.domain.service.CadastroRestauranteService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private CadastroRestauranteService cadastroRestaurante;

	@GetMapping
	public List<Restaurante> listar() {
		return restauranteRepository.findAll();
	}

	@GetMapping("/{restauranteId}")
	public ResponseEntity<Restaurante> buscar(@PathVariable Long restauranteId) {

		Restaurante restaurante = restauranteRepository.findById(restauranteId)
				.orElse(null);

		if (restaurante != null) {
			return ResponseEntity.ok(restaurante);
		}

		return ResponseEntity.notFound().build();

	}

	@PostMapping
	public ResponseEntity<?> adicionar(@RequestBody Restaurante restaurante) {

		restaurante = cadastroRestaurante.salvar(restaurante);

		return ResponseEntity.status(HttpStatus.CREATED).body(restaurante);

	}

	@PutMapping("/{restauranteId}")
	public ResponseEntity<?> atualizar(@PathVariable Long restauranteId, 
			@RequestBody Restaurante restaurante) {
		try {
			Restaurante restauranteAtual = restauranteRepository.findById(restauranteId)
					.orElse(null);

			if (restauranteAtual != null) {
				BeanUtils.copyProperties(restaurante, restauranteAtual, "id");
				restauranteAtual = cadastroRestaurante.salvar(restauranteAtual);

				return ResponseEntity.ok(restauranteAtual);
			}

			return ResponseEntity.notFound().build();

		} catch (EntidadeNaoEncontradaException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PatchMapping("/{restauranteId}")
	public ResponseEntity<?> atualizarParcial(@PathVariable Long restauranteId,
			@RequestBody Map<String, Object> campos) {

		Restaurante restauranteAtual = restauranteRepository.findById(restauranteId)
				.orElse(null);

		if (restauranteAtual == null) {
			return ResponseEntity.notFound().build();
		}
		
		merge(campos, restauranteAtual);

		return atualizar(restauranteId, restauranteAtual);
	}
	
	private void  merge(Map<String, Object> dadosOrigem, Restaurante restauranteDestino) {
		ObjectMapper objectMapper = new ObjectMapper();
		Restaurante restauranteOrigem = objectMapper.convertValue(dadosOrigem, Restaurante.class);
		
		dadosOrigem.forEach((nomePropriedade, valorPropriedade) ->{
			Field field = org.springframework.util.ReflectionUtils.findField(Restaurante.class, nomePropriedade);
			field.setAccessible(true);
			
			Object novoValor = ReflectionUtils.getField(field, restauranteOrigem);
			
			
			ReflectionUtils.setField(field, restauranteDestino, novoValor);
			
		});		
	}
	
	@GetMapping("/teste")
	public List<Restaurante> teste(String nome, 
			BigDecimal taxaFreteInicial,BigDecimal taxaFreteFinal) {
		return restauranteRepository.find(nome,taxaFreteInicial,taxaFreteFinal);
	}
	
	@GetMapping("/com-frete-gratis")
	public List<Restaurante> restauranteComFreteGratis(String nome) {
		return restauranteRepository.findComFreteGratis(nome);
	}
	
	@GetMapping("/primeiro")
	public Optional<Restaurante> restaurantePrimeiro() {	
		return restauranteRepository.buscarPrimeiro();
	}

}
