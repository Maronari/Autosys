/**
 * Утилиты для работы с CSRF токеном
 */

// Генерация случайного CSRF токена
export function generateCsrfToken(length = 32): string {
  // Используем Web Crypto API для генерации криптографически стойкого случайного значения
  const array = new Uint8Array(length)
  window.crypto.getRandomValues(array)

  // Преобразуем массив байтов в строку в формате base64
  return Array.from(array)
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("")
}

// Сохранение CSRF токена в localStorage
export function saveCsrfToken(token: string): void {
  if (typeof window !== "undefined") {
    localStorage.setItem("csrf-token", token)
  }
}

// Получение сохраненного CSRF токена
export function getSavedCsrfToken(): string | null {
  if (typeof window !== "undefined") {
    return localStorage.getItem("csrf-token")
  }
  return null
}

// Получение или генерация CSRF токена
export function getOrCreateCsrfToken(): string {
  const savedToken = getSavedCsrfToken()
  if (savedToken) {
    return savedToken
  }

  const newToken = generateCsrfToken()
  saveCsrfToken(newToken)
  return newToken
}
