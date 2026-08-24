import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { ArrowDownLeft, ArrowUpRight, Bell, ChevronDown, CircleHelp, CreditCard, Home, Landmark, LayoutDashboard, Menu, Plus, Search, Settings, Tags, Wallet, X } from 'lucide-react'
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { clearSession, createTransaction, getAccounts, getCategories, getCurrentUser, getTransactions, login, register, saveSession } from './api'
import type { Account, AuthResponse, Category, Transaction, User } from './api'
import { getBudgets } from './api'
import type { Budget } from './api'
import ManagementView from './ManagementView'
import './App.css'

function AuthScreen({ onAuthenticated }: { onAuthenticated: (auth: AuthResponse) => void }) {
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      const auth = mode === 'login' ? await login(email, password) : await register(name, email, password)
      saveSession(auth)
      onAuthenticated(auth)
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Unable to connect to Fintrack.')
    } finally {
      setLoading(false)
    }
  }

  return <main className="auth-page"><div className="auth-visual"><div className="auth-orbit orbit-one" /><div className="auth-orbit orbit-two" /><div className="auth-copy"><div className="brand-mark auth-brand"><span>F</span><strong>fintrack</strong></div><p className="kicker">YOUR MONEY, IN FOCUS</p><h1>A clearer view of your financial life.</h1><p>Track the everyday details, spot the bigger picture, and move with confidence.</p></div><div className="auth-caption">PERSONAL FINANCE, MADE CALM</div></div><section className="auth-panel"><div className="auth-mobile-brand"><div className="brand-mark"><span>F</span><strong>fintrack</strong></div></div><div className="auth-panel-inner"><p className="kicker">WELCOME BACK</p><h2>{mode === 'login' ? 'Sign in to your account' : 'Create your account'}</h2><p className="auth-subtitle">{mode === 'login' ? 'Your financial overview is waiting.' : 'Start building a better money routine.'}</p><form onSubmit={handleSubmit}>{mode === 'register' && <label>Full name<input value={name} onChange={(event) => setName(event.target.value)} placeholder="Jordan Davis" required /></label>}<label>Email address<input type="email" value={email} onChange={(event) => setEmail(event.target.value)} placeholder="you@example.com" required /></label><label>Password<input type="password" minLength={mode === 'register' ? 8 : undefined} value={password} onChange={(event) => setPassword(event.target.value)} placeholder="••••••••" required />{mode === 'register' && <small>At least 8 characters</small>}</label>{error && <div className="form-error">{error}</div>}<button className="auth-submit" disabled={loading}>{loading ? 'Connecting...' : mode === 'login' ? 'Sign in' : 'Create account'} <ArrowUpRight size={17} /></button></form><div className="auth-switch">{mode === 'login' ? "Don't have an account?" : 'Already have an account?'} <button onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError('') }}>{mode === 'login' ? 'Create one' : 'Sign in'}</button></div></div></section></main>
}

