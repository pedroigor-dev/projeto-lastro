# Release playbook

## Before the release

- The specification is `IMPLEMENTING` and all acceptance criteria are verified.
- Backend `mvn verify` and frontend tests/build are green.
- Quality, security and release-note gates contain reproducible references.
- Database changes are additive or have an explicit rollback procedure.
- The version and `CHANGELOG.md` are updated.

## Release

1. Create the tag from a reviewed commit on `main`.
2. Build immutable container images from that tag.
3. Apply Flyway migration with one backend replica.
4. Check `/actuator/health` and a read-only specification query.
5. Roll out the remaining replicas and the frontend.
6. Record the deployment URL or job as release evidence in Vertex.

## Rollback

Roll back the application image first. Database rollback is not automatic: use a reviewed forward-fix migration. If integrity is uncertain, stop writes and keep the read path available for investigation.
