package com.github.darkmukke.nomadpack.lexer

import com.github.darkmukke.nomadpack.NomadTplLanguage
import com.intellij.psi.tree.IElementType

class NomadTplTokenType(debugName: String) : IElementType(debugName, NomadTplLanguage)

object NomadTplTokenTypes {
    @JvmField val TEMPLATE_START = NomadTplTokenType("TEMPLATE_START")
    @JvmField val TEMPLATE_END = NomadTplTokenType("TEMPLATE_END")
    @JvmField val TEMPLATE_LEFT_TRIM = NomadTplTokenType("TEMPLATE_LEFT_TRIM")
    @JvmField val TEMPLATE_RIGHT_TRIM = NomadTplTokenType("TEMPLATE_RIGHT_TRIM")
    @JvmField val RAW_TEXT = NomadTplTokenType("RAW_TEXT")
    
    // Template expression tokens
    @JvmField val KEYWORD = NomadTplTokenType("KEYWORD")
    @JvmField val VARIABLE = NomadTplTokenType("VARIABLE")
    @JvmField val FUNCTION = NomadTplTokenType("FUNCTION")
    @JvmField val STRING_LITERAL = NomadTplTokenType("STRING_LITERAL")
    @JvmField val PIPE = NomadTplTokenType("PIPE")
    @JvmField val DOT = NomadTplTokenType("DOT")
    @JvmField val IDENTIFIER = NomadTplTokenType("IDENTIFIER")
    @JvmField val WHITE_SPACE = NomadTplTokenType("WHITE_SPACE")
    @JvmField val LPAREN = NomadTplTokenType("LPAREN")
    @JvmField val RPAREN = NomadTplTokenType("RPAREN")
    @JvmField val COMMA = NomadTplTokenType("COMMA")
    @JvmField val NUMBER = NomadTplTokenType("NUMBER")
    @JvmField val COLON = NomadTplTokenType("COLON")
    @JvmField val EQUALS = NomadTplTokenType("EQUALS")
    
