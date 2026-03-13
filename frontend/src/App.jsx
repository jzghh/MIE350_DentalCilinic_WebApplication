import { useState, useEffect } from 'react'
import { login, getDashboardStats } from './api'
import './App.css'

function App() {
  const [user, setUser] = useState(() => {
    try {
      const s = localStorage.getItem('dental_user')
      return s ? JSON.parse(s) : null
    } catch {
      return null
    }
  })
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [stats, setStats] = useState(null)
  const [statsError, setStatsError] = useState('')

  const handleLogin = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const data = await login(username, password)
      setUser(data)
      localStorage.setItem('dental_user', JSON.stringify(data))
    } catch (err) {
      setError(err.message || '登录失败')
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    setUser(null)
    localStorage.removeItem('dental_user')
    setStats(null)
  }

  useEffect(() => {
    if (!user) return
    setStatsError('')
    getDashboardStats()
      .then(setStats)
      .catch((err) => setStatsError(err.message || '获取数据失败'))
  }, [user])

  if (!user) {
    return (
      <div className="app login-page">
        <div className="login-card">
          <h1>牙科诊所管理系统</h1>
          <p className="subtitle">请登录</p>
          <form onSubmit={handleLogin}>
            <input
              type="text"
              placeholder="用户名"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
            />
            <input
              type="password"
              placeholder="密码"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
            />
            {error && <p className="error">{error}</p>}
            <button type="submit" disabled={loading}>
              {loading ? '登录中…' : '登录'}
            </button>
          </form>
          <p className="hint">测试账号: receptionist / dental123</p>
        </div>
      </div>
    )
  }

  return (
    <div className="app dashboard">
      <header>
        <h1>牙科诊所管理系统</h1>
        <div className="user-info">
          <span>{user.username}</span>
          <span className="role">({user.role})</span>
          <button type="button" onClick={handleLogout}>退出</button>
        </div>
      </header>
      <main>
        <h2>仪表盘</h2>
        {statsError && <p className="error">{statsError}</p>}
        {stats && (
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-value">{stats.monthlyRevenue ?? '-'}</div>
              <div className="stat-label">本月收入</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{stats.todayAppointments ?? '-'}</div>
              <div className="stat-label">今日预约</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{stats.totalPatients ?? '-'}</div>
              <div className="stat-label">患者总数</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{stats.lowStockCount ?? '-'}</div>
              <div className="stat-label">低库存项</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{stats.pendingBills ?? '-'}</div>
              <div className="stat-label">待结账单</div>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}

export default App
