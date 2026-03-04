package com.github.darkmukke.nomadpack.structure

/**
 * Defines the hierarchical structure of Nomad job specifications
 * Based on: https://developer.hashicorp.com/nomad/docs/job-specification
 */

enum class StanzaContext {
    ROOT,           // Top-level (job only)
    JOB,            // Inside job block
    GROUP,          // Inside group block
    TASK,           // Inside task block
    SERVICE,        // Inside service block
    CHECK,          // Inside check block
    NETWORK,        // Inside network block
    PORT,           // Inside port block
    RESOURCES,      // Inside resources block
    VAULT,          // Inside vault block
    TEMPLATE,       // Inside template block
    CONSTRAINT,     // Inside constraint block
    AFFINITY,       // Inside affinity block
    SPREAD,         // Inside spread block
    UPDATE,         // Inside update block
    MIGRATE,        // Inside migrate block
    RESCHEDULE,     // Inside reschedule block
    RESTART,        // Inside restart block
    EPHEMERAL_DISK, // Inside ephemeral_disk block
    PERIODIC,       // Inside periodic block
    PARAMETERIZED,  // Inside parameterized block
    MULTIREGION,    // Inside multiregion block
    VOLUME,         // Inside volume block
    VOLUME_MOUNT,   // Inside volume_mount block
    LOGS,           // Inside logs block
    ARTIFACT,       // Inside artifact block
    LIFECYCLE,      // Inside lifecycle block
    CONFIG,         // Inside config block (task driver config)
    ENV,            // Inside env block
    CONNECT,        // Inside connect block (Consul)
    SIDECAR_SERVICE,// Inside sidecar_service block
    SIDECAR_TASK,   // Inside sidecar_task block
    PROXY,          // Inside proxy block
    UPSTREAM,       // Inside upstream block
    GATEWAY,        // Inside gateway block
    INGRESS,        // Inside ingress block
    TERMINATING,    // Inside terminating block
    DNS,            // Inside dns block
    CSI_PLUGIN,     // Inside csi_plugin block
    SCALING,        // Inside scaling block
    IDENTITY,       // Inside identity block
    ACTION,         // Inside action block
    SCHEDULE,       // Inside schedule block
    DEVICE,         // Inside device block (resources child)
    NUMA,           // Inside numa block (resources child)
    HEADER,         // Inside header block (check child)
    CHECK_RESTART,  // Inside check_restart block (check child)
    EXPOSE,         // Inside expose block (proxy child)
    TRANSPARENT_PROXY, // Inside transparent_proxy block (proxy child)
    MESH_GATEWAY,   // Inside mesh_gateway block (upstreams child)
    TARGET,         // Inside target block (spread child)
    WAIT,           // Inside wait block (template child)
    STRATEGY,       // Inside strategy block (multiregion child)
    REGION,         // Inside region block (multiregion child)
    LISTENER,       // Inside listener block (ingress child)
    TLS,            // Inside tls block (ingress child)
    MESH,           // Inside mesh block (gateway child)
    PATH,           // Inside path block (expose child)
    MOUNT_OPTIONS,  // Inside mount_options block (volume child)
    META,           // Inside meta block
    CONSUL,         // Inside consul block
    DISCONNECT,     // Inside disconnect block
    CNI,            // Inside cni block (network child)
    OPTIONS,        // Inside options block (artifact child)
    HEADERS,        // Inside headers block (artifact child)
    POLICY,         // Inside policy block (scaling child)
    DISPATCH_PAYLOAD, // Inside dispatch_payload block
    CHANGE_SCRIPT   // Inside change_script block
}

enum class PropertyType {
    STRING,
    NUMBER,
    BOOLEAN,
    ARRAY_STRING,
    ARRAY_NUMBER,
    MAP,
    BLOCK,
    DURATION,      // e.g., "10s", "5m"
    EXPRESSION     // Template expression like [[ .var ]]
}

data class StanzaDefinition(
    val name: String,
    val description: String,
    val contexts: Set<StanzaContext>,
    val isBlock: Boolean = true,
    val childContext: StanzaContext? = null,
    val required: Boolean = false
)

