package com.colegio.lista.estudiantes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@Tag(name = "Estudiantes", description = "Administración de la lista de estudiantes")
public class EstudianteController {

	private final EstudianteService service;

	public EstudianteController(EstudianteService service) {
		this.service = service;
	}

	@PostMapping
	@Operation(summary = "Registrar un estudiante")
	public ResponseEntity<Estudiante> crear(@Valid @RequestBody EstudianteRequest request) {
		Estudiante estudiante = service.crear(request);
		return ResponseEntity.created(URI.create("/api/estudiantes/" + estudiante.getId()))
				.body(estudiante);
	}

	@GetMapping
	@Operation(summary = "Listar todos los estudiantes")
	public List<Estudiante> listar() {
		return service.listar();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Consultar un estudiante por su id")
	public Estudiante buscar(@PathVariable Long id) {
		return service.buscar(id);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar un estudiante")
	public Estudiante actualizar(
			@PathVariable Long id,
			@Valid @RequestBody EstudianteRequest request) {
		return service.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar un estudiante")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		service.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
