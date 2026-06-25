/* Kaatha Platform Single Page Application Logic */

const GATEWAY_URL = 'http://localhost:8080';

// Global state variables
let authState = {
  token: localStorage.getItem('kaatha_token') || null,
  phoneNumber: localStorage.getItem('kaatha_phone') || null,
  role: localStorage.getItem('kaatha_role') || 'SHOPKEEPER', // 'SHOPKEEPER' or 'CUSTOMER'
  userId: localStorage.getItem('kaatha_userId') || null,
  userName: localStorage.getItem('kaatha_userName') || 'User'
};

// Available items for purchase selection (populated when opening purchase modal)
let catalogItems = [];

// Initialize dashboard on load
document.addEventListener('DOMContentLoaded', () => {
  // Set date in top bars
  const dateStr = new Date().toLocaleDateString('en-US', {
    weekday: 'short', year: 'numeric', month: 'short', day: 'numeric'
  });
  const topbarDate = document.getElementById('topbar-date');
  if (topbarDate) topbarDate.textContent = dateStr;
  const custTopbarDate = document.getElementById('cust-topbar-date');
  if (custTopbarDate) custTopbarDate.textContent = dateStr;

  // Check login state
  checkSession();
});

/* ==========================================================================
   AUTHENTICATION LOGIC
   ========================================================================== */
function switchRole(role) {
  authState.role = role;
  document.querySelectorAll('.role-tab').forEach(tab => {
    if (tab.getAttribute('data-role') === role) {
      tab.classList.add('active');
    } else {
      tab.classList.remove('active');
    }
  });
}

function handlePhoneInput(input) {
  const sendBtn = document.getElementById('send-otp-btn');
  if (input.value.length === 10 && /^\d+$/.test(input.value)) {
    sendBtn.disabled = false;
  } else {
    sendBtn.disabled = true;
  }
}

// OTP input behavior
function otpBoxInput(input, idx) {
  if (input.value.length === 1 && idx < 5) {
    document.getElementById(`otp-${idx + 1}`).focus();
  }
}

function otpBoxKeydown(event, idx) {
  if (event.key === 'Backspace' && !event.target.value && idx > 0) {
    document.getElementById(`otp-${idx - 1}`).focus();
  }
}

function getOtpValue() {
  let otp = '';
  for (let i = 0; i < 6; i++) {
    otp += document.getElementById(`otp-${i}`).value;
  }
  return otp;
}

function clearOtpInputs() {
  for (let i = 0; i < 6; i++) {
    document.getElementById(`otp-${i}`).value = '';
  }
}

async function sendOtp() {
  const phone = document.getElementById('phone-input').value;
  const errDiv = document.getElementById('auth-error');
  const btn = document.getElementById('send-otp-btn');
  
  errDiv.classList.add('hidden');
  btn.disabled = true;
  btn.querySelector('.btn-loader').classList.remove('hidden');

  try {
    const res = await fetch(`${GATEWAY_URL}/auth/send-otp`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        phoneNumber: phone,
        userType: authState.role
      })
    });
    
    const result = await res.json();
    if (result.success) {
      document.getElementById('otp-phone-display').textContent = `+91 ${phone}`;
      document.getElementById('step-phone').classList.remove('active');
      document.getElementById('step-otp').classList.add('active');
      document.getElementById('otp-0').focus();
      showToast('OTP sent successfully!', 'success');
    } else {
      errDiv.textContent = result.message || 'Failed to send OTP. Is the number registered?';
      errDiv.classList.remove('hidden');
    }
  } catch (error) {
    console.error(error);
    errDiv.textContent = 'Server connection error. Ensure API Gateway is running.';
    errDiv.classList.remove('hidden');
  } finally {
    btn.disabled = false;
    btn.querySelector('.btn-loader').classList.add('hidden');
  }
}