    val KEYWORDS = setOf(
        // Template keywords (Go template syntax)
        "define", "if", "else", "end", "range", "with", "template", "block",
        "and", "or", "not", "eq", "ne", "lt", "le", "gt", "ge",

        // Top-level job stanzas
        "job", "region", "namespace", "id", "name", "type", "priority",
        "all_at_once", "datacenters", "node_pool", "constraint", "affinity",
        "update", "multiregion", "spread", "periodic", "parameterized",
        "reschedule", "migrate", "meta", "ui", "consul_namespace",
        "vault_namespace",

        // Job types
        "service", "batch", "system", "sysbatch",

        // TaskGroup stanzas
        "group", "count", "task", "volume", "restart", "disconnect",
        "ephemeral_disk", "network", "shutdown_delay", "scaling",
        "stop_after_client_disconnect", "max_client_disconnect",
        "prevent_reschedule_on_lost",

        // Task stanzas
        "driver", "user", "lifecycle", "config", "env", "resources",
        "logs", "artifact", "vault", "consul", "kill_timeout",
        "kill_signal", "leader", "kind", "secret", "identity",
        "action", "schedule", "csi_plugin", "dispatch_payload", "volume_mount",

        // Driver names (common)
        "docker", "exec", "java", "raw_exec", "qemu", "podman",

        // Service stanzas
        "tags", "canary_tags", "enable_tag_override", "port", "address_mode",
        "address", "check", "check_restart", "connect", "canary_meta",
        "tagged_addresses", "on_update", "weights", "provider", "cluster",

        // Service check types
        "http", "tcp", "script", "grpc",

        // Check stanzas
        "command", "args", "path", "protocol", "expose", "advertise",
        "interval", "timeout", "initial_status", "notes", "tls_server_name",
        "tls_skip_verify", "header", "method", "grpc_service", "grpc_use_tls",
        "success_before_passing", "failures_before_critical",
        "failures_before_warning", "body",

        // Network stanzas
        "mode", "device", "cidr", "ip", "dns", "reserved_ports",
        "hostname", "mbits", "cni",

        // Network modes
        "host", "bridge", "none",

        // DNS configuration
        "servers", "searches", "options",

        // Port configuration
        "static", "to", "host_network", "ignore_collision",

        // Resource stanzas
        "cpu", "cores", "memory", "memory_max", "disk", "numa",
        "secrets", "iops",

        // NUMA configuration
        "devices",

        // Device configuration

        // Constraint/Affinity/Spread operators
        "attribute", "value", "operator", "weight", "target", "percent",

        // Constraint operators
        "distinct_hosts", "distinct_property", "regexp", "set_contains",
        "version", "is_set", "is_not_set",

        // Update strategy
        "stagger", "max_parallel", "health_check", "min_healthy_time",
        "healthy_deadline", "progress_deadline", "canary", "auto_revert",
        "auto_promote",

        // Multiregion
        "strategy", "on_failure",

        // Periodic
        "enabled", "cron", "crons", "prohibit_overlap", "time_zone",

        // Parameterized
        "payload", "meta_required", "meta_optional",

        // UI
        "description", "link", "label", "url",

        // Restart policy
        "attempts", "delay", "mode", "render_templates",

        // Restart modes
        "fail", "delay",

        // Disconnect strategy
        "lost_after", "stop_on_client_after", "replace", "reconcile",

        // Reschedule policy
        "delay_function", "max_delay", "unlimited",

        // Delay functions
        "constant", "exponential", "fibonacci",

        // Migrate strategy

        // Ephemeral disk
        "sticky", "size",

        // Volume
        "source", "read_only", "access_mode", "attachment_mode",
        "mount_options", "per_alloc",

        // Access modes
        "single-node-reader-only", "single-node-writer",
        "multi-node-reader-only", "multi-node-multi-writer",
        "multi-node-single-writer",

        // Attachment modes
        "file-system", "block-device",

        // Volume mount
        "destination", "propagation_mode", "selinux_label",

        // Logs
        "max_files", "max_file_size", "disabled",

        // Dispatch payload
        "file",

        // Lifecycle
        "hook", "sidecar",

        // Lifecycle hooks
        "prestart", "poststart", "poststop",

        // Artifact
        "options", "headers", "insecure", "chown",

        // Template
        "data", "change_mode", "change_script", "change_signal",
        "once", "splay", "perms", "uid", "gid", "left_delimiter",
        "right_delimiter", "vault_grace", "wait", "error_on_missing_key",

        // Change modes
        "restart", "noop", "signal", "script",

        // Change script
        "fail_on_error",

        // Wait
        "min", "max",

        // Vault
        "policies", "role", "disable_file", "change_mode", "change_signal",
        "allow_token_expiration",

        // Secret

        // CSI plugin
        "mount_dir", "stage_publish_base_dir", "health_timeout",

        // CSI plugin types
        "node", "controller", "monolith",

        // Identity
        "aud", "filepath", "service_name", "ttl",

        // Action

        // Schedule
        "start",

        // Scaling
        "min", "policy",

        // Consul
        "partition",

        // Connect
        "native", "gateway", "sidecar_service", "sidecar_task",

        // Sidecar service
        "disable_default_tcp_check",

        // Sidecar task

        // Proxy
        "local_service_address", "local_service_port", "upstreams",
        "transparent_proxy",

        // Upstreams
        "destination_name", "destination_namespace", "destination_peer",
        "destination_partition", "destination_type", "local_bind_port",
        "datacenter", "local_bind_address", "local_bind_socket_path",
        "local_bind_socket_mode", "mesh_gateway",

        // Mesh gateway

        // Transparent proxy
        "outbound_port", "exclude_inbound_ports", "exclude_outbound_ports",
        "exclude_outbound_cidrs", "exclude_uids", "no_dns",

        // Expose
        "local_path_port", "listener_port",

        // Gateway
        "ingress", "terminating",

        // Gateway proxy
        "connect_timeout", "envoy_gateway_bind_tagged_addresses",
        "envoy_gateway_bind_addresses", "envoy_gateway_no_default_bind",
        "envoy_dns_discovery_type",

        // Ingress
        "tls", "listener",

        // Ingress TLS
        "tls_min_version", "tls_max_version", "cipher_suites", "sds",

        // SDS
        "cluster_name", "cert_resource",

        // Ingress listener

        // Ingress service
        "hosts", "request_headers", "response_headers", "max_connections",
        "max_pending_requests", "max_concurrent_requests",

        // HTTP header modifiers
        "add", "set", "remove",

        // Terminating
        "ca_file", "cert_file", "key_file", "sni",

        // Mesh

        // Weights
        "passing", "warning",

        // Check restart
        "limit", "grace", "ignore_warnings",

        // CNI

        // Common values
        "true", "false", "default", "global"
    )
}