function TransactionModal({ accounts, categories, onClose, onCreated }: { accounts: Account[]; categories: Category[]; onClose: () => void; onCreated: (transaction: Transaction) => void }) {
  const [amount, setAmount] = useState('')
  const [description, setDescription] = useState('')
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10))
  const [type, setType] = useState<'EXPENSE' | 'INCOME'>('EXPENSE')
  const [accountId, setAccountId] = useState(accounts[0]?.id.toString() ?? '')
  const [categoryId, setCategoryId] = useState('')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)
  const availableCategories = categories.filter((category) => category.type === type)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setSaving(true)
    try {
      const transaction = await createTransaction({ amount: Number(amount), description, date, type, accountId: Number(accountId), categoryId: Number(categoryId) })
      onCreated(transaction)
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Unable to save the transaction.')
    } finally {
      setSaving(false)
    }
  }

  return <div className="modal-backdrop" role="presentation" onMouseDown={(event) => { if (event.target === event.currentTarget) onClose() }}><section className="transaction-modal" role="dialog" aria-modal="true" aria-labelledby="transaction-title"><div className="modal-heading"><div><p className="kicker">NEW ACTIVITY</p><h2 id="transaction-title">Add transaction</h2></div><button className="modal-close" aria-label="Close" onClick={onClose}><X size={18} /></button></div><form onSubmit={handleSubmit}><div className="type-toggle"><button type="button" className={type === 'EXPENSE' ? 'selected expense-type' : ''} onClick={() => setType('EXPENSE')}>Expense</button><button type="button" className={type === 'INCOME' ? 'selected income-type' : ''} onClick={() => setType('INCOME')}>Income</button></div><label>Amount<input type="number" min="0.01" step="0.01" value={amount} onChange={(event) => setAmount(event.target.value)} placeholder="0.00" required /></label><label>Description<input value={description} onChange={(event) => setDescription(event.target.value)} placeholder="What was this for?" /></label><div className="form-row"><label>Date<input type="date" value={date} onChange={(event) => setDate(event.target.value)} required /></label><label>Account<select value={accountId} onChange={(event) => setAccountId(event.target.value)} required><option value="">Select account</option>{accounts.map((account) => <option value={account.id} key={account.id}>{account.name}</option>)}</select></label></div><label>Category<select value={categoryId} onChange={(event) => setCategoryId(event.target.value)} required><option value="">Select category</option>{availableCategories.map((category) => <option value={category.id} key={category.id}>{category.name}</option>)}</select></label>{accounts.length === 0 && <div className="form-error">Create an account before adding a transaction.</div>}{error && <div className="form-error">{error}</div>}<div className="modal-actions"><button type="button" className="cancel-button" onClick={onClose}>Cancel</button><button className="auth-submit" disabled={saving || accounts.length === 0}>{saving ? 'Saving...' : 'Save transaction'} <ArrowUpRight size={16} /></button></div></form></section></div>
}

