#!/usr/bin/env bash
set -euo pipefail

PACKAGE_NAME="com.github.nomadboxlab.monadbox"
ROUNDS=20
EVENTS_PER_ROUND=300
THROTTLE_MS=35
MONKEY_TIMEOUT_SEC=180
SEED_BASE=20260405
OUTPUT_DIR="build/llm-runtime-fuzz"
INSTALL_DEBUG=false
CLEAR_APP_DATA=false
STOP_ON_FAILURE=false

usage() {
  cat <<'EOF'
Usage: scripts/llm-runtime-fuzz.sh [options]

Options:
  --package-name <name>
  --rounds <n>
  --events-per-round <n>
  --throttle-ms <n>
  --monkey-timeout-sec <n>
  --seed-base <n>
  --output-dir <path>
  --install-debug
  --clear-app-data
  --stop-on-failure
  -h, --help
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --package-name) PACKAGE_NAME="$2"; shift 2 ;;
    --rounds) ROUNDS="$2"; shift 2 ;;
    --events-per-round) EVENTS_PER_ROUND="$2"; shift 2 ;;
    --throttle-ms) THROTTLE_MS="$2"; shift 2 ;;
    --monkey-timeout-sec) MONKEY_TIMEOUT_SEC="$2"; shift 2 ;;
    --seed-base) SEED_BASE="$2"; shift 2 ;;
    --output-dir) OUTPUT_DIR="$2"; shift 2 ;;
    --install-debug) INSTALL_DEBUG=true; shift ;;
    --clear-app-data) CLEAR_APP_DATA=true; shift ;;
    --stop-on-failure) STOP_ON_FAILURE=true; shift ;;
    -h|--help) usage; exit 0 ;;
    *) echo "Unknown argument: $1" >&2; usage; exit 1 ;;
  esac
done

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

resolve_adb_path() {
  if [[ -n "${ANDROID_SDK_ROOT:-}" && -x "$ANDROID_SDK_ROOT/platform-tools/adb" ]]; then
    echo "$ANDROID_SDK_ROOT/platform-tools/adb"
    return
  fi
  if [[ -x "$HOME/Library/Android/sdk/platform-tools/adb" ]]; then
    echo "$HOME/Library/Android/sdk/platform-tools/adb"
    return
  fi
  if [[ -x "$HOME/Android/Sdk/platform-tools/adb" ]]; then
    echo "$HOME/Android/Sdk/platform-tools/adb"
    return
  fi
  if command -v adb >/dev/null 2>&1; then
    command -v adb
    return
  fi
  echo "adb not found. Set ANDROID_SDK_ROOT or install Android platform-tools." >&2
  exit 1
}

json_escape() {
  sed -e 's/\\/\\\\/g' -e 's/"/\\"/g' -e ':a;N;$!ba;s/\n/\\n/g'
}

invoke_monkey_with_timeout() {
  local adb_path="$1"
  local device="$2"
  local package="$3"
  local seed="$4"
  local events="$5"
  local throttle="$6"
  local timeout_sec="$7"
  local output_file="$8"

  local rc=0
  local timed_out=false

  local cmd=("$adb_path" -s "$device" shell monkey -p "$package" --throttle "$throttle" --ignore-crashes --ignore-timeouts --ignore-security-exceptions -s "$seed" "$events")

  if command -v timeout >/dev/null 2>&1; then
    if ! timeout "${timeout_sec}s" "${cmd[@]}" >"$output_file" 2>&1; then
      rc=$?
      if [[ $rc -eq 124 ]]; then
        timed_out=true
      fi
    fi
  elif command -v gtimeout >/dev/null 2>&1; then
    if ! gtimeout "${timeout_sec}s" "${cmd[@]}" >"$output_file" 2>&1; then
      rc=$?
      if [[ $rc -eq 124 ]]; then
        timed_out=true
      fi
    fi
  else
    if ! "${cmd[@]}" >"$output_file" 2>&1; then
      rc=$?
    fi
  fi

  if [[ "$timed_out" == "true" ]]; then
    echo "monkey_timeout=true" >> "$output_file"
    "$adb_path" -s "$device" shell pkill -f monkey >/dev/null 2>&1 || true
  fi

  echo "$rc|$timed_out"
}

PATTERNS=(
  "FATAL EXCEPTION"
  "UnsatisfiedLinkError"
  "No implementation found"
  "RuntimeGatewayException"
  "RUNTIME_START_FAILED"
  "ANR in"
  "Input dispatching timed out"
  "SIGSEGV"
  "SIGABRT"
)

ADB="$(resolve_adb_path)"
ROOT_OUT="$PROJECT_ROOT/$OUTPUT_DIR"
LOG_DIR="$ROOT_OUT/logs"
SUMMARY_PATH="$ROOT_OUT/summary.json"
REPORT_PATH="$ROOT_OUT/report.txt"
mkdir -p "$LOG_DIR"

if [[ "$INSTALL_DEBUG" == "true" ]]; then
  echo "[llm-fuzz] Installing debug apk..."
  ./gradlew --no-daemon installDebug
fi

DEVICE="$($ADB devices | awk '/\tdevice$/ {print $1; exit}')"
if [[ -z "$DEVICE" ]]; then
  echo "No connected Android device/emulator found." >&2
  exit 1
fi

echo "[llm-fuzz] Using device: $DEVICE"

if [[ "$CLEAR_APP_DATA" == "true" ]]; then
  echo "[llm-fuzz] Clearing app data for $PACKAGE_NAME"
  "$ADB" -s "$DEVICE" shell pm clear "$PACKAGE_NAME" >/dev/null
