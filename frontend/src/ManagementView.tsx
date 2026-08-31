import { useState } from 'react'
import { Check, Plus, Trash2, Edit2 } from 'lucide-react'
import {
  createAccount, updateAccount, deleteAccount,
  createBudget, updateBudget, deleteBudget,
  createCategory, updateCategory, deleteCategory,
  createTransaction, updateTransaction, deleteTransaction
} from './api'
import type { Account, Budget, Category, Transaction } from './api'

type Section = 'transactions' | 'budgets' | 'accounts' | 'categories'
type Props = {
  section: Section
  accounts: Account[]
  categories: Category[]
  transactions: Transaction[]
  budgets: Budget[]
  onRefresh: () => void
}

function ManagementView({ section, accounts, categories, transactions, budgets, onRefresh }: Props) {
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState<number | null>(null)

  const title = {
    transactions: 'Transactions',
    budgets: 'Budgets',
    accounts: 'Accounts',
    categories: 'Categories'
  }[section]

  const description = {
    transactions: 'Review and manage your activity.',
    budgets: 'Keep spending aligned with your goals.',
    accounts: 'Your connected financial accounts.',
    categories: 'Organize the way you spend and earn.'
  }[section]

  const items = section === 'transactions' ? transactions
    : section === 'budgets' ? budgets
    : section === 'accounts' ? accounts
    : categories

  async function remove(id: number) {
    if (!window.confirm('Delete this item?')) return
    if (section === 'transactions') await deleteTransaction(id)
    if (section === 'budgets') await deleteBudget(id)
    if (section === 'accounts') await deleteAccount(id)
    if (section === 'categories') await deleteCategory(id)
    onRefresh()
  }

  function openNew() {
    setEditingId(null)
    setShowForm(true)
  }

  function openEdit(id: number) {
    setEditingId(id)
    setShowForm(true)
  }

  function closeForm() {
    setShowForm(false)
    setEditingId(null)
  }

  function handleCreated() {
    closeForm()
    onRefresh()
  }

  return (
    <div className="management-view">
      <header className="management-header">
        <div>
          <p className="kicker">FINTRACK / {section.toUpperCase()}</p>
          <h1>{title}</h1>
          <p>{description}</p>
        </div>
        <button className="primary-button" onClick={openNew}>
          <Plus size={18} /> Add {section === 'transactions' ? 'transaction' : section.slice(0, -1)}
        </button>
      </header>

      <div className="management-toolbar">
        <span>{items.length} {items.length === 1 ? 'item' : 'items'}</span>
      </div>

      <div className="management-list">
        {items.length === 0 ? (
          <div className="empty-state">
            <div><Check size={22} /></div>
            <h2>Nothing here yet</h2>
            <p>Create your first {section.slice(0, -1)} to see it in this list.</p>
            <button className="outline-button" onClick={openNew}>
              <Plus size={16} /> Get started
            </button>
          </div>
        ) : (
          items.map((item) => (
            <ManagementRow
              key={item.id}
              item={item}
              section={section}
              onEdit={() => openEdit(item.id)}
              onDelete={() => remove(item.id)}
            />
          ))
        )}
      </div>

      {showForm && (
        <ResourceForm
          section={section}
          accounts={accounts}
          categories={categories}
          editingItem={editingId ? items.find((item) => item.id === editingId) : undefined}
          onClose={closeForm}
          onCreated={handleCreated}
        />
      )}
    </div>
  )
}

interface ManagementRowProps {
  item: Account | Category | Transaction | Budget
  section: Section
  onEdit: () => void
  onDelete: () => void
}

