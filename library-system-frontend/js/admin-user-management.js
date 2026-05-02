/**
 * admin-user-management.js
 */
document.addEventListener('DOMContentLoaded', () => {
    renderAdminUserTable();
    
    document.getElementById('adminUserSearchBtn').addEventListener('click', () => {
        const keyword = document.getElementById('adminUserSearchInput').value;
        alert("搜尋使用者：" + keyword);
    });
});

function renderAdminUserTable() {
    const tbody = document.getElementById('adminUserTableBody');
    
    // 假資料：之後改為 fetch('/api/admin/users')
    const mockUsers = [
        { sno: "B13505015", name: "王大明", level: "一般使用者", status: "ACTIVE" },
        { sno: "B12345678", name: "李小華", level: "一般使用者", status: "SUSPENDED" }
    ];

    tbody.innerHTML = mockUsers.map(user => `
        <tr>
            <td>${user.sno}</td>
            <td>${user.name}</td>
            <td><span class="badge ${user.status === 'ACTIVE' ? 'badge-success' : 'badge-danger'}">${user.status}</span></td>
            <td>
                ${user.status === 'ACTIVE' ? 
                    `<button class="btn btn-sm btn-danger-outline" onclick="handleSuspendUser('${user.sno}')">停權</button>` : 
                    `<button class="btn btn-sm btn-primary" onclick="handleActivateUser('${user.sno}')">復權</button>`
                }
            </td>
        </tr>
    `).join('');
}

function handleSuspendUser(sno) {
    // 未來串接 Java: fetch('/api/admin/user/suspend', { method: 'POST', body: sno })
    confirm(`確定要停權使用者 ${sno} 嗎？`);
}

function handleActivateUser(sno) {
    alert(`使用者 ${sno} 已恢復權限。`);
}

// 在 admin-user-management.js 加入監聽
document.getElementById('adminUserSearchInput').addEventListener('input', function() {
    const keyword = this.value.toLowerCase();
    const tbody = document.getElementById('adminUserTableBody');
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
                    <td colspan="4">
                        <div class="no-data-content">
                            <div class="no-data-icon">👤</div>
                            <p>查無使用者「<strong>${keyword}</strong>」</p>
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
});