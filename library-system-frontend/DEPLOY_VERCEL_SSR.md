# Vercel 部署（TanStack Start SSR）

本專案是 **TanStack Start SSR**，不是純 Vite SPA。

## 必要設定（Project Settings）

1. **Root Directory**：`library-system-frontend`
2. **Framework Preset**：Vite（可維持自動偵測）
3. **Build Command Override**：關閉（使用 package.json 內的 `npm run build`）
4. **Output Directory Override**：關閉（不要填 `dist` 或 `dist/client`）
5. **不要新增 SPA rewrite 到 `/index.html`**

## package.json（已設定）

`build` 應該是：

```json
"build": "vite build"
```

## 重新部署步驟

1. Push 最新程式碼到 GitHub。
2. 到 Vercel 的 Deployments 重新部署（Redeploy）。
3. 開最新 deployment URL 測試：
   - `/`
   - `/login`
4. 確認不再 404 / 白屏。

## 若仍白屏（除錯順序）

1. 先開瀏覽器 DevTools Console，貼第一個紅字錯誤。
2. 開 Network，確認主 JS 檔有載入（非 404）。
3. 檢查 Environment Variables 是否有遺漏（前端需要的 `VITE_*`）。