function ManagementRow({ item, section, onEdit, onDelete }: ManagementRowProps) {
  if (section === 'accounts') {
    const account = item as Account
    return (
      <div className="management-row">
        <div className="row-badge violet-badge">$</div>
        <div>
          <strong>{account.name}</strong>
          <span>{account.currency} account</span>
        </div>
        <strong>{Number(account.balance).toLocaleString('en-US', { minimumFractionDigits: 2 })}</strong>
        <button className="edit-button" aria-label="Edit account" onClick={onEdit}>
          <Edit2 size={16} />
        </button>
        <button className="delete-button" aria-label="Delete account" onClick={onDelete}>
          <Trash2 size={16} />
        </button>
      </div>
    )
  }

  if (section === 'categories') {
    const category = item as Category
    return (
      <div className="management-row">
        <div className="row-badge pink-badge">{category.icon || '•'}</div>
        <div>
          <strong>{category.name}</strong>
          <span>{category.type === 'INCOME' ? 'Income' : 'Expense'} category</span>
        </div>
        <span className="color-chip" style={{ background: category.color || '#7450b5' }} />
        <button className="edit-button" aria-label="Edit category" onClick={onEdit}>
          <Edit2 size={16} />
        </button>
        <button className="delete-button" aria-label="Delete category" onClick={onDelete}>
          <Trash2 size={16} />
        </button>
      </div>
    )
  }

  if (section === 'budgets') {
    const budget = item as Budget
    return (
      <div className="management-row">
        <div className="row-badge gold-badge">%</div>
        <div>
          <strong>{budget.categoryName}</strong>
          <span>{budget.month}/{budget.year}</span>
        </div>
        <strong>${Number(budget.amount).toLocaleString('en-US', { minimumFractionDigits: 2 })}</strong>
        <button className="edit-button" aria-label="Edit budget" onClick={onEdit}>
          <Edit2 size={16} />
        </button>
        <button className="delete-button" aria-label="Delete budget" onClick={onDelete}>
          <Trash2 size={16} />
        </button>
      </div>
    )
  }

  const transaction = item as Transaction
  return (
    <div className="management-row">
      <div className={`row-badge ${transaction.type === 'INCOME' ? 'violet-badge' : 'pink-badge'}`}>
        {transaction.type === 'INCOME' ? '+' : '-'}
      </div>
      <div>
        <strong>{transaction.description || transaction.categoryName}</strong>
        <span>{transaction.categoryName} · {transaction.date}</span>
      </div>
      <strong className={transaction.type === 'INCOME' ? 'positive-amount' : ''}>
        {transaction.type === 'INCOME' ? '+' : '-'}${Number(transaction.amount).toLocaleString('en-US', { minimumFractionDigits: 2 })}
      </strong>
      <button className="edit-button" aria-label="Edit transaction" onClick={onEdit}>
        <Edit2 size={16} />
      </button>
      <button className="delete-button" aria-label="Delete transaction" onClick={onDelete}>
        <Trash2 size={16} />
      </button>
    </div>
  )
}

interface ResourceFormProps {
  section: Section
  accounts: Account[]
  categories: Category[]
  editingItem?: Account | Category | Transaction | Budget
  onClose: () => void
  onCreated: () => void
}

