package com.colegio.lista.estudiantes;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
@ConditionalOnProperty(name = "app.questionnaire.enabled", havingValue = "true")
public class TerminalCuestionario implements CommandLineRunner {

	private final EstudianteService service;
	private final Validator validator;

	public TerminalCuestionario(EstudianteService service, Validator validator) {
		this.service = service;
		this.validator = validator;
	}

	@Override
	public void run(String... args) {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Cuestionario para registrar estudiantes");
			while (true) {
				EstudianteRequest request = preguntarEstudiante(scanner);
				List<String> errores = validator.validate(request).stream()
						.map(ConstraintViolation::getMessage)
						.toList();

				if (!errores.isEmpty()) {
					System.out.println("Revisa estos datos:");
					errores.forEach(error -> System.out.println("- " + error));
				} else {
					try {
						Estudiante estudiante = service.crear(request);
						System.out.println("Estudiante registrado. ID interno: "
								+ estudiante.getId());
					} catch (ResponseStatusException exception) {
						if (exception.getStatusCode() != HttpStatus.CONFLICT) {
							throw exception;
						}
						System.out.println(exception.getReason());
					}
				}

				if (!preguntarSi(scanner, "¿Deseas registrar otro estudiante?")) {
					break;
				}
			}
			mostrarResumen();
		}
	}

	private EstudianteRequest preguntarEstudiante(Scanner scanner) {
		System.out.println();
		String id = preguntarTexto(scanner, "ID del estudiante:");
		String nombre = preguntarTexto(scanner, "Nombre del estudiante:");
		String correo = preguntarTexto(scanner, "Correo electrónico:");
		String grado = preguntarTexto(scanner, "Grado:");
		String materia = preguntarTexto(scanner, "Materia:");
		String profesor = preguntarTexto(scanner, "Profesor:");
		List<BigDecimal> notas = preguntarNotas(scanner);

		boolean comelon = preguntarSi(scanner, "¿Es comelón en clase?");
		boolean comparteComida = comelon
				&& preguntarSi(scanner, "¿Comparte su comida con los demás?");

		return new EstudianteRequest(
				id,
				nombre,
				correo,
				grado,
				profesor,
				materia,
				notas,
				comelon,
				preguntarSi(scanner, "¿Es cansón en clase?"),
				preguntarSi(scanner, "¿Es dormilón?"),
				preguntarSi(scanner, "¿Es ayudador?"),
				comparteComida);
	}

	private void mostrarResumen() {
		List<Estudiante> estudiantes = service.listar();
		System.out.println();
		System.out.println("Resumen de estudiantes registrados: " + estudiantes.size());
		if (estudiantes.isEmpty()) {
			System.out.println("No se registraron estudiantes.");
			return;
		}

		for (Estudiante estudiante : estudiantes) {
			System.out.printf(
					"ID: %s | Nombre: %s | Notas: %s | Promedio: %s | Promedio ajustado: %s%n",
					estudiante.getIdEstudiante(),
					estudiante.getEstudiante(),
					estudiante.getNotas(),
					estudiante.getPromedioNotas(),
					estudiante.getPromedioAjustado());
			if (estudiante.isDormilon()) {
				System.out.println("  Se tiene en cuenta que es dormilón; esto no modifica el promedio.");
			}
		}
	}

	private String preguntarTexto(Scanner scanner, String pregunta) {
		System.out.print(pregunta + " ");
		if (!scanner.hasNextLine()) {
			throw new IllegalStateException("El cuestionario necesita una terminal interactiva.");
		}
		return scanner.nextLine().trim();
	}

	private List<BigDecimal> preguntarNotas(Scanner scanner) {
		while (true) {
			System.out.print("Notas (separadas por coma, de 0 a 5): ");
			if (!scanner.hasNextLine()) {
				throw new IllegalStateException("El cuestionario necesita una terminal interactiva.");
			}
			String respuesta = scanner.nextLine().trim();
			try {
				List<BigDecimal> notas = new ArrayList<>();
				for (String nota : respuesta.split(",")) {
					notas.add(new BigDecimal(nota.trim()));
				}
				if (!notas.isEmpty()) {
					return notas;
				}
			} catch (NumberFormatException exception) {
				System.out.println("Escribe las notas como números, por ejemplo: 4.2, 4.8");
			}
		}
	}

	private boolean preguntarSi(Scanner scanner, String pregunta) {
		while (true) {
			System.out.print(pregunta + " (s/n): ");
			if (!scanner.hasNextLine()) {
				throw new IllegalStateException("El cuestionario necesita una terminal interactiva.");
			}
			String respuesta = scanner.nextLine().trim();
			if (respuesta.equalsIgnoreCase("s") || respuesta.equalsIgnoreCase("si")
					|| respuesta.equalsIgnoreCase("sí")) {
				return true;
			}
			if (respuesta.equalsIgnoreCase("n") || respuesta.equalsIgnoreCase("no")) {
				return false;
			}
			System.out.println("Responde s o n.");
		}
	}
}
