/**
 * search-books.js
 */
document.addEventListener('DOMContentLoaded', () => {
    const searchBtn = document.getElementById('bookSearchBtn');
    const closeModal = document.getElementById('closeModal');
    
    renderBookTable(); // 初始載入

    searchBtn.addEventListener('click', searchBooks);
    closeModal.addEventListener('click', () => {
        document.getElementById('bookDetailModal').style.display = 'none';
    });
});

// 假資料
const mockBooks = [
    { id: 101, title: "演算法圖解", publisher: "旗標", year: 2022, status: "AVAILABLE" },
    { id: 102, title: "Python 機器學習", publisher: "歐萊禮", year: 2023, status: "BORROWED" },
    { id: 103, title: "乾淨架構", publisher: "博碩", year: 2021, status: "AVAILABLE" }
];

function renderBookTable(data = mockBooks) {
    const tbody = document.getElementById('bookTableBody');
    
    /* 
    未來串接：
    fetch('/api/books/all').then(res => res.json()).then(books => ...)
    */

    tbody.innerHTML = data.map(book => `
        <tr>
            <td>${book.title}</td>
            <td>${book.publisher}</td>
            <td>${book.year}</td>
            <td><span class="badge ${book.status === 'AVAILABLE' ? 'badge-success' : 'badge-danger'}">${book.status}</span></td>
            <td>
                <button class="btn btn-sm btn-outline" onclick="showBookDetail(${book.id})">查看詳情</button>
                ${book.status === 'AVAILABLE' ? `<button class="btn btn-sm btn-primary" onclick="borrowBook(${book.id})">借閱</button>` : ''}
            </td>
        </tr>
    `).join('');
}

function searchBooks() {
    const keyword = document.getElementById('bookSearchInput').value.toLowerCase();
    
    /*
    未來串接：
    fetch(`/api/books/search?q=${keyword}`)
    */
    
    const filtered = mockBooks.filter(b => b.title.toLowerCase().includes(keyword));
    renderBookTable(filtered);
}

function showBookDetail(bookId) {
    const book = mockBooks.find(b => b.id === bookId);
    if (!book) return;

    document.getElementById('modalBookTitle').innerText = book.title;
    document.getElementById('modalPublisher').innerText = book.publisher;
    document.getElementById('modalYear').innerText = book.year;
    document.getElementById('modalStatus').innerText = book.status;
    
    document.getElementById('bookDetailModal').style.display = 'flex';
}

function borrowBook(id) {
    /* 
    未來串接：
    1. 取得使用者 ID (從 Session)
    2. 發送 POST 請求至 /api/borrow
    3. 後端更新 MySQL 狀態
    */
    alert("借閱請求已送出，書籍 ID: " + id);
}