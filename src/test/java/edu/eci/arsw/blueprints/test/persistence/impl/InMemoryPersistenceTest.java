/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.test.persistence.impl;

import edu.eci.arsw.blueprints.filter.Filter;
import edu.eci.arsw.blueprints.filter.impl.Default;
import edu.eci.arsw.blueprints.filter.impl.Redundancy;
import edu.eci.arsw.blueprints.filter.impl.Subsampling;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.model.Response;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.impl.InMemoryBlueprintPersistence;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import org.junit.Before;
import org.junit.Test;


import java.util.HashMap;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;


/**
 *
 * @author hcadavid
 */
public class InMemoryPersistenceTest {

    private BlueprintsServices services;

    @Before
    public void setup() {
        InMemoryBlueprintPersistence persistence = new InMemoryBlueprintPersistence();
        Map<String, Filter> filters = new HashMap<>();
        filters.put("default", new Default());
        filters.put("redundancy", new Redundancy());
        filters.put("subsampling", new Subsampling());
        services = new BlueprintsServices(persistence, filters);
    }

    /* =====================================================
     * Sección 1: Pruebas Directas de Persistencia
     * (Operan directamente sobre InMemoryBlueprintPersistence)
     * =====================================================
     */
    @Test
    public void saveNewAndLoadTest() throws BlueprintPersistenceException, BlueprintNotFoundException {
        InMemoryBlueprintPersistence ibpp = new InMemoryBlueprintPersistence();

        Point[] pts0 = new Point[]{new Point(40, 40), new Point(15, 15)};
        Blueprint bp0 = new Blueprint("mack", "mypaint", pts0);
        ibpp.saveBlueprint(bp0);

        Point[] pts = new Point[]{new Point(0, 0), new Point(10, 10)};
        Blueprint bp = new Blueprint("john", "thepaint", pts);
        ibpp.saveBlueprint(bp);

        assertNotNull("Loading a previously stored blueprint returned null.", ibpp.getBlueprint(bp.getAuthor(), bp.getName()));
        assertEquals("Loading a previously stored blueprint returned a different blueprint.", ibpp.getBlueprint(bp.getAuthor(), bp.getName()), bp);
    }

    /* =====================================================
     * Sección 2: Pruebas de Filtros Individuales (métodos filterBlueprint)
     * =====================================================
     */
    @Test
    public void testDefaultFilter() {
        Filter defaultFilter = new Default();
        Point p1 = new Point(0, 0);
        Point p2 = new Point(1, 1);
        Point p3 = new Point(2, 2);
        Blueprint bp = new Blueprint("Author", "BP1", new Point[]{p1, p2, p3});
        Blueprint filteredBP = defaultFilter.filterBlueprint(bp);
        assertEquals(bp, filteredBP);
    }

    @Test
    public void testRedundancyFilter() {
        Filter redundancyFilter = new Redundancy();
        Point p1 = new Point(0, 0);
        Point p1_dup = p1;
        Point p3 = new Point(1, 1);
        Point p3_dup = p3;
        Blueprint bp = new Blueprint("Author", "BP2", new Point[]{p1, p1_dup, p3, p3_dup});
        Blueprint filteredBP = redundancyFilter.filterBlueprint(bp);
        Blueprint expected = new Blueprint("Author", "BP2", new Point[]{p1, p3});
        assertEquals(expected, filteredBP);
    }

    @Test
    public void testSubsamplingFilter() {
        Filter subsamplingFilter = new Subsampling();
        Point p1 = new Point(0, 0);
        Point p2 = new Point(1, 1);
        Point p3 = new Point(2, 2);
        Point p4 = new Point(3, 3);
        Blueprint bp = new Blueprint("Author", "BP3", new Point[]{p1, p2, p3, p4});
        Blueprint filteredBP = subsamplingFilter.filterBlueprint(bp);
        Blueprint expected = new Blueprint("Author", "BP3", new Point[]{p1, p3});
        assertEquals(expected, filteredBP);
    }

