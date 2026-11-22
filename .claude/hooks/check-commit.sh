#!/bin/bash
# git commit 메시지가 Conventional Commits 형식인지 검사한다
CMD=$(jq -r '.tool_input.command // ""')

if [[ "$CMD" =~ git[[:space:]].*commit ]]; then
  if ! [[ "$CMD" =~ -m[[:space:]]*[\"\']?(feat|fix|refactor|docs|chore|test)(\([^\)]+\))?:[[:space:]] ]]; then
    echo "커밋 메시지는 'feat|fix|refactor|docs: 내용' 형식이어야 합니다." >&2
    exit 2
  fi
fi
exit 0