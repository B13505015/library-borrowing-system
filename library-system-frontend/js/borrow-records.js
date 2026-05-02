/**
 * borrow-records.js
 */
document.addEventListener('DOMContentLoaded', () => {
    renderBorrowRecords();
});

function renderBorrowRecords() {
    const tbody = document.getElementById('borrowRecordTableBody');
    
    // 假資料：之後改為 fetch('/api/user/borrow-records')
    // 狀態說明：BORROWING (借閱中), RETURNED (已還), OVERDUE (逾期)
    const mockRecords = [
        { id: 501, title: "Java 程式設計範例", bDate: "2026-04-20", dDate: "2026-05-10", rDate: "-", status: "BORROWING" },
        { id: 502, title: "資料庫系統概論", bDate: "2026-03-15", dDate: "2026-04-15", rDate: "2026-04-10", status: "RETURNED" },
        { id: 503, title: "計算機組織", bDate: "2026-03-01", dDate: "2026-03-31", rDate: "-", status: "OVERDUE" }
    ];

    tbody.innerHTML = mockRecords.map(record => `
        <tr>
            <td>${record.title}</td>
            <td>${record.bDate}</td>
            <td>${record.dDate}</td>
            <td>${record.rDate}</td>
            <td>
                <span class="badge ${getStatusClass(record.status)}">${record.status}</span>
            </td>
            <td>
                ${record.status !== 'RETURNED' ? 
                    `<button class="btn btn-sm btn-primary" onclick="handleReturnBook(${record.id})">還書</button>` : 
                    '-'}
            </td>
        </tr>
    `).join('');
}

function getStatusClass(status) {
    switch(status) {
        case 'BORROWING': return 'badge-info';
        case 'RETURNED': return 'badge-success';
        case 'OVERDUE': return 'badge-danger';
        default: return '';
    }
}

function handleReturnBook(recordId) {
    /*
    未來串接 Java 後端：
    fetch(`/api/borrow/return?recordId=${recordId}`, { method: 'POST' })
    */
    alert(`還書請求已送出 (紀錄 ID: ${recordId})，請等待系統確認。`);
}