async function verifyOtp() {
  const phone = document.getElementById('phone-input').value;
  const otp = getOtpValue();
  const errDiv = document.getElementById('auth-error');

  if (otp.length < 6) {
    errDiv.textContent = 'Please enter a 6-digit OTP code.';
    errDiv.classList.remove('hidden');
    return;
  }

  try {
    const res = await fetch(`${GATEWAY_URL}/auth/verify-otp`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        phoneNumber: phone,
        otp: otp,
        userType: authState.role
      })
    });

    const result = await res.json();
    if (result.success) {
      const data = result.data;
      authState.token = data.token;
      authState.phoneNumber = phone;
      authState.userId = data.userId;
      
      if (authState.role === 'SHOPKEEPER') {
        authState.userName = data.shopkeeperResponse ? `${data.shopkeeperResponse.firstName} ${data.shopkeeperResponse.lastName || ''}`.trim() : 'Shopkeeper';
      } else {
        authState.userName = data.customerResponse ? `${data.customerResponse.firstName} ${data.customerResponse.lastName || ''}`.trim() : 'Customer';
      }

      // Save to localStorage
      localStorage.setItem('kaatha_token', authState.token);
      localStorage.setItem('kaatha_phone', authState.phoneNumber);
      localStorage.setItem('kaatha_role', authState.role);
      localStorage.setItem('kaatha_userId', authState.userId);
      localStorage.setItem('kaatha_userName', authState.userName);

      showToast('Welcome back, ' + authState.userName + '!', 'success');
      enterDashboard();
    } else {
      errDiv.textContent = result.message || 'Invalid or expired OTP.';
      errDiv.classList.remove('hidden');
      clearOtpInputs();
      document.getElementById('otp-0').focus();
    }
  } catch (error) {
    console.error(error);
    errDiv.textContent = 'Verification error. Please try again.';
    errDiv.classList.remove('hidden');
  }
}

function backToPhone() {
  document.getElementById('step-otp').classList.remove('active');
  document.getElementById('step-phone').classList.add('active');
  document.getElementById('auth-error').classList.add('hidden');
  clearOtpInputs();
}

function checkSession() {
  if (authState.token && authState.phoneNumber) {
    enterDashboard();
  } else {
    document.getElementById('auth-screen').classList.add('active');
    document.getElementById('shopkeeper-dashboard').classList.remove('active');
    document.getElementById('customer-dashboard').classList.remove('active');
  }
}

function enterDashboard() {
  document.getElementById('auth-screen').classList.remove('active');
  
  if (authState.role === 'SHOPKEEPER') {
    document.getElementById('shopkeeper-dashboard').classList.add('active');
    document.getElementById('customer-dashboard').classList.remove('active');
    document.getElementById('sk-user-name').textContent = authState.userName;
    loadShopkeeperOverview();
  } else {
    document.getElementById('customer-dashboard').classList.add('active');
    document.getElementById('shopkeeper-dashboard').classList.remove('active');
    document.getElementById('cust-user-name').textContent = authState.userName;
    loadCustomerBalances();
  }
}

async function logout() {
  try {
    await fetch(`${GATEWAY_URL}/auth/logout/${authState.phoneNumber}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authState.token}`
      }
    });
  } catch (e) {
    console.error('Logout error on backend', e);
  }

  // Clear local storage state
  localStorage.clear();
  authState = { token: null, phoneNumber: null, role: 'SHOPKEEPER', userId: null, userName: 'User' };
  
  document.getElementById('phone-input').value = '';
  document.getElementById('send-otp-btn').disabled = true;
  backToPhone();
  checkSession();
  showToast('Logged out successfully.', 'success');
}

/* ==========================================================================
   NAVIGATION & UI UTILS
   ========================================================================== */
function showSection(sectionId, element) {
  document.querySelectorAll('#shopkeeper-dashboard .dashboard-section').forEach(s => s.classList.remove('active'));
  document.querySelectorAll('#shopkeeper-dashboard .nav-item').forEach(n => n.classList.remove('active'));
  
  document.getElementById(sectionId).classList.add('active');
  element.classList.add('active');
  
  // Set main title
  const skPageTitle = document.getElementById('sk-page-title');
  if (sectionId === 'sk-overview') skPageTitle.textContent = 'Dashboard Overview';
  else if (sectionId === 'sk-customers') {
    skPageTitle.textContent = 'Customer Management';
    loadShopkeeperCustomers();
  }
  else if (sectionId === 'sk-items') {
    skPageTitle.textContent = 'Inventory Catalog';
    loadShopkeeperItems();
  }
  else if (sectionId === 'sk-transactions') {
    skPageTitle.textContent = 'Transaction History';
    loadShopkeeperTransactions();
  }
  else if (sectionId === 'sk-ledger') {
    skPageTitle.textContent = 'Customer Ledger Books';
    loadShopkeeperLedger();
  }
}

