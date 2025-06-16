# `Pull Request`

## 介绍

### 非 `fork` 方式的 `Pull Request`

**Git Pull Request（中文：拉取请求）** 是 Git 协作流程中的一个核心概念，尤其在 GitHub、GitLab 等平台上广泛使用。它的核心目的是**让开发者将本地修改的代码提交到远程仓库的特定分支，并请求其他成员审核和合并**。以下是详细解释：

------

**核心流程**

1. **基于主分支创建新分支**
   开发者从 `main` 或 `master` 分支派生一个新分支（如 `feature/login`），在本地进行代码修改。
2. **提交并推送代码**
   将修改提交到本地仓库（`git commit`），然后推送到远程仓库（`git push origin feature/login`）。
3. **发起 Pull Request**
   在代码托管平台（如 GitHub）上，选择该分支，点击 **"New Pull Request"**，填写标题和描述，指定目标分支（如 `main`）。
4. **代码审核与讨论**
   - 团队成员审查代码变更，提出修改建议（通过评论或行内注释）。
   - 开发者根据反馈更新代码，再次推送（平台会自动关联更新到同一 PR）。
5. **合并代码**
   审核通过后，维护者将分支代码合并到目标分支（如 `main`），通常支持直接合并、变基（Rebase）或快进（Fast-forward）等方式。

------

**关键作用**

- **代码审查**：确保代码质量，减少错误。
- **协作透明**：所有变更可追溯，团队成员可参与讨论。
- **权限控制**：只有维护者能合并代码，避免直接推送（Force Push）的风险。

------

**常见场景**

- 功能开发完成后合并到主分支。
- 修复紧急 Bug 的热更新。
- 依赖第三方代码时的协作审核。

------

**与 `git pull` 的区别**

- `git pull`：本地命令，用于拉取远程最新代码到本地分支（`git fetch` + `git merge`）。
- Pull Request：是平台功能，强调**协作流程**，而非 Git 命令。

------

通过 Pull Request，团队能实现更安全、高效的代码协作，尤其适合开源项目或分布式团队。



### `fork` 方式的 `Pull Request`

通过 **Fork 方式** 发起 Git Pull Request 是开源项目协作中的常见流程，它允许开发者在不影响原始项目的情况下进行代码修改，并通过 PR 机制将更改提交给项目维护者审查和合并。以下是该流程的详细说明：

------

**Fork 方式的 Git Pull Request 流程**

1. **Fork 原始仓库**

   - 在 GitHub/Gitee 等平台上找到目标项目，点击 **Fork** 按钮，将项目复制到自己的账户中。
   - 此时，你拥有了一个与原始项目独立的副本，可以自由修改。

2. **克隆 Fork 的仓库到本地**

   ```bash
   git clone https://github.com/your-username/forked-repo.git
   cd forked-repo
   ```

3. **添加原始仓库为上游（upstream）**

   - 为了保持与原始项目的同步，添加原始仓库的远程地址：

     ```bash
     git remote add upstream https://github.com/original-author/original-repo.git
     ```

4. **创建本地开发分支**

   - 基于原始项目的最新分支（如 `main` 或 `dev`）创建一个新分支：

     ```bash
     git checkout -b feature/your-feature
     ```

5. **提交代码更改**

   - 在本地分支中进行代码修改，提交并推送到你的 Fork 仓库：

     ```bash
     git add .
     git commit -m "描述你的更改"
     git push -u origin feature/your-feature
     ```

6. **发起 Pull Request**

   - 打开你的 Fork 仓库页面，点击 **New Pull Request**。
   - 选择你的分支（`feature/your-feature`）和目标分支（如 `original-repo/main`）。
   - 填写 PR 的标题和描述，说明更改的目的和内容。
   - 点击 **Create Pull Request**，等待项目维护者审查。

7. **处理审查反馈**

   - 项目维护者可能会提出修改建议，你需要根据反馈更新代码并推送到同一分支。
   - PR 会自动更新，无需重新创建。

8. **合并 PR**

   - 审查通过后，维护者会将你的更改合并到原始项目中。
   - 你的名字将出现在项目的贡献者列表中。

------

**Fork 方式的优点**

