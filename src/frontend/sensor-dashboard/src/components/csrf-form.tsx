"use client"

import { useEffect, useState, type ReactNode } from "react"
import { fetchCsrfToken } from "@/lib/csrf"

interface CsrfFormProps {
  children: ReactNode
  onTokenFetched?: (token: string | null) => void
  className?: string
}

export default function CsrfForm({ children, onTokenFetched, className }: CsrfFormProps) {
  const [csrfToken, setCsrfToken] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const getCsrfToken = async () => {
      try {
        const token = await fetchCsrfToken()
        setCsrfToken(token)
        if (onTokenFetched) {
          onTokenFetched(token)
        }
      } catch (error) {
        console.error("Ошибка при получении CSRF токена:", error)
      } finally {
        setLoading(false)
      }
    }

    getCsrfToken()
  }, [onTokenFetched])

  if (loading) {
    return <div className="text-center py-4">Загрузка формы...</div>
  }

  return (
    <form className={className}>
      {csrfToken && <input type="hidden" name="_csrf" value={csrfToken} />}
      {children}
    </form>
  )
}
