export const TOKEN_KEY = 'token'
export const LOGIN_INFO_KEY = 'loginInfo'

export const getToken = () => localStorage.getItem(TOKEN_KEY) || ''

export const getLoginInfo = () => {
  const text = localStorage.getItem(LOGIN_INFO_KEY)
  if (!text) return null
  try {
    return JSON.parse(text)
  } catch {
    return null
  }
}

export const setAuth = (loginInfo) => {
  localStorage.setItem(TOKEN_KEY, loginInfo.token)
  localStorage.setItem(LOGIN_INFO_KEY, JSON.stringify(loginInfo))
}

export const clearAuth = () => {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(LOGIN_INFO_KEY)
}

export const getPermissions = () => getLoginInfo()?.permissions || []

export const hasPermission = (permission) => getPermissions().includes(permission)
