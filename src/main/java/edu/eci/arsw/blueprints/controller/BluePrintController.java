package edu.eci.arsw.blueprints.controller;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Response;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blueprint")
public class BluePrintController {

    private BlueprintsServices blueprintServices;

    public BluePrintController(BlueprintsServices blueprintServices) {
        this.blueprintServices = blueprintServices;
    }

    @PostMapping("/add")
    public ResponseEntity<Response<?>> addNewBlueprint(@RequestBody Blueprint bpp) {
        Response<?> response = blueprintServices.addNewBlueprint(bpp);
        if (response.code != 200)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/get")
    public ResponseEntity<Response<?>> getBlueprint(@RequestParam String author, @RequestParam String name,
            @RequestParam(value = "filter", defaultValue = "default") String filter) {
        Response<?> response = blueprintServices.getBlueprint(author, name);
        if (response.code != 200)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<Response<?>> getAllBlueprints(
            @RequestParam(value = "filter", defaultValue = "default") String filter) {

        Response<?> response = blueprintServices.getAllBlueprints(filter);
        if (response.code != 200)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getByAuthor")
    public ResponseEntity<Response<?>> getByAuthor(@RequestParam String author,
            @RequestParam(value = "filter", defaultValue = "default") String filter) {
        Response<?> response = blueprintServices.getBlueprintsByAuthor(author, filter);
        if (response.code != 200)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Response<?>> updateBlueprint(@RequestBody Blueprint bpp) {
        Response<?> response = blueprintServices.updateBlueprint(bpp);
        if (response.code != 200)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<?>> deleteBlueprint(@RequestParam String author, @RequestParam String name) {
        Response<?> response = blueprintServices.deleteBlueprint(author, name);
        if (response.code != 200)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
