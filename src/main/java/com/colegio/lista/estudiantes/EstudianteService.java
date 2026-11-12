package com.colegio.lista.estudiantes;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EstudianteService {

	private final Map<Long, Estudiante> estudiantes = new HashMap<>();
	private long siguienteId = 1;

	public synchronized List<Estudiante> listar() {
		return estudiantes.values().stream()
				.sorted((primero, segundo) -> primero.getId().compareTo(segundo.getId()))
				.toList();
	}

	public synchronized Estudiante buscar(Long id) {
		Estudiante estudiante = estudiantes.get(id);
		if (estudiante == null) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "No se encontró el estudiante con id " + id);
		}
		return estudiante;
	}

	public synchronized Estudiante crear(EstudianteRequest request) {
		if (existeIdEstudiante(request.idEstudiante(), null)) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT, "Ya existe un estudiante con ese ID");
		}
		Estudiante estudiante = new Estudiante(siguienteId++, request);
		estudiantes.put(estudiante.getId(), estudiante);
		return estudiante;
	}

	public synchronized Estudiante actualizar(Long id, EstudianteRequest request) {
		Estudiante estudiante = buscar(id);
		if (existeIdEstudiante(request.idEstudiante(), id)) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT, "Ya existe un estudiante con ese ID");
		}
		estudiante.actualizarCon(request);
		return estudiante;
	}

	public synchronized void eliminar(Long id) {
		buscar(id);
		estudiantes.remove(id);
	}

	private boolean existeIdEstudiante(String idEstudiante, Long excluirId) {
		return estudiantes.values().stream()
				.anyMatch(estudiante -> !estudiante.getId().equals(excluirId)
						&& estudiante.getIdEstudiante().equalsIgnoreCase(idEstudiante));
	}
}
