package sg.edu.nus.iss.vttp5_ssf_day16workshop.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.vttp5_ssf_day16workshop.constants.Constant;
import sg.edu.nus.iss.vttp5_ssf_day16workshop.repo.ListRepo;
import sg.edu.nus.iss.vttp5_ssf_day16workshop.repo.ValueRepo;

@Service
public class GameRestService {
    
    @Autowired
    ValueRepo gameRepo;
    
    
    public void saveJson() throws IOException{
        String jsonString = "";

        FileReader fr  = new FileReader("src/main/resources/static/json/game.json");
        BufferedReader br = new BufferedReader(fr);
        
        String line = "";
        while (((line = br.readLine()) != null)){
            jsonString += line;
        }       
        gameRepo.addValue(Constant.redisKey, jsonString);
    }
    

    public Boolean getBoardGame(Integer boardgame_id) throws IOException{

        String jsonString = gameRepo.getValue(Constant.redisKey);
        StringReader sr = new StringReader(jsonString);
        JsonReader jr = Json.createReader(sr);

        JsonArray jsonArray = jr.readArray();

        for (int i = 0; i < jsonArray.size(); i++){
            JsonObject gameJsonObject = jsonArray.get(i).asJsonObject();
            if (gameJsonObject.getInt("gid") == boardgame_id){
                String jsonToWrite = gameJsonObject.toString();

                File outputFile = new File("src/main/resources/static/json/" + String.valueOf(boardgame_id) + ".json");
                if (!(outputFile.exists())){
                    outputFile.createNewFile();
                }
                
                FileWriter fw = new FileWriter(outputFile);
                BufferedWriter bw = new BufferedWriter(fw);
                
                bw.write(jsonToWrite);
                bw.flush();
                bw.close();
                return true;
            }
        }

        //boardgame is not found
        return false;
    }

    public Boolean updateBoardGame(Integer boardgame_id, String newGameJson, Boolean upsert){
        
        Boolean gameFound = false;

        StringReader new_sr = new StringReader(newGameJson);
        JsonReader new_jr = Json.createReader(new_sr);
        JsonObject newGameJsonObject = new_jr.readObject();

        JsonArrayBuilder jab = Json.createArrayBuilder();

        String jsonString = gameRepo.getValue(Constant.redisKey);
        StringReader sr = new StringReader(jsonString);
        JsonReader jr = Json.createReader(sr);

        JsonArray jsonArray = jr.readArray();

        for (int i = 0; i < jsonArray.size(); i++){
            JsonObject gameJsonObject = jsonArray.get(i).asJsonObject();

            if (gameJsonObject.getInt("gid") == boardgame_id){
                jab = jab.add(newGameJsonObject);
                gameFound = true;
                continue;
            }
            jab = jab.add(gameJsonObject);
        }

        if (upsert == true){
            jab = jab.add(newGameJsonObject);
        }

        JsonArray finalArray = jab.build();

        gameRepo.addValue(Constant.redisKey, finalArray.toString());

        return gameFound;
    }


}