function showCustSection(sectionId, element) {
  document.querySelectorAll('#customer-dashboard .dashboard-section').forEach(s => s.classList.remove('active'));
  document.querySelectorAll('#customer-dashboard .nav-item').forEach(n => n.classList.remove('active'));

  document.getElementById(sectionId).classList.add('active');
  element.classList.add('active');

  if (sectionId === 'cust-balances') {
    loadCustomerBalances();
  } else if (sectionId === 'cust-history') {
    loadCustomerHistory();
  }
}

function toggleSidebar() {
  const sidebar = document.getElementById('sidebar');
  if (sidebar) sidebar.classList.toggle('active');
}

function openModal(id) {
  document.getElementById(id).classList.add('active');
  
  // Pre-load logic for modals
  if (id === 'purchase-modal') {
    // Clear dynamic inputs
    document.getElementById('purchase-customer-id').value = '';
    document.getElementById('purchase-notes').value = '';
    document.getElementById('purchase-items-container').innerHTML = '';
    document.getElementById('purchase-total').textContent = '₹ 0.00';
    addPurchaseItemRow(); // start with one blank row
  } else if (id === 'payment-modal') {
    document.getElementById('payment-customer-id').value = '';
    document.getElementById('payment-amount').value = '';
    document.getElementById('payment-notes').value = '';
  } else if (id === 'add-item-modal') {
    document.getElementById('item-name').value = '';
    document.getElementById('item-desc').value = '';
    document.getElementById('item-price').value = '';
    document.getElementById('item-sku').value = '';
    document.getElementById('item-stock').value = '';
  } else if (id === 'add-customer-modal') {
    document.getElementById('cust-fname').value = '';
    document.getElementById('cust-lname').value = '';
    document.getElementById('cust-phone').value = '';
    document.getElementById('cust-email').value = '';
    document.getElementById('cust-address').value = '';
  }
}

function closeModal(id) {
  document.getElementById(id).classList.remove('active');
}

function showToast(message, type = 'success') {
  const toast = document.getElementById('toast');
  const msgSpan = document.getElementById('toast-msg');
  msgSpan.textContent = message;
  
  if (type === 'error') {
    toast.classList.add('error');
    document.getElementById('toast-icon').textContent = '✕';
  } else {
    toast.classList.remove('error');
    document.getElementById('toast-icon').textContent = '✓';
  }
  
  toast.classList.remove('hidden');
  setTimeout(() => {
    toast.classList.add('hidden');
  }, 4000);
}

function filterTable(input, tableId) {
  const filter = input.value.toUpperCase();
  const table = document.getElementById(tableId);
  const trs = table.getElementsByTagName('tr');

  for (let i = 1; i < trs.length; i++) {
    const tds = trs[i].getElementsByTagName('td');
    let found = false;
    for (let j = 0; j < tds.length; j++) {
      if (tds[j] && tds[j].textContent.toUpperCase().indexOf(filter) > -1) {
        found = true;
        break;
      }
    }
    if (found) {
      trs[i].style.display = '';
    } else {
      trs[i].style.display = 'none';
    }
  }
}

/* ==========================================================================
   BACKEND ENDPOINTS CALLS: SHOPKEEPER PORTAL
   ========================================================================== */