    /* =====================================================
     * Sección 3: Pruebas de Operaciones Básicas de BlueprintsServices
     * (Registro, Consulta Individual, Actualización y Eliminación)
     * =====================================================
     */
    @Test
    public void testSaveBlueprintSuccess() {
        Blueprint bp = new Blueprint("Author1", "Blueprint1", new Point[]{
                new Point(0, 0), new Point(10, 10)
        });

        Response<?> addResponse = services.addNewBlueprint(bp);
        assertEquals("El blueprint debe registrarse exitosamente", 200, addResponse.getCode());
        assertEquals(bp, addResponse.getDescription());


        Response<?> getResponse = services.getBlueprint("Author1", "Blueprint1", "default");
        assertEquals("La consulta del blueprint debe ser exitosa", 200, getResponse.getCode());
        assertEquals(bp, getResponse.getDescription());
    }

    @Test
    public void testSaveBlueprintFailureDuplicate() {
        Blueprint bp = new Blueprint("Author1", "Blueprint1", new Point[]{
                new Point(0, 0)
        });

        Response<?> response1 = services.addNewBlueprint(bp);
        assertEquals("El primer registro debe ser exitoso", 200, response1.getCode());

        Response<?> response2 = services.addNewBlueprint(bp);
        assertEquals("El registro duplicado debe retornar error", 400, response2.getCode());
    }

    @Test
    public void testGetBlueprintNotFound() {
        Response<?> response = services.getBlueprint("Nonexistent", "NoBlueprint", "default");
        assertEquals("La consulta de un blueprint inexistente debe retornar error", 400, response.getCode());
    }

    @Test
    public void testGetBlueprintByAuthorNotFound() {
        Response<?> response = services.getBlueprintsByAuthor("Nonexistent");
        assertEquals("La consulta de un blueprint inexistente debe retornar error", 400, response.getCode());
    }

    @Test
    public void testUpdateBlueprintSuccess() {

        Blueprint bp = new Blueprint("Author5", "BP1", new Point[]{
                new Point(0, 0)
        });
        Response<?> addResponse = services.addNewBlueprint(bp);
        assertEquals("El blueprint original debe registrarse exitosamente", 200, addResponse.getCode());


        Blueprint updated = new Blueprint("Author5", "BP1", new Point[]{
                new Point(0, 0), new Point(5, 5)
        });

        Response<?> updateResponse = services.updateBlueprint(updated);
        assertEquals("La actualización debe ser exitosa", 200, updateResponse.getCode());
        assertEquals(updated, updateResponse.getDescription());

        Response<?> getResponse = services.getBlueprint("Author5", "BP1", "default");
        assertEquals("La consulta del blueprint actualizado debe ser exitosa", 200, getResponse.getCode());
        assertEquals(updated, getResponse.getDescription());
    }

    @Test
    public void testDeleteBlueprintSuccess() {

        Blueprint bp = new Blueprint("Author6", "BP1", new Point[]{
                new Point(0, 0)
        });
        Response<?> addResponse = services.addNewBlueprint(bp);
        assertEquals("El blueprint debe ser registrado exitosamente", 200, addResponse.getCode());

        // Se elimina el blueprint usando el servicio.
        Response<?> deleteResponse = services.deleteBlueprint("Author6", "BP1");
        assertEquals("La eliminación debe ser exitosa", 200, deleteResponse.getCode());
        assertEquals("Blueprint deleted successfully", deleteResponse.getDescription());

        // Se consulta el blueprint eliminado y se espera que retorne error (código 400).
        Response<?> getResponse = services.getBlueprint("Author6", "BP1", "default");
        assertEquals("El blueprint eliminado no debe encontrarse", 400, getResponse.getCode());
    }

