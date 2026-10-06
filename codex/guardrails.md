# Codex Guardrails

- Work only inside `/home/swastik/Projects/quick_cart`.
- Do not read, edit, stage, commit, or push files outside this repository.
- Do not use remote infrastructure credentials on the `local-dev` branch.
- Prefer localhost services for development: PostgreSQL and Redis must run locally.
- Do not add generated files, build output, secrets, or temporary test artifacts to Git.
- If a temporary file or folder is created for testing, delete it after the check is complete.
- Do not run destructive Git commands unless the user explicitly asks for them.
- Do not commit or push unless the user explicitly asks in that turn.
