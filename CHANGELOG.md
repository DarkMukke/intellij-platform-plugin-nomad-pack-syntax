# Changelog

All notable changes to the Nomad Pack Support plugin will be documented in this file.

## [Unreleased]

### Added
- Nomad Pack Registry detection (README + LICENSE + packs/ structure)
- Automatic `.tpl` file type override in registries  
- Context-aware stanza autocompletion with full hierarchy support
- Property autocompletion with type information
- Variable completion from `variables.hcl` files
- Error highlighting for invalid stanza placement
- Support for 70+ Nomad stanzas (based on Nomad v1.11.x)
- Syntax highlighting for `[[ ]]` Go template expressions
- HCL language injection in template files
- Custom icons for packs, templates, metadata, and variables
- Performance optimizations for large files
- Dynamic regex generation for stanza detection
- Template expression handling in context detection

### Features
- File types: `.nomad`, `.nomad.tpl`, `.tpl` (in Nomad Packs)
- Complete Nomad job specification hierarchy
- Service mesh support (Connect, sidecar, gateway, ingress)
- Resource management (device, NUMA)
- Network configuration (port, DNS, CNI)

## [0.1.0] - Initial Development

### Added
- Basic project structure
- File type detection
- Syntax highlighting
- Project view customization

[Unreleased]: https://github.com/darkmukke/intellij-platform-plugin-nomad-pack-syntax/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/darkmukke/intellij-platform-plugin-nomad-pack-syntax/releases/tag/v0.1.0
