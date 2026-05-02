/**
 * admin-borrow-management.js
 */
document.addEventListener('DOMContentLoaded', () => {
    renderAdminBorrowTable();
});

function renderAdminBorrowTable() {
    const tbody = document.getElementById('adminBorrowTableBody');
    
    // 假資料：之後改為 fetch('/api/admin/all-borrows')
    const mockBorrows = [
        { sno: "B13505015", name: "王大明", title: "Java 核心技術", dDate: "2026-05-15", status: "BORROWED" },
        { sno: "B12345678", name: "李小華", title: "計算機網路", dDate: "2026-04-10", status: "OVERDUE" }
    ];

    tbody.innerHTML = mockBorrows.map(b => `
        <tr>
            <td>${b.sno}</td>
            <td>${b.title}</td>
            <td>${b.dDate}</td>
            <td><span class="badge ${b.status === 'OVERDUE' ? 'badge-danger' : 'badge-info'}">${b.status}</span></td>
        </tr>
    `).join('');
}

// 在 admin-borrow-management.js 加入
document.getElementById('adminBorrowSearchBtn').onclick = function() {
    const keyword = document.getElementById('adminBorrowSearchInput').value.toLowerCase();
    const tbody = document.getElementById('adminBorrowTableBody');
    const rows = tbody.querySelectorAll('tr:not(.no-data-row)');
    let found = false;

    rows.forEach(row => {
        const match = row.innerText.toLowerCase().includes(keyword);
        row.style.display = match ? '' : 'none';
        if (match) found = true;
    });

    const msg = tbody.querySelector('.no-data-row');
    if (!found) {
        if (!msg) {
            tbody.insertAdjacentHTML('beforeend', `
                <tr class="no-data-row">
                    <td colspan="6"> <!-- 借閱紀錄欄位較多，設為 6 -->
                        <div class="no-data-content">
                            <div class="no-data-icon">📑</div>
                            <p>沒有找到關於「<strong>${keyword}</strong>」的借閱紀錄</p>
                        </div>
                    </td>
                </tr>
            `);
        } else {
            msg.querySelector('strong').innerText = keyword;
        }
    } else if (msg) {
        msg.remove();
    }
};