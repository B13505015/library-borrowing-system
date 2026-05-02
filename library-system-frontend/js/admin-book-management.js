/**
 * admin-book-management.js
 * 優化版：整合新增/編輯與即時搜尋
 */
document.addEventListener('DOMContentLoaded', () => {
    renderAdminBookTable();
    initEventListeners();
});

// 假資料 (之後對接 MySQL)
let mockBooks = [
    { id: 101, title: "Java 核心技術", pub: "台大出版", year: 2023, status: "AVAILABLE" },
    { id: 102, title: "MySQL 指南", pub: "歐萊禮", status: "BORROWED" }
];

function initEventListeners() {
    const modal = document.getElementById('bookFormModal');
    
    // 開啟新增視窗
    document.getElementById('addBookBtn').onclick = () => {
        openModal("新增書籍");
    };

    // 關閉視窗
    document.getElementById('closeBookModal').onclick = closeModal;
    document.getElementById('cancelBtn').onclick = closeModal;

    // 儲存按鈕 (判斷是新增還是修改)
    document.getElementById('saveBookBtn').onclick = handleSaveBook;

    // 即時搜尋功能 (Debounce)
    const searchInput = document.getElementById('adminBookSearchInput');
    searchInput.addEventListener('input', filterTable);
}

function openModal(title, bookData = null) {
    const modal = document.getElementById('bookFormModal');
    document.getElementById('modalTitle').innerText = title;
    
    if (bookData) {
        // 編輯模式：填入舊資料
        document.getElementById('m-book-id').value = bookData.id;
        document.getElementById('m-title').value = bookData.title;
        document.getElementById('m-publisher').value = bookData.pub;
        document.getElementById('m-year').value = bookData.year || "";
    } else {
        // 新增模式：清空表單
        document.getElementById('bookForm').reset();
        document.getElementById('m-book-id').value = "";
    }
    modal.style.display = 'flex';
}

function closeModal() {
    document.getElementById('bookFormModal').style.display = 'none';
}

function renderAdminBookTable() {
    const tbody = document.getElementById('adminBookTableBody');
    tbody.innerHTML = mockBooks.map(b => `
        <tr>
            <td>${b.title}</td>
            <td>${b.pub}</td>
            <td><span class="badge ${b.status === 'AVAILABLE' ? 'badge-success' : 'badge-danger'}">${b.status}</span></td>
            <td>
                <button class="btn btn-sm btn-outline" onclick="editBookTrigger(${b.id})">編輯</button>
                <button class="btn btn-sm btn-danger-outline" onclick="handleRemoveBook(${b.id})">下架</button>
            </td>
        </tr>
    `).join('');
}

// 觸發編輯按鈕
window.editBookTrigger = function(id) {
    const book = mockBooks.find(b => b.id === id);
    openModal("修改書籍資訊", book);
};

function handleSaveBook() {
    const id = document.getElementById('m-book-id').value;
    const title = document.getElementById('m-title').value;
    
    if (!title) return alert("書名不能為空！");

    if (id) {
        // 編輯邏輯
        console.log(`更新書籍 ID: ${id}`);
        // 未來：fetch('/api/books/update', { method: 'PUT', body: ... })
    } else {
        // 新增邏輯
        console.log("新增書籍至 MySQL");
        // 未來：fetch('/api/books/add', { method: 'POST', body: ... })
    }
    
    alert("儲存成功！");
    closeModal();
    renderAdminBookTable(); // 重新整理列表
}

function filterTable() {
    const keyword = document.getElementById('adminBookSearchInput').value.toLowerCase();
    const rows = document.querySelectorAll('#adminBookTableBody tr');
    rows.forEach(row => {
        row.style.display = row.innerText.toLowerCase().includes(keyword) ? '' : 'none';
    });
}

searchInput.addEventListener('input', () => {
    clearTimeout(searchTimer);
    // 等使用者停止打字 300 毫秒後才執行，節省效能
    searchTimer = setTimeout(() => {
        const keyword = searchInput.value.toLowerCase();
        const rows = document.querySelectorAll('#adminBookTableBody tr');
        
        rows.forEach(row => {
            const text = row.innerText.toLowerCase();
            row.style.display = text.includes(keyword) ? '' : 'none';
        });
    }, 300);
});

function filterTable() {
    const keyword = document.getElementById('adminBookSearchInput').value.toLowerCase();
    const tbody = document.getElementById('adminBookTableBody');
    const rows = tbody.querySelectorAll('tr:not(.no-data-row)'); // 抓取原本的資料列
    let hasResults = false;

    rows.forEach(row => {
        const text = row.innerText.toLowerCase();
        const isMatch = text.includes(keyword);
        row.style.display = isMatch ? '' : 'none';
        if (isMatch) hasResults = true;
    });

    // 處理查無資料提示
    const existingMsg = tbody.querySelector('.no-data-row');
    if (!hasResults) {
        if (!existingMsg) {
            const tr = document.createElement('tr');
            tr.className = 'no-data-row';
            // 注意：colspan 要等於你表格的總欄位數（此處為 4）
            tr.innerHTML = `
                <td colspan="4">
                    <div class="no-data-content">
                        <div class="no-data-icon">🔍</div>
                        <p>找不到與「<strong>${keyword}</strong>」相關的書籍</p>
                        <small>建議縮短關鍵字或檢查是否有錯字</small>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        } else {
            existingMsg.querySelector('strong').innerText = keyword;
        }
    } else if (existingMsg) {
        existingMsg.remove();
    }
}