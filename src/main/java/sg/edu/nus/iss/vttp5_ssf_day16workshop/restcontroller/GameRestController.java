package sg.edu.nus.iss.vttp5_ssf_day16workshop.restcontroller;

import java.io.FileReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import sg.edu.nus.iss.vttp5_ssf_day16workshop.constants.Constant;
import sg.edu.nus.iss.vttp5_ssf_day16workshop.service.GameRestService;

@RestController
@RequestMapping("/api/boardgame")
public class GameRestController {

    @Autowired
    GameRestService gameRestService;

    private Integer noOfUpdates = 0;


    @PostMapping("")
    public ResponseEntity<String> saveJsonFile() throws IOException{
        gameRestService.saveJson();
        
        JsonObjectBuilder jab = Json.createObjectBuilder();
        jab.add("update_count", 1)
            .add("id", Constant.redisKey);

        String reponse = jab.build().toString();
        return new ResponseEntity<String>(reponse, HttpStatus.CREATED);
    }



    @GetMapping("/{boardgame_id}")
    public ResponseEntity<String> getJsonGame(@PathVariable("boardgame_id") String boardgame_id) throws NumberFormatException, IOException{
        
        Boolean isCompleted = gameRestService.getBoardGame(Integer.parseInt(boardgame_id));

        if (isCompleted) {
            return new ResponseEntity<String>("Found and saved as " + boardgame_id + ".json", HttpStatus.FOUND);
        }

        return new ResponseEntity<String>("Could not find the id " + boardgame_id, HttpStatus.NOT_FOUND);
    }



    @PutMapping("/{boardgame_id}")
    public ResponseEntity<String> updateJsonGame(@PathVariable("boardgame_id") String boardgame_id,@RequestParam(name = "upsert", required = false, defaultValue = "false") Boolean upsert, @RequestBody String newGameJson){
        Boolean isCompleted = gameRestService.updateBoardGame(Integer.parseInt(boardgame_id), newGameJson, upsert);
        
        if (isCompleted){
            JsonObjectBuilder jab = Json.createObjectBuilder();
                this.noOfUpdates ++;
                jab.add("insert_count", noOfUpdates)
                .add("id", Constant.redisKey);

            String response = jab.build().toString();
            return new ResponseEntity<String>(response, HttpStatus.FOUND);
        }

        return new ResponseEntity<String>("Could not find the id " + boardgame_id, HttpStatus.NOT_FOUND);
    }
}
