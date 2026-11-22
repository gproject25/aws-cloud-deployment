#!/bin/bash
# 에이전트가 도구를 쓸 때마다 logs/YYYY-MM-DD.log 에 한 줄씩 남긴다
LOG_DIR="$CLAUDE_PROJECT_DIR/logs"
mkdir -p "$LOG_DIR"

jq -r --arg root "$CLAUDE_PROJECT_DIR/" '
  [ (now | strflocaltime("%H:%M:%S")),
    .tool_name,
    ((.tool_input.file_path
      // .tool_input.command
      // .tool_input.skill
      // .tool_input.pattern
      // .tool_input.subagent_type
      // "-") | tostring | split("\n")[0] | ltrimstr($root))
  ] | join(" | ")
' >> "$LOG_DIR/$(date +%F).log"