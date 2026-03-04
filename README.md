# Nomad Pack Support for IntelliJ

[![Build](https://github.com/darkmukke/intellij-platform-plugin-nomad-pack-syntax/workflows/Build/badge.svg)](https://github.com/darkmukke/intellij-platform-plugin-nomad-pack-syntax/actions)

<!-- Plugin description -->
IntelliJ IDEA plugin providing comprehensive support for [HashiCorp Nomad Pack](https://github.com/hashicorp/nomad-pack) template files and registries.

Features include:
- Nomad Pack Registry detection (README + LICENSE + packs/)
- Context-aware autocompletion for Nomad stanzas and properties
- Syntax highlighting for `[[ ]]` Go template expressions
- Variable completion from `variables.hcl`
- Error highlighting for invalid stanza hierarchy
- Support for 70+ Nomad stanzas (based on Nomad v1.11.x)
- HCL language injection in template files
- Custom icons for packs, templates, and metadata files
<!-- Plugin description end -->

This is my first IntelliJ plugin, if you have any suggestions, feel free to open an issue or submit a PR.

## Features

### 🎯 Smart Project Detection
- **Nomad Pack Registry Detection**: Automatically detects registry projects (README + LICENSE + packs/)
- **Automatic File Type Override**: All `.tpl` files in registries use Nomad syntax
- **Individual Pack Detection**: Works with standalone packs (metadata.hcl + variables.hcl)

### ✨ Language Support
- **Syntax Highlighting**: Full support for `[[ ]]` Go template expressions
- **File Type Recognition**: `.nomad.tpl`, `.tpl`, and `.nomad` files
- **HCL Language Injection**: Seamless HCL support in template files

### 🚀 Smart Code Completion
- **Context-Aware Stanza Completion**: Suggests valid Nomad stanzas based on hierarchy
- **Property Autocompletion**: Complete properties with type information
- **Variable Completion**: Auto-complete variables from `variables.hcl`
- **Hierarchy Validation**: Real-time error highlighting for invalid stanza placement

### 🏗️ Nomad Job Specification Support
- **Complete Stanza Hierarchy**: 70+ stanzas including:
  - Core: job, group, task, service
  - Networking: network, port, dns, cni
  - Service Mesh: connect, sidecar_service, gateway, ingress
  - Resources: resources, device, numa
  - And many more...
- **Based on Nomad v1.11.x** specification

### 🎨 Enhanced Project View
- **Custom Icons**: Distinct icons for packs, templates, metadata, and variables
- **Structure Recognition**: Automatically decorates Nomad Pack directories

## Installation

### From JetBrains Marketplace
1. Open IntelliJ IDEA
2. Go to `Settings` → `Plugins` → `Marketplace`
3. Search for "Nomad Pack Support"
4. Click `Install`

### From Source
```bash
git clone https://github.com/darkmukke/intellij-platform-plugin-nomad-pack-syntax.git
cd intellij-platform-plugin-nomad-pack-syntax
./gradlew buildPlugin
```

The plugin will be built in `build/distributions/`.

## Usage

### Nomad Pack Registry
The plugin automatically detects Nomad Pack registries with this structure:

```
my-registry/
├── README.md
├── LICENSE
└── packs/
    ├── webapp/
    │   ├── metadata.hcl
    │   ├── variables.hcl
    │   └── templates/
    │       ├── _helpers.tpl       # ✓ Recognized as Nomad template
    │       └── webapp.nomad.tpl   # ✓ Full language support
    └── database/
        └── ...
```

### Individual Pack
Works with standalone packs too:

```
my-pack/
├── metadata.hcl
├── variables.hcl
└── templates/
    └── job.nomad.tpl
```

### Code Completion
Inside `.tpl` files, press `Ctrl+Space` (or `Cmd+Space` on Mac) to:

- **In root context**: Get `job` stanza
- **Inside `job {}`**: Get valid job-level stanzas (group, constraint, periodic, etc.)
- **Inside `task {}`**: Get valid task-level stanzas (config, resources, template, etc.)
- **For properties**: Get valid properties with types (name, port, driver, etc.)

### Variable Completion
Inside `[[ ]]` template expressions, press `Ctrl+Space` after `.` to complete:
- Variables from `variables.hcl`
- Built-in Nomad Pack variables (`nomad_pack.pack.name`, etc.)

## Development

### Requirements
- JDK 17+
- IntelliJ IDEA 2023.3+

### Build
```bash
./gradlew buildPlugin
```

### Run in Development Mode
```bash
./gradlew runIde
```

### Run Tests
```bash
./gradlew test
```

## Project Structure

```
src/main/kotlin/com/github/darkmukke/nomadpack/
├── annotator/           # Error highlighting
├── completion/          # Code completion providers
├── filetype/           # File type detection and icons
├── highlighter/        # Syntax highlighting
├── injection/          # Language injection (HCL in templates)
├── lexer/              # Lexical analysis
├── markdown/           # Markdown code fence support
├── parser/             # Template parsing
├── project/            # Project/registry detection
├── projectview/        # Project view customization
├── psi/                # PSI (Program Structure Interface)
└── structure/          # Nomad stanza hierarchy definitions
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Adding New Stanzas
1. Update `NomadStanzaHierarchy.kt` with new stanza definitions
2. Add context enum in `StanzaContext`
3. Properties are auto-completed based on hierarchy

### Updating Documentation
The `.aiassistant/nomad_stanza.md` file contains the complete stanza hierarchy documentation.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- Nomad job specification based on [HashiCorp Nomad v1.11.x](https://developer.hashicorp.com/nomad/docs/job-specification)
- Inspired by the [HCL plugin](https://github.com/VladRassokhin/intellij-hcl)
- Written with Claude AI Assistant

## Support

- [Report Issues](https://github.com/darkmukke/intellij-platform-plugin-nomad-pack-syntax/issues)
- [Request Features](https://github.com/darkmukke/intellij-platform-plugin-nomad-pack-syntax/issues/new)

## Links

- [HashiCorp Nomad](https://www.nomadproject.io/)
- [Nomad Pack](https://github.com/hashicorp/nomad-pack)
- [Nomad Pack Registry](https://github.com/hashicorp/nomad-pack-community-registry)
- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)
