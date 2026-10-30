# Kernel patches

These patches are applied in filename order by `scripts/sync-kernel.{sh,ps1}`
immediately after the upstream mihomo checkout is refreshed.

- `0001-tracker-lifecycle-callbacks.patch` adds optional join/leave callbacks to
  `tunnel/statistic/manager.go`. The native tunnel uses them to record normal
  connection closes synchronously for the recent-request history, replacing the
  permanent 20ms close-diff sampler.
- `0002-rejected-flow-process-attribution.patch` retries process attribution for
  rejected and reject-drop flows while their sockets are still available. It adds
  a forced package-owner lookup for unresolved flows so a cached UID miss does
  not prevent another attempt.

Keep patches small and add a regression test alongside the upstream code when
possible. A failed `git apply` must stop the sync/build rather than silently
building an unpatched kernel.
