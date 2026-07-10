# ExAPK: APK 抽出・バックアップツール

<p align="center">
  <img src="images/logo.png" alt="ExAPK Logo" width="120"/>
</p>

<h1 align="center">ExAPK</h1>
<p align="center">
  <strong>Android 向け APK 抽出・バックアップユーティリティ</strong>
</p>

<p align="center">
  <a href="https://github.com/Curzyori/ex-apk"><strong>🌐 公式サイト</strong></a>
</p>

<div align="center">

[![Stars](https://img.shields.io/github/stars/Curzyori/ex-apk?style=for-the-badge&color=374151)](https://github.com/Curzyori/ex-apk/stargazers)
[![Forks](https://img.shields.io/github/forks/Curzyori/ex-apk?style=for-the-badge&color=374151)](https://github.com/Curzyori/ex-apk/network/members)
[![License](https://img.shields.io/badge/License-Apache--2.0-blue?style=for-the-badge)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android--10+-black?style=for-the-badge)](#)

</div>

<p align="center">
  <a href="#why-exapk">なぜ作ったか</a> ·
  <a href="#key-features">機能</a> ·
  <a href="#installation">インストール</a> ·
  <a href="#quick-start">クイックスタート</a> ·
  <a href="#preview">プレビュー</a> ·
  <a href="#support">サポート</a>
</p>

<p align="center">🌐 4+ 言語対応 —
  <a href="README.md">EN</a> ·
  <a href="README_ID.md">ID</a> ·
  <a href="README_CN.md">CN</a> ·
  <a href="README_JP.md"><b>JP</b></a>
</p>

---

## <a id="why-exapk"></a>🕒 なぜ ExAPK か?

root 権限なしで、インストール済みの任意の APK を抽出できます。出荷時リセット前のバックアップ、他者へのアプリ共有、QA テスト、個人のアプリアーカイブの構築に最適です。完全オフラインで動作します——インターネットは不要です。

|                              |                                                              |
| ----------------------------- | ------------------------------------------------------------ |
| ✅ **Root 不要**            | 標準 Android API を使用して任意のインストール済みアプリから APK を抽出 |
| ✅ **一括抽出**            | 複数のアプリを選択して一度に抽出 |
| ✅ **オフライン最優先**    | ネットワーク権限ゼロ——完全オフラインで動作 |
| ✅ **洗練された Material 3 UI** | 1000 以上のアプリを快適に操作できる高速でレスポンシブな UI |
| ✅ **柔軟な出力**          | APK ファイルは /Download/ExAPK/ に保存されアクセス容易 |

---

## <a id="key-features"></a>🎯 機能

| 機能 | 状態 | 説明 |
| :--- | :---: | :--- |
| **アプリ一覧** | ✅ | インストール済みアプリ（ユーザー＋システム）を表示 |
| **検索** | ✅ | アプリ名によるリアルタイム検索 |
| **フィルター** | ✅ | すべて / ユーザーアプリ / システムアプリ で絞り込み |
| **並べ替え** | ✅ | 名前 / サイズ / インストール日 で並べ替え |
| **一括抽出** | ✅ | 複数アプリを選択して一度に抽出 |
| **進捗追跡** | ✅ | 一括操作の抽出進捗を確認 |
| **APK 共有** | ✅ | システム intent で抽出した APK を共有 |
| **設定** | ✅ | ダーク/ライト/自動テーマ ＋ 言語（EN/ID/CN/JP） |
| **オフライン** | ✅ | ネットワーク権限不要 |

---

## <a id="installation"></a>📦 インストール

1. **APK をダウンロード**
   - 最新の APK を [GitHub Releases](https://github.com/Curzyori/ex-apk/releases) から取得
   - またはサイトへ：https://ex-apk.curzy.dev/

2. **Android にインストール**
   - デバイス設定で「提供元不明のアプリ」を許可
   - APK ファイルをタップしてインストール

3. **権限を付与**
   - 求められたら **ストレージ/ファイル** 権限を許可
   - インストール済みアプリの読み取りと APK 書き込みに必要

4. **使い始める**
   - ExAPK を開きインストール済みアプリを表示
   - アプリをタップして詳細を確認し APK を抽出
   - または複数アプリを選択し「Extract」FAB ボタンを使用

---

## <a id="quick-start"></a>🚀 クイックスタート

```bash
# ビルド不要 —— 事前ビルド済み APK を使用
# ダウンロード：https://github.com/Curzyori/ex-apk/releases

# インストール後：
# 1. ExAPK アプリを開く
# 2. インストール済みアプリを表示
# 3. 任意のアプリをタップ → 「Extract APK」
# 4. APK は /Download/ExAPK/ に保存
```

---

## <a id="preview"></a>🖼️ プレビュー

<p align="center">
  <img src="images/dashboard.jpg" alt="ExAPK ダッシュボード" width="400"/>
</p>

<p align="center">
  <img src="images/extrack-preview.jpg" alt="APK 抽出プレビュー" width="400"/>
</p>

<p align="center">
  <img src="images/setting-lang-preview.jpg" alt="言語設定" width="400"/>
</p>

<p align="center">
  <img src="images/file-output-preview.jpg" alt="抽出された APK ファイル" width="400"/>
</p>

---

## <a id="support"></a>☕ サポート

このプロジェクトを支援していただき、コーヒーをおごってください！💝

<a href="https://donate.curzy.dev/">
  <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" width="200">
</a>

---

## 📄 ライセンス

このプロジェクトは **Apache License 2.0** の下で公開されています — 詳細は [LICENSE](LICENSE) を参照してください。

<sub>50 Projects Challenge の第 19 番目のプロジェクトとして、**@Curzyori** が情熱を込めて開発しました</sub>
