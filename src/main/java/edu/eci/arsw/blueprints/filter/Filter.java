package edu.eci.arsw.blueprints.filter;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;

import java.util.List;
import java.util.Set;


public interface Filter {
    public Set<Blueprint> filterSet(Set<Blueprint> blueprints);
    public Blueprint filterBlueprint(Blueprint blueprint);
}