async function loadShopkeeperOverview() {
  try {
    // 1. Fetch catalog items count
    const itemsRes = await fetch(`${GATEWAY_URL}/items/shopkeeper/${authState.userId}`, {
      headers: { 'Authorization': `Bearer ${authState.token}` }
    });
    const itemsData = await itemsRes.json();
    const itemCount = itemsData.success && itemsData.data ? itemsData.data.length : 0;
    document.getElementById('sk-stat-items').textContent = itemCount;
    catalogItems = itemsData.success ? itemsData.data : [];

    // 2. Fetch customers mapped to shopkeeper
    const custRes = await fetch(`${GATEWAY_URL}/customers/shopkeeper/${authState.userId}`, {
      headers: { 'Authorization': `Bearer ${authState.token}` }
    });
    const custData = await custRes.json();
    const custCount = custData.success && custData.data ? custData.data.length : 0;
    document.getElementById('sk-stat-customers').textContent = custCount;

    // 3. Fetch Ledgers to compute outstanding totals
    const ledgerRes = await fetch(`${GATEWAY_URL}/ledgers/shopkeeper/${authState.userId}`, {
      headers: { 'Authorization': `Bearer ${authState.token}` }
    });
    const ledgerData = await ledgerRes.json();
    let totalOutstanding = 0;
    const ledgerList = ledgerData.success ? ledgerData.data : [];
    
    // Sort ledgers to find top debtors
    const topDebtors = [...ledgerList].sort((a,b) => b.outstandingBalance - a.outstandingBalance).slice(0, 5);
    
    ledgerList.forEach(l => totalOutstanding += parseFloat(l.outstandingBalance || 0));
    document.getElementById('sk-stat-outstanding').textContent = `₹ ${totalOutstanding.toFixed(2)}`;

    // Populate Top Debtors widget
    const debtorsContainer = document.getElementById('sk-top-debtors');
    if (topDebtors.length === 0) {
      debtorsContainer.innerHTML = '<div class="empty-state">No ledger data yet</div>';
    } else {
      debtorsContainer.innerHTML = topDebtors.map(d => `
        <div class="debtor-item">
          <div class="debtor-profile">
            <div class="debtor-avatar">CID</div>
            <div class="debtor-details">
              <span class="debtor-name">Customer ID: ${d.customerId}</span>
              <span class="debtor-phone">Last Active: ${d.lastTransactionDate ? new Date(d.lastTransactionDate).toLocaleDateString() : '—'}</span>
            </div>
          </div>
          <span class="debtor-amount">₹ ${parseFloat(d.outstandingBalance).toFixed(2)}</span>
        </div>
      `).join('');
    }

    // 4. Fetch Transactions
    const txnsRes = await fetch(`${GATEWAY_URL}/transactions/shopkeeper/${authState.userId}`, {
      headers: { 'Authorization': `Bearer ${authState.token}` }
    });
    const txnsData = await txnsRes.json();
    const txnList = txnsData.success && txnsData.data ? txnsData.data : [];
    
    // Count transactions today
    const today = new Date().toDateString();
    const countToday = txnList.filter(t => new Date(t.transactionDate).toDateString() === today).length;
    document.getElementById('sk-stat-txns').textContent = countToday;

    // Populate Recent Transactions widget
    const recentTxnsContainer = document.getElementById('sk-recent-txns');
    const recentList = txnList.slice(0, 5);
    if (recentList.length === 0) {
      recentTxnsContainer.innerHTML = '<div class="empty-state">No transactions yet</div>';
    } else {
      recentTxnsContainer.innerHTML = recentList.map(t => {
        const typeClass = t.type.toLowerCase();
        const amtPrefix = t.type === 'PURCHASE' ? '+' : '-';
        return `
          <div class="txn-item">
            <div class="txn-profile">
              <span class="txn-badge ${typeClass}">${t.type}</span>
              <div class="txn-details">
                <span class="txn-cust-info">CID: ${t.customerId}</span>
                <span class="txn-date">${new Date(t.transactionDate).toLocaleDateString()}</span>
              </div>
            </div>
            <span class="txn-amount-val ${typeClass}">${amtPrefix} ₹ ${parseFloat(t.amount).toFixed(2)}</span>
          </div>
        `;
      }).join('');
    }
  } catch (error) {
    console.error('Error loading overview data', error);
  }
}

async function loadShopkeeperCustomers() {
  const tbody = document.getElementById('customers-tbody');
  tbody.innerHTML = '<tr><td colspan="4" class="table-empty">Loading customers...</td></tr>';
  
  try {
    // Get Customer List
    const custRes = await fetch(`${GATEWAY_URL}/customers/shopkeeper/${authState.userId}`, {
      headers: { 'Authorization': `Bearer ${authState.token}` }
    });
    const custData = await custRes.json();
    const customers = custData.success && custData.data ? custData.data : [];

    // Get Ledger outstanding balances to match
    const ledgerRes = await fetch(`${GATEWAY_URL}/ledgers/shopkeeper/${authState.userId}`, {
      headers: { 'Authorization': `Bearer ${authState.token}` }
    });
    const ledgerData = await ledgerRes.json();
    const ledgers = ledgerData.success && ledgerData.data ? ledgerData.data : [];
    
    // Create mapping CustomerId -> Balance
    const balanceMap = {};
    ledgers.forEach(l => {
      balanceMap[l.customerId] = l.outstandingBalance;
    });

    if (customers.length === 0) {
      tbody.innerHTML = '<tr><td colspan="4" class="table-empty">No customers registered yet.</td></tr>';
      return;
    }

    tbody.innerHTML = customers.map(c => {
      const balance = balanceMap[c.id] || 0;
      return `
        <tr>
          <td><strong>${c.firstName} ${c.lastName || ''}</strong></td>
          <td>${c.phoneNumber}</td>
          <td><span class="${balance > 0 ? 'debtor-amount' : ''}">₹ ${parseFloat(balance).toFixed(2)}</span></td>
          <td>
            <button class="btn btn-ghost btn-sm" onclick="quickSelectCustomerForPurchase(${c.id})">📦 Credit Purchase</button>
            <button class="btn btn-ghost btn-sm" onclick="quickSelectCustomerForPayment(${c.id})">💰 Payment</button>
          </td>
        </tr>
      `;
    }).join('');
  } catch (error) {
    console.error(error);
    tbody.innerHTML = '<tr><td colspan="4" class="table-empty error">Failed to load customer catalog.</td></tr>';
  }
}

