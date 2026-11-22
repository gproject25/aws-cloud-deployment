#!/bin/bash
# .env / application*.yml 접근, docs/ 밖 Write·Edit 을 막는다
INPUT=$(cat)
TOOL=$(echo "$INPUT" | jq -r '.tool_name')
FILE=$(echo "$INPUT" | jq -r '.tool_input.file_path // ""')
CMD=$(echo "$INPUT" | jq -r '.tool_input.command // ""')

FILE=${FILE//\\//}
ROOT=${CLAUDE_PROJECT_DIR//\\//}

if [[ "$FILE $CMD" =~ (\.env|application[^[:space:]]*\.ya?ml) ]]; then
  echo ".env / yml 설정 파일은 읽거나 수정할 수 없습니다." >&2
  exit 2
fi

if [[ "$TOOL" =~ ^(Write|Edit)$ && "$FILE" != "$ROOT"/docs/* ]]; then
  echo "docs/ 밖의 파일은 수정할 수 없습니다." >&2
  exit 2
fi
exit 0