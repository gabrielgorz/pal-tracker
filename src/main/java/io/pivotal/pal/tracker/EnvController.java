package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {

    private final Map<String, String> environment;

    public EnvController(@Value("${port:NOT SET}") final String port,
                         @Value("${memory.limit:NOT SET}") final String memoryLimit,
                         @Value("${cf.instance.index:NOT SET}") final String cfInstanceIdx,
                         @Value("${cf.instance.addr:NOT SET}") final String cfInstanceAddr) {
        this.environment = new HashMap<>();
        this.environment.put("PORT", port);
        this.environment.put("MEMORY_LIMIT", memoryLimit);
        this.environment.put("CF_INSTANCE_INDEX", cfInstanceIdx);
        this.environment.put("CF_INSTANCE_ADDR", cfInstanceAddr);
    }

    @GetMapping("/env")
    public Map<String, String> getEnv() {
        return this.environment;
    }
}