async function addCustomer() {
  const fname = document.getElementById('cust-fname').value;
  const lname = document.getElementById('cust-lname').value;
  const phone = document.getElementById('cust-phone').value;
  const email = document.getElementById('cust-email').value;
  const address = document.getElementById('cust-address').value;

  if (!fname || !phone) {
    showToast('First Name and Phone Number are required.', 'error');
    return;
  }

  try {
    const res = await fetch(`${GATEWAY_URL}/customers/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authState.token}`
      },
      body: JSON.stringify({
        shopkeeperId: authState.userId,
        firstName: fname,
        lastName: lname,
        phoneNumber: phone,
        email: email,
        address: address
      })
    });
    
    const result = await res.json();
    if (result.success) {
      closeModal('add-customer-modal');
      showToast('Customer created successfully!', 'success');
      loadShopkeeperCustomers();
    } else {
      showToast(result.message || 'Failed to create customer.', 'error');
    }
  } catch (error) {
    console.error(error);
    showToast('Failed to connect to Customer Service.', 'error');
  }
}

async function loadShopkeeperItems() {
  const tbody = document.getElementById('items-tbody');
  tbody.innerHTML = '<tr><td colspan="6" class="table-empty">Loading inventory catalog...</td></tr>';

  try {
    const res = await fetch(`${GATEWAY_URL}/items/shopkeeper/${authState.userId}`, {
      headers: { 'Authorization': `Bearer ${authState.token}` }
    });
    const result = await res.json();
    const items = result.success && result.data ? result.data : [];
    catalogItems = items; // cache items

    if (items.length === 0) {
      tbody.innerHTML = '<tr><td colspan="6" class="table-empty">Inventory is empty. Add your first item!</td></tr>';
      return;
    }

    tbody.innerHTML = items.map(i => {
      let stockClass = 'in-stock';
      let stockLabel = 'In Stock';
      
      if (i.stockQuantity <= 0) {
        stockClass = 'out-of-stock';
        stockLabel = 'Out of Stock';
      } else if (i.stockQuantity < 10) {
        stockClass = 'low';
        stockLabel = 'Low Stock';
      }

      return `
        <tr>
          <td><strong>${i.name}</strong><br><small style="color:var(--text-muted)">${i.description || ''}</small></td>
          <td><code>${i.sku || 'N/A'}</code></td>
          <td>₹ ${parseFloat(i.price).toFixed(2)}</td>
          <td><strong>${i.stockQuantity}</strong></td>
          <td><span class="stock-badge ${stockClass}">${stockLabel}</span></td>
          <td>
            <button class="btn-action-icon" title="Edit Item" onclick="showToast('Edit item action not yet implemented.')">✏️</button>
          </td>
        </tr>
      `;
    }).join('');
  } catch (error) {
    console.error(error);
    tbody.innerHTML = '<tr><td colspan="6" class="table-empty error">Failed to load item inventory.</td></tr>';
  }
}

