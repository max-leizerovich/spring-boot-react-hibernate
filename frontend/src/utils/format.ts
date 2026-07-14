/**
 * Formats a numeric amount as USD currency.
 */
export const formatCurrency = (amount: number): string =>
  new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount)

/**
 * Formats an ISO-8601 timestamp for display.
 */
export const formatDateTime = (iso: string): string =>
  new Intl.DateTimeFormat('en-US', { dateStyle: 'medium', timeStyle: 'short' }).format(
    new Date(iso),
  )
