package com.delivery.myfood.api.controller;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
	public Restaurante buscar(@PathVariable Long restauranteId) {
		return cadastroRestaurante.buscarOuFalhar(restauranteId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Restaurante adicionar(@RequestBody Restaurante restaurante) {
		return cadastroRestaurante.salvar(restaurante);
	}

	@PutMapping("/{restauranteId}")
	public Restaurante atualizar(@PathVariable Long restauranteId, 
			@RequestBody Restaurante restaurante) {
		Restaurante restauranteAtual = cadastroRestaurante.buscarOuFalhar(restauranteId);
		
		BeanUtils.copyProperties(restaurante, restauranteAtual, 
					"id", "formasPagamento", "endereco", "dataCadastro", "produtos");
		return cadastroRestaurante.salvar(restauranteAtual);
	}

	@PatchMapping("/{restauranteId}")
	public Restaurante atualizarParcial(@PathVariable Long restauranteId,
			@RequestBody Map<String, Object> campos) {
		Restaurante restauranteAtual = cadastroRestaurante.buscarOuFalhar(restauranteId);
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
