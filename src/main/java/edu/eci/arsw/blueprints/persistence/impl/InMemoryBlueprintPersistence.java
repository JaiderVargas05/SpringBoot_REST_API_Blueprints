/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;

import java.util.*;

import org.springframework.stereotype.Component;

/**
 *
 * @author hcadavid
 */
@Component("inMemory")
public class InMemoryBlueprintPersistence implements BlueprintsPersistence {

    private final Map<Tuple<String, String>, Blueprint> blueprints = new HashMap<>();

    public InMemoryBlueprintPersistence() {
        // load stub data
        Point[] pts = new Point[] { new Point(140, 140), new Point(115, 115) };
        Blueprint bp = new Blueprint("_authorname_", "_bpname_ ", pts);
        blueprints.put(new Tuple<>(bp.getAuthor(), bp.getName()), bp);

    }

    @Override
    public Blueprint saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        if (blueprints.containsKey(new Tuple<>(bp.getAuthor(), bp.getName()))) {
            throw new BlueprintPersistenceException("The given blueprint already exists: " + bp);
        } else {
            blueprints.put(new Tuple<>(bp.getAuthor(), bp.getName()), bp);
            return blueprints.get(new Tuple<>(bp.getAuthor(), bp.getName()));
        }
    }

    @Override
    public Blueprint getBlueprint(String author, String bprintname) throws BlueprintNotFoundException {
        Blueprint bp = blueprints.get(new Tuple<>(author, bprintname));
        if (bp == null)
            throw new BlueprintNotFoundException("Not results found");
        return bp;
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        Set<Blueprint> bpps = new HashSet<>();
        for (Map.Entry<Tuple<String, String>, Blueprint> entry : blueprints.entrySet()) {
            if (entry.getKey().getElem1().equals(author)) {
                bpps.add(entry.getValue());
            }
        }
        if (bpps.isEmpty())
            throw new BlueprintNotFoundException("Not results found");
        return bpps;
    }

    @Override
    public Set<Blueprint> getAllBluePrints() throws BlueprintNotFoundException {
        if (blueprints.isEmpty())
            throw new BlueprintNotFoundException("Not results found");
        Set<Blueprint> blueprintSet = new HashSet<>(blueprints.values());
        return blueprintSet;
    }

    @Override
    public Blueprint updateBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        Tuple<String, String> key = new Tuple<>(bp.getAuthor(), bp.getName());
        if (!blueprints.containsKey(key)) {
            throw new BlueprintPersistenceException("Blueprint not found: " + bp);
        }
        blueprints.put(key, bp);
        return blueprints.get(key);
    }

    @Override
    public void deleteBlueprint(String author, String name) throws BlueprintPersistenceException {
        Tuple<String, String> key = new Tuple<>(author, name);
        if (!blueprints.containsKey(key)) {
            throw new BlueprintPersistenceException("Blueprint not found: " + author + " - " + name);
        }
        blueprints.remove(key);
    }

}
