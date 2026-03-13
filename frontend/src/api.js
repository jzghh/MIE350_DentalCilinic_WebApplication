/**
 * 后端 API 基础地址
 * 开发时 Vite 会使用 .env.development 中的 VITE_API_URL
 */
const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export function getApiUrl(path) {
  const base = API_BASE.replace(/\/$/, '')
  const p = path.startsWith('/') ? path : `/${path}`
  return `${base}${p}`
}

/**
 * 通用请求封装
 */
export async function apiRequest(path, options = {}) {
  const url = getApiUrl(path)
  const res = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  })
  const data = await res.json().catch(() => ({}))
  if (!res.ok) {
    throw new Error(data.message || data.error || `HTTP ${res.status}`)
  }
  return data
}

/** 登录 */
export async function login(username, password) {
  return apiRequest('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  })
}

/** 仪表盘统计 */
export async function getDashboardStats() {
  return apiRequest('/api/dashboard/stats')
}

/** 患者列表 */
export async function getPatients() {
  return apiRequest('/api/patients')
}