    @Test
    public void testGetBlueprintWithoutFilter() {
        // Crear y registrar un blueprint.
        Blueprint bp = new Blueprint("Author7", "BP7", new Point[]{
                new Point(2, 2), new Point(3, 3)
        });
        services.addNewBlueprint(bp);
        // Consultar usando la sobrecarga sin filtro.
        Response<?> responseWithoutFilter = services.getBlueprint("Author7", "BP7");
        // Consultar explícitamente usando el filtro "default".
        Response<?> responseWithDefault = services.getBlueprint("Author7", "BP7", "default");

        // Se espera que ambas consultas retornen el mismo resultado.
        assertEquals("La consulta sin filtro debe retornar el mismo blueprint que con filtro default",
                responseWithDefault.getCode(), responseWithoutFilter.getCode());
        assertEquals(responseWithDefault.getDescription(), responseWithoutFilter.getDescription());
    }

    @Test
    public void testGetAllBlueprintsNoFilter() {
        Blueprint bp = new Blueprint("Author8", "BP8", new Point[]{
                new Point(5, 5)
        });
        services.addNewBlueprint(bp);
        // Consultar todos los blueprints sin especificar filtro.
        Response<?> responseNoFilter = services.getAllBlueprints();
        // Consultar con filtro "default".
        Response<?> responseDefault = services.getAllBlueprints("default");

        assertEquals("La consulta sin filtro debe retornar el mismo conjunto que con filtro default",
                responseDefault.getCode(), responseNoFilter.getCode());
        assertEquals(responseDefault.getDescription(), responseNoFilter.getDescription());
    }

    @Test
    public void testUpdateBlueprintNotFound() {
        // Crear un blueprint que no se ha registrado.
        Blueprint bp = new Blueprint("Nonexistent", "BP9", new Point[]{
                new Point(0, 0)
        });
        Response<?> response = services.updateBlueprint(bp);
        assertEquals("Actualizar blueprint inexistente debe retornar error", 400, response.getCode());
    }

    @Test
    public void testDeleteBlueprintNotFound() {
        Response<?> response = services.deleteBlueprint("Nonexistent", "BP10");
        assertEquals("Eliminar blueprint inexistente debe retornar error", 400, response.getCode());
    }

    @Test
    public void testGetAllBlueprints() {
        Blueprint bp1 = new Blueprint("Author3", "BP1", new Point[]{
                new Point(3, 3)
        });
        Blueprint bp2 = new Blueprint("Author4", "BP1", new Point[]{
                new Point(4, 4)
        });

        services.addNewBlueprint(bp1);
        services.addNewBlueprint(bp2);

        Response<?> response = services.getAllBlueprints("default");
        assertEquals("La consulta de todos los blueprints debe ser exitosa", 200, response.getCode());

        @SuppressWarnings("unchecked")
        Set<Blueprint> allBps = (Set<Blueprint>) response.getDescription();

        assertTrue("El conjunto debe contener bp1", allBps.contains(bp1));
        assertTrue("El conjunto debe contener bp2", allBps.contains(bp2));
        assertEquals("El conjunto debe contener al menos 3 blueprints", 3, allBps.size());
    }

    @Test
    public void testGetBlueprintsByAuthorNoFilterOverload() {
        Blueprint bp1 = new Blueprint("TestAuthor", "TestBP1", new Point[]{ new Point(0, 0) });
        Blueprint bp2 = new Blueprint("TestAuthor", "TestBP2", new Point[]{ new Point(1, 1) });
        services.addNewBlueprint(bp1);
        services.addNewBlueprint(bp2);
        Response<?> responseNoFilter = services.getBlueprintsByAuthor("TestAuthor");
        Response<?> responseDefault = services.getBlueprintsByAuthor("TestAuthor", "default");
        assertEquals("La respuesta sin filtro debe ser igual a la respuesta con filtro 'default'",
                responseDefault.getCode(), responseNoFilter.getCode());
        assertEquals(responseDefault.getDescription(), responseNoFilter.getDescription());

        @SuppressWarnings("unchecked")
        Set<Blueprint> bpSet = (Set<Blueprint>) responseNoFilter.getDescription();
        assertEquals("El conjunto de blueprints para 'TestAuthor' debe tener 2 elementos", 2, bpSet.size());
    }

