document.addEventListener('DOMContentLoaded', () => {
    // 1. 自動讓當前頁面的側邊欄項目變藍色
    const currentPath = window.location.pathname.split('/').pop();
    document.querySelectorAll('.sidebar-menu a').forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.parentElement.classList.add('active');
        }
    });

    // 2. 登出確認邏輯
    const logoutLinks = document.querySelectorAll('a[href="login.html"]');
    const logoutModal = document.getElementById('logoutConfirmModal');
    
    if (logoutModal) {
        logoutLinks.forEach(link => {
            link.onclick = (e) => {
                e.preventDefault();
                logoutModal.style.display = 'flex';
            };
        });

        document.getElementById('cancelLogoutBtn').onclick = () => {
            logoutModal.style.display = 'none';
        };

        document.getElementById('confirmLogoutBtn').onclick = () => {
            window.location.href = 'login.html';
        };
    }
});