# Document Title Template

## Purpose

Generate the document title section for a tech design document.

## Template Variables

- `{{TITLE}}` - The purpose of the technical design (e.g., "Integration with New Payment Service Provider")
- `{{VERSION}}` - Document version number (e.g., "1.0", "2.1")
- `{{DATE}}` - Date of creation or last update (e.g., "2025-11-04")
- `{{AUTHORS}}` - List of contributors and reviewers (e.g., "John Doe, Jane Smith")

## Output Format

```markdown
# {{TITLE}}

**Version**: {{VERSION}}  
**Date**: {{DATE}}  
**Authors**: {{AUTHORS}}
```

## Example Usage

```markdown
# Tech Design: Integration with New Payment Service Provider

**Version**: 1.0  
**Date**: 2025-11-04  
**Authors**: John Doe, Jane Smith
```

## Guidelines

- Title should clearly state the purpose and scope
- Use semantic versioning for document versions
- Include all contributors and reviewers in authors list
- Update date whenever document is modified