    /* =====================================================
     * Sección 4: Pruebas de Filtrado Aplicado a Conjuntos de Blueprints
     * (Uso de filterSet en getAllBlueprints o getBlueprintsByAuthor)
     * =====================================================
     */
    @Test
    public void testDefaultFilterSet() {
        // Se crean dos blueprints.
        Blueprint bp1 = new Blueprint("Author", "BP1", new Point[]{
                new Point(0, 0), new Point(1, 1), new Point(2, 2)
        });
        Blueprint bp2 = new Blueprint("Author", "BP2", new Point[]{
                new Point(0, 0), new Point(2, 2)
        });

        // Se registran los blueprints a través del servicio.
        services.addNewBlueprint(bp1);
        services.addNewBlueprint(bp2);

        // Se consulta el conjunto de blueprints usando el filtro "default".
        Response<?> response = services.getAllBlueprints("default");
        assertEquals("La consulta de todos los blueprints debe ser exitosa", 200, response.getCode());

        @SuppressWarnings("unchecked")
        Set<Blueprint> filteredSet = (Set<Blueprint>) response.getDescription();

        // Se verifica que el conjunto devuelto contenga los blueprints registrados.
        assertTrue("El conjunto debe contener bp1", filteredSet.contains(bp1));
        assertTrue("El conjunto debe contener bp2", filteredSet.contains(bp2));
        // Dado que el filtro default no modifica los blueprints, se espera que se conserven sin cambios.
    }

    @Test
    public void testRedundancyFilterSet() {
        // Crear un blueprint con puntos consecutivos duplicados.
        Point p1 = new Point(0, 0);
        Point p1_dup = p1;  // duplicado (misma referencia)
        Point p2 = new Point(1, 1);
        Point p2_dup = p2;  // duplicado (misma referencia)
        Blueprint bp = new Blueprint("Author", "BP3", new Point[]{p1, p1_dup, p2, p2_dup});

        // Registrar el blueprint mediante el servicio.
        Response<?> addResponse = services.addNewBlueprint(bp);
        assertEquals("El blueprint debe registrarse exitosamente", 200, addResponse.getCode());

        // Consultar todos los blueprints usando el filtro "redundancy".
        Response<?> response = services.getAllBlueprints("redundancy");
        assertEquals("La consulta de todos los blueprints debe ser exitosa", 200, response.getCode());

        @SuppressWarnings("unchecked")
        Set<Blueprint> allBps = (Set<Blueprint>) response.getDescription();

        // Buscar en el conjunto el blueprint que corresponda a "Author"/"BP3".
        Blueprint filteredBp = null;
        for (Blueprint b : allBps) {
            if ("Author".equals(b.getAuthor()) && "BP3".equals(b.getName())) {
                filteredBp = b;
                break;
            }
        }
        assertNotNull("El blueprint filtrado debe encontrarse en el conjunto", filteredBp);

        // Se espera que, al aplicar el filtro redundancy, el blueprint contenga solo los puntos p1 y p2.
        Blueprint expected = new Blueprint("Author", "BP3", new Point[]{p1, p2});
        assertEquals("El blueprint filtrado debe tener únicamente los puntos no duplicados", expected, filteredBp);
    }

