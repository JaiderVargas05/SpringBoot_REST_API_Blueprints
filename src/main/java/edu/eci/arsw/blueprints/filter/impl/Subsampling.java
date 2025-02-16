package edu.eci.arsw.blueprints.filter.impl;

import edu.eci.arsw.blueprints.filter.Filter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("subsampling")
public class Subsampling implements Filter {
    @Override
    public Set<Blueprint> filterSet(Set<Blueprint> blueprints){
        Set<Blueprint> filteredBlueprints = new HashSet<Blueprint>();
        for(Blueprint blueprint : blueprints){
            filteredBlueprints.add(filterBlueprint(blueprint));
        }
        return filteredBlueprints;
    }

    @Override
    public Blueprint filterBlueprint(Blueprint blueprint){
        List<Point> points = blueprint.getPoints();
        List<Point> subsamplingPoints = new ArrayList<Point>();
        for(int i = 0; i < points.size() && (i % 2 == 0); i++){
            subsamplingPoints.add(points.get(i));
        }
        Blueprint filterBlueprint = new Blueprint(blueprint.getAuthor(), blueprint.getName(), subsamplingPoints.toArray(new Point[subsamplingPoints.size()]));
        return filterBlueprint;
    }

}
