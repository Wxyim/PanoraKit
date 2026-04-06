#!/usr/bin/env bash
set -euo pipefail

WARN_PATH_LENGTH=220
FAIL_PATH_LENGTH=245
FAIL_ON_WARNING=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --warn-path-length)
      WARN_PATH_LENGTH="$2"
      shift 2
      ;;
    --fail-path-length)
      FAIL_PATH_LENGTH="$2"
      shift 2
      ;;
    --fail-on-warning)
      FAIL_ON_WARNING=true
      shift
      ;;
    -h|--help)
      cat <<'EOF'
Usage: scripts/repo-health.sh [options]

Options:
  --warn-path-length <n>   Path length warning threshold (default: 220)
  --fail-path-length <n>   Path length failure threshold (default: 245)
  --fail-on-warning        Treat warnings as failures
EOF
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      exit 1
      ;;
  esac
done

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

EXCLUDE_DIRS=(.git .gradle .idea .kotlin build node_modules)

is_excluded_path() {
  local path="$1"
  for name in "${EXCLUDE_DIRS[@]}"; do
    if [[ "$path" == *"/$name/"* || "$path" == *"/$name" ]]; then
      return 0
    fi
  done
  return 1
}

collect_files() {
  find "$PROJECT_ROOT" \
    \( -name .git -o -name .gradle -o -name .idea -o -name .kotlin -o -name build -o -name node_modules \) -prune \
    -o -type f -print
}

echo "[repo-health] Scanning workspace: $PROJECT_ROOT"

mapfile -t FILES < <(collect_files)
echo "[repo-health] Files scanned: ${#FILES[@]}"

PATH_WARNINGS=()
PATH_FAILURES=()