    @Test
    public void testSubsamplingFilterSet() {
        // Crear un blueprint con 4 puntos.
        Point p1 = new Point(0, 0);
        Point p2 = new Point(1, 1);
        Point p3 = new Point(2, 2);
        Point p4 = new Point(3, 3);
        Blueprint bp = new Blueprint("Author", "BP4", new Point[]{p1, p2, p3, p4});

        // Registrar el blueprint mediante el servicio.
        Response<?> addResponse = services.addNewBlueprint(bp);
        assertEquals("El blueprint debe registrarse exitosamente", 200, addResponse.getCode());

        // Consultar todos los blueprints usando el filtro "subsampling".
        Response<?> response = services.getAllBlueprints("subsampling");
        assertEquals("La consulta de todos los blueprints debe ser exitosa", 200, response.getCode());

        @SuppressWarnings("unchecked")
        Set<Blueprint> filteredSet = (Set<Blueprint>) response.getDescription();

        // Debido al blueprint "stub" cargado en la persistencia, se espera que el conjunto contenga 2 elementos.
        assertEquals("El conjunto de blueprints debe tener la cantidad esperada (stub + BP4)", 2, filteredSet.size());

        // Buscar en el conjunto el blueprint correspondiente a "Author" y "BP4".
        Blueprint filteredBP = null;
        for (Blueprint b : filteredSet) {
            if ("Author".equals(b.getAuthor()) && "BP4".equals(b.getName())) {
                filteredBP = b;
                break;
            }
        }
        assertNotNull("El blueprint filtrado BP4 debe encontrarse en el conjunto", filteredBP);

        // Se espera que el filtro subsampling retenga únicamente los puntos en posiciones pares: p1 y p3.
        Blueprint expected = new Blueprint("Author", "BP4", new Point[]{p1, p3});
        assertEquals("El blueprint filtrado debe coincidir con el esperado", expected, filteredBP);
    }
    @Test
    public void testGetBlueprintsByAuthorWithRedundancy() {
        // Crear un blueprint con puntos duplicados consecutivos para el mismo autor.
        Point p1 = new Point(0, 0);
        Point p1Dup = p1; // duplicado (misma referencia)
        Point p2 = new Point(1, 1);
        Blueprint bp = new Blueprint("AuthorR", "BP_Red", new Point[]{p1, p1Dup, p2});

        // Registrar el blueprint mediante el servicio.
        Response<?> addResponse = services.addNewBlueprint(bp);
        assertEquals("El blueprint debe registrarse exitosamente", 200, addResponse.getCode());

        // Consultar los blueprints del autor usando el filtro "redundancy".
        Response<?> response = services.getBlueprintsByAuthor("AuthorR", "redundancy");
        assertEquals("La consulta por autor con filtro redundancy debe ser exitosa", 200, response.getCode());

        @SuppressWarnings("unchecked")
        Set<Blueprint> bps = (Set<Blueprint>) response.getDescription();

        // Se espera que el filtro redundancy elimine los puntos duplicados consecutivos.
        Blueprint expected = new Blueprint("AuthorR", "BP_Red", new Point[]{p1, p2});

        boolean found = false;
        for (Blueprint b : bps) {
            if ("AuthorR".equals(b.getAuthor()) && "BP_Red".equals(b.getName()) && b.equals(expected)) {
                found = true;
                break;
            }
        }
        assertTrue("El blueprint filtrado (redundancy) debe coincidir con el esperado", found);
    }

    @Test
    public void testGetBlueprintsByAuthorWithSubsampling() {
        // Crear un blueprint con 4 puntos para el mismo autor.
        Point p1 = new Point(0, 0);
        Point p2 = new Point(1, 1);
        Point p3 = new Point(2, 2);
        Point p4 = new Point(3, 3);
        Blueprint bp = new Blueprint("AuthorS", "BP_Sub", new Point[]{p1, p2, p3, p4});

        // Registrar el blueprint mediante el servicio.
        Response<?> addResponse = services.addNewBlueprint(bp);
        assertEquals("El blueprint debe registrarse exitosamente", 200, addResponse.getCode());

        // Consultar los blueprints del autor usando el filtro "subsampling".
        Response<?> response = services.getBlueprintsByAuthor("AuthorS", "subsampling");
        assertEquals("La consulta por autor con filtro subsampling debe ser exitosa", 200, response.getCode());

        @SuppressWarnings("unchecked")
        Set<Blueprint> bps = (Set<Blueprint>) response.getDescription();

        // El filtro subsampling retiene solo los puntos en índices pares: p1 y p3.
        Blueprint expected = new Blueprint("AuthorS", "BP_Sub", new Point[]{p1, p3});

        boolean found = false;
        for (Blueprint b : bps) {
            if ("AuthorS".equals(b.getAuthor()) && "BP_Sub".equals(b.getName()) && b.equals(expected)) {
                found = true;
                break;
            }
        }
        assertTrue("El blueprint filtrado (subsampling) debe coincidir con el esperado", found);
    }
}







