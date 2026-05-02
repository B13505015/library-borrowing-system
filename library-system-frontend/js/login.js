/**
 * login.js - 處理登入頁面邏輯
 */

document.addEventListener('DOMContentLoaded', () => {
    const userLoginBtn = document.getElementById('userLoginBtn');
    const adminLoginBtn = document.getElementById('adminLoginBtn');
    const studentNoInput = document.getElementById('studentNoInput');
    const passwordInput = document.getElementById('passwordInput');
    const errorMessage = document.getElementById('loginErrorMessage');

    // 綁定使用者登入事件
    userLoginBtn.addEventListener('click', () => {
        handleUserLogin();
    });

    // 綁定管理員登入事件
    adminLoginBtn.addEventListener('click', () => {
        handleAdminLogin();
    });

    /**
     * 處理一般使用者登入
     */
    function handleUserLogin() {
        const studentNo = studentNoInput.value.trim();
        const password = passwordInput.value.trim();

        if (!studentNo || !password) {
            showError("請輸入學號與密碼");
            return;
        }

        console.log("嘗試使用者登入:", studentNo);

        /* 
        未來串接 Java 後端規則：
        1. 使用 fetch('/api/login/user', { method: 'POST', body: ... })
        2. 將 studentNo 與 password 封裝成 JSON 送往後端 Servlet/Controller
        3. 根據後端回傳的狀態碼（如 200 OK）進行 Session 存儲並跳轉頁面
        */
        
        // 假資料驗證範例 (未來請刪除)
        if (studentNo.startsWith('B')) {
            alert("使用者登入成功！");
            window.location.href = 'user-dashboard.html'; // 跳轉至使用者首頁
        } else {
            showError("學號格式錯誤或帳號不存在");
        }
    }

    /**
     * 處理管理員登入
     */
    function handleAdminLogin() {
        const username = studentNoInput.value.trim();
        const password = passwordInput.value.trim();

        console.log("嘗試管理員登入:", username);

        /* 
        未來串接 Java 後端規則：
        1. 改為呼叫後端管理員驗證 API (如 /api/login/admin)
        2. 後端需從 MySQL 驗證該帳號是否具備管理員權限
        */

        // 假資料驗證範例 (未來請刪除)
        if (username === 'admin' && password === 'admin123') {
            alert("管理員登入成功！");
            window.location.href = 'admin-dashboard.html'; // 跳轉至管理員儀表板
        } else {
            showError("管理員帳號或密碼錯誤");
        }
    }

    function showError(msg) {
        errorMessage.innerText = msg;
        errorMessage.style.display = 'block';
    }
});