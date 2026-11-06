package com.tecsup.petclinic.exceptions;

/**
 * Excepcion cuando no se encuentra un tipo de mascota
 */
public class PetTypeNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public PetTypeNotFoundException(String message) {
		super(message);
	}
}
