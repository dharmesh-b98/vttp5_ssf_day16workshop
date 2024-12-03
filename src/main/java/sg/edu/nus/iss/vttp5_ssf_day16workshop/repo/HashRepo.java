package sg.edu.nus.iss.vttp5_ssf_day16workshop.repo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.vttp5_ssf_day16workshop.constants.*;

@Repository
public class HashRepo {
    @Autowired
    @Qualifier(Constant.template01)
    private RedisTemplate<String, String> template;

    public void put(String key, String hashKey, String value){
        template.opsForHash().put(key, hashKey, value);
    }

    public Object get(String key, String hashKey){
        return template.opsForHash().get(key,hashKey);
    }

    public Boolean hasKey(String key, String hashKey){
        return template.opsForHash().hasKey(key, hashKey);
    }

    public long delete(String key, String hashKey){
        return template.opsForHash().delete(key, hashKey);
    }
    
    public long size(String key){
        return template.opsForHash().size(key);
    }

    public Set<String> keys(String key){
        Set<Object> objectKeys = template.opsForHash().keys(key);
        Set<String> StringKeys = objectKeys.stream().map(e -> (String)e).collect(Collectors.toSet());
        return StringKeys;
    }

    public Map<String, String> entries (String key){
        Map<Object, Object> ObjectEntries = template.opsForHash().entries(key);
        Map<String, String> StringEntries = new HashMap<>();
        for (Map.Entry<Object, Object> entry : ObjectEntries.entrySet()){
            String stringKey = (String) entry.getKey();
            String stringValue = (String) entry.getValue();
            StringEntries.put(stringKey,stringValue);
        }
        return StringEntries;
    }
}