data class PropertyDefinition(
    val name: String,
    val type: PropertyType,
    val description: String,
    val contexts: Set<StanzaContext>,
    val validValues: List<String>? = null,
    val defaultValue: String? = null,
    val required: Boolean = false
)

/**
 * Complete Nomad stanza hierarchy definition
 */
object NomadStanzaHierarchy {
    
    val stanzas = listOf(
        // Root level - only job
        StanzaDefinition(
            "job", "Defines a job specification", 
            setOf(StanzaContext.ROOT), 
            childContext = StanzaContext.JOB,
            required = true
        ),
        
        // Job-level stanzas
        StanzaDefinition(
            "group", "Defines a task group within a job",
            setOf(StanzaContext.JOB),
            childContext = StanzaContext.GROUP,
            required = true
        ),
        StanzaDefinition(
            "constraint", "Restricts where a job can be scheduled",
            setOf(StanzaContext.JOB, StanzaContext.GROUP, StanzaContext.TASK),
            childContext = StanzaContext.CONSTRAINT
        ),
        StanzaDefinition(
            "affinity", "Expresses placement preference",
            setOf(StanzaContext.JOB, StanzaContext.GROUP, StanzaContext.TASK),
            childContext = StanzaContext.AFFINITY
        ),
        StanzaDefinition(
            "spread", "Distributes allocations across attributes",
            setOf(StanzaContext.JOB, StanzaContext.GROUP, StanzaContext.TASK),
            childContext = StanzaContext.SPREAD
        ),
        StanzaDefinition(
            "update", "Specifies rolling update strategy",
            setOf(StanzaContext.JOB, StanzaContext.GROUP),
            childContext = StanzaContext.UPDATE
        ),
        StanzaDefinition(
            "migrate", "Specifies migration strategy for task groups",
            setOf(StanzaContext.JOB, StanzaContext.GROUP),
            childContext = StanzaContext.MIGRATE
        ),
        StanzaDefinition(
            "reschedule", "Controls rescheduling behavior on failure",
            setOf(StanzaContext.JOB, StanzaContext.GROUP),
            childContext = StanzaContext.RESCHEDULE
        ),
        StanzaDefinition(
            "periodic", "Configures periodic (cron) job execution",
            setOf(StanzaContext.JOB),
            childContext = StanzaContext.PERIODIC
        ),
        StanzaDefinition(
            "parameterized", "Configures parameterized job dispatch",
            setOf(StanzaContext.JOB),
            childContext = StanzaContext.PARAMETERIZED
        ),
        StanzaDefinition(
            "multiregion", "Configures multi-region deployment",
            setOf(StanzaContext.JOB),
            childContext = StanzaContext.MULTIREGION
        ),
        
        // Group-level stanzas
        StanzaDefinition(
            "task", "Defines a task within a group",
            setOf(StanzaContext.GROUP),
            childContext = StanzaContext.TASK,
            required = true
        ),
        StanzaDefinition(
            "network", "Configures networking for a task group",
            setOf(StanzaContext.GROUP),
            childContext = StanzaContext.NETWORK
        ),
        StanzaDefinition(
            "service", "Registers services with Consul or Nomad",
            setOf(StanzaContext.GROUP, StanzaContext.TASK),
            childContext = StanzaContext.SERVICE
        ),
        StanzaDefinition(
            "volume", "Declares a volume mount requirement",
            setOf(StanzaContext.GROUP),
            childContext = StanzaContext.VOLUME
        ),
        StanzaDefinition(
            "restart", "Controls restart behavior for tasks",
            setOf(StanzaContext.GROUP, StanzaContext.TASK),
            childContext = StanzaContext.RESTART
        ),
        StanzaDefinition(
            "ephemeral_disk", "Configures ephemeral disk for task group",
            setOf(StanzaContext.GROUP),
            childContext = StanzaContext.EPHEMERAL_DISK
        ),
        StanzaDefinition(
            "scaling", "Configures autoscaling policies",
            setOf(StanzaContext.GROUP, StanzaContext.TASK),
            childContext = StanzaContext.SCALING
        ),
        
        // Task-level stanzas
        StanzaDefinition(
            "config", "Driver-specific configuration",
            setOf(StanzaContext.TASK),
            childContext = StanzaContext.CONFIG,
            required = true
        ),
        StanzaDefinition(
            "resources", "Specifies resource requirements",
            setOf(StanzaContext.TASK),
            childContext = StanzaContext.RESOURCES,
            required = true
        ),
        StanzaDefinition(
            "env", "Sets environment variables",
            setOf(StanzaContext.TASK),
            childContext = StanzaContext.ENV
        ),
        StanzaDefinition(
            "template", "Renders templates with runtime data",
            setOf(StanzaContext.TASK),
            childContext = StanzaContext.TEMPLATE
        ),
        StanzaDefinition(
            "artifact", "Downloads artifacts before task starts",
            setOf(StanzaContext.TASK),
            childContext = StanzaContext.ARTIFACT
        ),
        StanzaDefinition(
            "vault", "Integrates with Vault for secrets",
            setOf(StanzaContext.TASK),
            childContext = StanzaContext.VAULT
        ),
        StanzaDefinition(
            "logs", "Configures log rotation",
            setOf(StanzaContext.TASK),
            childContext = StanzaContext.LOGS
        ),
        StanzaDefinition(
            "lifecycle", "Defines task lifecycle (prestart, poststart, poststop)",
            setOf(StanzaContext.TASK),
            childContext = StanzaContext.LIFECYCLE
        ),
        StanzaDefinition(
            "volume_mount", "Mounts a volume into the task",
            setOf(StanzaContext.TASK),
            childContext = StanzaContext.VOLUME_MOUNT
        ),
        StanzaDefinition(
            "csi_plugin", "Configures CSI plugin",
            setOf(StanzaContext.TASK),
            childContext = StanzaContext.CSI_PLUGIN
        ),
        StanzaDefinition(
            "identity", "Configures workload identity",
            setOf(StanzaContext.TASK),
            childContext = StanzaContext.IDENTITY
        ),
        StanzaDefinition(
            "action", "Defines task actions",
            setOf(StanzaContext.TASK),
            childContext = StanzaContext.ACTION
        ),
        
        // Service-level stanzas
        StanzaDefinition(
            "check", "Defines health checks",
            setOf(StanzaContext.SERVICE),
            childContext = StanzaContext.CHECK
        ),
        StanzaDefinition(
            "connect", "Configures Consul Connect service mesh",
            setOf(StanzaContext.SERVICE),
            childContext = StanzaContext.CONNECT
        ),
        
        // Network-level stanzas
        StanzaDefinition(
            "port", "Declares a network port",
            setOf(StanzaContext.NETWORK),
            childContext = StanzaContext.PORT
        ),
        StanzaDefinition(
            "dns", "Configures DNS settings",
            setOf(StanzaContext.NETWORK),
            childContext = StanzaContext.DNS
        ),
        
        // Connect-level stanzas
        StanzaDefinition(
            "sidecar_service", "Configures sidecar proxy service",
            setOf(StanzaContext.CONNECT),
            childContext = StanzaContext.SIDECAR_SERVICE
        ),
        StanzaDefinition(
            "sidecar_task", "Configures sidecar proxy task",
            setOf(StanzaContext.CONNECT),
            childContext = StanzaContext.SIDECAR_TASK
        ),
        StanzaDefinition(
            "gateway", "Configures service mesh gateway",
            setOf(StanzaContext.CONNECT),
            childContext = StanzaContext.GATEWAY
        ),
        
        // Sidecar service stanzas
        StanzaDefinition(
            "proxy", "Configures Consul Connect proxy",
            setOf(StanzaContext.SIDECAR_SERVICE),
            childContext = StanzaContext.PROXY
        ),
        
        // Proxy stanzas
        StanzaDefinition(
            "upstreams", "Declares upstream service dependencies",
            setOf(StanzaContext.PROXY),
            childContext = StanzaContext.UPSTREAM
        ),
        
        // Gateway stanzas
        StanzaDefinition(
            "ingress", "Configures ingress gateway",
            setOf(StanzaContext.GATEWAY),
            childContext = StanzaContext.INGRESS
        ),
        StanzaDefinition(
            "terminating", "Configures terminating gateway",
            setOf(StanzaContext.GATEWAY),
            childContext = StanzaContext.TERMINATING
        ),
        StanzaDefinition(
            "mesh", "Configures mesh gateway",
            setOf(StanzaContext.GATEWAY),
            childContext = StanzaContext.MESH
        ),

        // Resources child stanzas
        StanzaDefinition(
            "device", "Declares device requirements",
            setOf(StanzaContext.RESOURCES),
            childContext = StanzaContext.DEVICE
        ),
        StanzaDefinition(
            "numa", "Configures NUMA preferences",
            setOf(StanzaContext.RESOURCES),
            childContext = StanzaContext.NUMA
        ),

        // Check child stanzas
        StanzaDefinition(
            "header", "Defines HTTP headers for checks",
            setOf(StanzaContext.CHECK),
            childContext = StanzaContext.HEADER
        ),
        StanzaDefinition(
            "check_restart", "Configures restart behavior on check failure",
            setOf(StanzaContext.CHECK),
            childContext = StanzaContext.CHECK_RESTART
        ),

        // Proxy child stanzas
        StanzaDefinition(
            "expose", "Configures exposed paths for Connect",
            setOf(StanzaContext.PROXY),
            childContext = StanzaContext.EXPOSE
        ),
        StanzaDefinition(
            "config", "Proxy configuration",
            setOf(StanzaContext.PROXY, StanzaContext.UPSTREAM),
            childContext = StanzaContext.CONFIG
        ),
        StanzaDefinition(
            "transparent_proxy", "Configures transparent proxy mode",
            setOf(StanzaContext.PROXY),
            childContext = StanzaContext.TRANSPARENT_PROXY
        ),

        // Upstreams child stanzas
        StanzaDefinition(
            "mesh_gateway", "Configures mesh gateway mode for upstream",
            setOf(StanzaContext.UPSTREAM),
            childContext = StanzaContext.MESH_GATEWAY
        ),

        // Spread child stanzas
        StanzaDefinition(
            "target", "Defines spread target with percentage",
            setOf(StanzaContext.SPREAD),
            childContext = StanzaContext.TARGET
        ),

        // Template child stanzas
        StanzaDefinition(
            "wait", "Configures wait conditions for template rendering",
            setOf(StanzaContext.TEMPLATE),
            childContext = StanzaContext.WAIT
        ),
        StanzaDefinition(
            "change_script", "Script to run when template changes",
            setOf(StanzaContext.TEMPLATE),
            childContext = StanzaContext.CHANGE_SCRIPT
        ),

        // Multiregion child stanzas
        StanzaDefinition(
            "strategy", "Defines multi-region deployment strategy",
            setOf(StanzaContext.MULTIREGION),
            childContext = StanzaContext.STRATEGY
        ),
        StanzaDefinition(
            "region", "Defines region-specific configuration",
            setOf(StanzaContext.MULTIREGION),
            childContext = StanzaContext.REGION
        ),

        // Ingress child stanzas
        StanzaDefinition(
            "listener", "Defines ingress listener",
            setOf(StanzaContext.INGRESS),
            childContext = StanzaContext.LISTENER
        ),
        StanzaDefinition(
            "tls", "Configures TLS for ingress",
            setOf(StanzaContext.INGRESS),
            childContext = StanzaContext.TLS
        ),

        // Listener child stanzas
        StanzaDefinition(
            "service", "Defines service for listener",
            setOf(StanzaContext.LISTENER, StanzaContext.TERMINATING),
            childContext = StanzaContext.SERVICE
        ),

        // Expose child stanzas
        StanzaDefinition(
            "path", "Defines exposed path",
            setOf(StanzaContext.EXPOSE),
            childContext = StanzaContext.PATH
        ),

        // Volume child stanzas
        StanzaDefinition(
            "mount_options", "Configures volume mount options",
            setOf(StanzaContext.VOLUME),
            childContext = StanzaContext.MOUNT_OPTIONS
        ),

        // Meta stanza (can appear at multiple levels)
        StanzaDefinition(
            "meta", "User-defined metadata key-value pairs",
            setOf(StanzaContext.JOB, StanzaContext.GROUP, StanzaContext.TASK, StanzaContext.SERVICE),
            childContext = StanzaContext.META
        ),

        // Consul stanza (group and task level)
        StanzaDefinition(
            "consul", "Consul-specific configuration",
            setOf(StanzaContext.GROUP, StanzaContext.TASK),
            childContext = StanzaContext.CONSUL
        ),

        // Disconnect stanza
        StanzaDefinition(
            "disconnect", "Client disconnect strategy",
            setOf(StanzaContext.GROUP),
            childContext = StanzaContext.DISCONNECT
        ),

        // Network child: CNI
        StanzaDefinition(
            "cni", "CNI network configuration arguments",
            setOf(StanzaContext.NETWORK),
            childContext = StanzaContext.CNI
        ),

        // Artifact child stanzas
        StanzaDefinition(
            "options", "Artifact download options",
            setOf(StanzaContext.ARTIFACT),
            childContext = StanzaContext.OPTIONS
        ),
        StanzaDefinition(
            "headers", "HTTP headers for artifact download",
            setOf(StanzaContext.ARTIFACT),
            childContext = StanzaContext.HEADERS
        ),

        // Scaling child stanzas
        StanzaDefinition(
            "policy", "Autoscaling policy definition",
            setOf(StanzaContext.SCALING),
            childContext = StanzaContext.POLICY
        ),

        // Task child: dispatch_payload
        StanzaDefinition(
            "dispatch_payload", "Configures parameterized job payload",
            setOf(StanzaContext.TASK),
            childContext = StanzaContext.DISPATCH_PAYLOAD
        )
    )
    