async function addItem() {
  const name = document.getElementById('item-name').value;
  const desc = document.getElementById('item-desc').value;
  const price = document.getElementById('item-price').value;
  const sku = document.getElementById('item-sku').value;
  const stock = document.getElementById('item-stock').value;

  if (!name || !price || !stock) {
    showToast('Name, Price, and Stock quantity are required.', 'error');
    return;
  }

  try {
    const res = await fetch(`${GATEWAY_URL}/items`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authState.token}`
      },
      body: JSON.stringify({
        shopkeeperId: authState.userId,
        name: name,
        description: desc,
        price: parseFloat(price),
        sku: sku,
        stockQuantity: parseInt(stock)
      })
    });
    
    const result = await res.json();
    if (result.success) {
      closeModal('add-item-modal');
      showToast('Item added to catalog!', 'success');
      loadShopkeeperItems();
    } else {
      showToast(result.message || 'Failed to add item.', 'error');
    }
  } catch (error) {
    console.error(error);
    showToast('Failed to connect to Item Service.', 'error');
  }
}

async function loadShopkeeperTransactions() {
  const tbody = document.getElementById('txns-tbody');
  tbody.innerHTML = '<tr><td colspan="6" class="table-empty">Loading transactions...</td></tr>';

  try {
    const res = await fetch(`${GATEWAY_URL}/transactions/shopkeeper/${authState.userId}`, {
      headers: { 'Authorization': `Bearer ${authState.token}` }
    });
    const result = await res.json();
    const txns = result.success && result.data ? result.data : [];

    if (txns.length === 0) {
      tbody.innerHTML = '<tr><td colspan="6" class="table-empty">No transactions recorded.</td></tr>';
      return;
    }

    tbody.innerHTML = txns.map(t => {
      const typeClass = t.type.toLowerCase();
      const amtPrefix = t.type === 'PURCHASE' ? '+' : '-';
      const itemsList = t.items && t.items.length > 0 
        ? t.items.map(item => `${item.itemName} (${item.quantity}x)`).join(', ')
        : 'None';

      return `
        <tr>
          <td>${new Date(t.transactionDate).toLocaleString()}</td>
          <td><code>CID-${t.customerId}</code></td>
          <td><span class="txn-badge ${typeClass}">${t.type}</span></td>
          <td><span class="txn-amount-val ${typeClass}">${amtPrefix} ₹ ${parseFloat(t.amount).toFixed(2)}</span></td>
          <td>${t.notes || '—'}</td>
          <td><small style="color: var(--text-secondary)">${itemsList}</small></td>
        </tr>
      `;
    }).join('');
  } catch (error) {
    console.error(error);
    tbody.innerHTML = '<tr><td colspan="6" class="table-empty error">Failed to load transaction history.</td></tr>';
  }
}

async function loadShopkeeperLedger() {
  const tbody = document.getElementById('ledger-tbody');
  tbody.innerHTML = '<tr><td colspan="5" class="table-empty">Loading ledger balance sheets...</td></tr>';

  try {
    const res = await fetch(`${GATEWAY_URL}/ledgers/shopkeeper/${authState.userId}`, {
      headers: { 'Authorization': `Bearer ${authState.token}` }
    });
    const result = await res.json();
    const ledgers = result.success && result.data ? result.data : [];

    if (ledgers.length === 0) {
      tbody.innerHTML = '<tr><td colspan="5" class="table-empty">No ledger books created yet.</td></tr>';
      return;
    }

    tbody.innerHTML = ledgers.map(l => {
      const typeClass = (l.lastTransactionType || '').toLowerCase();
      return `
        <tr>
          <td><strong>CustomerID: ${l.customerId}</strong></td>
          <td><span class="debtor-amount">₹ ${parseFloat(l.outstandingBalance).toFixed(2)}</span></td>
          <td>₹ ${l.lastTransactionAmount ? parseFloat(l.lastTransactionAmount).toFixed(2) : '—'}</td>
          <td><span class="txn-badge ${typeClass}">${l.lastTransactionType || 'N/A'}</span></td>
          <td>${l.lastTransactionDate ? new Date(l.lastTransactionDate).toLocaleString() : '—'}</td>
        </tr>
      `;
    }).join('');
  } catch (error) {
    console.error(error);
    tbody.innerHTML = '<tr><td colspan="5" class="table-empty error">Failed to load ledgers.</td></tr>';
  }
}

// Quick helper clicks on customer management list
function quickSelectCustomerForPurchase(id) {
  openModal('purchase-modal');
  document.getElementById('purchase-customer-id').value = id;
}

function quickSelectCustomerForPayment(id) {
  openModal('payment-modal');
  document.getElementById('payment-customer-id').value = id;
}

/* ==========================================================================
   DYNAMIC PURCHASE DIALOG LOGIC
   ========================================================================== */
function addPurchaseItemRow() {
  const container = document.getElementById('purchase-items-container');
  const index = container.children.length;
  
  const row = document.createElement('div');
  row.className = 'purchase-item-row';
  row.id = `purchase-row-${index}`;
  
  // Build items select options
  let options = '<option value="">-- Choose Item --</option>';
  catalogItems.forEach(i => {
    options += `<option value="${i.id}" data-price="${i.price}">${i.name} (₹ ${i.price})</option>`;
  });

  row.innerHTML = `
    <select class="form-input" onchange="onPurchaseItemSelect(${index}, this)" required>
      ${options}
    </select>
    <input type="number" class="form-input" placeholder="Qty" min="1" value="1" oninput="recalculatePurchaseTotal()" id="purchase-qty-${index}" disabled />
    <input type="number" class="form-input" placeholder="Price" id="purchase-price-${index}" disabled readonly />
    <span class="purchase-subtotal" id="purchase-subtotal-${index}">₹ 0.00</span>
    <button type="button" class="btn-remove" onclick="removePurchaseItemRow(${index})">✕</button>
  `;
  
  container.appendChild(row);
}

function removePurchaseItemRow(index) {
  const row = document.getElementById(`purchase-row-${index}`);
  if (row) {
    row.remove();
    recalculatePurchaseTotal();
  }
}

function onPurchaseItemSelect(index, select) {
  const option = select.options[select.selectedIndex];
  const qtyInput = document.getElementById(`purchase-qty-${index}`);
  const priceInput = document.getElementById(`purchase-price-${index}`);
  
  if (option.value) {
    const price = option.getAttribute('data-price');
    qtyInput.disabled = false;
    priceInput.value = price;
    recalculatePurchaseTotal();
  } else {
    qtyInput.disabled = true;
    priceInput.value = '';
    recalculatePurchaseTotal();
  }
}

function recalculatePurchaseTotal() {
  const container = document.getElementById('purchase-items-container');
  let grandTotal = 0;
  
  Array.from(container.children).forEach(row => {
    const select = row.querySelector('select');
    const index = row.id.split('-').pop();
    const qtyInput = document.getElementById(`purchase-qty-${index}`);
    const priceInput = document.getElementById(`purchase-price-${index}`);
    const subtotalSpan = document.getElementById(`purchase-subtotal-${index}`);

    if (select.value && qtyInput.value) {
      const qty = parseInt(qtyInput.value) || 0;
      const price = parseFloat(priceInput.value) || 0;
      const subtotal = qty * price;
      
      subtotalSpan.textContent = `₹ ${subtotal.toFixed(2)}`;
      grandTotal += subtotal;
    } else {
      subtotalSpan.textContent = '₹ 0.00';
    }
  });

  document.getElementById('purchase-total').textContent = `₹ ${grandTotal.toFixed(2)}`;
}

async function submitPurchase() {
  const custId = document.getElementById('purchase-customer-id').value;
  const notes = document.getElementById('purchase-notes').value;
  const container = document.getElementById('purchase-items-container');

  if (!custId) {
    showToast('Customer ID is required.', 'error');
    return;
  }

  // Construct items payload
  const purchaseItems = [];
  let itemValidationError = false;
  
  Array.from(container.children).forEach(row => {
    const select = row.querySelector('select');
    const index = row.id.split('-').pop();
    const qtyInput = document.getElementById(`purchase-qty-${index}`);
    
    if (select.value) {
      const qty = parseInt(qtyInput.value);
      if (!qty || qty < 1) {
        itemValidationError = true;
        return;
      }
      purchaseItems.push({
        itemId: parseInt(select.value),
        quantity: qty
      });
    }
  });

  if (purchaseItems.length === 0 || itemValidationError) {
    showToast('Please add at least one valid item with a quantity >= 1.', 'error');
    return;
  }

  try {
    const res = await fetch(`${GATEWAY_URL}/transactions/purchase`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authState.token}`
      },
      body: JSON.stringify({
        shopkeeperId: parseInt(authState.userId),
        customerId: parseInt(custId),
        notes: notes,
        items: purchaseItems
      })
    });
    
    const result = await res.json();
    if (result.success) {
      closeModal('purchase-modal');
      showToast('Purchase recorded on credit!', 'success');
      loadShopkeeperOverview();
    } else {
      showToast(result.message || 'Failed to record purchase.', 'error');
    }
  } catch (error) {
    console.error(error);
    showToast('Error connecting to Transaction Service.', 'error');
  }
}

