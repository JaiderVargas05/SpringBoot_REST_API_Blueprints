/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.services;

import edu.eci.arsw.blueprints.filter.Filter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.model.Response;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author hcadavid
 */
@Service
public class BlueprintsServices {
    private BlueprintsPersistence bpp;
    private Filter filterBlueprint;
    @Autowired
    public BlueprintsServices(BlueprintsPersistence bpp, @Qualifier("redundancy") Filter filterBlueprint) {
        this.bpp=bpp;
        this.filterBlueprint=filterBlueprint;
    }

    
    public Response addNewBlueprint(Blueprint bp){
        Response response;
        try{
            this.bpp.saveBlueprint(bp);
            response = new Response(200,"OK");
        } catch (BlueprintPersistenceException e) {
            response = new Response(400,e.getMessage());
        }
        return response;
    }
    
    public Response getAllBlueprints(){
        Response response;
        try{
            Set<Blueprint> bps = bpp.getAllBluePrints();
            response = new Response(200,filterBlueprint.filterSet(bps));
        }
        catch (BlueprintNotFoundException e){
            response = new Response(400,e.getMessage());
        }
        return response;
    }
    
    /**
     * 
     * @param author blueprint's author
     * @param name blueprint's name
     * @return the blueprint of the given name created by the given author
     * @throws BlueprintNotFoundException if there is no such blueprint
     */
    public Response getBlueprint(String author,String name) {
        Response response;
        try{
            Blueprint bp = this.bpp.getBlueprint(author,name);
            response = new Response(200,bp);
        } catch (BlueprintNotFoundException e) {
            response = new Response(400,e.getMessage());
        }
        return response;
    }
    
    /**
     * 
     * @param author blueprint's author
     * @return all the blueprints of the given author
     * @throws BlueprintNotFoundException if the given author doesn't exist
     */
    public Response getBlueprintsByAuthor(String author) {
        Response response;
        try{
            Set<Blueprint> bps = this.bpp.getBlueprintsByAuthor(author);
            response = new Response(200,filterBlueprint.filterSet(bps));
        } catch (BlueprintNotFoundException e) {
            response = new Response(400,e.getMessage());
        }
        return response;
    }
    
}