- **独立性**：开发者可以在自己的 Fork 仓库中自由修改，不影响原始项目。
- **协作性**：通过 PR 机制，项目维护者可以审查代码，确保质量。
- **灵活性**：适合开源项目，外部贡献者无需直接访问原始仓库。

------

**注意事项**

- 同步上游：在开发过程中，定期从原始仓库拉取最新代码，避免冲突：

  ```bash
  git fetch upstream
  git merge upstream/main
  git push origin main
  ```

- **小而清晰的 PR**：每次 PR 应只关注一个功能或问题，方便维护者审查。

- **遵循贡献指南**：阅读项目的 `CONTRIBUTING.md`，按要求格式化代码和提交 PR。

------

**总结**

通过 Fork 方式发起 Git Pull Request 是开源协作的核心流程，它既保证了代码的独立性，又通过 PR 机制实现了代码审查和合并。这一流程促进了开源社区的发展，使多人能够有序地贡献和合并代码。



## 实践

在 `gitee` 中创建名为 `demo-git` 的仓库（未初始化的）。

在本地创建 `demo-git` 仓库：

```sh
# 使用 git init 初始化仓库
git init demo-git

# 切换到 demo-git 仓库
cd demo-git
```

创建文件 `master.txt`：

```
1
```

提交并推送修改到远程仓库

```sh
# 本地仓库指向远程仓库
git remote add origin https://gitee.com/dexterleslie/demo-git

# stage master.txt 文件
git add master.txt

# 提交
git commit -m "第一次提交"

# 推送到远程
git push --set-upstream origin master
```

登录另外一个 `gitee` 帐号 `dexles` 并从 `https://gitee.com/dexterleslie/demo-git` `fork` 仓库到自己的帐号中。

克隆自己的 `fork` 仓库到本地中

```sh
git clone https://gitee.com/dexles/demo-git.git
```

在 `fork` 仓库中为了保持与原始项目的同步，添加原始仓库的远程地址：

```bash
git remote add upstream https://gitee.com/dexterleslie/demo-git
```

- 在 `fork` 仓库中同步上游：在开发过程中，定期从原始仓库拉取最新代码，避免冲突：

  ```sh
  git pull upstream master
  git push
  ```

模拟在 `fork` 仓库开发过程中源仓库也有更新：

- 在源仓库中修改 `master.txt` 添加多一个行：

  ```
  1
  3
  ```

- 在源仓库中推送修改

  ```sh
  git add master.txt
  git commit -m "第二次提交"
  git push
  ```

在 `fork` 仓库中创建 `new-feature` 分支：

```sh
git checkout -b new-feature
```

在 `fork` 仓库中修改 `master.txt` 内容为：

```
12
3
```

在 `fork` 仓库中提交并推送修改

```sh
git add master.txt
git commit -m "fork仓库第一次提交"
git push --set-upstream origin new-feature
```

在 `fork` 仓库中发起 `Pull Request`：

- 打开你的 `fork` 仓库页面，点击 `New Pull Request`。
- 选择你的分支（`dexles/new-feature`）和目标分支（如 `dexterleslie/master`）。
- 填写 `PR` 的标题和描述，说明更改的目的和内容。
- 点击 `Create Pull Request`，等待项目维护者审查。

处理审查反馈：

- 项目维护者可能会提出修改建议，你需要根据反馈更新代码并推送到同一分支。

- `PR` 会自动更新，无需重新创建。

- 在 `fork` 仓库中修改 `master.txt` 内容为：

  ```
  124
  3
  ```

- 在 `fork` 仓库中提交并推送修改

  ```sh
  git add master.txt
  git commit -m "fork仓库第二次提交"
  git push
  ```

合并 `PR`：

- 审查通过后，维护者会将你的更改合并到原始项目中。
- 你的名字将出现在项目的贡献者列表中。

在 `fork` 仓库中同步最新的 `master` 分支代码并推送到远程仓库中：

- 同步源仓库 `master` 分支的最新代码：

  ```sh
  # 切换到 master 分支
  git checkout master
  
  # 同步源仓库 master 分支的最新代码
  git pull upstream master
  
  # 推送最新代码
  git push
  ```

  
