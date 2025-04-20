"use client"

import { createContext, useContext, useState, useEffect, type ReactNode } from "react"
import { useRouter } from "next/navigation"

type AuthContextType = {
  isAuthenticated: boolean
  login: (username: string, password: string) => Promise<boolean>
  logout: () => Promise<void>
  loading: boolean
  error: string | null
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}

type AuthProviderProps = {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const router = useRouter()

  // Проверяем статус аутентификации при загрузке
  useEffect(() => {
    const checkAuthStatus = async () => {
      try {
        // Проверяем статус аутентификации через API
        const response = await fetch("/api/auth/status", {
          credentials: "include",
        })

        if (response.ok) {
          setIsAuthenticated(true)
        } else {
          setIsAuthenticated(false)
        }
      } catch (err) {
        console.error("Error checking auth status:", err)
        setIsAuthenticated(false)
      } finally {
        setLoading(false)
      }
    }

    checkAuthStatus()
  }, [])

  const login = async (username: string, password: string): Promise<boolean> => {
    setError(null)
    setLoading(true)

    try {
      // Создаем URLSearchParams для отправки данных формы
      const params = new URLSearchParams()
      params.append("username", username)
      params.append("password", password)

      // Отправляем запрос на авторизацию
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        body: params,
        credentials: "include",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
      })

      if (response.ok) {
        setIsAuthenticated(true)
        return true
      } else {
        const errorData = await response.text()
        setError(errorData || "Ошибка авторизации. Проверьте логин и пароль.")
        return false
      }
    } catch (err) {
      console.error("Ошибка при авторизации:", err)
      setError("Ошибка соединения с сервером. Пожалуйста, попробуйте позже.")
      return false
    } finally {
      setLoading(false)
    }
  }

  const logout = async (): Promise<void> => {
    setLoading(true)

    try {
      // Отправляем запрос на выход
      await fetch("http://localhost:8080/logout", {
        method: "POST",
        credentials: "include",
      })

      setIsAuthenticated(false)
      router.push("/")
    } catch (err) {
      console.error("Ошибка при выходе:", err)
    } finally {
      setLoading(false)
    }
  }

  const value = {
    isAuthenticated,
    login,
    logout,
    loading,
    error,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
