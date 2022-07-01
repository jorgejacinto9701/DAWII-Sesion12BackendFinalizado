package com.empresa.entidades;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataRegistroBoleta {
	
	private Cliente cliente;
	private List<Seleccion> seleccionados;
}
