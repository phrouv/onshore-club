---
description: Rules and Best Practices for version control with gitmoji and conventional commits

applyTo: "**"
---

# Version control

## Commit messages

### Format:

Use gitmoji with conventional commits format
```
[gitmoji] [type][optional scope]: [description]

[body]
```

### Example:
```
✨ feat(api): send an email to the customer when a product is shipped

- Add email templates
- Implement send email logic
```

### Types with corresponding gitmoji:

| Type | Gitmoji | Description |
|------|---------|-------------|
| `feat` | ✨ `:sparkles:` | Introduce new features |
| `fix` | 🐛 `:bug:` | Fix a bug |
| `docs` | 📝 `:memo:` | Add or update documentation |
| `style` | 💄 `:lipstick:` | Add or update the UI and style files |
| `refactor` | ♻️ `:recycle:` | Refactor code |
| `perf` | ⚡️ `:zap:` | Improve performance |
| `test` | ✅ `:white_check_mark:` | Add, update, or pass tests |
| `build` | 👷 `:construction_worker:` | Add or update build system |
| `ci` | 💚 `:green_heart:` | Fix CI Build |
| `chore` | 🔧 `:wrench:` | Add or update configuration files |
| `revert` | ⏪️ `:rewind:` | Revert changes |

### Additional useful gitmoji:

| Gitmoji | Code | Description |
|---------|------|-------------|
| 🎉 | `:tada:` | Begin a project |
| 🚀 | `:rocket:` | Deploy stuff |
| 🔥 | `:fire:` | Remove code or files |
| 🚨 | `:rotating_light:` | Fix compiler / linter warnings |
| 💥 | `:boom:` | Introduce breaking changes |
| 🔒️ | `:lock:` | Fix security issues |
| 🔐 | `:closed_lock_with_key:` | Add or update secrets |
| 🗃️ | `:card_file_box:` | Perform database related changes |
| 📱 | `:iphone:` | Work on responsive design |

### More examples:
```
🐛 fix(auth): resolve login timeout issue

The session was expiring too quickly due to incorrect timer configuration.

🔧 chore: update eslint configuration

- Add new rules for TypeScript
- Update existing rules for better code quality

📝 docs(readme): add installation instructions

Added step-by-step guide for:
- Prerequisites
- Installation process
- Initial configuration

💥 feat!: change user authentication flow

BREAKING CHANGE: The authentication API now requires a different token format.
Users will need to re-authenticate after this update.
```

### Specification:

- **Clear and descriptive**: Commit messages should clearly explain what was changed
- **Reference the features or changes**: Include relevant context about the work being done
- **Document breaking changes**: Use `!` after type/scope and include `BREAKING CHANGE:` in body
- **Use present tense**: "Add feature" not "Added feature"
- **Capitalize first letter**: After the gitmoji and type
- **No period**: Don't end the description with a period
- **Body optional**: Include body for complex changes, explaining the what and why
- **50 character limit**: Keep the description line under 50 characters when possible
