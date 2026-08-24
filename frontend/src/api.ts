const API_BASE = '/api'

export type User = {
  id: number
  name: string
  email: string
}

export type AuthResponse = {
  token: string
  userId: number
  name: string
  email: string
}

export type Account = {
  id: number
  name: string
  currency: string
  balance: number
  createdAt: string
}

export type Category = {
  id: number
  name: string
  type: 'INCOME' | 'EXPENSE' | 'BOTH'
  icon?: string
  color?: string
}

export type Transaction = {
  id: number
  amount: number
  description: string
  date: string
  type: 'INCOME' | 'EXPENSE'
  accountId: number
  categoryId: number
  categoryName: string
  createdAt: string
}

export type Budget = {
  id: number
  amount: number
  month: number
  year: number
  accountId: number
  categoryId: number
  categoryName: string
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('fintrack_token')
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  })

  if (!response.ok) {
    const error = await response.json().catch(() => null)
    throw new Error(error?.message ?? 'Something went wrong. Please try again.')
  }

  if (response.status === 204) return undefined as T
  return response.json() as Promise<T>
}

export function login(email: string, password: string) {
  return request<AuthResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  })
}

export function register(name: string, email: string, password: string) {
  return request<AuthResponse>('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ name, email, password }),
  })
}

export function getCurrentUser() {
  return request<User>('/auth/me')
}

export function getAccounts() {
  return request<Account[]>('/accounts')
}

export function getCategories() {
  return request<Category[]>('/categories')
}

export function getTransactions(accountId: number) {
  return request<Transaction[]>(`/transactions?accountId=${accountId}`)
}

export function createTransaction(transaction: Omit<Transaction, 'id' | 'categoryName' | 'createdAt'>) {
  return request<Transaction>('/transactions', {
    method: 'POST',
    body: JSON.stringify(transaction),
  })
}

export function createAccount(account: { name: string; currency: string }) {
  return request<Account>('/accounts', { method: 'POST', body: JSON.stringify(account) })
}

export function deleteAccount(id: number) {
  return request<void>(`/accounts/${id}`, { method: 'DELETE' })
}

export function createCategory(category: { name: string; type: 'INCOME' | 'EXPENSE'; icon: string; color: string }) {
  return request<Category>('/categories', { method: 'POST', body: JSON.stringify(category) })
}

export function deleteCategory(id: number) {
  return request<void>(`/categories/${id}`, { method: 'DELETE' })
}

export function deleteTransaction(id: number) {
  return request<void>(`/transactions/${id}`, { method: 'DELETE' })
}

export function getBudgets(accountId: number) {
  return request<Budget[]>(`/budgets?accountId=${accountId}`)
}

export function createBudget(budget: Omit<Budget, 'id' | 'categoryName'>) {
  return request<Budget>('/budgets', { method: 'POST', body: JSON.stringify(budget) })
}

export function deleteBudget(id: number) {
  return request<void>(`/budgets/${id}`, { method: 'DELETE' })
}

export function saveSession(auth: AuthResponse) {
  localStorage.setItem('fintrack_token', auth.token)
  localStorage.setItem('fintrack_user', JSON.stringify({ id: auth.userId, name: auth.name, email: auth.email }))
}

export function clearSession() {
  localStorage.removeItem('fintrack_token')
  localStorage.removeItem('fintrack_user')
}
