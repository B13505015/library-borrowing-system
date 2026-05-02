/**
 * register.js - 處理使用者註冊邏輯
 */

document.addEventListener('DOMContentLoaded', () => {
    const registerBtn = document.getElementById('registerBtn');
    const studentNoInput = document.getElementById('registerStudentNoInput');
    const nameInput = document.getElementById('registerNameInput');
    const passwordInput = document.getElementById('registerPasswordInput');
    const confirmInput = document.getElementById('confirmPasswordInput');
    const errorMessage = document.getElementById('registerErrorMessage');

    registerBtn.addEventListener('click', () => {
        handleRegister();
    });

    /**
     * 處理註冊邏輯
     */
    function handleRegister() {
        const studentNo = studentNoInput.value.trim();
        const name = nameInput.value.trim();
        const password = passwordInput.value.trim();
        const confirm = confirmInput.value.trim();

        // 基本前端驗證
        if (!studentNo || !name || !password || !confirm) {
            showError("所有欄位均為必填");
            return;
        }

        if (password !== confirm) {
            showError("兩次輸入的密碼不一致");
            return;
        }

        if (password.length < 4) {
            showError("密碼長度至少需要 4 位數");
            return;
        }

        console.log("提交註冊資料:", { studentNo, name });

        /* 
        未來串接 Java 後端規則：
        1. 使用 fetch('/api/register', { 
             method: 'POST', 
             headers: { 'Content-Type': 'application/json' },
             body: JSON.stringify({ studentNo, name, password }) 
           })
        2. Java 後端需先檢查 MySQL 中該學號是否已被註冊
        3. 若註冊成功，後端回傳成功訊息，前端跳轉至 login.html
        */

        // 模擬註冊成功
        alert("註冊成功！請重新登入");
        window.location.href = 'login.html';
    }

    function showError(msg) {
        errorMessage.innerText = msg;
        errorMessage.style.display = 'block';
    }
});