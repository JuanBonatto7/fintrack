import { useState } from 'react'
import { ArrowDownLeft, ArrowUpRight, Bell, ChevronDown, CircleHelp, CreditCard, Home, Landmark, LayoutDashboard, Menu, Plus, Search, Settings, Tags, Wallet, X } from 'lucide-react'
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import './App.css'

const chartData = [
  { month: 'Jan', income: 3200, expenses: 2120 }, { month: 'Feb', income: 3500, expenses: 2380 },
  { month: 'Mar', income: 3400, expenses: 2010 }, { month: 'Apr', income: 3800, expenses: 2670 },
  { month: 'May', income: 4200, expenses: 2860 }, { month: 'Jun', income: 4600, expenses: 2940 },
]

const transactions = [
  { name: 'Whole Foods Market', category: 'Food & dining', date: 'Today, 10:42 AM', amount: '-$84.20', icon: 'WF', tone: 'coral' },
  { name: 'Salary deposit', category: 'Income', date: 'Jun 28, 2024', amount: '+$4,600.00', icon: 'S', tone: 'mint' },
  { name: 'Spotify Premium', category: 'Subscriptions', date: 'Jun 26, 2024', amount: '-$11.99', icon: 'SP', tone: 'violet' },
  { name: 'Uber', category: 'Transport', date: 'Jun 25, 2024', amount: '-$24.50', icon: 'U', tone: 'yellow' },
]

const accounts = [
  { name: 'Everyday checking', number: '•• 4821', balance: '$8,420.50', icon: Landmark, color: 'green' },
  { name: 'Savings account', number: '•• 1098', balance: '$12,840.00', icon: Wallet, color: 'navy' },
]

