package com.tecsup.petclinic.exceptions;

/**
 * Excepcion cuando no se encuentra un dueno
 */
public class OwnerNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public OwnerNotFoundException(String message) {
		super(message);
	}
}
