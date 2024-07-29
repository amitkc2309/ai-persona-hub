#!/bin/bash

set -Eeuox pipefail

REPO_NAME="$1"
REPO_URL="$2"
COMMIT_HASH="$3"
MAX_RETRIES=1000
RETRY_DELAY=10

mkdir -p /repositories/"$REPO_NAME"
cd /repositories/"$REPO_NAME"
git init
git remote add origin "$REPO_URL"

for i in $(seq 1 $MAX_RETRIES); do
  if git fetch origin "$COMMIT_HASH" --depth=1; then
    break
  elif [ "$i" -eq "$MAX_RETRIES" ]; then
    echo "Failed to fetch repository after $MAX_RETRIES attempts."
    exit 1
  else
    echo "Retrying fetch in $RETRY_DELAY seconds..."
    sleep $RETRY_DELAY
  fi
done

git reset --hard "$COMMIT_HASH"
rm -rf .git
