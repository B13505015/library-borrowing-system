# Backend Consolidation Plan

## Decision
- Primary backend runtime: `library-api/library-api`.
- Legacy backend module: `library-system-backend` (kept temporarily for importer/testing helpers only).

## Immediate actions completed
1. Documented primary runtime in root README.
2. Added legacy module notice in `library-system-backend`.

## Next actions
1. Move importer classes from `library-system-backend` into `library-api` or `tools/`.
2. Port any remaining manual tests to proper unit/integration tests under `library-api/src/test`.
3. Remove duplicated services/repositories/entities from `library-system-backend` once importer migration is done.