function App() {
  const [range, setRange] = useState('6 months')
  const [mobileMenu, setMobileMenu] = useState(false)
  const [showToast, setShowToast] = useState(false)

  return (
    <div className="app-shell">
      <aside className={`sidebar ${mobileMenu ? 'sidebar-open' : ''}`}>
        <div className="brand-mark"><span>F</span><strong>fintrack</strong></div>
        <button className="mobile-close" aria-label="Close menu" onClick={() => setMobileMenu(false)}><X size={20} /></button>
        <div className="workspace-switcher"><div className="avatar avatar-small">JD</div><div><span className="eyebrow">Workspace</span><strong>Jordan's finances</strong></div><ChevronDown size={15} /></div>
        <nav className="main-nav" aria-label="Main navigation">
          <span className="nav-label">Overview</span>
          <a className="nav-item active" href="#dashboard"><LayoutDashboard size={18} /> Dashboard</a>
          <a className="nav-item" href="#transactions"><CreditCard size={18} /> Transactions <span className="nav-count">12</span></a>
          <a className="nav-item" href="#budgets"><Tags size={18} /> Budgets</a>
          <a className="nav-item" href="#accounts"><Landmark size={18} /> Accounts</a>
          <span className="nav-label space-top">Manage</span>
          <a className="nav-item" href="#categories"><Wallet size={18} /> Categories</a>
          <a className="nav-item" href="#settings"><Settings size={18} /> Settings</a>
        </nav>
        <div className="sidebar-footer"><div className="help-card"><CircleHelp size={18} /><div><strong>Need a hand?</strong><span>Visit our help center</span></div></div><div className="profile-row"><div className="avatar">JD</div><div><strong>Jordan Davis</strong><span>jordan@email.com</span></div><ChevronDown size={15} /></div></div>
      </aside>
      {mobileMenu && <button className="mobile-overlay" aria-label="Close menu" onClick={() => setMobileMenu(false)} />}
      <main className="main-content" id="dashboard">
        <header className="topbar"><button className="menu-button" aria-label="Open menu" onClick={() => setMobileMenu(true)}><Menu size={21} /></button><div className="breadcrumb"><Home size={15} /> <span>/</span> Dashboard</div><div className="topbar-actions"><button className="icon-button" aria-label="Search"><Search size={19} /></button><button className="icon-button notification" aria-label="Notifications"><Bell size={19} /><i /></button><div className="avatar">JD</div></div></header>
        <div className="page-wrap">
          <section className="welcome-row"><div><p className="kicker">MONDAY, JUNE 30, 2024</p><h1>Good morning, Jordan <span>↗</span></h1><p className="subheading">Here's your financial overview for this month.</p></div><button className="primary-button" onClick={() => setShowToast(true)}><Plus size={18} /> Add transaction</button></section>
          <section className="metric-grid" aria-label="Financial summary">
            <article className="metric-card balance-card"><div className="metric-top"><span>Total balance</span><span className="trend positive">+8.4% <ArrowUpRight size={14} /></span></div><strong>$21,260.50</strong><div className="sparkline"><span /><span /><span /><span /><span /><span /><span /><span /></div><small>Compared to last month</small></article>
            <article className="metric-card"><div className="metric-top"><span>Income</span><span className="metric-icon income"><ArrowDownLeft size={17} /></span></div><strong>$4,600.00</strong><div className="metric-foot"><span className="trend positive">+12.5%</span><small>This month</small></div></article>
            <article className="metric-card"><div className="metric-top"><span>Expenses</span><span className="metric-icon expense"><ArrowUpRight size={17} /></span></div><strong>$2,940.00</strong><div className="metric-foot"><span className="trend negative">-4.2%</span><small>vs. $3,070 last month</small></div></article>
            <article className="metric-card"><div className="metric-top"><span>Savings rate</span><span className="metric-icon save"><Wallet size={17} /></span></div><strong>36.1%</strong><div className="progress-track"><span style={{ width: '72%' }} /></div><div className="metric-foot"><small>Goal: 50%</small><small>72% complete</small></div></article>
          </section>
          <section className="content-grid">
            <article className="panel chart-panel"><div className="panel-heading"><div><h2>Cash flow</h2><p>Income vs. expenses over time</p></div><select value={range} onChange={(event) => setRange(event.target.value)} aria-label="Select date range"><option>6 months</option><option>12 months</option><option>This year</option></select></div><div className="legend"><span><i className="income-dot" /> Income</span><span><i className="expense-dot" /> Expenses</span></div><div className="chart-wrap"><ResponsiveContainer width="100%" height="100%"><AreaChart data={chartData} margin={{ top: 10, right: 4, left: -20, bottom: 0 }}><defs><linearGradient id="incomeFill" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#b9e8c6" stopOpacity={0.65} /><stop offset="100%" stopColor="#b9e8c6" stopOpacity={0.04} /></linearGradient><linearGradient id="expenseFill" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#f7c7b4" stopOpacity={0.55} /><stop offset="100%" stopColor="#f7c7b4" stopOpacity={0.04} /></linearGradient></defs><CartesianGrid vertical={false} stroke="#e8e6df" /><XAxis dataKey="month" axisLine={false} tickLine={false} tick={{ fill: '#8a918b', fontSize: 12 }} /><YAxis axisLine={false} tickLine={false} tick={{ fill: '#8a918b', fontSize: 12 }} tickFormatter={(value) => `$${value / 1000}k`} /><Tooltip contentStyle={{ border: '1px solid #dce1db', borderRadius: 8, fontFamily: 'Manrope', fontSize: 12 }} formatter={(value) => [`$${Number(value).toLocaleString()}`, '']} /><Area type="monotone" dataKey="income" stroke="#3d8f5d" strokeWidth={2.5} fill="url(#incomeFill)" /><Area type="monotone" dataKey="expenses" stroke="#d97857" strokeWidth={2.5} fill="url(#expenseFill)" /></AreaChart></ResponsiveContainer></div></article>
            <article className="panel budget-panel" id="budgets"><div className="panel-heading"><div><h2>Budget overview</h2><p>June 2024</p></div><button className="text-button">View all <ArrowUpRight size={14} /></button></div><div className="budget-total"><div className="budget-ring"><span>62%</span></div><div><strong>$2,940 <small>of $4,750</small></strong><p>spent this month</p></div></div><div className="budget-list"><div><span className="category-dot food" /><span>Food & dining</span><strong>$620 <small>/ $800</small></strong></div><div><span className="category-dot home" /><span>Housing</span><strong>$1,200 <small>/ $1,400</small></strong></div><div><span className="category-dot transport" /><span>Transport</span><strong>$310 <small>/ $500</small></strong></div></div></article>
          </section>
          <section className="lower-grid">
            <article className="panel transactions-panel" id="transactions"><div className="panel-heading"><div><h2>Recent transactions</h2><p>Your latest activity</p></div><button className="text-button">See all <ArrowUpRight size={14} /></button></div><div className="transaction-list">{transactions.map((transaction) => <div className="transaction-row" key={`${transaction.name}-${transaction.date}`}><div className={`transaction-icon ${transaction.tone}`}>{transaction.icon}</div><div className="transaction-info"><strong>{transaction.name}</strong><span>{transaction.category} <b>·</b> {transaction.date}</span></div><strong className={transaction.amount.startsWith('+') ? 'amount positive-amount' : 'amount'}>{transaction.amount}</strong></div>)}</div></article>
            <article className="panel accounts-panel" id="accounts"><div className="panel-heading"><div><h2>Your accounts</h2><p>2 connected accounts</p></div><button className="round-button" aria-label="Add account"><Plus size={17} /></button></div><div className="accounts-list">{accounts.map((account) => { const Icon = account.icon; return <div className="account-row" key={account.name}><div className={`account-icon ${account.color}`}><Icon size={18} /></div><div><strong>{account.name}</strong><span>{account.number}</span></div><strong>{account.balance}</strong></div> })}</div><button className="outline-button"><Plus size={16} /> Connect account</button></article>
          </section>
        </div>
      </main>
      {showToast && <div className="toast"><div className="toast-check">✓</div><div><strong>Transaction flow ready</strong><span>We'll connect this to the API next.</span></div><button aria-label="Dismiss" onClick={() => setShowToast(false)}><X size={16} /></button></div>}
    </div>
  )
}

export default App