    val properties = listOf(
        // Job properties
        PropertyDefinition("region", PropertyType.STRING, "Region to run job", setOf(StanzaContext.JOB)),
        PropertyDefinition("namespace", PropertyType.STRING, "Namespace for job", setOf(StanzaContext.JOB)),
        PropertyDefinition("datacenters", PropertyType.ARRAY_STRING, "List of datacenters", setOf(StanzaContext.JOB), required = true),
        PropertyDefinition("type", PropertyType.STRING, "Job type", setOf(StanzaContext.JOB), 
            validValues = listOf("service", "batch", "system", "sysbatch"), defaultValue = "service"),
        PropertyDefinition("priority", PropertyType.NUMBER, "Job priority (0-100)", setOf(StanzaContext.JOB), defaultValue = "50"),
        PropertyDefinition("all_at_once", PropertyType.BOOLEAN, "Run all task groups at once", setOf(StanzaContext.JOB)),
        
        // Group properties
        PropertyDefinition("count", PropertyType.NUMBER, "Number of task group instances", setOf(StanzaContext.GROUP), defaultValue = "1"),
        PropertyDefinition("shutdown_delay", PropertyType.DURATION, "Delay before shutdown", setOf(StanzaContext.GROUP)),
        
        // Task properties
        PropertyDefinition("driver", PropertyType.STRING, "Task driver", setOf(StanzaContext.TASK), 
            validValues = listOf("docker", "exec", "java", "raw_exec", "qemu", "podman"), required = true),
        PropertyDefinition("user", PropertyType.STRING, "User to run task as", setOf(StanzaContext.TASK)),
        PropertyDefinition("kill_timeout", PropertyType.DURATION, "Time to wait before force killing", setOf(StanzaContext.TASK), defaultValue = "5s"),
        PropertyDefinition("kill_signal", PropertyType.STRING, "Signal to send on shutdown", setOf(StanzaContext.TASK)),
        PropertyDefinition("leader", PropertyType.BOOLEAN, "Mark as leader task", setOf(StanzaContext.TASK)),
        
        // Service properties
        PropertyDefinition("name", PropertyType.STRING, "Service name", 
            setOf(StanzaContext.SERVICE, StanzaContext.CHECK, StanzaContext.VOLUME, StanzaContext.IDENTITY)),
        PropertyDefinition("port", PropertyType.STRING, "Port label", 
            setOf(StanzaContext.SERVICE, StanzaContext.CHECK, StanzaContext.PORT)),
        PropertyDefinition("tags", PropertyType.ARRAY_STRING, "Service tags", setOf(StanzaContext.SERVICE)),
        PropertyDefinition("address_mode", PropertyType.STRING, "Address mode", 
            setOf(StanzaContext.SERVICE, StanzaContext.CHECK),
            validValues = listOf("alloc", "driver", "host", "auto")),
        PropertyDefinition("provider", PropertyType.STRING, "Service provider", 
            setOf(StanzaContext.SERVICE),
            validValues = listOf("consul", "nomad")),
        
        // Check properties
        PropertyDefinition("type", PropertyType.STRING, "Check type", setOf(StanzaContext.CHECK),
            validValues = listOf("http", "tcp", "script", "grpc"), required = true),
        PropertyDefinition("path", PropertyType.STRING, "HTTP path", setOf(StanzaContext.CHECK)),
        PropertyDefinition("protocol", PropertyType.STRING, "Protocol", setOf(StanzaContext.CHECK),
            validValues = listOf("http", "https")),
        PropertyDefinition("interval", PropertyType.DURATION, "Check interval", setOf(StanzaContext.CHECK), required = true),
        PropertyDefinition("timeout", PropertyType.DURATION, "Check timeout", setOf(StanzaContext.CHECK), required = true),
        
        // Network properties
        PropertyDefinition("mode", PropertyType.STRING, "Network mode", setOf(StanzaContext.NETWORK, StanzaContext.RESTART),
            validValues = listOf("host", "bridge", "none", "fail", "delay")),
        PropertyDefinition("hostname", PropertyType.STRING, "Container hostname", setOf(StanzaContext.NETWORK)),
        
        // Port properties
        PropertyDefinition("static", PropertyType.NUMBER, "Static port number", setOf(StanzaContext.PORT)),
        PropertyDefinition("to", PropertyType.NUMBER, "Port to map to inside container", setOf(StanzaContext.PORT)),
        PropertyDefinition("host_network", PropertyType.STRING, "Host network to use", setOf(StanzaContext.PORT)),
        
        // Resources properties
        PropertyDefinition("cpu", PropertyType.NUMBER, "CPU in MHz", setOf(StanzaContext.RESOURCES)),
        PropertyDefinition("cores", PropertyType.NUMBER, "CPU cores", setOf(StanzaContext.RESOURCES)),
        PropertyDefinition("memory", PropertyType.NUMBER, "Memory in MB", setOf(StanzaContext.RESOURCES)),
        PropertyDefinition("memory_max", PropertyType.NUMBER, "Maximum memory in MB", setOf(StanzaContext.RESOURCES)),
        
        // Restart properties
        PropertyDefinition("attempts", PropertyType.NUMBER, "Restart attempts", 
            setOf(StanzaContext.RESTART, StanzaContext.RESCHEDULE)),
        PropertyDefinition("delay", PropertyType.DURATION, "Delay between restarts", 
            setOf(StanzaContext.RESTART, StanzaContext.RESCHEDULE)),
        
        // Update properties
        PropertyDefinition("max_parallel", PropertyType.NUMBER, "Max parallel updates", 
            setOf(StanzaContext.UPDATE, StanzaContext.MIGRATE, StanzaContext.MULTIREGION)),
        PropertyDefinition("health_check", PropertyType.STRING, "Health check type", 
            setOf(StanzaContext.UPDATE, StanzaContext.MIGRATE),
            validValues = listOf("checks", "task_states", "manual")),
        PropertyDefinition("min_healthy_time", PropertyType.DURATION, "Minimum healthy time", 
            setOf(StanzaContext.UPDATE, StanzaContext.MIGRATE)),
        PropertyDefinition("healthy_deadline", PropertyType.DURATION, "Healthy deadline", 
            setOf(StanzaContext.UPDATE, StanzaContext.MIGRATE)),
        PropertyDefinition("auto_revert", PropertyType.BOOLEAN, "Auto-revert on failure", setOf(StanzaContext.UPDATE)),
        PropertyDefinition("canary", PropertyType.NUMBER, "Number of canaries", setOf(StanzaContext.UPDATE)),
        
        // Volume properties
        PropertyDefinition("type", PropertyType.STRING, "Volume type", 
            setOf(StanzaContext.VOLUME, StanzaContext.CSI_PLUGIN),
            validValues = listOf("host", "csi")),
        PropertyDefinition("source", PropertyType.STRING, "Volume source", 
            setOf(StanzaContext.VOLUME, StanzaContext.ARTIFACT, StanzaContext.TEMPLATE)),
        PropertyDefinition("read_only", PropertyType.BOOLEAN, "Mount as read-only", 
            setOf(StanzaContext.VOLUME, StanzaContext.VOLUME_MOUNT)),
        PropertyDefinition("access_mode", PropertyType.STRING, "CSI access mode", setOf(StanzaContext.VOLUME)),
        PropertyDefinition("attachment_mode", PropertyType.STRING, "CSI attachment mode", setOf(StanzaContext.VOLUME)),
        
        // Volume mount properties
        PropertyDefinition("volume", PropertyType.STRING, "Volume name to mount", setOf(StanzaContext.VOLUME_MOUNT)),
        PropertyDefinition("destination", PropertyType.STRING, "Mount destination path", 
            setOf(StanzaContext.VOLUME_MOUNT, StanzaContext.ARTIFACT, StanzaContext.TEMPLATE)),
        
        // Template properties
        PropertyDefinition("data", PropertyType.STRING, "Inline template data", setOf(StanzaContext.TEMPLATE)),
        PropertyDefinition("change_mode", PropertyType.STRING, "Action on template change", 
            setOf(StanzaContext.TEMPLATE, StanzaContext.VAULT, StanzaContext.IDENTITY),
            validValues = listOf("restart", "noop", "signal", "script")),
        PropertyDefinition("change_signal", PropertyType.STRING, "Signal to send on change", 
            setOf(StanzaContext.TEMPLATE, StanzaContext.VAULT, StanzaContext.IDENTITY)),
        PropertyDefinition("perms", PropertyType.STRING, "File permissions", setOf(StanzaContext.TEMPLATE)),
        PropertyDefinition("env", PropertyType.BOOLEAN, "Render as environment variables", 
            setOf(StanzaContext.TEMPLATE, StanzaContext.VAULT, StanzaContext.IDENTITY)),
        
        // Vault properties
        PropertyDefinition("policies", PropertyType.ARRAY_STRING, "Vault policies", setOf(StanzaContext.VAULT)),
        PropertyDefinition("namespace", PropertyType.STRING, "Vault namespace", setOf(StanzaContext.VAULT)),
        
        // Lifecycle properties
        PropertyDefinition("hook", PropertyType.STRING, "Lifecycle hook", setOf(StanzaContext.LIFECYCLE),
            validValues = listOf("prestart", "poststart", "poststop")),
        PropertyDefinition("sidecar", PropertyType.BOOLEAN, "Run as sidecar", setOf(StanzaContext.LIFECYCLE)),
        
        // Periodic properties
        PropertyDefinition("cron", PropertyType.STRING, "Cron expression", setOf(StanzaContext.PERIODIC)),
        PropertyDefinition("prohibit_overlap", PropertyType.BOOLEAN, "Prevent overlapping runs", setOf(StanzaContext.PERIODIC)),
        PropertyDefinition("time_zone", PropertyType.STRING, "Time zone", setOf(StanzaContext.PERIODIC)),
        
        // Constraint properties
        PropertyDefinition("attribute", PropertyType.STRING, "Attribute to constrain", setOf(StanzaContext.CONSTRAINT, StanzaContext.AFFINITY)),
        PropertyDefinition("operator", PropertyType.STRING, "Constraint operator", setOf(StanzaContext.CONSTRAINT)),
        PropertyDefinition("value", PropertyType.STRING, "Constraint value", setOf(StanzaContext.CONSTRAINT, StanzaContext.AFFINITY)),
        
        // Affinity properties
        PropertyDefinition("weight", PropertyType.NUMBER, "Affinity weight (-100 to 100)", setOf(StanzaContext.AFFINITY, StanzaContext.SERVICE))
    )
    