function App() {
  const [range, setRange] = useState('6 months')
  const [mobileMenu, setMobileMenu] = useState(false)
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('fintrack_user')
    return saved ? JSON.parse(saved) as User : null
  })
  const [accountData, setAccountData] = useState<Account[]>([])
  const [categoryData, setCategoryData] = useState<Category[]>([])
  const [transactionData, setTransactionData] = useState<Transaction[]>([])
  const [budgetData, setBudgetData] = useState<Budget[]>([])
  const [activeSection, setActiveSection] = useState<'dashboard' | 'transactions' | 'budgets' | 'accounts' | 'categories'>('dashboard')
  const [showTransactionForm, setShowTransactionForm] = useState(false)
  const [checkingSession, setCheckingSession] = useState(Boolean(localStorage.getItem('fintrack_token')))
  const userId = user?.id

  useEffect(() => {
    if (!userId || !localStorage.getItem('fintrack_token')) {
      return
    }
    Promise.all([getCurrentUser(), getAccounts(), getCategories()]).then(([currentUser, accountsResponse, categoriesResponse]) => {
      setUser(currentUser)
      setAccountData(accountsResponse)
      setCategoryData(categoriesResponse)
      Promise.all(accountsResponse.map((account) => Promise.all([getTransactions(account.id), getBudgets(account.id)]))).then((resources) => { setTransactionData(resources.flatMap(([transactionsResponse]) => transactionsResponse)); setBudgetData(resources.flatMap(([, budgetsResponse]) => budgetsResponse)) }).catch(() => undefined)
      localStorage.setItem('fintrack_user', JSON.stringify(currentUser))
    }).catch(() => { clearSession(); setUser(null) }).finally(() => setCheckingSession(false))
  }, [userId])

  if (checkingSession) return <div className="loading-screen">Loading your workspace...</div>
  if (!user) return <AuthScreen onAuthenticated={(auth) => { setUser({ id: auth.userId, name: auth.name, email: auth.email }); setCheckingSession(false) }} />

  const displayName = user.name.split(' ')[0]
  const currency = accountData[0]?.currency || 'USD'
  const money = (value: number) => new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(value)
  const visibleAccounts = accountData.map((account, index) => ({ name: account.name, number: `•• ${account.id}`, balance: money(Number(account.balance)), icon: index % 2 === 0 ? Landmark : Wallet, color: index % 2 === 0 ? 'green' : 'navy' }))
  const visibleTransactions = transactionData.slice(0, 4).map((transaction) => ({ name: transaction.description || transaction.categoryName, category: transaction.categoryName, date: transaction.date, amount: `${transaction.type === 'INCOME' ? '+' : '-'}${money(Number(transaction.amount))}`, icon: transaction.categoryName.slice(0, 2).toUpperCase(), tone: transaction.type === 'INCOME' ? 'mint' : 'coral' }))
  const todayLabel = new Date().toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' }).toUpperCase()
  const refreshData = () => { Promise.all([getAccounts(), getCategories()]).then(([accountsResponse, categoriesResponse]) => { setAccountData(accountsResponse); setCategoryData(categoriesResponse); return Promise.all(accountsResponse.map((account) => Promise.all([getTransactions(account.id), getBudgets(account.id)]))) }).then((resources) => { setTransactionData(resources.flatMap(([transactionsResponse]) => transactionsResponse)); setBudgetData(resources.flatMap(([, budgetsResponse]) => budgetsResponse)) }).catch(() => undefined) }
  const totalBalance = accountData.reduce((sum, account) => sum + Number(account.balance || 0), 0)
  const currentMonth = new Date().toISOString().slice(0, 7)
  const monthTransactions = transactionData.filter((transaction) => transaction.date.startsWith(currentMonth))
  const totalIncome = monthTransactions.filter((transaction) => transaction.type === 'INCOME').reduce((sum, transaction) => sum + Number(transaction.amount), 0)
  const totalExpenses = monthTransactions.filter((transaction) => transaction.type === 'EXPENSE').reduce((sum, transaction) => sum + Number(transaction.amount), 0)
  const savingsRate = totalIncome > 0 ? Math.max(0, ((totalIncome - totalExpenses) / totalIncome) * 100) : 0
  const cashFlowData = Array.from({ length: 6 }, (_, index) => { const date = new Date(); date.setMonth(date.getMonth() - (5 - index)); const prefix = date.toISOString().slice(0, 7); const month = date.toLocaleString('en-US', { month: 'short' }); return { month, income: transactionData.filter((transaction) => transaction.date.startsWith(prefix) && transaction.type === 'INCOME').reduce((sum, transaction) => sum + Number(transaction.amount), 0), expenses: transactionData.filter((transaction) => transaction.date.startsWith(prefix) && transaction.type === 'EXPENSE').reduce((sum, transaction) => sum + Number(transaction.amount), 0) } })
  const totalBudget = budgetData.reduce((sum, budget) => sum + Number(budget.amount || 0), 0)
  const budgetUsage = totalBudget > 0 ? Math.min(100, (totalExpenses / totalBudget) * 100) : 0

  return (
    <div className="app-shell">
      <aside className={`sidebar ${mobileMenu ? 'sidebar-open' : ''}`}>
        <div className="brand-mark"><span>F</span><strong>fintrack</strong></div>
        <button className="mobile-close" aria-label="Close menu" onClick={() => setMobileMenu(false)}><X size={20} /></button>
        <div className="workspace-switcher"><div className="avatar avatar-small">{user.name.slice(0, 2).toUpperCase()}</div><div><span className="eyebrow">Workspace</span><strong>{user.name}'s finances</strong></div><ChevronDown size={15} /></div>
        <nav className="main-nav" aria-label="Main navigation">
          <span className="nav-label">Overview</span>
          <a className={`nav-item ${activeSection === 'dashboard' ? 'active' : ''}`} href="#dashboard" onClick={() => setActiveSection('dashboard')}><LayoutDashboard size={18} /> Dashboard</a>
          <a className={`nav-item ${activeSection === 'transactions' ? 'active' : ''}`} href="#transactions" onClick={() => setActiveSection('transactions')}><CreditCard size={18} /> Transactions <span className="nav-count">{transactionData.length}</span></a>
          <a className={`nav-item ${activeSection === 'budgets' ? 'active' : ''}`} href="#budgets" onClick={() => setActiveSection('budgets')}><Tags size={18} /> Budgets</a>
          <a className={`nav-item ${activeSection === 'accounts' ? 'active' : ''}`} href="#accounts" onClick={() => setActiveSection('accounts')}><Landmark size={18} /> Accounts</a>
          <span className="nav-label space-top">Manage</span>
          <a className={`nav-item ${activeSection === 'categories' ? 'active' : ''}`} href="#categories" onClick={() => setActiveSection('categories')}><Wallet size={18} /> Categories</a>
          <a className="nav-item" href="#settings"><Settings size={18} /> Settings</a>
        </nav>
        <div className="sidebar-footer"><div className="help-card"><CircleHelp size={18} /><div><strong>Need a hand?</strong><span>Visit our help center</span></div></div><div className="profile-row"><div className="avatar">{user.name.slice(0, 2).toUpperCase()}</div><div><strong>{user.name}</strong><span>{user.email}</span></div><button className="logout-button" onClick={() => { clearSession(); setUser(null) }}>Log out</button></div></div>
      </aside>
      {mobileMenu && <button className="mobile-overlay" aria-label="Close menu" onClick={() => setMobileMenu(false)} />}
      <main className="main-content" id="dashboard">
        <header className="topbar"><button className="menu-button" aria-label="Open menu" onClick={() => setMobileMenu(true)}><Menu size={21} /></button><div className="breadcrumb"><Home size={15} /> <span>/</span> Dashboard</div><div className="topbar-actions"><button className="icon-button" aria-label="Search"><Search size={19} /></button><button className="icon-button notification" aria-label="Notifications"><Bell size={19} /><i /></button><div className="avatar">{user.name.slice(0, 2).toUpperCase()}</div></div></header>
        {activeSection === 'dashboard' ? <div className="page-wrap">
          <section className="welcome-row"><div><p className="kicker">{todayLabel}</p><h1>Good morning, {displayName} <span>↗</span></h1><p className="subheading">Here's your financial overview for this month.</p></div><button className="primary-button" onClick={() => setShowTransactionForm(true)}><Plus size={18} /> Add transaction</button></section>
          <section className="metric-grid" aria-label="Financial summary">
            <article className="metric-card balance-card"><div className="metric-top"><span>Total balance</span><span className="trend positive"><ArrowUpRight size={14} /> Live</span></div><strong>{money(totalBalance)}</strong><div className="sparkline"><span /><span /><span /><span /><span /><span /><span /><span /></div><small>Across {accountData.length} account{accountData.length === 1 ? '' : 's'}</small></article>
            <article className="metric-card"><div className="metric-top"><span>Income</span><span className="metric-icon income"><ArrowDownLeft size={17} /></span></div><strong>{money(totalIncome)}</strong><div className="metric-foot"><span className="trend positive">This month</span><small>{monthTransactions.filter((transaction) => transaction.type === 'INCOME').length} transaction{monthTransactions.filter((transaction) => transaction.type === 'INCOME').length === 1 ? '' : 's'}</small></div></article>
            <article className="metric-card"><div className="metric-top"><span>Expenses</span><span className="metric-icon expense"><ArrowUpRight size={17} /></span></div><strong>{money(totalExpenses)}</strong><div className="metric-foot"><span className="trend negative">This month</span><small>{monthTransactions.filter((transaction) => transaction.type === 'EXPENSE').length} transaction{monthTransactions.filter((transaction) => transaction.type === 'EXPENSE').length === 1 ? '' : 's'}</small></div></article>
            <article className="metric-card"><div className="metric-top"><span>Savings rate</span><span className="metric-icon save"><Wallet size={17} /></span></div><strong>{savingsRate.toFixed(1)}%</strong><div className="progress-track"><span style={{ width: `${Math.min(100, savingsRate * 2)}%` }} /></div><div className="metric-foot"><small>Based on this month</small><small>{savingsRate >= 50 ? 'Goal reached' : 'Goal: 50%'}</small></div></article>
          </section>
          <section className="content-grid">
            <article className="panel chart-panel"><div className="panel-heading"><div><h2>Cash flow</h2><p>Income vs. expenses over time</p></div><select value={range} onChange={(event) => setRange(event.target.value)} aria-label="Select date range"><option>6 months</option><option>12 months</option><option>This year</option></select></div><div className="legend"><span><i className="income-dot" /> Income</span><span><i className="expense-dot" /> Expenses</span></div><div className="chart-wrap"><ResponsiveContainer width="100%" height="100%"><AreaChart data={cashFlowData} margin={{ top: 10, right: 4, left: -20, bottom: 0 }}><defs><linearGradient id="incomeFill" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#d9c3fa" stopOpacity={0.65} /><stop offset="100%" stopColor="#d9c3fa" stopOpacity={0.04} /></linearGradient><linearGradient id="expenseFill" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#f3c3d7" stopOpacity={0.55} /><stop offset="100%" stopColor="#f3c3d7" stopOpacity={0.04} /></linearGradient></defs><CartesianGrid vertical={false} stroke="#e8e1ec" /><XAxis dataKey="month" axisLine={false} tickLine={false} tick={{ fill: '#8a8190', fontSize: 12 }} /><YAxis axisLine={false} tickLine={false} tick={{ fill: '#8a8190', fontSize: 12 }} tickFormatter={(value) => `${currency} ${value / 1000}k`} /><Tooltip contentStyle={{ border: '1px solid #e5dfea', borderRadius: 8, fontFamily: 'Manrope', fontSize: 12 }} formatter={(value) => [money(Number(value)), '']} /><Area type="monotone" dataKey="income" stroke="#7450b5" strokeWidth={2.5} fill="url(#incomeFill)" /><Area type="monotone" dataKey="expenses" stroke="#d978a2" strokeWidth={2.5} fill="url(#expenseFill)" /></AreaChart></ResponsiveContainer></div></article>
            <article className="panel budget-panel" id="budgets"><div className="panel-heading"><div><h2>Budget overview</h2><p>{new Date().toLocaleString('en-US', { month: 'long', year: 'numeric' })}</p></div><button className="text-button" onClick={() => setActiveSection('budgets')}>View all <ArrowUpRight size={14} /></button></div><div className="budget-total"><div className="budget-ring" style={{ background: `conic-gradient(#7450b5 0 ${budgetUsage}%, #eee8f3 ${budgetUsage}% 100%)` }}><span>{budgetUsage.toFixed(0)}%</span></div><div><strong>{money(totalExpenses)} <small>of {money(totalBudget)}</small></strong><p>spent this month</p></div></div><div className="budget-list">{budgetData.slice(0, 3).map((budget) => <div key={budget.id}><span className="category-dot food" /><span>{budget.categoryName}</span><strong>{money(Number(budget.amount))}</strong></div>)}{budgetData.length === 0 && <div className="budget-empty">No budgets created yet</div>}</div></article>
          </section>
          <section className="lower-grid">
            <article className="panel transactions-panel" id="transactions"><div className="panel-heading"><div><h2>Recent transactions</h2><p>Your latest activity</p></div><button className="text-button">See all <ArrowUpRight size={14} /></button></div><div className="transaction-list">{visibleTransactions.map((transaction) => <div className="transaction-row" key={`${transaction.name}-${transaction.date}`}><div className={`transaction-icon ${transaction.tone}`}>{transaction.icon}</div><div className="transaction-info"><strong>{transaction.name}</strong><span>{transaction.category} <b>·</b> {transaction.date}</span></div><strong className={transaction.amount.startsWith('+') ? 'amount positive-amount' : 'amount'}>{transaction.amount}</strong></div>)}</div></article>
            <article className="panel accounts-panel" id="accounts"><div className="panel-heading"><div><h2>Your accounts</h2><p>{visibleAccounts.length} connected accounts</p></div><button className="round-button" aria-label="Add account"><Plus size={17} /></button></div><div className="accounts-list">{visibleAccounts.map((account) => { const Icon = account.icon; return <div className="account-row" key={account.name}><div className={`account-icon ${account.color}`}><Icon size={18} /></div><div><strong>{account.name}</strong><span>{account.number}</span></div><strong>{account.balance}</strong></div> })}</div><button className="outline-button"><Plus size={16} /> Connect account</button></article>
          </section>
        </div> : <ManagementView section={activeSection} accounts={accountData} categories={categoryData} transactions={transactionData} budgets={budgetData} onRefresh={refreshData} />}
      </main>
      {showTransactionForm && <TransactionModal accounts={accountData} categories={categoryData} onClose={() => setShowTransactionForm(false)} onCreated={(transaction) => { setTransactionData((current) => [transaction, ...current]); setShowTransactionForm(false) }} />}
    </div>
  )
}

export default App