fi

START_TS="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
START_EPOCH="$(date +%s)"

FAILED_COUNT=0
RESULTS_JSON=()
REPORT_LINES=()

for (( round=1; round<=ROUNDS; round++ )); do
  seed=$((SEED_BASE + round))
  log_file="$LOG_DIR/round-$(printf '%03d' "$round").log"
  monkey_output_file="$LOG_DIR/round-$(printf '%03d' "$round").monkey.out"

  expected_sec=$(awk -v e="$EVENTS_PER_ROUND" -v t="$THROTTLE_MS" 'BEGIN { printf "%.1f", (e * t) / 1000.0 }')
  echo "[llm-fuzz] Round $round/$ROUNDS seed=$seed events=$EVENTS_PER_ROUND expected~${expected_sec}s timeout=${MONKEY_TIMEOUT_SEC}s"

  "$ADB" -s "$DEVICE" logcat -c >/dev/null
  result_meta="$(invoke_monkey_with_timeout "$ADB" "$DEVICE" "$PACKAGE_NAME" "$seed" "$EVENTS_PER_ROUND" "$THROTTLE_MS" "$MONKEY_TIMEOUT_SEC" "$monkey_output_file")"
  monkey_exit_code="${result_meta%%|*}"
  monkey_timed_out="${result_meta##*|}"
  "$ADB" -s "$DEVICE" logcat -d > "$log_file"

  matched_patterns=()
  for pattern in "${PATTERNS[@]}"; do
    if grep -Fqi "$pattern" "$log_file"; then
      matched_patterns+=("$pattern")
    fi
  done

  has_failure=false
  if [[ "$monkey_exit_code" -ne 0 || ${#matched_patterns[@]} -gt 0 ]]; then
    has_failure=true
    FAILED_COUNT=$((FAILED_COUNT + 1))
    echo "[llm-fuzz] Failure signals detected in round $round"
  fi

  matched_json=""
  if (( ${#matched_patterns[@]} > 0 )); then
    for p in "${matched_patterns[@]}"; do
      escaped="$(printf '%s' "$p" | json_escape)"
      if [[ -z "$matched_json" ]]; then
        matched_json="\"$escaped\""
      else
        matched_json="$matched_json,\"$escaped\""
      fi
    done
  fi

  monkey_output_tail="$(tail -n 8 "$monkey_output_file" 2>/dev/null || true)"
  escaped_tail="$(printf '%s' "$monkey_output_tail" | json_escape)"
  escaped_log_file="$(printf '%s' "$log_file" | json_escape)"

  RESULTS_JSON+=("{\"round\":$round,\"seed\":$seed,\"events\":$EVENTS_PER_ROUND,\"monkeyExitCode\":$monkey_exit_code,\"monkeyTimedOut\":$monkey_timed_out,\"hasFailureSignals\":$has_failure,\"matchedPatterns\":[${matched_json}],\"monkeyOutputTail\":\"$escaped_tail\",\"logFile\":\"$escaped_log_file\"}")

  REPORT_LINES+=("round $(printf '%03d' "$round") seed=$seed monkeyExit=$monkey_exit_code fail=$has_failure")
  if (( ${#matched_patterns[@]} > 0 )); then
    REPORT_LINES+=("  matched: $(IFS=', '; echo "${matched_patterns[*]}")")
  fi
  REPORT_LINES+=("  log: $log_file")

  if [[ "$has_failure" == "true" && "$STOP_ON_FAILURE" == "true" ]]; then
    break
  fi
done

END_TS="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
END_EPOCH="$(date +%s)"
DURATION_SEC=$((END_EPOCH - START_EPOCH))
ROUNDS_EXECUTED=${#RESULTS_JSON[@]}

results_joined=""
for item in "${RESULTS_JSON[@]}"; do
  if [[ -z "$results_joined" ]]; then
    results_joined="$item"
  else
    results_joined="$results_joined,$item"
  fi
done

cat > "$SUMMARY_PATH" <<EOF
{
  "packageName": "$(printf '%s' "$PACKAGE_NAME" | json_escape)",
  "device": "$(printf '%s' "$DEVICE" | json_escape)",
  "roundsRequested": $ROUNDS,
  "roundsExecuted": $ROUNDS_EXECUTED,
  "failedRoundCount": $FAILED_COUNT,
  "startedAt": "$(printf '%s' "$START_TS" | json_escape)",
  "finishedAt": "$(printf '%s' "$END_TS" | json_escape)",
  "durationSeconds": $DURATION_SEC,
  "outputDir": "$(printf '%s' "$ROOT_OUT" | json_escape)",
  "logDir": "$(printf '%s' "$LOG_DIR" | json_escape)",
  "results": [${results_joined}]
}
EOF

{
  echo "LLM Runtime Fuzz Report"
  echo "package: $PACKAGE_NAME"
  echo "device: $DEVICE"
  echo "rounds requested: $ROUNDS"
  echo "rounds executed: $ROUNDS_EXECUTED"
  echo "failed rounds: $FAILED_COUNT"
  echo "duration sec: $DURATION_SEC"
  echo "summary json: $SUMMARY_PATH"
  echo
  printf '%s\n' "${REPORT_LINES[@]}"
} > "$REPORT_PATH"

echo "[llm-fuzz] Done. Summary: $SUMMARY_PATH"
echo "[llm-fuzz] Report:  $REPORT_PATH"
if (( FAILED_COUNT > 0 )); then
  exit 2
fi
exit 0