    /**
     * Get valid child stanzas for a given context
     */
    fun getValidStanzas(context: StanzaContext): List<StanzaDefinition> {
        return stanzas.filter { it.contexts.contains(context) }
    }
    
    /**
     * Get valid properties for a given context
     */
    fun getValidProperties(context: StanzaContext): List<PropertyDefinition> {
        return properties.filter { it.contexts.contains(context) }
    }
    
    /**
     * Get all recognized stanza names
     */
    fun getAllStanzaNames(): Set<String> {
        return stanzas.map { it.name }.toSet()
    }

    /**
     * Build regex pattern for matching stanza blocks
     * Generates pattern dynamically from stanza definitions
     */
    fun buildStanzaMatchPattern(): String {
        val stanzaNames = getAllStanzaNames().sorted().joinToString("|")
        return """($stanzaNames)\s*(?:"[^"]*")?\s*\{"""
    }

    /**
     * Determine context from block hierarchy
     */
    fun determineContext(blockNames: List<String>): StanzaContext {
        if (blockNames.isEmpty()) return StanzaContext.ROOT

        val lastBlock = blockNames.last()
        return when (lastBlock) {
            "job" -> StanzaContext.JOB
            "group" -> StanzaContext.GROUP
            "task" -> StanzaContext.TASK
            "service" -> StanzaContext.SERVICE
            "check" -> StanzaContext.CHECK
            "network" -> StanzaContext.NETWORK
            "port" -> StanzaContext.PORT
            "resources" -> StanzaContext.RESOURCES
            "vault" -> StanzaContext.VAULT
            "template" -> StanzaContext.TEMPLATE
            "constraint" -> StanzaContext.CONSTRAINT
            "affinity" -> StanzaContext.AFFINITY
            "spread" -> StanzaContext.SPREAD
            "update" -> StanzaContext.UPDATE
            "migrate" -> StanzaContext.MIGRATE
            "reschedule" -> StanzaContext.RESCHEDULE
            "restart" -> StanzaContext.RESTART
            "ephemeral_disk" -> StanzaContext.EPHEMERAL_DISK
            "periodic" -> StanzaContext.PERIODIC
            "parameterized" -> StanzaContext.PARAMETERIZED
            "multiregion" -> StanzaContext.MULTIREGION
            "volume" -> StanzaContext.VOLUME
            "volume_mount" -> StanzaContext.VOLUME_MOUNT
            "logs" -> StanzaContext.LOGS
            "artifact" -> StanzaContext.ARTIFACT
            "lifecycle" -> StanzaContext.LIFECYCLE
            "config" -> StanzaContext.CONFIG
            "env" -> StanzaContext.ENV
            "connect" -> StanzaContext.CONNECT
            "sidecar_service" -> StanzaContext.SIDECAR_SERVICE
            "sidecar_task" -> StanzaContext.SIDECAR_TASK
            "proxy" -> StanzaContext.PROXY
            "upstreams" -> StanzaContext.UPSTREAM
            "gateway" -> StanzaContext.GATEWAY
            "ingress" -> StanzaContext.INGRESS
            "terminating" -> StanzaContext.TERMINATING
            "dns" -> StanzaContext.DNS
            "csi_plugin" -> StanzaContext.CSI_PLUGIN
            "scaling" -> StanzaContext.SCALING
            "identity" -> StanzaContext.IDENTITY
            "action" -> StanzaContext.ACTION
            "schedule" -> StanzaContext.SCHEDULE
            "device" -> StanzaContext.DEVICE
            "numa" -> StanzaContext.NUMA
            "header" -> StanzaContext.HEADER
            "check_restart" -> StanzaContext.CHECK_RESTART
            "expose" -> StanzaContext.EXPOSE
            "transparent_proxy" -> StanzaContext.TRANSPARENT_PROXY
            "mesh_gateway" -> StanzaContext.MESH_GATEWAY
            "target" -> StanzaContext.TARGET
            "wait" -> StanzaContext.WAIT
            "strategy" -> StanzaContext.STRATEGY
            "region" -> StanzaContext.REGION
            "listener" -> StanzaContext.LISTENER
            "tls" -> StanzaContext.TLS
            "mesh" -> StanzaContext.MESH
            "path" -> StanzaContext.PATH
            "mount_options" -> StanzaContext.MOUNT_OPTIONS
            "meta" -> StanzaContext.META
            "consul" -> StanzaContext.CONSUL
            "disconnect" -> StanzaContext.DISCONNECT
            "cni" -> StanzaContext.CNI
            "options" -> StanzaContext.OPTIONS
            "headers" -> StanzaContext.HEADERS
            "policy" -> StanzaContext.POLICY
            "dispatch_payload" -> StanzaContext.DISPATCH_PAYLOAD
            "change_script" -> StanzaContext.CHANGE_SCRIPT
            else -> StanzaContext.ROOT
        }
    }
}