async function submitPayment() {
  const custId = document.getElementById('payment-customer-id').value;
  const amount = document.getElementById('payment-amount').value;
  const notes = document.getElementById('payment-notes').value;

  if (!custId || !amount || parseFloat(amount) <= 0) {
    showToast('Valid Customer ID and Payment Amount are required.', 'error');
    return;
  }

  try {
    const res = await fetch(`${GATEWAY_URL}/transactions/payment`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authState.token}`
      },
      body: JSON.stringify({
        shopkeeperId: parseInt(authState.userId),
        customerId: parseInt(custId),
        amount: parseFloat(amount),
        notes: notes
      })
    });

    const result = await res.json();
    if (result.success) {
      closeModal('payment-modal');
      showToast('Payment recorded successfully!', 'success');
      loadShopkeeperOverview();
    } else {
      showToast(result.message || 'Failed to record payment.', 'error');
    }
  } catch (error) {
    console.error(error);
    showToast('Error connecting to Transaction Service.', 'error');
  }
}

/* ==========================================================================
   BACKEND ENDPOINTS CALLS: CUSTOMER PORTAL
   ========================================================================== */

async function loadCustomerBalances() {
  const cardsContainer = document.getElementById('cust-balance-cards');
  cardsContainer.innerHTML = '<div class="empty-state">Loading your credit accounts...</div>';

  try {
    // 1. Get Outstanding balances for this customer across shops
    const res = await fetch(`${GATEWAY_URL}/ledgers/customer/${authState.userId}`, {
      headers: { 'Authorization': `Bearer ${authState.token}` }
    });
    const result = await res.json();
    const ledgers = result.success && result.data ? result.data : [];

    if (ledgers.length === 0) {
      cardsContainer.innerHTML = '<div class="empty-state">No active credit ledger records found for your account.</div>';
      return;
    }

    cardsContainer.innerHTML = ledgers.map(l => `
      <div class="balance-card glass-card">
        <div class="balance-card-header">
          <div class="shop-icon">🏪</div>
          <div class="shop-details">
            <span class="shop-name">Shopkeeper ID: ${l.shopkeeperId}</span>
            <span class="shop-id">Ledger Reference: #${l.id}</span>
          </div>
        </div>
        <div class="balance-card-body">
          <span class="balance-card-label">OUTSTANDING CREDIT BALANCE</span>
          <span class="balance-card-val">₹ ${parseFloat(l.outstandingBalance).toFixed(2)}</span>
        </div>
        <div class="balance-card-footer">
          <span>Last Activity: ${l.lastTransactionDate ? new Date(l.lastTransactionDate).toLocaleDateString() : 'None'}</span>
          <span>${l.lastTransactionType ? `(${l.lastTransactionType})` : ''}</span>
        </div>
      </div>
    `).join('');
  } catch (error) {
    console.error(error);
    cardsContainer.innerHTML = '<div class="empty-state error">Failed to load outstanding balance statements.</div>';
  }
}

async function loadCustomerHistory() {
  const tbody = document.getElementById('cust-txns-tbody');
  tbody.innerHTML = '<tr><td colspan="5" class="table-empty">Loading history logs...</td></tr>';

  try {
    const res = await fetch(`${GATEWAY_URL}/transactions/customer/${authState.userId}`, {
      headers: { 'Authorization': `Bearer ${authState.token}` }
    });
    const result = await res.json();
    const txns = result.success && result.data ? result.data : [];

    if (txns.length === 0) {
      tbody.innerHTML = '<tr><td colspan="5" class="table-empty">No transaction logs available.</td></tr>';
      return;
    }

    tbody.innerHTML = txns.map(t => {
      const typeClass = t.type.toLowerCase();
      const amtPrefix = t.type === 'PURCHASE' ? '+' : '-';
      return `
        <tr>
          <td>${new Date(t.transactionDate).toLocaleString()}</td>
          <td><code>Shop-${t.shopkeeperId}</code></td>
          <td><span class="txn-badge ${typeClass}">${t.type}</span></td>
          <td><span class="txn-amount-val ${typeClass}">${amtPrefix} ₹ ${parseFloat(t.amount).toFixed(2)}</span></td>
          <td>${t.notes || '—'}</td>
        </tr>
      `;
    }).join('');
  } catch (error) {
    console.error(error);
    tbody.innerHTML = '<tr><td colspan="5" class="table-empty error">Failed to load your transaction logs.</td></tr>';
  }
}