function ResourceForm({ section, accounts, categories, editingItem, onClose, onCreated }: ResourceFormProps) {
  const isEditing = !!editingItem
  
  const [name, setName] = useState(() => {
    if (!editingItem) return ''
    if (section === 'transactions') return (editingItem as Transaction).description || ''
    if (section === 'accounts' || section === 'categories') return (editingItem as any).name || ''
    return ''
  })

  const [amount, setAmount] = useState(() => {
    if (!editingItem) return ''
    if (section === 'transactions' || section === 'budgets') return String((editingItem as any).amount || '')
    return ''
  })

  const [type, setType] = useState<'INCOME' | 'EXPENSE'>(() => {
    if (!editingItem) return 'EXPENSE'
    if (section === 'transactions' || section === 'categories') return (editingItem as any).type || 'EXPENSE'
    return 'EXPENSE'
  })

  const [accountId, setAccountId] = useState(() => {
    if (!editingItem) return accounts[0]?.id.toString() || ''
    if (section === 'transactions' || section === 'budgets') return String((editingItem as any).accountId || '')
    return ''
  })

  const [categoryId, setCategoryId] = useState(() => {
    if (!editingItem) return ''
    if (section === 'transactions' || section === 'budgets') return String((editingItem as any).categoryId || '')
    return ''
  })

  const [date, setDate] = useState(() => {
    if (!editingItem || section !== 'transactions') return new Date().toISOString().slice(0, 10)
    return (editingItem as Transaction).date
  })

  const [month, setMonth] = useState(() => {
    if (!editingItem || section !== 'budgets') return String(new Date().getMonth() + 1)
    return String((editingItem as Budget).month)
  })

  const [year, setYear] = useState(() => {
    if (!editingItem || section !== 'budgets') return String(new Date().getFullYear())
    return String((editingItem as Budget).year)
  })

  const [currency, setCurrency] = useState(() => {
    if (!editingItem || section !== 'accounts') return 'USD'
    return (editingItem as Account).currency
  })

  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  const filteredCategories = categories.filter((category) => category.type === type || category.type === 'BOTH')

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setSaving(true)

    try {
      if (section === 'accounts') {
        const accountData = { name, currency }
        if (isEditing) {
          await updateAccount(editingItem!.id, accountData)
        } else {
          await createAccount(accountData)
        }
      }

      if (section === 'categories') {
        const categoryData = { name, type, icon: '•', color: '#7450b5' }
        if (isEditing) {
          await updateCategory(editingItem!.id, categoryData)
        } else {
          await createCategory(categoryData)
        }
      }

      if (section === 'transactions') {
        const transactionData = {
          amount: Number(amount),
          description: name,
          date,
          type,
          accountId: Number(accountId),
          categoryId: Number(categoryId)
        }
        if (isEditing) {
          await updateTransaction(editingItem!.id, transactionData)
        } else {
          await createTransaction(transactionData)
        }
      }

      if (section === 'budgets') {
        const budgetData = {
          amount: Number(amount),
          month: Number(month),
          year: Number(year),
          accountId: Number(accountId),
          categoryId: Number(categoryId)
        }
        if (isEditing) {
          await updateBudget(editingItem!.id, budgetData)
        } else {
          await createBudget(budgetData)
        }
      }

      onCreated()
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Unable to save this item.')
    } finally {
      setSaving(false)
    }
  }

  const label = section === 'accounts' ? 'account' : section.slice(0, -1)
  const heading = isEditing ? `Edit ${label}` : `Add ${label}`
  const kicker = isEditing ? `EDIT ${section.toUpperCase()}` : `NEW ${section.toUpperCase()}`

  return (
    <div className="modal-backdrop">
      <section className="transaction-modal resource-modal" role="dialog" aria-modal="true">
        <div className="modal-heading">
          <div>
            <p className="kicker">{kicker}</p>
            <h2>{heading}</h2>
          </div>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        <form onSubmit={submit}>
          {section === 'accounts' && (
            <>
              <label>
                Name
                <input
                  value={name}
                  onChange={(event) => setName(event.target.value)}
                  placeholder="Everyday checking"
                  required
                />
              </label>
              <label>
                Currency
                <select value={currency} onChange={(event) => setCurrency(event.target.value)}>
                  <option>USD</option>
                  <option>EUR</option>
                  <option>ARS</option>
                </select>
              </label>
            </>
          )}

          {section === 'categories' && (
            <>
              <label>
                Name
                <input
                  value={name}
                  onChange={(event) => setName(event.target.value)}
                  placeholder="Groceries"
                  required
                />
              </label>
              <div className="type-toggle">
                <button
                  type="button"
                  className={type === 'EXPENSE' ? 'selected' : ''}
                  onClick={() => setType('EXPENSE')}
                >
                  Expense
                </button>
                <button
                  type="button"
                  className={type === 'INCOME' ? 'selected' : ''}
                  onClick={() => setType('INCOME')}
                >
                  Income
                </button>
              </div>
            </>
          )}

          {(section === 'transactions' || section === 'budgets') && (
            <>
              <label>
                Amount
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={amount}
                  onChange={(event) => setAmount(event.target.value)}
                  placeholder="0.00"
                  required
                />
              </label>

              {section === 'transactions' && (
                <>
                  <label>
                    Description
                    <input
                      value={name}
                      onChange={(event) => setName(event.target.value)}
                      placeholder="What was this for?"
                    />
                  </label>
                  <label>
                    Date
                    <input
                      type="date"
                      value={date}
                      onChange={(event) => setDate(event.target.value)}
                      required
                    />
                  </label>
                  <div className="type-toggle">
                    <button
                      type="button"
                      className={type === 'EXPENSE' ? 'selected' : ''}
                      onClick={() => setType('EXPENSE')}
                    >
                      Expense
                    </button>
                    <button
                      type="button"
                      className={type === 'INCOME' ? 'selected' : ''}
                      onClick={() => setType('INCOME')}
                    >
                      Income
                    </button>
                  </div>
                </>
              )}

              {section === 'budgets' && (
                <div className="form-row">
                  <label>
                    Month
                    <input
                      type="number"
                      min="1"
                      max="12"
                      value={month}
                      onChange={(event) => setMonth(event.target.value)}
                      required
                    />
                  </label>
                  <label>
                    Year
                    <input
                      type="number"
                      value={year}
                      onChange={(event) => setYear(event.target.value)}
                      required
                    />
                  </label>
                </div>
              )}

              <label>
                Account
                <select value={accountId} onChange={(event) => setAccountId(event.target.value)} required>
                  {accounts.map((account) => (
                    <option key={account.id} value={account.id}>
                      {account.name}
                    </option>
                  ))}
                </select>
              </label>

              <label>
                Category
                <select value={categoryId} onChange={(event) => setCategoryId(event.target.value)} required>
                  <option value="">Select a category</option>
                  {filteredCategories.map((category) => (
                    <option key={category.id} value={category.id}>
                      {category.name}
                    </option>
                  ))}
                </select>
              </label>
            </>
          )}

          {error && <div className="form-error">{error}</div>}

          <div className="form-actions">
            <button type="submit" className="primary-button" disabled={saving}>
              {saving ? 'Saving...' : isEditing ? 'Update' : 'Create'}
            </button>
            <button type="button" className="outline-button" onClick={onClose} disabled={saving}>
              Cancel
            </button>
          </div>
        </form>
      </section>
    </div>
  )
}

export default ManagementView
