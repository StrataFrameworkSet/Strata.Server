//////////////////////////////////////////////////////////////////////////////
// HealthCheckController.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.health;

import jakarta.persistence.EntityManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/health")
public
class HealthCheckController
{
    private final Logger               logger;
    private final Instant              startupTime;
    private final EntityManagerFactory factory;

    @Autowired
    public
    HealthCheckController(EntityManagerFactory factory)
    {
        this.logger      = LogManager.getLogger(HealthCheckController.class);
        this.startupTime = Instant.now();
        this.factory     = factory;
    }

    /**
     * Kubernetes liveness probe endpoint.
     * Indicates whether the application process is running
     * and should not be restarted.
     * @return 200 OK if the process is alive
     */
    @GetMapping(
        value    = "/live",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Object>>
    liveness()
    {
        return
            ResponseEntity
                .ok(
                    Map.of(
                        "status","UP",
                        "check","liveness",
                        "timestamp",Instant.now().toString()));
    }

    /**
     * Kubernetes readiness probe endpoint.
     * Indicates whether the application is ready to
     * accept traffic. Override {@link #isReady()} in
     * a subclass to add custom readiness checks
     * (e.g. database connectivity, downstream services).
     * @return 200 OK if ready; 503 Service Unavailable otherwise
     */
    @GetMapping(
        value    = "/ready",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Object>>
    readiness()
    {
        boolean ready    = isReady();
        boolean database = isDatabaseReady();
        boolean overall  = ready && database;

        Map<String,Object> body = new LinkedHashMap<>();

        body.put("status",overall ? "UP" : "DOWN");
        body.put("check","readiness");
        body.put("database",database ? "UP" : "DOWN");
        body.put("timestamp",Instant.now().toString());

        return
            overall
                ? ResponseEntity.ok(body)
                : ResponseEntity
                    .status(503)
                    .body(body);
    }

    /**
     * Kubernetes startup probe endpoint.
     * Indicates whether the application has completed its
     * initialization. Override {@link #isStarted()} in
     * a subclass to add custom startup checks.
     * @return 200 OK if started; 503 Service Unavailable otherwise
     */
    @GetMapping(
        value    = "/startup",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Object>>
    startup()
    {
        boolean started = isStarted();

        Map<String,Object> body =
            Map.of(
                "status",started ? "UP" : "DOWN",
                "check","startup",
                "startupTime",startupTime.toString(),
                "timestamp",Instant.now().toString());

        return
            started
                ? ResponseEntity.ok(body)
                : ResponseEntity
                    .status(503)
                    .body(body);
    }

    protected boolean
    isReady()
    {
        return true;
    }

    protected boolean
    isStarted()
    {
        return true;
    }

    protected boolean
    isDatabaseReady()
    {
        if (Objects.isNull(factory))
            return false;

        try
        {
            return factory.isOpen();
        }
        catch (Exception e)
        {
            logger.error("Database readiness check failed",e);
            return false;
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
