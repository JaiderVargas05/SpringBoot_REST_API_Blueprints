package edu.eci.arsw.blueprints.filter.impl;

import edu.eci.arsw.blueprints.filter.Filter;
import edu.eci.arsw.blueprints.model.Blueprint;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("default")
public class Default implements Filter {
    @Override
    public Set<Blueprint> filterSet(Set<Blueprint> blueprints){
        return blueprints;
    }

    @Override
    public Blueprint filterBlueprint(Blueprint blueprint){
        return blueprint;
    }
}
