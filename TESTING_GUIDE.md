# Testing Guide for Nomad Pack Plugin

This guide will help you test the Nomad Pack IntelliJ plugin functionality.

## Running the Plugin

1. **Start the sandbox IDE:**
   ```bash
   ./gradlew runIde
   ```
   Or click the "Run Plugin" configuration in IntelliJ IDEA.

2. Wait for a new IntelliJ IDEA window to open (the sandbox environment).

## Testing Syntax Highlighting

### Test 1: Create a `.nomad.tpl` file

In the sandbox IDE:

1. Create a new file: `test.nomad.tpl`
2. Paste this content:

```hcl
job [[ template "job_name" . ]] {
  region = "us-east-1"
  datacenters = ["dc1", "dc2"]
  type = "service"
  
  group "app" {
    count = [[ .my_app.count ]]
    
    network {
      port "http" {
        to = 8080
      }
    }
    
    service {
      name = "my-service"
      port = "http"
      tags = ["http", "api"]
      
      check {
        type = "http"
        path = "/health"
        interval = "10s"
        timeout = "2s"
      }
    }
    
    restart {
      attempts = 3
      interval = "5m"
      delay = "15s"
      mode = "fail"
    }
    
    task "server" {
      driver = "docker"
      
      config {
        image = "nginx:latest"
        ports = ["http"]
      }
      
      env {
        PORT = "8080"
        ENV = [[ .my_app.environment | quote ]]
      }
      
      resources {
        cpu = 500
        memory = 256
      }
    }
  }
}
```

**Expected Results:**
- Keywords should be highlighted in color:
  - `job`, `group`, `task`, `service`, `network`, `port`, `check`, `restart`, `driver`, `config`, `env`, `resources`
  - `type`, `count`, `tags`, `attempts`, `interval`, `delay`, `mode`, `cpu`, `memory`
  - `true`, `false` (if used)
- Template delimiters `[[` and `]]` should be highlighted as braces
- Template expressions like `template "job_name" .` should be highlighted
- Strings in quotes should be highlighted as strings
- Numbers should be highlighted as numbers

### Test 2: Create a `.tpl` file (helper template)

Create: `_helpers.tpl`

```go-template
[[ define "job_name" ]]
my-nomad-job-[[ .my_app.name ]]
[[ end ]]

[[ define "region" ]]
[[ if .my_app.region ]]
region = [[ .my_app.region | quote ]]
[[ end ]]
[[ end ]]
```

**Expected Results:**
- `define`, `if`, `end` should be highlighted as keywords
- `[[` and `]]` should be highlighted
- Variable paths like `.my_app.name` should be highlighted

### Test 3: Create a `.nomad` file

Create: `example.nomad`

```hcl
job "example" {
  datacenters = ["dc1"]
  type = "service"
  
  group "web" {
    count = 3
    
    task "frontend" {
      driver = "docker"
      
      config {
        image = "nginx"
      }
      
      resources {
        cpu = 100
        memory = 128
      }
    }
  }
}
```

**Expected Results:**
- All Nomad stanzas should be highlighted as keywords

## Testing Autocompletion

### Test 1: Stanza Autocompletion

1. Open `test.nomad.tpl`
2. Start typing on a new line: `res` then press **Ctrl+Space**
3. You should see completion suggestions including:
   - `resources`
   - `restart`
   - `reschedule`
   - etc.

### Test 2: Inside Task Block

1. Inside a `task` block, type: `ar` then press **Ctrl+Space**
2. You should see: `artifact`

### Test 3: Anywhere in File

1. Type: `con` then press **Ctrl+Space**
2. You should see:
   - `config`
   - `constraint`
   - `connect`
   - `consul`
   - etc.

## Testing Variable Autocompletion

### Setup: Create Pack Structure

Create this structure in the sandbox IDE:

```
test_pack/
├── metadata.hcl
├── variables.hcl
└── templates/
    └── test.nomad.tpl
```

**metadata.hcl:**
```hcl
app {
  url = "https://example.com"
  name = "test_app"
}
```

**variables.hcl:**
```hcl
variable "count" {
  type = number
  default = 1
}

variable "region" {
  type = string
  default = "us-east-1"
}

variable "environment" {
  type = string
}
```

**templates/test.nomad.tpl:**
```hcl
job "example" {
  group "app" {
    count = [[ . ]]
  }
}
```

### Test Variable Completion

1. Open `templates/test.nomad.tpl`
2. Place cursor after `[[ .` 
3. Press **Ctrl+Space**
4. You should see:
   - `.test_app.count` (with type: number)
   - `.test_app.region` (with type: string)
   - `.test_app.environment` (with type: string)
   - `.nomad_pack.pack.name` (built-in)
   - `.nomad_pack.pack.version` (built-in)

## Testing Project View Icons

1. Create a pack structure (see above)
2. Look in the Project tool window
3. **Expected Results:**
   - Pack root directory (`test_pack/`) should have a custom purple icon
   - `metadata.hcl` should have an orange "M" icon
   - `variables.hcl` should have an orange "V" icon
   - `templates/` directory should have a teal "T" icon
   - `.nomad.tpl` files should have a blue "HT" icon
   - `.tpl` files should have a green "T" icon

## Troubleshooting

### No Highlighting Appears

1. Check that the file extension is correct (`.nomad.tpl`, `.tpl`, or `.nomad`)
2. Right-click the file → "Override File Type" → Select "Nomad HCL Template" or "Nomad Template"
3. Close and reopen the file

### Autocompletion Not Working

1. Make sure you're pressing **Ctrl+Space** (or **Cmd+Space** on Mac)
2. Try typing a few characters first, then press Ctrl+Space
3. Check that the file type is correctly detected (shown in status bar)

### Variable Completion Not Working

1. Verify the pack structure is correct:
   - Both `metadata.hcl` and `variables.hcl` must exist
   - Template file must be in a subdirectory of the pack root
2. Check that `variables.hcl` has valid syntax
3. Variables must be inside `[[ ]]` template expressions

### Plugin Doesn't Load

1. Check the IDE log: **Help → Show Log in Explorer/Finder**
2. Look for errors related to `com.github.darkmukke.nomadpack`
3. Try: **File → Invalidate Caches → Invalidate and Restart**

## Expected Behavior Summary

| Feature | Working? |
|---------|----------|
| `.nomad.tpl` file type detection | ✓ |
| `.tpl` file type detection | ✓ |
| `.nomad` file type detection | ✓ |
| Keyword highlighting (job, task, group, etc.) | ✓ |
| Template delimiter highlighting `[[ ]]` | ✓ |
| String highlighting | ✓ |
| Number highlighting | ✓ |
| Stanza autocompletion (Ctrl+Space) | ✓ |
| Variable autocompletion from variables.hcl | ✓ |
| Pack detection in Project view | ✓ |
| Custom file icons | ✓ |

## Reporting Issues

If something doesn't work:

1. Note which test fails
2. Check the IDE log for errors
3. Take a screenshot showing the issue
4. Note your IntelliJ Platform version
5. Create an issue with these details

## Success Criteria

The plugin is working correctly if:

1. ✅ Keywords are highlighted in different colors
2. ✅ Template expressions `[[ ]]` are highlighted
3. ✅ Ctrl+Space shows Nomad stanza suggestions
4. ✅ Variables from `variables.hcl` appear in completion
5. ✅ Pack directories show custom icons
6. ✅ Different file types have different icons

---

**Last Updated:** 2026-02-27
