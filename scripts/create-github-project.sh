#!/usr/bin/env bash
# =============================================================================
# create-github-project.sh
# =============================================================================
# Populates the nico-salsa/Sofkify_BE GitHub repository with:
#   1. Labels  (epics, services, layers, patterns, sizes, types)
#   2. Issues  (25 INVEST user stories from docs_IA/invest_user_stories.json)
#   3. GitHub Project (table view, columns: Backlog / In Progress / In Review / Done)
#      and adds every issue to the project in the "Backlog" column.
#
# Prerequisites:
#   - gh CLI  (https://cli.github.com)  authenticated with:
#       gh auth login   OR   export GH_TOKEN=<PAT with repo + project scopes>
#   - python3 (stdlib only — no jq dependency)
#
# Usage:
#   chmod +x scripts/create-github-project.sh
#   ./scripts/create-github-project.sh
# =============================================================================

set -euo pipefail

REPO="nico-salsa/Sofkify_BE"
STORIES_FILE="$(cd "$(dirname "$0")/.." && pwd)/docs_IA/invest_user_stories.json"
PROJECT_TITLE="Sofkify Backend"
TMPDIR_STORIES="$(mktemp -d)"
trap 'rm -rf "$TMPDIR_STORIES"' EXIT

# ── colour helpers ──────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
info()    { echo -e "${CYAN}[INFO]${NC}  $*"; }
success() { echo -e "${GREEN}[OK]${NC}    $*"; }
warn()    { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*" >&2; }

# ── pre-flight checks ────────────────────────────────────────────────────────
command -v gh      >/dev/null 2>&1 || { error "gh CLI not found. Install from https://cli.github.com"; exit 1; }
command -v python3 >/dev/null 2>&1 || { error "python3 not found."; exit 1; }
[[ -f "$STORIES_FILE" ]]           || { error "Stories file not found: $STORIES_FILE"; exit 1; }

info "Authenticated as: $(gh api user --jq '.login' 2>/dev/null || echo 'unknown')"
info "Target repo: $REPO"
echo ""

# ════════════════════════════════════════════════════════════════════════════
# STEP 1 — Create Labels
# ════════════════════════════════════════════════════════════════════════════
info "── STEP 1: Creating labels ──────────────────────────────────────────"

declare -A LABELS=(
  # Epics
  ["epic:user-api-auth"]="0075ca"
  ["epic:product-catalog-stock"]="008672"
  ["epic:cart-management"]="e4e669"
  ["epic:order-processing-events"]="d73a4a"
  ["epic:observability-ops"]="cfd3d7"
  ["epic:containerization-local-env"]="e99695"
  # Services
  ["service:user-service"]="b4d4fb"
  ["service:product-service"]="c2e0c6"
  ["service:cart-service"]="f9d0c4"
  ["service:order-service"]="fef2c0"
  # Types
  ["type:feature"]="a2eeef"
  ["type:bug"]="d73a4a"
  ["type:chore"]="cfd3d7"
  # Layers
  ["layer:api"]="1d76db"
  ["layer:domain-rule"]="0e8a16"
  ["layer:messaging"]="e4e669"
  ["layer:infra"]="bfd4f2"
  # Patterns
  ["pattern:cross-service"]="d4c5f9"
  ["pattern:event-consumer"]="fbca04"
  ["pattern:domain-event"]="f9d0c4"
  ["pattern:idempotency"]="e99695"
  # Sizes
  ["size:S"]="c5def5"
  ["size:M"]="0075ca"
  ["size:L"]="d93f0b"
)

created_labels=0
skipped_labels=0

for label_name in "${!LABELS[@]}"; do
  color="${LABELS[$label_name]}"
  if gh label create "$label_name" \
       --repo "$REPO" \
       --color "$color" \
       --description "" \
       --force 2>/dev/null; then
    success "Label: $label_name"
    ((created_labels++))
  else
    warn "Skipped (exists): $label_name"
    ((skipped_labels++))
  fi
done

echo ""
info "Labels: $created_labels created, $skipped_labels skipped."
echo ""

# ════════════════════════════════════════════════════════════════════════════
# STEP 2 — Generate per-story temp files (avoids shell string escaping risks)
# ════════════════════════════════════════════════════════════════════════════
info "── STEP 2: Generating issue content files ───────────────────────────"

python3 - "$STORIES_FILE" "$TMPDIR_STORIES" << 'PYEOF'
import json, os, sys

stories_file = sys.argv[1]
out_dir      = sys.argv[2]

with open(stories_file, encoding='utf-8') as f:
    data = json.load(f)

for s in data['stories']:
    ac_lines = '\n'.join(f'- {ac}' for ac in s['acceptance_criteria'])

    ctx = s.get('technical_context', {})
    tech_lines = []
    if isinstance(ctx, dict):
        if ctx.get('endpoint'):
            tech_lines.append(f"**Endpoint:** `{ctx['endpoint']}`")
        if ctx.get('request_dto'):
            tech_lines.append(f"**Request DTO:** `{ctx['request_dto']}`")
        if ctx.get('http_status_success'):
            tech_lines.append(f"**Success status:** `{ctx['http_status_success']}`")
    tech_section = '  \n'.join(tech_lines) if tech_lines else '_No specific endpoint (infrastructure/async story)._'

    inv = s.get('invest_validation', {})
    invest_rows = ''
    for k in ['independent', 'negotiable', 'valuable', 'estimable', 'small', 'testable']:
        invest_rows += f"| **{k[0].upper()} — {k.capitalize()}** | {inv.get(k, '—')} |\n"

    body = (
        "## User Story\n"
        f"> {s['user_story']}\n\n"
        f"{s['description']}\n\n"
        "---\n\n"
        "## Acceptance Criteria\n\n"
        f"{ac_lines}\n\n"
        "---\n\n"
        "## Technical Context\n\n"
        f"{tech_section}\n\n"
        "---\n\n"
        "## INVEST Validation\n\n"
        "<details>\n<summary>Expand</summary>\n\n"
        "| Criterion | Validation |\n|---|---|\n"
        f"{invest_rows}"
        "</details>\n\n"
        "---\n\n"
        f"**Size:** `{s['size']}` · **Epic:** `{s['epic']}` · **Story ID:** `{s['id']}`\n"
    )

    # Write body to file (avoids any shell string-escaping issues)
    body_file = os.path.join(out_dir, f"{s['id']}.body.md")
    with open(body_file, 'w', encoding='utf-8') as bf:
        bf.write(body)

    # Write metadata (one JSON object per line) to a manifest
    meta = {
        'id':     s['id'],
        'title':  f"[{s['id']}] {s['title']}",
        'labels': s.get('labels', []),
        'body_file': body_file,
    }
    manifest_file = os.path.join(out_dir, 'manifest.jsonl')
    with open(manifest_file, 'a', encoding='utf-8') as mf:
        mf.write(json.dumps(meta, ensure_ascii=False) + '\n')

print(f"Generated {len(data['stories'])} story files in {out_dir}")
PYEOF

echo ""

# ════════════════════════════════════════════════════════════════════════════
# STEP 3 — Create GitHub Issues (one per story)
# ════════════════════════════════════════════════════════════════════════════
info "── STEP 3: Creating GitHub Issues ──────────────────────────────────"

MANIFEST="$TMPDIR_STORIES/manifest.jsonl"
declare -A ISSUE_NUMBERS=()

while IFS= read -r line; do
  story_id=$(python3 -c "import json,sys; d=json.loads(sys.argv[1]); print(d['id'])" "$line")
  title=$(python3    -c "import json,sys; d=json.loads(sys.argv[1]); print(d['title'])" "$line")
  body_file=$(python3 -c "import json,sys; d=json.loads(sys.argv[1]); print(d['body_file'])" "$line")
  labels_json=$(python3 -c "import json,sys; d=json.loads(sys.argv[1]); print(json.dumps(d['labels']))" "$line")

  # Check if issue already exists
  existing=$(gh issue list \
    --repo "$REPO" \
    --search "\"$story_id\" in:title" \
    --json number \
    --jq '.[0].number // empty' 2>/dev/null || echo "")

  if [[ -n "$existing" ]]; then
    warn "Issue exists (#$existing): $title"
    ISSUE_NUMBERS["$story_id"]="$existing"
    continue
  fi

  # Build --label flags safely using an array (no eval)
  label_args=()
  while IFS= read -r lbl; do
    label_args+=(--label "$lbl")
  done < <(python3 -c "import json,sys; [print(l) for l in json.loads(sys.argv[1])]" "$labels_json")

  # Create issue — body is read from file, avoiding any quoting issues
  issue_url=$(gh issue create \
    --repo "$REPO" \
    --title "$title" \
    --body-file "$body_file" \
    "${label_args[@]}" 2>/dev/null) || {
    error "Failed to create issue for $story_id"
    continue
  }

  issue_number=$(echo "$issue_url" | grep -oE '[0-9]+$')
  ISSUE_NUMBERS["$story_id"]="$issue_number"
  success "Issue #$issue_number: $title"

done < "$MANIFEST"

echo ""
info "Issues created/found: ${#ISSUE_NUMBERS[@]}"
echo ""

# ════════════════════════════════════════════════════════════════════════════
# STEP 4 — Create GitHub Project (table view)
# ════════════════════════════════════════════════════════════════════════════
info "── STEP 4: Creating GitHub Project ─────────────────────────────────"

OWNER=$(echo "$REPO" | cut -d'/' -f1)

EXISTING_PROJECT_ID=$(gh project list \
  --owner "$OWNER" \
  --format json \
  --jq ".projects[] | select(.title == \"$PROJECT_TITLE\") | .id" 2>/dev/null || echo "")

if [[ -n "$EXISTING_PROJECT_ID" ]]; then
  warn "Project '$PROJECT_TITLE' already exists (ID: $EXISTING_PROJECT_ID). Skipping creation."
  PROJECT_ID="$EXISTING_PROJECT_ID"
else
  PROJECT_URL=$(gh project create \
    --owner "$OWNER" \
    --title "$PROJECT_TITLE" \
    --format json \
    --jq '.url' 2>/dev/null) || {
    error "Failed to create GitHub Project. Ensure your token has 'project' scope."
    error "Run: gh auth refresh -s project"
    exit 1
  }
  PROJECT_ID=$(gh project list \
    --owner "$OWNER" \
    --format json \
    --jq ".projects[] | select(.title == \"$PROJECT_TITLE\") | .id" 2>/dev/null)
  success "Project created: $PROJECT_URL (ID: $PROJECT_ID)"
fi

echo ""

# ════════════════════════════════════════════════════════════════════════════
# STEP 5 — Add Status field note
# ════════════════════════════════════════════════════════════════════════════
info "── STEP 5: Project columns ──────────────────────────────────────────"
warn "GitHub Projects V2 'Status' field options (Backlog, In Progress, In Review, Done)"
warn "must be configured via the GitHub UI or GraphQL API."
warn "Navigate to: https://github.com/users/$OWNER/projects → Settings → Fields → Status"
echo ""

# ════════════════════════════════════════════════════════════════════════════
# STEP 6 — Add all issues to the project
# ════════════════════════════════════════════════════════════════════════════
info "── STEP 6: Adding issues to project ────────────────────────────────"

added=0
failed=0

for story_id in "${!ISSUE_NUMBERS[@]}"; do
  issue_number="${ISSUE_NUMBERS[$story_id]}"
  issue_url="https://github.com/$REPO/issues/$issue_number"

  if gh project item-add "$PROJECT_ID" \
       --owner "$OWNER" \
       --url "$issue_url" 2>/dev/null; then
    success "Added to project: $story_id (#$issue_number)"
    ((added++))
  else
    warn "Could not add to project: $story_id (#$issue_number)"
    ((failed++))
  fi
done

echo ""
info "Project items: $added added, $failed failed."
echo ""

# ════════════════════════════════════════════════════════════════════════════
# Summary
# ════════════════════════════════════════════════════════════════════════════
echo "════════════════════════════════════════════════════════════"
success "✅  GitHub Project setup complete!"
echo ""
echo "  Repository : https://github.com/$REPO"
echo "  Project    : https://github.com/users/$OWNER/projects"
echo "  Backlog    : https://github.com/$REPO/issues"
echo ""
echo "  Next steps:"
echo "  1. Open the project and set 'Status' field options:"
echo "     Backlog | In Progress | In Review | Done"
echo "  2. Move stories to the correct column as work starts."
echo "  3. Assign stories to team members via the project board."
echo "════════════════════════════════════════════════════════════"
