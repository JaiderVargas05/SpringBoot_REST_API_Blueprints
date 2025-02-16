package edu.eci.arsw.blueprints.filter.impl;

import edu.eci.arsw.blueprints.filter.Filter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("redundancy")
public class Redundancy implements Filter {
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
        List<Point> redundancyPoints = new ArrayList<Point>();
        List<Point> points = blueprint.getPoints();
        redundancyPoints.add(points.get(0));
        for(int i = 1; i < points.size(); i++){
            Point point = points.get(i);
            Point lastPoint = redundancyPoints.get(redundancyPoints.size()-1);
            if(point.getX() != lastPoint.getX() || point.getY() != lastPoint.getY() ) {
                redundancyPoints.add(point);
            }
        }
        Blueprint filterBlueprint = new Blueprint(blueprint.getAuthor(), blueprint.getName(), redundancyPoints.toArray(new Point[redundancyPoints.size()]));
        return filterBlueprint;
    }
}
