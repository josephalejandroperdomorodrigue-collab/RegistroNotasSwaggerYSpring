package com.colegio.lista.estudiantes;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Estudiante {

	private Long id;

	private String idEstudiante;

	private String estudiante;

	private String correoElectronico;

	private String grado;

	private String profesor;

	private String materia;

	private List<BigDecimal> notas = new ArrayList<>();

	private boolean comelon;

	private boolean canson;

	private boolean dormilon;

	private boolean ayudador;

	private boolean comparteComida;

	public Estudiante(Long id, EstudianteRequest request) {
		this.id = id;
		actualizarCon(request);
	}

	public void actualizarCon(EstudianteRequest request) {
		idEstudiante = request.idEstudiante();
		estudiante = request.estudiante();
		correoElectronico = request.correoElectronico();
		grado = request.grado();
		profesor = request.profesor();
		materia = request.materia();
		notas = new ArrayList<>(request.notas());
		comelon = request.comelon();
		canson = request.canson();
		dormilon = request.dormilon();
		ayudador = request.ayudador();
		comparteComida = request.comparteComida();
	}

	public Long getId() {
		return id;
	}

	@JsonProperty("ID")
	public String getIdEstudiante() {
		return idEstudiante;
	}

	public String getEstudiante() {
		return estudiante;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public String getGrado() {
		return grado;
	}

	public String getProfesor() {
		return profesor;
	}

	public String getMateria() {
		return materia;
	}

	public List<BigDecimal> getNotas() {
		return List.copyOf(notas);
	}

	public boolean isComelon() {
		return comelon;
	}

	public boolean isCanson() {
		return canson;
	}

	public boolean isDormilon() {
		return dormilon;
	}

	public boolean isAyudador() {
		return ayudador;
	}

	public boolean isComparteComida() {
		return comparteComida;
	}

	public BigDecimal getPromedioNotas() {
		return notas.stream()
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
	}

	public BigDecimal getPromedioAjustado() {
		BigDecimal promedio = getPromedioNotas();
		if (comelon && !comparteComida) {
			promedio = promedio.subtract(new BigDecimal("0.2"));
		}
		if (canson) {
			promedio = promedio.subtract(new BigDecimal("0.5"));
		}
		if (ayudador) {
			promedio = promedio.add(new BigDecimal("0.3"));
		}
		return promedio.max(BigDecimal.ZERO).min(new BigDecimal("5.0"))
				.setScale(2, RoundingMode.HALF_UP);
	}
}
