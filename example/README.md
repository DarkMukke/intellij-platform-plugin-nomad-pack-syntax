# Nomad Pack Example

This is a test file to verify that the `nomadpack` language identifier works in markdown code fences.

## Example 1: Using `nomadpack` identifier

```nomadpack
job "hello-world" {
  region = "us-east-1"
  datacenters = ["dc1"]
  type = "service"
  
  group "app" {
    count = [[ .hello_world.count ]]
    
    network {
      port "http" {
        to = 8080
      }
    }
    
    service {
      name = "hello-world"
      port = "http"
      tags = ["http", "api"]
      
      check {
        type = "http"
        path = "/health"
        interval = "10s"
        timeout = "2s"
      }
    }
    
    task "server" {
      driver = "docker"
      
      config {
        image = "mnomitch/hello_world_server"
        ports = ["http"]
      }
      
      env {
        MESSAGE = [[ .hello_world.message | quote ]]
      }
      
      resources {
        cpu = 500
        memory = 256
      }
    }
  }
}
```

## Example 2: Using `nomad-pack` identifier

```nomad-pack
group "cache" {
  count = 2
  
  task "redis" {
    driver = "docker"
    
    config {
      image = "redis:7"
    }
  }
}
```

## Example 3: Template expressions with `nomad.tpl`

```nomad.tpl
[[ define "job_name" ]]
my-job-[[ .app.name ]]
[[ end ]]

[[ if .app.enable_monitoring ]]
service {
  name = "monitoring"
  port = "metrics"
}
[[ end ]]
```

## Example 4: Plain Nomad job with `nomad`

```nomad
job "example" {
  datacenters = ["dc1"]
  type = "batch"
  
  group "process" {
    task "script" {
      driver = "exec"
      
      config {
        command = "/bin/bash"
        args = ["-c", "echo hello"]
      }
    }
  }
}
```

## Features Highlighted

All code blocks above should have:
- ✅ Keyword highlighting (job, group, task, service, etc.)
- ✅ Template delimiter highlighting (`[[`, `]]`)
- ✅ String highlighting
- ✅ Number highlighting
- ✅ Variable highlighting (`.hello_world.count`)
- ✅ Function highlighting (`quote`, `toJson`)

## Autocompletion Test

Try typing this in a markdown file:
```
(backticks)nom
```

Then press **Ctrl+Space** to see suggestions for:
- `nomadpack`
- `nomad-pack`
- `nomad.tpl`
- `nomad`
