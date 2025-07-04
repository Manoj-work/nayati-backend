
# TEAM GIT WORKFLOW GUIDE

A clean reference for teams working together in the same repository.

---

## 1. Branching Rules

| Branch Type         | Purpose                                       |
|---------------------|-----------------------------------------------|
| main                | Stable, production-ready code only.           |
| feature/xyz         | New features (one feature per branch).        |
| bugfix/xyz          | Fix specific bugs.                            |
| hotfix/xyz          | Urgent production fix.                        |

**Rules:**
- Never push directly to main.
- Always create a new branch for each task.
- Open a Pull Request (PR) to merge your work back.

---

## 2. Create a New Feature Branch

Start from the latest main:

```bash
# Update local info
git checkout main
git pull origin main

# Create your feature branch
git checkout -b feature/your-feature-name

# Do your work
git add .
git commit -m "feat: add your feature details"

# Push and set tracking branch
git push -u origin feature/your-feature-name
```

---

## 3. Work on Someone Else’s Branch

If you want to extend your teammate’s work:

```bash
# Fetch latest
git fetch origin

# Checkout their branch
git checkout feature/their-branch
git pull origin feature/their-branch

# Create your branch from theirs
git checkout -b feature/your-sub-branch

# Work, commit, push
git push -u origin feature/your-sub-branch
```

---

## 4. Keep Your Branch Up to Date

Before pushing, always pull first:

```bash
git pull
# or for cleaner history
git pull --rebase
```

- `git pull` merges remote changes into your local commits.
- `git pull --rebase` reapplies your local commits on top of updated remote commits. This keeps history linear.

---

## 5. What Happens if Two People Work on the Same Branch

**Scenario:**
- You and your teammate both work on `feature/xyz`.
- Your friend commits and pushes first.
- You made local edits but did not commit.
- Now you run `git pull`.

**What happens?**

- If your local edits do not conflict with the incoming commits, Git pulls the remote changes and keeps your local uncommitted edits intact.
- If there is a conflict, Git blocks the pull with an error:  
  `error: Your local changes would be overwritten by merge`
- Git never overwrites uncommitted local edits automatically.
- You must resolve it by either committing or stashing your edits first.

**How to handle:**

Option 1: Commit your edits, then pull with rebase:

```bash
git add .
git commit -m "wip: progress"
git pull --rebase
```

Option 2: Stash your edits, pull, then re-apply:

```bash
git stash
git pull --rebase
git stash pop
```

Option 3: Discard your edits if not needed:

```bash
git reset --hard HEAD
git pull
```

**Key point:** Pull before you push and handle conflicts when they appear.

---

## 6. If There’s a Conflict

1. Git shows conflict markers `<<<<<<<`, `=======`, `>>>>>>>` in files.
2. Edit the files to fix.
3. Mark resolved: `git add <file>`
4. If rebasing: `git rebase --continue`
5. Abort if needed: `git rebase --abort`
6. Push updated work.

---

## 7. Delete Old Branches

After merging:

```bash
# Delete local branch
git branch -d feature/xyz

# Delete remote branch
git push origin --delete feature/xyz

# Remove stale remotes
git fetch --prune
```

---

## 8. Sample Branch Diagram

```
main
 ├── feature/expenses
 │     ├── feature/expenses-ui
 │     ├── feature/expenses-api
 ├── feature/invoice
 ├── bugfix/login
 └── hotfix/prod-crash
```

---

## 9. Protect main

Use GitHub branch protection rules to enforce pull requests. No direct push to main.

---

## 10. Handy Command Cheatsheet

| Purpose                    | Command |
|----------------------------|---------|
| Clone repo                 | git clone <repo-url> |
| Fetch all remotes          | git fetch origin |
| Create branch from main    | git checkout -b feature/xyz |
| Push new branch            | git push -u origin feature/xyz |
| Pull with merge            | git pull |
| Pull with rebase           | git pull --rebase |
| See branches               | git branch -a |
| Delete local branch        | git branch -d branch-name |
| Delete remote branch       | git push origin --delete branch-name |
| Clean up stale remote refs | git fetch --prune |

---

## 11. Golden Rule

Pull before you push. Communicate. Use small, frequent pull requests. This avoids conflicts and keeps history clean.

