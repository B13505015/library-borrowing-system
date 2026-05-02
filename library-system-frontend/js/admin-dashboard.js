/**
 * Dashboard 頁面邏輯
 * 用於載入統計數據與紀錄
 */
document.addEventListener('DOMContentLoaded', () => {
    fetchDashboardStats();
    renderRecentBorrowRecords();
    renderOverdueRecords();
});

function fetchDashboardStats() {
    // 未來串接：從後端 API 取得 MySQL count 數據
    // fetch('/api/admin/stats')...
    document.getElementById('totalBooks').innerText = "1,250";
    document.getElementById('borrowedBooks').innerText = "84";
    document.getElementById('totalUsers').innerText = "312";
    document.getElementById('overdueCount').innerText = "5";
}

function renderRecentBorrowRecords() {
    const tbody = document.getElementById('recentActivitiesBody');
    const mockData = [
        { user: "B13505015", book: "Java 程式設計範例", date: "2026-05-01" },
        { user: "B13505001", book: "MySQL 指南", date: "2026-05-01" }
    ];

    tbody.innerHTML = mockData.map(item => `
        <tr>
            <td>${item.user}</td>
            <td>${item.book}</td>
            <td>${item.date}</td>
        </tr>
    `).join('');
}

function renderOverdueRecords() {
    const list = document.getElementById('overdueList');
    // 模擬從後端取得逾期名單
    const overdues = ["使用者 B12345678 - 逾期 3 天", "使用者 B87654321 - 逾期 1 天"];
    list.innerHTML = overdues.map(msg => `<li class="text-danger">${msg}</li>`).join('');
}

// 監聽左側按鈕
document.querySelectorAll('.sidebar-menu li').forEach(item => {
    item.addEventListener('click', function() {
        // 移除所有人的 active class
        document.querySelectorAll('.sidebar-menu li').forEach(i => i.classList.remove('active'));
        // 給自己加上 active
        this.classList.add('active');
        
        // 根據點擊的項目，呼叫不同的渲染函式，而不是跳轉網頁
        const target = this.dataset.target; // 假設你在 li 加了 data-target="user"
        if(target === 'user') {
            renderAdminUserTable(); // 只更新右邊表格，不換頁
        }
    });
});

// 在 admin-dashboard.js 中加入，自動根據網址點亮對應的側邊欄項目
document.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname.split('/').pop();
    const menuItems = document.querySelectorAll('.sidebar-menu a');
    
    menuItems.forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.parentElement.classList.add('active');
        } else {
            link.parentElement.classList.remove('active');
        }
    });
});