import { useEffect, useState } from 'react'

/**
 * Returns a debounced copy of the value after the delay elapses.
 */
export const useDebounce = <T>(value: T, delayMs: number): T => {
  const [debouncedValue, setDebouncedValue] = useState(value)

  useEffect(() => {
    const timer = window.setTimeout(() => setDebouncedValue(value), delayMs)

    return () => window.clearTimeout(timer)
  }, [value, delayMs])

  return debouncedValue
}
