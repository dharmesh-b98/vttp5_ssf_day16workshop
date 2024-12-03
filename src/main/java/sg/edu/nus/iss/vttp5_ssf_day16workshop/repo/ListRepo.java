package sg.edu.nus.iss.vttp5_ssf_day16workshop.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.vttp5_ssf_day16workshop.constants.*;

@Repository
public class ListRepo {
    
    @Autowired
    @Qualifier(Constant.template01)
    private RedisTemplate<String, String> template;

    public long leftPush(String key, String value){
        return template.opsForList().leftPush(key, value);
    }

    public long rightPush(String key, String value){
        return template.opsForList().rightPush(key, value);
    }

    public List<String> leftPop(String key) {
        return template.opsForList().leftPop(key, 1);
    }
    
    public List<String> rightPop(String key){
        return template.opsForList().rightPop(key, 1);
    }

    public String get(String key, Integer index){
        return template.opsForList().index(key, index).toString();
    }

    public long size(String key) {
        return template.opsForList().size(key);
    }

    public List<String> getList(String key) {
        List<String> list = template.opsForList().range(key, 0, -1);
        return list;
    }

    public Boolean deleteItem(String key,Integer id){
        
        Boolean isDeleted = false;
        List<String> retrievedList = template.opsForList().range(key,0,-1);//each entry is a comma separated string
       
 
        Optional<String> entryIfFound = retrievedList.stream().filter(a->a.contains(String.valueOf(id))).findFirst(); //string entry of person if found

        if (entryIfFound.isPresent()){
            String entryIfFoundString = entryIfFound.get();
            template.opsForList().remove(key,0,entryIfFoundString);
            isDeleted = true;
        }

        return isDeleted;
    }



}
