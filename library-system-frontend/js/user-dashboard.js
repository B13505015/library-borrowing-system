document.addEventListener('DOMContentLoaded', () => {
    fetchDashboardStats();
    renderRecentBorrowRecords();
    renderOverdueRecords();
});

function fetchDashboardStats() {
    // 未來串接 Java: fetch('/api/admin/stats')
    document.getElementById('totalBooksValue').innerText = "520";
    document.getElementById('borrowedBooksValue').innerText = "42";
    document.getElementById('totalUsersValue').innerText = "158";
    document.getElementById('overdueBooksValue').innerText = "7";
}

function renderRecentBorrowRecords() {
    const tbody = document.getElementById('recentBorrowTableBody');
    const mockData = [
        { uid: "B13505015", title: "計算機組織", date: "2026-05-02" },
        { uid: "B12345678", title: "離散數學", date: "2026-05-01" }
    ];
    tbody.innerHTML = mockData.map(row => `
        <tr><td>${row.uid}</td><td>${row.title}</td><td>${row.date}</td></tr>
    `).join('');
}

function renderOverdueRecords() {
    const tbody = document.getElementById('overdueTableBody');
    const mockOverdue = [
        { uid: "B11122233", days: 5 },
        { uid: "B44455566", days: 12 }
    ];
    tbody.innerHTML = mockOverdue.map(row => `
        <tr><td>${row.uid}</td><td class="text-danger">${row.days} 天</td></tr>
    `).join('');
}