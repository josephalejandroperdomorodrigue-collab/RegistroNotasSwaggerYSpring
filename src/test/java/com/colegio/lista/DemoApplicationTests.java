package com.colegio.lista;

import com.colegio.lista.estudiantes.Estudiante;
import com.colegio.lista.estudiantes.EstudianteRequest;
import com.colegio.lista.estudiantes.EstudianteService;
import com.colegio.lista.estudiantes.TerminalCuestionario;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "app.questionnaire.enabled=false")
@AutoConfigureMockMvc
class DemoApplicationTests {

	@Autowired
	private EstudianteService estudianteService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private Validator validator;

	@Test
	void contextLoads() {
	}

	@Test
	void canCreateUpdateAndDeleteAnEstudiante() {
		String idEstudiante = "EST-" + UUID.randomUUID();
		var request = new EstudianteRequest(
				idEstudiante, "Alex", "alex@example.com", "10A", "Profe Ana",
				"Ciencias", List.of(new BigDecimal("4.50")),
				false, false, false, true, false);

		Estudiante created = estudianteService.crear(request);
		assertEquals("Alex", created.getEstudiante());
		assertEquals(idEstudiante, created.getIdEstudiante());
		assertEquals("alex@example.com", created.getCorreoElectronico());
		assertEquals("10A", created.getGrado());
		assertEquals(false, created.isComparteComida());
		assertEquals(new BigDecimal("4.50"), created.getNotas().get(0));
		assertEquals(new BigDecimal("4.80"), created.getPromedioAjustado());
		assertTrue(estudianteService.listar().stream()
				.anyMatch(estudiante -> estudiante.getId().equals(created.getId())));

		EstudianteRequest updatedRequest = new EstudianteRequest(
				idEstudiante, "Alex Rivera", "alex@example.com", "10A", "Profe Ana",
				"Ciencias", List.of(new BigDecimal("5.00")),
				false, false, false, true, false);
		assertEquals("Alex Rivera",
				estudianteService.actualizar(created.getId(), updatedRequest).getEstudiante());

		estudianteService.eliminar(created.getId());
		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> estudianteService.buscar(created.getId()));
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	@Test
	void studentIdMustBeUnique() {
		String idEstudiante = "EST-" + UUID.randomUUID();
		var request = new EstudianteRequest(
				idEstudiante, "Sam", "sam@example.com", "9B", "Profe Luis",
				"Historia", List.of(new BigDecimal("4.00")),
				false, false, false, false, false);
		estudianteService.crear(request);

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> estudianteService.crear(request));
		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
	}

	@Test
	void requestUsesUppercaseIdProperty() throws Exception {
		String json = """
				{
				  "ID": "EST-003",
				  "estudiante": "Taylor",
				  "correoElectronico": "taylor@example.com",
				  "grado": "8A",
				  "profesor": "Profe Sol",
				  "materia": "Arte",
				  "notas": [4.5],
				  "comelon": false,
				  "canson": false,
				  "dormilon": false,
				  "ayudador": true,
				  "comparteComida": false
				}
				""";

		ObjectMapper objectMapper = new ObjectMapper();
		EstudianteRequest request = objectMapper.readValue(json, EstudianteRequest.class);
		assertEquals("EST-003", request.idEstudiante());
		assertEquals("EST-003",
				objectMapper.readTree(objectMapper.writeValueAsString(request)).get("ID").asText());
	}

	@Test
	void postEndpointAcceptsAndReturnsUppercaseId() throws Exception {
		String idEstudiante = "EST-" + UUID.randomUUID();
		String json = """
				{
				  "ID": "%s",
				  "estudiante": "Jordan",
				  "correoElectronico": "jordan@example.com",
				  "grado": "7B",
				  "profesor": "Profe Mar",
				  "materia": "Música",
				  "notas": [4.5],
				  "comelon": false,
				  "canson": false,
				  "dormilon": false,
				  "ayudador": true,
				  "comparteComida": false
				}
				""".formatted(idEstudiante);

		mockMvc.perform(post("/api/estudiantes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.ID").value(idEstudiante))
				.andExpect(jsonPath("$.codigoEstudiante").doesNotExist());
	}

	@Test
	void adjustedAverageAppliesFoodBehaviorAndLimits() {
		var sharingRequest = new EstudianteRequest(
				"EST-" + UUID.randomUUID(), "Casey", "casey@example.com", "8A", "Profe",
				"Ciencias", List.of(new BigDecimal("4.0"), new BigDecimal("4.0")),
				true, true, true, true, true);
		Estudiante sharesFood = new Estudiante(20L, sharingRequest);
		assertEquals(new BigDecimal("3.80"), sharesFood.getPromedioAjustado());

		var nonSharingRequest = new EstudianteRequest(
				"EST-" + UUID.randomUUID(), "Riley", "riley@example.com", "8A", "Profe",
				"Ciencias", List.of(new BigDecimal("4.0"), new BigDecimal("4.0")),
				true, false, false, false, false);
		Estudiante doesNotShareFood = new Estudiante(21L, nonSharingRequest);
		assertEquals(new BigDecimal("3.80"), doesNotShareFood.getPromedioAjustado());

		var cappedRequest = new EstudianteRequest(
				"EST-" + UUID.randomUUID(), "Avery", "avery@example.com", "8A", "Profe",
				"Ciencias", List.of(new BigDecimal("5.0")),
				false, false, false, true, false);
		Estudiante capped = new Estudiante(22L, cappedRequest);
		assertEquals(new BigDecimal("5.00"), capped.getPromedioAjustado());
	}

	@Test
	void terminalQuestionnaireStartsImmediatelyAndPrintsStudentSummary() {
		String idEstudiante = "EST-" + UUID.randomUUID();
		String respuestas = String.join("\n",
				idEstudiante,
				"Jamie Prueba",
				"jamie@example.com",
				"8A",
				"Ciencias",
				"Profe Ana",
				"4, 4",
				"s",
				"n",
				"n",
				"s",
				"n",
				"n") + "\n";
		ByteArrayOutputStream salida = new ByteArrayOutputStream();
		PrintStream salidaOriginal = System.out;
		var entradaOriginal = System.in;

		try {
			System.setIn(new ByteArrayInputStream(respuestas.getBytes(StandardCharsets.UTF_8)));
			System.setOut(new PrintStream(salida, true, StandardCharsets.UTF_8));

			new TerminalCuestionario(estudianteService, validator).run();
		} finally {
			System.setIn(entradaOriginal);
			System.setOut(salidaOriginal);
		}

		String texto = salida.toString(StandardCharsets.UTF_8);
		assertTrue(texto.startsWith("Cuestionario para registrar estudiantes"));
		assertTrue(texto.contains("ID del estudiante:"));
		assertTrue(texto.contains("Resumen de estudiantes registrados:"));
		assertTrue(texto.contains("ID: " + idEstudiante + " | Nombre: Jamie Prueba | Notas: [4, 4]"));
		assertTrue(texto.contains("Promedio ajustado: 3.80"));
		assertTrue(texto.contains("Se tiene en cuenta que es dormilón"));
	}

	@Test
	void swaggerUsesCrudOperationOrder() throws Exception {
		mockMvc.perform(get("/swagger-ui/swagger-initializer.js"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(
						"post: 0, get: 1, put: 2, delete: 3")));
	}
}
