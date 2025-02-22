/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.services;

import edu.eci.arsw.blueprints.filter.Filter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Response;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

/**
 *
 * @author hcadavid
 */
@Service
public class BlueprintsServices {
    private BlueprintsPersistence bpp;
    private final Map<String, Filter> filters;

    public BlueprintsServices(BlueprintsPersistence bpp, Map<String, Filter> filters) {
        this.bpp = bpp;
        this.filters = filters;
    }

    public Response<?> addNewBlueprint(Blueprint bp) {
        Response<?> response;
        try {
            bp = this.bpp.saveBlueprint(bp);
            response = new Response<Blueprint>(200, bp);
        } catch (BlueprintPersistenceException e) {
            response = new Response<String>(400, e.getMessage());
        }
        return response;
    }

    public Response<?> getBlueprint(String author, String name) {
        return getBlueprint(author, name, "default");
    }

    /**
     * 
     * @param author blueprint's author
     * @param name   blueprint's name
     * @return the blueprint of the given name created by the given author
     * @throws BlueprintNotFoundException if there is no such blueprint
     */
    public Response<?> getBlueprint(String author, String name, String filter) {
        Filter filterBlueprint = setFilter(filter);
        Response<?> response;
        try {
            Blueprint bp = this.bpp.getBlueprint(author, name);
            response = new Response<Blueprint>(200, filterBlueprint.filterBlueprint(bp));
        } catch (BlueprintNotFoundException e) {
            response = new Response<String>(400, e.getMessage());
        }
        return response;
    }

    public Response<?> getAllBlueprints() {
        return getAllBlueprints("default");
    }

    public Response<?> getAllBlueprints(String filter) {
        Filter filterBlueprint = setFilter(filter);
        Response<?> response;
        try {
            Set<Blueprint> bps = bpp.getAllBluePrints();
            response = new Response<Set<Blueprint>>(200, filterBlueprint.filterSet(bps));
        } catch (BlueprintNotFoundException e) {
            response = new Response<String>(400, e.getMessage());
        }
        return response;
    }

    public Response<?> getBlueprintsByAuthor(String author) {
        return getBlueprintsByAuthor(author,"default");
    }

    /**
     * 
     * @param author blueprint's author
     * @return all the blueprints of the given author
     * @throws BlueprintNotFoundException if the given author doesn't exist
     */
    public Response<?> getBlueprintsByAuthor(String author, String filter) {
        Filter filterBlueprint = setFilter(filter);
        Response<?> response;
        try {
            Set<Blueprint> bps = this.bpp.getBlueprintsByAuthor(author);
            response = new Response<Set<Blueprint>>(200, filterBlueprint.filterSet(bps));
        } catch (BlueprintNotFoundException e) {
            response = new Response<String>(400, e.getMessage());
        }
        return response;
    }

    public Response<?> updateBlueprint(Blueprint bp) {
        Response<?> response;
        try {
            bp = this.bpp.updateBlueprint(bp);
            response = new Response<Blueprint>(200, bp);
        } catch (BlueprintPersistenceException e) {
            response = new Response<String>(400, e.getMessage());
        }
        return response;
    }

    public Response<?> deleteBlueprint(String author, String name) {
        Response<?> response;
        try {
            this.bpp.deleteBlueprint(author, name);
            response = new Response<String>(200, "Blueprint deleted successfully");
        } catch (BlueprintPersistenceException e) {
            response = new Response<String>(400, e.getMessage());
        }
        return response;
    }

    private Filter setFilter(String filter) {
        return filters.getOrDefault(filter, filters.get("default"));
    }
}
