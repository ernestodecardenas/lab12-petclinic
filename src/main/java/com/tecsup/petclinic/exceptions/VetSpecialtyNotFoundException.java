package com.tecsup.petclinic.exceptions;

/**
 * Excepcion cuando un veterinario no tiene asignada la especialidad
 */
public class VetSpecialtyNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public VetSpecialtyNotFoundException(String message) {
		super(message);
	}
}