for file in "${FILES[@]}"; do
  len=${#file}
  if (( len >= FAIL_PATH_LENGTH )); then
    PATH_FAILURES+=("PATH_FAIL|$len|$file")
  elif (( len >= WARN_PATH_LENGTH )); then
    PATH_WARNINGS+=("PATH_WARN|$len|$file")
  fi
done

# Gather top-level Kotlin declarations by package name using simple brace-depth tracking.
SYMBOL_TEMP="$(mktemp)"
for file in "${FILES[@]}"; do
  [[ "$file" == *.kt ]] || continue
  awk -v f="$file" '
    BEGIN { pkg=""; depth=0; lineNo=0 }
    {
      lineNo++
      depthStart=depth
      if (pkg=="" && match($0, /^[[:space:]]*package[[:space:]]+([A-Za-z0-9_.]+)/, m)) {
        pkg=m[1]
      }
      if (depthStart==0 && match($0, /^[[:space:]]*(public[[:space:]]+|internal[[:space:]]+|private[[:space:]]+|protected[[:space:]]+)?(data[[:space:]]+|sealed[[:space:]]+|enum[[:space:]]+|annotation[[:space:]]+)?(class|object|interface)[[:space:]]+([A-Za-z_][A-Za-z0-9_]*)/, d)) {
        sym=d[4]
        if (pkg!="") {
          print pkg "." sym "|" f ":" lineNo
        } else {
          print sym "|" f ":" lineNo
        }
      }
      opens=gsub(/\{/, "{", $0)
      closes=gsub(/\}/, "}", $0)
      depth += opens - closes
      if (depth < 0) depth = 0
    }
  ' "$file" >> "$SYMBOL_TEMP"
done

DUPLICATE_SYMBOLS=()
if [[ -s "$SYMBOL_TEMP" ]]; then
  while IFS= read -r symbol; do
    mapfile -t locs < <(grep -F "${symbol}|" "$SYMBOL_TEMP" | cut -d'|' -f2- | sort -u)
    if (( ${#locs[@]} > 1 )); then
      joined="$(printf '%s | ' "${locs[@]}")"
      joined="${joined% | }"
      DUPLICATE_SYMBOLS+=("DUP_KT_SYMBOL|$symbol|$joined")
    fi
  done < <(cut -d'|' -f1 "$SYMBOL_TEMP" | sort | uniq -d)
fi
rm -f "$SYMBOL_TEMP"

SERVICE_RUNBLOCKING=()
SERVICE_UNSAFE_ASSERTIONS=()

for file in "${FILES[@]}"; do
  [[ "$file" == *.kt ]] || continue
  if [[ "$file" =~ (/runtime/service/src/service/|/modules/runtime/service/src/service/) ]]; then
    line_no=0
    while IFS= read -r line; do
      line_no=$((line_no + 1))
      if [[ "$line" == *"runBlocking"* ]]; then
        SERVICE_RUNBLOCKING+=("RUNBLOCKING_SERVICE|$file:$line_no|$(echo "$line" | sed 's/^[[:space:]]*//')")
      fi
      if [[ "$line" == *"!!"* ]]; then
        SERVICE_UNSAFE_ASSERTIONS+=("UNSAFE_ASSERT_SERVICE|$file:$line_no|$(echo "$line" | sed 's/^[[:space:]]*//')")
      fi
    done < "$file"
  fi
done

BRIDGE_DIR="$PROJECT_ROOT/modules/core/src/core/bridge"
BRIDGE_WARN_THRESHOLD=25
BRIDGE_NATIVE_ENTRY_COUNT=0
BRIDGE_WARNING=""

if [[ -d "$BRIDGE_DIR" ]]; then
  BRIDGE_NATIVE_ENTRY_COUNT=$(rg -n --glob '*.kt' '\bexternal\s+fun\s+native' "$BRIDGE_DIR" 2>/dev/null | wc -l | tr -d ' ')
  if (( BRIDGE_NATIVE_ENTRY_COUNT > BRIDGE_WARN_THRESHOLD )); then
    BRIDGE_WARNING="BRIDGE_SURFACE_WARN|$BRIDGE_NATIVE_ENTRY_COUNT|$BRIDGE_DIR"
  fi
fi

if (( ${#PATH_WARNINGS[@]} > 0 )); then
  echo "[repo-health] Path warnings: ${#PATH_WARNINGS[@]}"
  printf '%s\n' "${PATH_WARNINGS[@]:0:50}"
fi

if (( ${#PATH_FAILURES[@]} > 0 )); then
  echo "[repo-health] Path failures: ${#PATH_FAILURES[@]}"
  printf '%s\n' "${PATH_FAILURES[@]:0:50}"
fi

if (( ${#DUPLICATE_SYMBOLS[@]} > 0 )); then
  echo "[repo-health] Duplicate Kotlin symbols: ${#DUPLICATE_SYMBOLS[@]}"
  printf '%s\n' "${DUPLICATE_SYMBOLS[@]:0:100}"
fi

if (( ${#SERVICE_RUNBLOCKING[@]} > 0 )); then
  echo "[repo-health] Service-layer runBlocking findings: ${#SERVICE_RUNBLOCKING[@]}"
  printf '%s\n' "${SERVICE_RUNBLOCKING[@]:0:100}"
fi

if (( ${#SERVICE_UNSAFE_ASSERTIONS[@]} > 0 )); then
  echo "[repo-health] Service-layer unsafe assertion findings: ${#SERVICE_UNSAFE_ASSERTIONS[@]}"
  printf '%s\n' "${SERVICE_UNSAFE_ASSERTIONS[@]:0:100}"
fi

if [[ -n "$BRIDGE_WARNING" ]]; then
  echo "[repo-health] Native bridge surface warning"
  echo "$BRIDGE_WARNING"
fi

HAS_ANY_WARNING=false
if (( ${#PATH_WARNINGS[@]} > 0 )) || [[ -n "$BRIDGE_WARNING" ]]; then
  HAS_ANY_WARNING=true
fi

HAS_WARNING_FAILURE=false
if [[ "$FAIL_ON_WARNING" == "true" && "$HAS_ANY_WARNING" == "true" ]]; then
  HAS_WARNING_FAILURE=true
fi

if (( ${#PATH_FAILURES[@]} > 0 )) || (( ${#DUPLICATE_SYMBOLS[@]} > 0 )) || (( ${#SERVICE_RUNBLOCKING[@]} > 0 )) || (( ${#SERVICE_UNSAFE_ASSERTIONS[@]} > 0 )) || [[ "$HAS_WARNING_FAILURE" == "true" ]]; then
  echo "[repo-health] FAILED"
  exit 1
fi

echo "[repo-health] PASSED"
