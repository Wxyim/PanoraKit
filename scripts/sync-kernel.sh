#!/usr/bin/env sh
set -eu

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
KERNEL_PROPERTIES="$PROJECT_ROOT/config/kernel.properties"
GOLANG_ROOT="$PROJECT_ROOT/lib/mihomo"
GOLANG_MAIN="$PROJECT_ROOT/lib/native/go"
MIHOMO_DIR="$GOLANG_ROOT/mihomo"

usage() {
  echo "Usage: $(basename "$0") <alpha|meta|smart> [--print-state]"
  exit 1
}

CHOICE="${1:-}"
MODE="${2:-}"

case "$CHOICE" in
  alpha|Alpha)
    REPO_URL="https://github.com/MetaCubeX/mihomo.git"
    RELEASE_TAG="Prerelease-Alpha"
    RELEASE_API_URL=""
    VERSION_SUFFIX=""
    ;;
  meta|Meta)
    REPO_URL="https://github.com/MetaCubeX/mihomo.git"
    RELEASE_TAG=""
    RELEASE_API_URL="https://api.github.com/repos/MetaCubeX/mihomo/releases/latest"
    VERSION_SUFFIX=""
    ;;
  smart|Smart)
    REPO_URL="https://github.com/vernesong/mihomo.git"
    RELEASE_TAG="Prerelease-Alpha"
    RELEASE_API_URL=""
    VERSION_SUFFIX="-Smart"
    ;;
  *)
    usage
    ;;
esac

case "$MODE" in
  ""|--print-state)
    ;;
  *)
    usage
    ;;
esac

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

require_cmd git
require_cmd curl

if [ "$MODE" != "--print-state" ]; then
  require_cmd go
fi

resolve_release_tag() {
  if [ -n "${RELEASE_TAG:-}" ]; then
    return
  fi

  if [ -z "${RELEASE_API_URL:-}" ]; then
    echo "Failed to resolve release tag: no release API configured" >&2
    exit 1
  fi

  RELEASE_TAG="$(curl -fsSL -H 'Accept: application/vnd.github+json' "$RELEASE_API_URL" \
    | sed -n 's/.*"tag_name"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p' | head -n 1)"

  if [ -z "${RELEASE_TAG:-}" ]; then
    echo "Failed to resolve release tag from ${RELEASE_API_URL:-configured source}" >&2
    exit 1
  fi
}

resolve_release_revision() {
  peeled_revision="$(git ls-remote "$REPO_URL" "refs/tags/$RELEASE_TAG^{}" | awk 'NR==1 { print $1 }')"
  direct_revision="$(git ls-remote "$REPO_URL" "refs/tags/$RELEASE_TAG" | awk 'NR==1 { print $1 }')"
  RELEASE_REVISION="${peeled_revision:-$direct_revision}"

  if [ -z "${RELEASE_REVISION:-}" ]; then
    echo "Failed to resolve revision for tag $RELEASE_TAG from $REPO_URL" >&2
    exit 1
  fi
}

update_kernel_properties() {
  tmp_file="$KERNEL_PROPERTIES.tmp.$$"
  : > "$tmp_file"
  seen_repo=0
  seen_branch=0
  seen_suffix=0

  while IFS= read -r line || [ -n "$line" ]; do
    case "$line" in
      external.mihomo.repo=*)
        echo "external.mihomo.repo=$REPO_URL" >> "$tmp_file"
        seen_repo=1
        ;;
      external.mihomo.branch=*)
        echo "external.mihomo.branch=$RELEASE_TAG" >> "$tmp_file"
        seen_branch=1
        ;;
      external.mihomo.suffix=*)
        echo "external.mihomo.suffix=$VERSION_SUFFIX" >> "$tmp_file"
        seen_suffix=1
        ;;
      *)
        echo "$line" >> "$tmp_file"
        ;;
    esac
  done < "$KERNEL_PROPERTIES"

  if [ "$seen_repo" -eq 0 ]; then
    echo "external.mihomo.repo=$REPO_URL" >> "$tmp_file"
  fi
  if [ "$seen_branch" -eq 0 ]; then
    echo "external.mihomo.branch=$RELEASE_TAG" >> "$tmp_file"
  fi
  if [ "$seen_suffix" -eq 0 ]; then
    echo "external.mihomo.suffix=$VERSION_SUFFIX" >> "$tmp_file"
  fi

  mv "$tmp_file" "$KERNEL_PROPERTIES"
  echo "Updated kernel.properties -> repo=$REPO_URL tag=$RELEASE_TAG revision=$RELEASE_REVISION suffix=$VERSION_SUFFIX"
}

sync_repo() {
  if [ -d "$MIHOMO_DIR" ]; then
    echo "Removing existing directory $MIHOMO_DIR"
    rm -rf "$MIHOMO_DIR"
  fi
  echo "Cloning $REPO_URL -> $MIHOMO_DIR"
  git clone --no-checkout "$REPO_URL" "$MIHOMO_DIR"
  git -C "$MIHOMO_DIR" checkout --force "$RELEASE_REVISION"
}

run_tidy() {
  if [ ! -f "$1/go.mod" ]; then
    echo "Skipping tidy for $1 (no go.mod found)"
    return
  fi
  echo "Running go mod tidy in $1"
  (
    cd "$1"
    go mod tidy
  )
}

resolve_release_tag
resolve_release_revision

if [ "$MODE" = "--print-state" ]; then
  printf '%s|%s|%s\n' "$REPO_URL" "$RELEASE_TAG" "$RELEASE_REVISION"
  exit 0
fi

update_kernel_properties
sync_repo
run_tidy "$GOLANG_ROOT"
run_tidy "$GOLANG_MAIN"

echo "Done: selected $CHOICE (tag=$RELEASE_TAG revision=$RELEASE_REVISION)"