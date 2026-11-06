package com.tecsup.petclinic.exceptions;

/**
 * Excepcion cuando se intenta asignar una especialidad ya asignada al veterinario
 */
public class VetSpecialtyAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public VetSpecialtyAlreadyExistsException(String message) {
		super(message);
	}
}
