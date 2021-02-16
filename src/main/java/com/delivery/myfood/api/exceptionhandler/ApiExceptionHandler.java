package com.delivery.myfood.api.exceptionhandler;

import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.delivery.myfood.domain.exception.EntidadeEmUsoException;
import com.delivery.myfood.domain.exception.EntidadeNaoEncontradaException;
import com.delivery.myfood.domain.exception.NegocioException;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<?> tratarEntidaEntidadeNaoEncontradaException(
			EntidadeNaoEncontradaException ex, WebRequest request){
		return handleExceptionInternal(ex, ex.getMessage(), null, HttpStatus.NOT_FOUND, request);
	}
	
	@ExceptionHandler(EntidadeEmUsoException.class)
	public ResponseEntity<?> tratarEntidadeEmUsoException(
			EntidadeEmUsoException ex, WebRequest request){
		return handleExceptionInternal(ex, ex.getMessage(), null, HttpStatus.CONFLICT, request);
	}
	
	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<?> tratarNegocioException(
			EntidadeNaoEncontradaException ex, WebRequest request){
		return handleExceptionInternal(ex, ex.getMessage(), null, HttpStatus.BAD_REQUEST, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		if(body == null) {
			body = Problema.builder()
					.dataHora(LocalDateTime.now())
					.mensagem(status.getReasonPhrase())
					.build();
			
		} else if(body instanceof String) {
			body = Problema.builder()
					.dataHora(LocalDateTime.now())
					.mensagem((String) body)
					.build();
		}
		
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}
}
